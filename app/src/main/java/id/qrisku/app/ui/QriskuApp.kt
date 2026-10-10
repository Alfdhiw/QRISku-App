package id.qrisku.app.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.qrisku.app.AppController
import id.qrisku.app.R
import id.qrisku.app.ui.components.*
import id.qrisku.app.ui.history.*
import id.qrisku.app.ui.home.HomeScreen
import id.qrisku.app.ui.onboarding.OnboardingScreen
import id.qrisku.app.ui.settings.SettingsScreen
import id.qrisku.app.ui.theme.QriskuColors

data class DeviceActions(
    val permission: () -> Unit,
    val voiceSettings: () -> Unit,
    val volumeSettings: () -> Unit,
    val batterySettings: () -> Unit,
    val openDana: () -> Unit
)

@Composable
fun QriskuApp(controller: AppController, actions: DeviceActions) {
    val device by controller.device.collectAsStateWithLifecycle()
    val preferences by controller.preferences.state.collectAsStateWithLifecycle()
    val history by controller.history.state.collectAsStateWithLifecycle()
    val speech by controller.voice.state.collectAsStateWithLifecycle()
    var destination by rememberSaveable { mutableStateOf("Beranda") }
    var selectedRecord by rememberSaveable { mutableStateOf<String?>(null) }
    var showHelp by rememberSaveable { mutableStateOf(false) }
    BackHandler(enabled = preferences.onboardingComplete && destination != "Beranda" && selectedRecord == null) {
        destination = "Beranda"
    }
    Scaffold(
        containerColor = QriskuColors.Background,
        topBar = { QriskuTopBar { showHelp = true } },
        bottomBar = {
            if (preferences.onboardingComplete) {
                Surface(shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                    border = BorderStroke(1.dp, QriskuColors.Border), color = QriskuColors.Surface) {
                    NavigationBar(containerColor = QriskuColors.Surface, tonalElevation = 0.dp) {
                        listOf("Beranda" to R.drawable.ic_home, "Riwayat" to R.drawable.ic_history,
                            "Pengaturan" to R.drawable.ic_settings).forEach { (label, resource) ->
                            val selected = destination == label
                            NavigationBarItem(selected = selected, onClick = { destination = label },
                                icon = { AppIcon(resource, tint = if (selected) QriskuColors.Primary
                                    else QriskuColors.SecondaryText) },
                                label = { Text(label, style = MaterialTheme.typography.labelMedium,
                                    modifier = Modifier.padding(horizontal = 2.dp), textAlign = TextAlign.Center,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal) },
                                colors = NavigationBarItemDefaults.colors(
                                    indicatorColor = QriskuColors.PrimarySoft,
                                    selectedTextColor = QriskuColors.Primary,
                                    unselectedTextColor = QriskuColors.SecondaryText))
                        }
                    }
                }
            }
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding).consumeWindowInsets(padding)) {
            if (!preferences.onboardingComplete) {
                OnboardingScreen(device, speech, actions.permission, controller::testVoice,
                    controller.voice::stopAll, controller.voice::retry, actions.voiceSettings,
                    controller::completeOnboarding)
            } else when (destination) {
                "Riwayat" -> HistoryScreen(history, { selectedRecord = it },
                    controller::reloadHistory, controller::loadMoreHistory)
                "Pengaturan" -> SettingsScreen(device, preferences, history, speech,
                    controller::setAutomatic, controller::setSpeechRate, controller::testVoice,
                    controller.voice::stopAll, controller.voice::retry, actions.voiceSettings,
                    actions.volumeSettings, actions.permission, actions.batterySettings, controller::clearHistory,
                    controller::reloadHistory)
                else -> HomeScreen(device, preferences, history, speech, actions.permission,
                    controller::setAutomatic, controller::testVoice, controller.voice::stopAll,
                    controller.voice::retry, actions.voiceSettings, { destination = "Riwayat" },
                    controller::reloadHistory)
            }
        }
    }
    val selected = history.records.firstOrNull { it.id == selectedRecord }
    if (selected != null) {
        NotificationDetailSheet(selected, speech, { selectedRecord = null }, { controller.replay(selected) }, actions.openDana)
    }
    if (showHelp) {
        AlertDialog(onDismissRequest = { showHelp = false },
            title = { Text("Bantuan Qrisku") },
            text = { Text("Aktifkan akses notifikasi, lalu gunakan Uji Suara. Pastikan volume media terdengar " +
                "dan suara Bahasa Indonesia offline tersedia.\n\n" +
                "Qrisku hanya membacakan notifikasi DANA Bisnis yang sesuai. " +
                "Untuk memastikan pembayaran, periksa aplikasi DANA.") },
            confirmButton = { TextButton(onClick = { showHelp = false }) { Text("Mengerti") } })
    }
}
