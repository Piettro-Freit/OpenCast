package com.arturo254.opentune.playback

import android.content.Context
import com.google.android.gms.cast.CastMediaControlIntent
import com.google.android.gms.cast.framework.CastOptions
import com.google.android.gms.cast.framework.OptionsProvider
import com.google.android.gms.cast.framework.SessionProvider
import com.google.android.gms.cast.framework.media.CastMediaOptions

/**
 * Configuração do Google Cast com o Default Media Receiver do Google.
 * A notificação/sessão de mídia do Cast SDK fica desligada porque o
 * MusicService (Media3) já cuida disso.
 */
class CastOptionsProvider : OptionsProvider {
    override fun getCastOptions(context: Context): CastOptions =
        CastOptions.Builder()
            .setReceiverApplicationId(CastMediaControlIntent.DEFAULT_MEDIA_RECEIVER_APPLICATION_ID)
            .setStopReceiverApplicationWhenEndingSession(true)
            .setCastMediaOptions(
                CastMediaOptions.Builder()
                    .setMediaSessionEnabled(false)
                    .setNotificationOptions(null)
                    .build(),
            ).build()

    override fun getAdditionalSessionProviders(context: Context): List<SessionProvider>? = null
}
