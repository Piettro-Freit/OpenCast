package com.arturo254.opentune.ui.component

import android.view.ContextThemeWrapper
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.mediarouter.app.MediaRouteButton
import com.arturo254.opentune.R
import com.google.android.gms.cast.framework.CastButtonFactory

/**
 * Botão de Cast no estilo dos outros ícones do player (Material 3, tintado).
 * Um MediaRouteButton invisível fica anexado à tela e recebe o clique,
 * assim o diálogo oficial de seleção/controle de dispositivos é aberto
 * (exige que a Activity seja FragmentActivity).
 */
@Composable
fun CastIconButton(
    isCasting: Boolean,
    tint: Color,
    modifier: Modifier = Modifier,
    iconSize: Dp = 24.dp,
) {
    val routeButton =
        remember { arrayOfNulls<MediaRouteButton>(1) }

    Box(modifier = modifier) {
        AndroidView(
            modifier = Modifier.size(1.dp),
            factory = { ctx ->
                val themed = ContextThemeWrapper(ctx, androidx.appcompat.R.style.Theme_AppCompat_DayNight_NoActionBar)
                MediaRouteButton(themed).also {
                    runCatching { CastButtonFactory.setUpMediaRouteButton(ctx.applicationContext, it) }
                    routeButton[0] = it
                }
            },
        )
        Icon(
            painter = painterResource(if (isCasting) R.drawable.cast_connected else R.drawable.cast),
            contentDescription = "Cast",
            tint = tint,
            modifier =
                Modifier
                    .size(iconSize)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) { routeButton[0]?.performClick() },
        )
    }
}
