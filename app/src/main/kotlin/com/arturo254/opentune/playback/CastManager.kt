package com.arturo254.opentune.playback

import android.content.Context
import android.os.SystemClock
import androidx.media3.cast.CastPlayer
import androidx.media3.cast.SessionAvailabilityListener
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.google.android.gms.cast.Cast
import com.google.android.gms.cast.framework.CastContext
import kotlin.math.roundToInt
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import kotlin.math.abs

/**
 * Espelha o ExoPlayer local em um dispositivo Google Cast.
 *
 * O ExoPlayer continua sendo a "fonte da verdade" (fila, UI, letras, Android Auto, etc.),
 * mas fica mudo enquanto o Cast toca. Play/pause/seek/troca de faixa são repassados ao Cast,
 * e a posição do Cast corrige o relógio local (o Cast é o mestre de posição).
 */
@UnstableApi
class CastManager(
    private val context: Context,
    private val player: ExoPlayer,
    private val scope: CoroutineScope,
    private val resolveStream: suspend (mediaId: String) -> CastStream?,
    private val onRemoteActive: (Boolean) -> Unit,
    private val onRemoteVolumeChanged: () -> Unit = {},
) : SessionAvailabilityListener, Player.Listener {

    data class CastStream(val url: String, val mimeType: String)

    private var castPlayer: CastPlayer? = null
    private var castContext: CastContext? = null
    private var loadJob: Job? = null
    private var syncJob: Job? = null
    private var loadedMediaId: String? = null
    private var ignoreLocalSeekUntil = 0L
    private var lastCastPositionMs = 0L

    var isCasting = false
        private set

    fun init() {
        runCatching {
            CastContext.getSharedInstance(context, MoreExecutors.directExecutor())
                .addOnSuccessListener { ctx ->
                    castContext = ctx
                    val cp = CastPlayer(ctx)
                    cp.setSessionAvailabilityListener(this)
                    cp.addListener(castListener)
                    castPlayer = cp
                    if (cp.isCastSessionAvailable) onCastSessionAvailable()
                }.addOnFailureListener { Timber.tag(TAG).w(it, "Cast indisponível") }
        }.onFailure { Timber.tag(TAG).w(it, "Cast não inicializado") }
    }

    fun release() {
        stopCasting(resumeLocal = false)
        castPlayer?.setSessionAvailabilityListener(null)
        castPlayer?.release()
        castPlayer = null
    }

    override fun onCastSessionAvailable() {
        if (isCasting) return
        isCasting = true
        castSession()?.addCastListener(volumeListener)
        onRemoteActive(true)
        player.addListener(this)
        loadCurrent(player.currentPosition, player.playWhenReady)
        startSyncLoop()
    }

    override fun onCastSessionUnavailable() = stopCasting(resumeLocal = true)

    private fun stopCasting(resumeLocal: Boolean) {
        if (!isCasting) return
        isCasting = false
        castSession()?.removeCastListener(volumeListener)
        loadJob?.cancel()
        syncJob?.cancel()
        player.removeListener(this)
        val pos = lastCastPositionMs
        runCatching { castPlayer?.stop(); castPlayer?.clearMediaItems() }
        onRemoteActive(false)
        if (resumeLocal && player.currentMediaItem != null && pos > 0) {
            ignoreLocalSeekUntil = SystemClock.elapsedRealtime() + 800
            player.seekTo(pos)
        }
    }

    private fun loadCurrent(positionMs: Long, play: Boolean) {
        loadJob?.cancel()
        val item = player.currentMediaItem ?: return
        val mediaId = item.mediaId
        loadJob = scope.launch {
            val stream = withContext(Dispatchers.IO) { resolveStream(mediaId) }
            if (stream == null || !isCasting) {
                Timber.tag(TAG).w("Sem stream para $mediaId")
                return@launch
            }
            val castItem =
                MediaItem.Builder()
                    .setMediaId(mediaId)
                    .setUri(stream.url)
                    .setMimeType(stream.mimeType)
                    .setMediaMetadata(item.mediaMetadata)
                    .build()
            loadedMediaId = mediaId
            castPlayer?.apply {
                setMediaItem(castItem, positionMs)
                playWhenReady = play
                prepare()
            }
        }
    }

    // ---- Volume remoto (botões físicos do celular) ----
    private val volumeListener =
        object : Cast.Listener() {
            override fun onVolumeChanged() = onRemoteVolumeChanged()
        }

    private fun castSession() = castContext?.sessionManager?.currentCastSession

    fun remoteVolumeSteps(): Int = ((castSession()?.getVolume() ?: 0.0) * VOLUME_STEPS).roundToInt()

    fun isRemoteMuted(): Boolean = castSession()?.isMute() ?: false

    fun setRemoteVolumeSteps(steps: Int) {
        runCatching { castSession()?.setVolume(steps.coerceIn(0, VOLUME_STEPS).toDouble() / VOLUME_STEPS) }
        onRemoteVolumeChanged()
    }

    fun adjustRemoteVolume(direction: Int) {
        if (isRemoteMuted()) runCatching { castSession()?.setMute(false) }
        setRemoteVolumeSteps(remoteVolumeSteps() + direction)
    }

    fun setRemoteMuted(muted: Boolean) {
        runCatching { castSession()?.setMute(muted) }
        onRemoteVolumeChanged()
    }

    // ---- ExoPlayer local -> Cast ----
    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
        if (isCasting && mediaItem != null) loadCurrent(0L, player.playWhenReady)
    }

    override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
        if (isCasting && castPlayer?.playWhenReady != playWhenReady) castPlayer?.playWhenReady = playWhenReady
    }

    override fun onPositionDiscontinuity(
        oldPosition: Player.PositionInfo,
        newPosition: Player.PositionInfo,
        reason: Int,
    ) {
        if (!isCasting || reason != Player.DISCONTINUITY_REASON_SEEK) return
        if (SystemClock.elapsedRealtime() < ignoreLocalSeekUntil) return
        if (oldPosition.mediaItemIndex == newPosition.mediaItemIndex) castPlayer?.seekTo(newPosition.positionMs)
    }

    // ---- Cast -> ExoPlayer local ----
    private val castListener =
        object : Player.Listener {
            override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
                // Botões do diálogo do Cast (play/pause) refletem no player local
                if (isCasting && player.playWhenReady != playWhenReady) player.playWhenReady = playWhenReady
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                if (!isCasting || playbackState != Player.STATE_ENDED) return
                val endedId = loadedMediaId
                scope.launch {
                    delay(1500)
                    // se o player local ainda está na mesma faixa, avança
                    if (isCasting && player.currentMediaItem?.mediaId == endedId) {
                        if (player.hasNextMediaItem()) player.seekToNextMediaItem() else player.pause()
                    }
                }
            }

            override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                Timber.tag(TAG).w(error, "Erro no Cast")
            }
        }

    private fun startSyncLoop() {
        syncJob?.cancel()
        syncJob =
            scope.launch {
                while (isActive && isCasting) {
                    delay(4000)
                    val cp = castPlayer ?: continue
                    if (cp.playbackState == Player.STATE_READY && cp.currentMediaItem?.mediaId == player.currentMediaItem?.mediaId) {
                        val castPos = cp.currentPosition
                        lastCastPositionMs = castPos
                        if (abs(castPos - player.currentPosition) > 2500) {
                            ignoreLocalSeekUntil = SystemClock.elapsedRealtime() + 800
                            player.seekTo(castPos)
                        }
                    }
                }
            }
    }

    companion object {
        private const val TAG = "CastManager"
        const val VOLUME_STEPS = 20
    }
}
