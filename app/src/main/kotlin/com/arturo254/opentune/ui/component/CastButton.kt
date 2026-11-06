package com.arturo254.opentune.ui.component

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioDeviceCallback
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.mediarouter.media.MediaRouteSelector
import androidx.mediarouter.media.MediaRouter
import com.arturo254.opentune.LocalPlayerConnection
import com.arturo254.opentune.R
import com.google.android.gms.cast.CastMediaControlIntent
import kotlinx.coroutines.delay

/**
 * Botão "Conectar a um dispositivo" (ícone de celular + caixa de som).
 * Abre a folha [ConnectDevicesSheet], no estilo do Spotify Connect.
 * Mantém a mesma assinatura do antigo botão de Cast para funcionar em todos os designs.
 */
@Composable
fun CastIconButton(
    isCasting: Boolean,
    tint: Color,
    modifier: Modifier = Modifier,
    iconSize: Dp = 24.dp,
) {
    var showSheet by remember { mutableStateOf(false) }
    Box(
        modifier =
            modifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) { showSheet = true },
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(R.drawable.connect_devices),
            contentDescription = "Conectar a um dispositivo",
            tint = tint,
            modifier = Modifier.size(iconSize),
        )
    }
    if (showSheet) ConnectDevicesSheet(onDismiss = { showSheet = false })
}

private fun externalOutputTypes(): Set<Int> =
    buildSet {
        add(AudioDeviceInfo.TYPE_BLUETOOTH_A2DP)
        add(AudioDeviceInfo.TYPE_WIRED_HEADSET)
        add(AudioDeviceInfo.TYPE_WIRED_HEADPHONES)
        add(22) // TYPE_USB_HEADSET
        add(AudioDeviceInfo.TYPE_USB_DEVICE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) add(23) // TYPE_HEARING_AID
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            add(26) // TYPE_BLE_HEADSET
            add(27) // TYPE_BLE_SPEAKER
        }
    }

private fun isBluetooth(type: Int) = type == AudioDeviceInfo.TYPE_BLUETOOTH_A2DP || type == 26 || type == 27

private class DeviceEntry(
    val key: String,
    val title: String,
    val subtitle: String,
    @DrawableRes val icon: Int,
    @DrawableRes val subtitleIcon: Int? = null,
    val onClick: () -> Unit,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConnectDevicesSheet(onDismiss: () -> Unit) {
    val context = LocalContext.current
    val playerConnection = LocalPlayerConnection.current ?: return
    val service = playerConnection.service
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val metadata by playerConnection.mediaMetadata.collectAsState()
    val castActive by playerConnection.castActive.collectAsState()
    val preferredId by service.preferredOutputId.collectAsState()

    // ---- saídas de áudio locais (Bluetooth, fone com fio, USB) ----
    val audioManager = remember { context.getSystemService(Context.AUDIO_SERVICE) as AudioManager }
    var outputs by remember { mutableStateOf(emptyList<AudioDeviceInfo>()) }
    val readOutputs = {
        outputs = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS).toList()
    }
    val btPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { readOutputs() }
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED
        ) {
            btPermissionLauncher.launch(Manifest.permission.BLUETOOTH_CONNECT)
        }
    }
    DisposableEffect(audioManager) {
        readOutputs()
        val callback =
            object : AudioDeviceCallback() {
                override fun onAudioDevicesAdded(addedDevices: Array<out AudioDeviceInfo>?) = readOutputs()

                override fun onAudioDevicesRemoved(removedDevices: Array<out AudioDeviceInfo>?) = readOutputs()
            }
        audioManager.registerAudioDeviceCallback(callback, null)
        onDispose { audioManager.unregisterAudioDeviceCallback(callback) }
    }

    // ---- dispositivos Cast (Chromecast, Smart TVs com Chromecast, caixas Google) ----
    val router = remember { MediaRouter.getInstance(context.applicationContext) }
    val selector =
        remember {
            MediaRouteSelector.Builder()
                .addControlCategory(CastMediaControlIntent.categoryForCast(CastMediaControlIntent.DEFAULT_MEDIA_RECEIVER_APPLICATION_ID))
                .build()
        }
    var castRoutes by remember { mutableStateOf(emptyList<MediaRouter.RouteInfo>()) }
    var selectedCastName by remember { mutableStateOf<String?>(null) }
    DisposableEffect(router) {
        val callback =
            object : MediaRouter.Callback() {
                fun refresh() {
                    castRoutes = router.routes.filter { !it.isDefaultOrBluetooth && it.isEnabled && it.matchesSelector(selector) }
                    selectedCastName = router.selectedRoute.takeIf { !it.isDefaultOrBluetooth }?.name
                }

                override fun onRouteAdded(router: MediaRouter, route: MediaRouter.RouteInfo) = refresh()

                override fun onRouteRemoved(router: MediaRouter, route: MediaRouter.RouteInfo) = refresh()

                override fun onRouteChanged(router: MediaRouter, route: MediaRouter.RouteInfo) = refresh()

                override fun onRouteSelected(router: MediaRouter, route: MediaRouter.RouteInfo, reason: Int) = refresh()

                override fun onRouteUnselected(router: MediaRouter, route: MediaRouter.RouteInfo, reason: Int) = refresh()
            }
        router.addCallback(selector, callback, MediaRouter.CALLBACK_FLAG_REQUEST_DISCOVERY)
        callback.refresh()
        onDispose { router.removeCallback(callback) }
    }
    var scanning by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        delay(8000)
        scanning = false
    }

    // ---- ações ----
    val builtIn = outputs.firstOrNull { it.type == AudioDeviceInfo.TYPE_BUILTIN_SPEAKER }
    val externals = outputs.filter { it.isSink && it.type in externalOutputTypes() }

    fun stopCastIfNeeded() {
        if (castActive || selectedCastName != null) router.unselect(MediaRouter.UNSELECT_REASON_STOPPED)
    }

    fun nameOf(device: AudioDeviceInfo): String =
        device.productName?.toString()?.takeIf { it.isNotBlank() && !it.equals(Build.MODEL, ignoreCase = true) }
            ?: when (device.type) {
                AudioDeviceInfo.TYPE_WIRED_HEADSET, AudioDeviceInfo.TYPE_WIRED_HEADPHONES -> "Fones com fio"
                22, AudioDeviceInfo.TYPE_USB_DEVICE -> "Áudio USB"
                else -> "Dispositivo de áudio"
            }

    val phone =
        DeviceEntry(
            key = "phone",
            title = "Este telefone",
            subtitle = "Alto-falante do aparelho",
            icon = R.drawable.connect_phone,
            onClick = {
                stopCastIfNeeded()
                service.setPreferredOutput(builtIn)
                onDismiss()
            },
        )
    val externalEntries =
        externals.map { device ->
            DeviceEntry(
                key = "out-${device.id}",
                title = nameOf(device),
                subtitle =
                    when {
                        isBluetooth(device.type) -> "Bluetooth"
                        device.type == 22 || device.type == AudioDeviceInfo.TYPE_USB_DEVICE -> "USB"
                        else -> "Com fio"
                    },
                icon = if (device.type == 27 || device.type == AudioDeviceInfo.TYPE_USB_DEVICE) R.drawable.connect_speaker else R.drawable.connect_headphones,
                subtitleIcon = if (isBluetooth(device.type)) R.drawable.connect_bluetooth else null,
                onClick = {
                    stopCastIfNeeded()
                    service.setPreferredOutput(device)
                    onDismiss()
                },
            )
        }
    val castEntries =
        castRoutes.map { route ->
            DeviceEntry(
                key = "cast-${route.id}",
                title = route.name,
                subtitle = if (route.description.isNullOrBlank()) "Google Cast" else "Google Cast · ${route.description}",
                icon =
                    when (route.deviceType) {
                        MediaRouter.RouteInfo.DEVICE_TYPE_TV -> R.drawable.connect_tv
                        MediaRouter.RouteInfo.DEVICE_TYPE_SPEAKER -> R.drawable.connect_speaker
                        else -> R.drawable.cast
                    },
                onClick = {
                    router.selectRoute(route)
                    onDismiss()
                },
            )
        }

    // ---- quem está ativo agora ----
    val activeKey: String =
        when {
            castActive && selectedCastName != null -> castEntries.firstOrNull { it.title == selectedCastName }?.key ?: "cast-active"
            preferredId != null ->
                externalEntries.firstOrNull { it.key == "out-$preferredId" }?.key ?: "phone"
            externalEntries.isNotEmpty() -> externalEntries.first().key
            else -> "phone"
        }
    val allEntries = listOf(phone) + externalEntries + castEntries
    val activeEntry =
        allEntries.firstOrNull { it.key == activeKey }
            ?: DeviceEntry("cast-active", selectedCastName ?: "Cast", "Google Cast", R.drawable.cast, onClick = {})
    val others = allEntries.filter { it.key != activeEntry.key }

    val nowPlaying =
        listOfNotNull(
            metadata?.title,
            metadata?.artists?.joinToString { it.name }?.takeIf { it.isNotBlank() },
        ).joinToString(" – ")

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .navigationBarsPadding(),
        ) {
            Text(
                text = "Conectar",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
            )

            // Dispositivo ativo
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surfaceContainerHighest,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp),
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = activeEntry.title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = nowPlaying.ifBlank { activeEntry.subtitle },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Icon(
                        painter = painterResource(activeEntry.icon),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(36.dp),
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            LazyColumn(modifier = Modifier.weight(1f, fill = false)) {
                items(others, key = { it.key }) { entry -> DeviceRow(entry) }
                item {
                    if (castEntries.isEmpty()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 14.dp),
                        ) {
                            if (scanning) {
                                CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
                                Spacer(Modifier.width(16.dp))
                            }
                            Text(
                                text =
                                    if (scanning) "Procurando Chromecast e TVs na sua rede…"
                                    else "Nenhum Chromecast ou TV encontrado. Conecte o celular ao mesmo Wi‑Fi.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // Encontrar dispositivos Bluetooth
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceContainerHighest,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .clickable {
                            runCatching {
                                context.startActivity(Intent(Settings.ACTION_BLUETOOTH_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                            }
                            onDismiss()
                        },
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                ) {
                    Icon(
                        painter = painterResource(R.drawable.connect_bluetooth),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "Encontre dispositivos Bluetooth",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun DeviceRow(entry: DeviceEntry) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable { entry.onClick() }
                .padding(horizontal = 8.dp, vertical = 14.dp),
    ) {
        Icon(
            painter = painterResource(entry.icon),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(28.dp),
        )
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = entry.title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (entry.subtitleIcon != null) {
                    Icon(
                        painter = painterResource(entry.subtitleIcon),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp),
                    )
                    Spacer(Modifier.width(4.dp))
                }
                Text(
                    text = entry.subtitle,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}
