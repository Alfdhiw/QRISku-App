package id.qrisku.app.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import id.qrisku.app.DeviceState
import id.qrisku.app.R
import id.qrisku.app.data.*
import id.qrisku.app.ui.components.*
import id.qrisku.app.ui.theme.QriskuColors

@Composable
fun HomeScreen(device: DeviceState, preferences: VoicePreferences, history: HistoryState,
               speech: SpeechState, onPermission: () -> Unit, onAutomatic: (Boolean) -> Unit,
               onTest: () -> Unit, onStop: () -> Unit, onRetryVoice: () -> Unit,
               onVoiceSettings: () -> Unit, onHistory: () -> Unit, onRetryHistory: () -> Unit) {
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            MonitoringStatusCard(monitoringStatus(device.notificationAccess, device.listenerConnected,
                preferences.automatic), onPermission)
        }
        item { AutoVoiceToggle(preferences.automatic, onAutomatic) }
        item { VoiceTestCard(speech, device.mediaVolume, onTest, onStop, onRetryVoice, onVoiceSettings) }
        history.error?.let { item { StorageError(it, onRetryHistory) } }
        item {
            Text("Notifikasi terakhir", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(12.dp))
            val latest = history.records.firstOrNull()
            when {
                history.loading -> CircularProgressIndicator()
                latest == null -> EmptyState("Belum ada notifikasi",
                    "Belum ada notifikasi pembayaran yang terdeteksi.")
                else -> QriskuCard {
                    Text(PaymentSource.LABEL, style = MaterialTheme.typography.labelLarge,
                        color = QriskuColors.PrimaryDark)
                    Text(amountLabel(latest.amount), style = MaterialTheme.typography.headlineLarge)
                    Text(timeLabel(latest.detectedAt), style = MaterialTheme.typography.bodyMedium,
                        color = QriskuColors.SecondaryText)
                    PlaybackBadge(latest.playbackStatus)
                    Text("Notifikasi DANA Bisnis terdeteksi.", style = MaterialTheme.typography.bodyMedium)
                    SecondaryAction("Lihat Riwayat Notifikasi", onHistory)
                }
            }
        }
        item { InfoNotice("Qrisku membacakan notifikasi yang muncul di perangkat. " +
            "Untuk memastikan status pembayaran, periksa aplikasi DANA.") }
    }
}

@Composable
fun MonitoringStatusCard(status: MonitoringStatus, onPermission: () -> Unit) {
    val ready = status == MonitoringStatus.ACTIVE || status == MonitoringStatus.ACTIVE_MUTED
    QriskuCard(color = if (ready) QriskuColors.PrimarySoft else QriskuColors.WarningSoft) {
        AppIcon(if (ready) R.drawable.ic_check else R.drawable.ic_warning,
            tint = if (ready) QriskuColors.Success else QriskuColors.Warning)
        Text(when (status) {
            MonitoringStatus.PERMISSION_REQUIRED -> "Akses notifikasi belum aktif"
            MonitoringStatus.NEEDS_ATTENTION -> "Pemantauan perlu diperiksa"
            MonitoringStatus.ACTIVE -> "Pemantauan aktif"
            MonitoringStatus.ACTIVE_MUTED -> "Pemantauan aktif · Suara nonaktif"
        }, style = MaterialTheme.typography.titleLarge)
        Text(when (status) {
            MonitoringStatus.PERMISSION_REQUIRED -> "Berikan akses untuk mendeteksi notifikasi DANA Bisnis."
            MonitoringStatus.NEEDS_ATTENTION -> "Akses sudah diberikan, tetapi layanan belum terhubung. Periksa pengaturan akses dan pembatasan baterai."
            else -> "Siap mendeteksi notifikasi DANA Bisnis."
        }, style = MaterialTheme.typography.bodyLarge)
        if (!ready) PrimaryAction(if (status == MonitoringStatus.PERMISSION_REQUIRED)
            "Aktifkan Akses" else "Periksa Pengaturan", onPermission)
    }
}
