package com.arturo254.opentune.playback

import androidx.media3.common.DeviceInfo
import androidx.media3.common.ForwardingPlayer
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import java.util.concurrent.CopyOnWriteArraySet

/**
 * Player entregue à MediaSession. Enquanto o Cast está ativo, ele se declara como
 * reprodução REMOTA com volume próprio: assim os botões físicos de volume do celular
 * (e o volume do Android Auto/relógio) passam a controlar o volume do Chromecast.
 */
@UnstableApi
class CastAwarePlayer(
    player: Player,
    private val castManager: () -> CastManager?,
) : ForwardingPlayer(player) {
    private val listeners = CopyOnWriteArraySet<Player.Listener>()

    private fun cast(): CastManager? = castManager()?.takeIf { it.isCasting }

    override fun addListener(listener: Player.Listener) {
        super.addListener(listener)
        listeners.add(listener)
    }

    override fun removeListener(listener: Player.Listener) {
        super.removeListener(listener)
        listeners.remove(listener)
    }

    /** Chamar quando o Cast conectar/desconectar ou o volume remoto mudar. */
    fun notifyRemoteStateChanged() {
        val info = deviceInfo
        val volume = deviceVolume
        val muted = isDeviceMuted
        listeners.forEach {
            it.onDeviceInfoChanged(info)
            it.onDeviceVolumeChanged(volume, muted)
        }
    }

    override fun getDeviceInfo(): DeviceInfo =
        if (cast() != null) {
            DeviceInfo.Builder(DeviceInfo.PLAYBACK_TYPE_REMOTE)
                .setMinVolume(0)
                .setMaxVolume(CastManager.VOLUME_STEPS)
                .build()
        } else {
            super.getDeviceInfo()
        }

    override fun getDeviceVolume(): Int = cast()?.remoteVolumeSteps() ?: super.getDeviceVolume()

    override fun isDeviceMuted(): Boolean = cast()?.isRemoteMuted() ?: super.isDeviceMuted()

    @Suppress("DEPRECATION")
    override fun setDeviceVolume(volume: Int) {
        val c = cast()
        if (c != null) c.setRemoteVolumeSteps(volume) else super.setDeviceVolume(volume)
    }

    override fun setDeviceVolume(volume: Int, flags: Int) {
        val c = cast()
        if (c != null) c.setRemoteVolumeSteps(volume) else super.setDeviceVolume(volume, flags)
    }

    @Suppress("DEPRECATION")
    override fun increaseDeviceVolume() {
        val c = cast()
        if (c != null) c.adjustRemoteVolume(+1) else super.increaseDeviceVolume()
    }

    override fun increaseDeviceVolume(flags: Int) {
        val c = cast()
        if (c != null) c.adjustRemoteVolume(+1) else super.increaseDeviceVolume(flags)
    }

    @Suppress("DEPRECATION")
    override fun decreaseDeviceVolume() {
        val c = cast()
        if (c != null) c.adjustRemoteVolume(-1) else super.decreaseDeviceVolume()
    }

    override fun decreaseDeviceVolume(flags: Int) {
        val c = cast()
        if (c != null) c.adjustRemoteVolume(-1) else super.decreaseDeviceVolume(flags)
    }

    @Suppress("DEPRECATION")
    override fun setDeviceMuted(muted: Boolean) {
        val c = cast()
        if (c != null) c.setRemoteMuted(muted) else super.setDeviceMuted(muted)
    }

    override fun setDeviceMuted(muted: Boolean, flags: Int) {
        val c = cast()
        if (c != null) c.setRemoteMuted(muted) else super.setDeviceMuted(muted, flags)
    }
}
