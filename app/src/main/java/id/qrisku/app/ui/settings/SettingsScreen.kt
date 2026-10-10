package id.qrisku.app.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import id.qrisku.app.DeviceState
import id.qrisku.app.R
import id.qrisku.app.data.*
import id.qrisku.app.ui.components.*
import id.qrisku.app.ui.theme.QriskuColors

@Composable
fun SettingsScreen(device: DeviceState, preferences: VoicePreferences, history: HistoryState,
                   speech: SpeechState, onAutomatic: (Boolean) -> Unit, onRate: (Float) -> Unit,
                   onTest: () -> Unit, onStop: () -> Unit, onRetryVoice: () -> Unit,
                   onVoiceSettings: () -> Unit, onVolumeSettings: () -> Unit,
                   onPermission: () -> Unit, onBatterySettings: () -> Unit, onClear: () -> Unit,
                   onRetryHistory: () -> Unit) {
    var showDelete by remember { mutableStateOf(false) }
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item { Text("Pengaturan", style = MaterialTheme.typography.headlineMedium) }
        item { Text("Suara asisten", style = MaterialTheme.typography.titleLarge) }
        item { AutoVoiceToggle(preferences.automatic, onAutomatic) }
        item {
            QriskuCard {
                Text("Kecepatan bicara", style = MaterialTheme.typography.titleMedium)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Lambat" to 0.8f, "Normal" to 1f, "Cepat" to 1.2f).forEach { (label, rate) ->
                        FilterChip(selected = preferences.speechRate == rate, onClick = { onRate(rate) },
                            label = { Text(label, style = MaterialTheme.typography.labelLarge) },
                            modifier = Modifier.heightIn(min = 48.dp))
                    }
                }
                Text("Volume media perangkat: " + device.mediaVolume + "%",
                    style = MaterialTheme.typography.bodyLarge)
                Text("Gunakan tombol volume HP atau pengaturan suara perangkat.",
                    style = MaterialTheme.typography.bodyMedium, color = QriskuColors.SecondaryText)
                SecondaryAction("Pengaturan Volume Perangkat", onVolumeSettings)
            }
        }
        item { VoiceTestCard(speech, device.mediaVolume, onTest, onStop, onRetryVoice, onVoiceSettings) }
        item { Text("Pemantauan", style = MaterialTheme.typography.titleLarge) }
        item {
            QriskuCard {
                Text("Akses notifikasi", style = MaterialTheme.typography.titleMedium)
                Text(if (device.notificationAccess) "Akses sudah diberikan" else "Akses belum diberikan",
                    style = MaterialTheme.typography.bodyLarge)
                Text(if (device.listenerConnected) "Layanan pemantauan terhubung"
                    else "Layanan pemantauan belum terhubung", style = MaterialTheme.typography.bodyMedium)
                SecondaryAction("Periksa Akses Notifikasi", onPermission)
                Text("Jika notifikasi tidak terbaca, periksa akses dan pembatasan baterai aplikasi. " +
                    "Pengaturan dapat berbeda pada setiap HP.", style = MaterialTheme.typography.bodyMedium)
                SecondaryAction("Pengaturan Baterai", onBatterySettings)
            }
        }
        item { Text("Data & privasi", style = MaterialTheme.typography.titleLarge) }
        history.error?.let { item { StorageError(it, onRetryHistory) } }
        item {
            QriskuCard {
                Text("Data disimpan di perangkat", style = MaterialTheme.typography.titleMedium)
                Text("Notifikasi tersimpan: " + history.totalCount, style = MaterialTheme.typography.bodyMedium)
                Text("Hanya nominal, waktu, sumber, dan status suara yang disimpan. " +
                    "Riwayat tetap tersedia sampai Anda menghapusnya.", style = MaterialTheme.typography.bodyLarge)
                Text("Isi mentah notifikasi dan identitas pembayar tidak disimpan. " +
                    "Qrisku menggunakan suara Bahasa Indonesia offline.", style = MaterialTheme.typography.bodyMedium)
                OutlinedButton(onClick = { showDelete = true }, enabled = history.totalCount > 0,
                    modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = QriskuColors.Error)) {
                    AppIcon(R.drawable.ic_delete, tint = QriskuColors.Error)
                    Spacer(Modifier.width(8.dp))
                    Text("Hapus Riwayat")
                }
            }
        }
        item {
            InfoNotice("Tentang Qrisku · Asisten notifikasi pembayaran.\n" +
                "Qrisku tidak memverifikasi dana secara langsung. Periksa aplikasi DANA untuk kepastian pembayaran.")
        }
    }
    if (showDelete) {
        AlertDialog(onDismissRequest = { showDelete = false },
            title = { Text("Hapus semua riwayat?") },
            text = { Text("Semua riwayat notifikasi pada perangkat ini akan dihapus. Pengaturan suara tetap disimpan.") },
            confirmButton = {
                TextButton(onClick = { showDelete = false; onClear() },
                    colors = ButtonDefaults.textButtonColors(contentColor = QriskuColors.Error)) {
                    Text("Hapus Riwayat")
                }
            },
            dismissButton = { TextButton(onClick = { showDelete = false }) { Text("Batal") } })
    }
}
