package com.arturo254.opentune.ui.component

import android.content.Context
import android.os.Bundle
import android.view.ContextThemeWrapper
import androidx.compose.ui.draw.alpha
import androidx.mediarouter.app.MediaRouteChooserDialog
import androidx.mediarouter.app.MediaRouteChooserDialogFragment
import androidx.mediarouter.app.MediaRouteControllerDialog
import androidx.mediarouter.app.MediaRouteControllerDialogFragment
import androidx.mediarouter.app.MediaRouteDialogFactory
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

    Box(
        modifier =
            modifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) { routeButton[0]?.performClick() },
        contentAlignment = androidx.compose.ui.Alignment.Center,
    ) {
        CastRouteAnchor(routeButton)
        Icon(
            painter = painterResource(if (isCasting) R.drawable.cast_connected else R.drawable.cast),
            contentDescription = "Cast",
            tint = tint,
            modifier = Modifier.size(iconSize),
        )
    }
}

/**
 * MediaRouteButton invisível, anexado à tela, que guarda a referência em [holder].
 * Chamar `holder[0]?.performClick()` abre o diálogo oficial do Cast.
 */
@Composable
fun CastRouteAnchor(holder: Array<MediaRouteButton?>) {
    AndroidView(
        modifier = Modifier.size(1.dp).alpha(0f),
        factory = { ctx ->
            val themed = ContextThemeWrapper(ctx, androidx.appcompat.R.style.Theme_AppCompat_DayNight_NoActionBar)
            MediaRouteButton(themed).also {
                runCatching { CastButtonFactory.setUpMediaRouteButton(ctx.applicationContext, it) }
                it.dialogFactory = OpaqueThemeMediaRouteDialogFactory()
                holder[0] = it
            }
        },
    )
}

/**
 * O tema da Activity do OpenTune tem fundo transparente, e os diálogos do Cast
 * (MediaRouterThemeHelper) lançam "background can not be translucent".
 * Aqui os diálogos são criados com um tema AppCompat opaco.
 */
private fun Context.opaque(): Context =
    ContextThemeWrapper(this, androidx.appcompat.R.style.Theme_AppCompat_DayNight_NoActionBar)

class OpaqueThemeMediaRouteDialogFactory : MediaRouteDialogFactory() {
    override fun onCreateChooserDialogFragment(): MediaRouteChooserDialogFragment = OpaqueChooserDialogFragment()

    override fun onCreateControllerDialogFragment(): MediaRouteControllerDialogFragment = OpaqueControllerDialogFragment()
}

class OpaqueChooserDialogFragment : MediaRouteChooserDialogFragment() {
    override fun onCreateChooserDialog(
        context: Context,
        savedInstanceState: Bundle?,
    ): MediaRouteChooserDialog = super.onCreateChooserDialog(context.opaque(), savedInstanceState)
}

class OpaqueControllerDialogFragment : MediaRouteControllerDialogFragment() {
    override fun onCreateControllerDialog(
        context: Context,
        savedInstanceState: Bundle?,
    ): MediaRouteControllerDialog = super.onCreateControllerDialog(context.opaque(), savedInstanceState)
}
