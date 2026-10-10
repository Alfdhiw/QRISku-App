package id.qrisku.app.ui.onboarding

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import id.qrisku.app.DeviceState
import id.qrisku.app.R
import id.qrisku.app.data.*
import id.qrisku.app.ui.components.*
import id.qrisku.app.ui.theme.QriskuColors

@Composable
fun OnboardingScreen(device: DeviceState, speech: SpeechState, onPermission: () -> Unit,
                     onTest: () -> Unit, onStop: () -> Unit, onRetryVoice: () -> Unit,
                     onVoiceSettings: () -> Unit, onFinish: () -> Unit) {
    var step by rememberSaveable { mutableIntStateOf(0) }
    BackHandler(enabled = step > 0) { step-- }
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)) {
        item {
            Text("PENGATURAN AWAL · " + (step + 1) + " / 3", style = MaterialTheme.typography.labelMedium,
                color = QriskuColors.PrimaryDark)
        }
        when (step) {
            0 -> {
                item {
                    QriskuCard(color = QriskuColors.PrimarySoft) {
                        Image(painterResource(R.drawable.onboarding_illustration),
                            "Ilustrasi notifikasi pembayaran pada ponsel dengan gelombang suara",
                            Modifier.fillMaxWidth().height(180.dp))
                        Text("Contoh suara", style = MaterialTheme.typography.bodyMedium)
                        Text("Pembayaran masuk sebesar dua puluh sembilan ribu rupiah.",
                            style = MaterialTheme.typography.bodyMedium)
                    }
                }
                item {
                    Text("Pembayaran masuk,\nlangsung terdengar.", style = MaterialTheme.typography.headlineMedium)
                    Spacer(Modifier.height(12.dp))
                    Text("Qrisku membantu membacakan notifikasi pembayaran dari DANA Bisnis.",
                        style = MaterialTheme.typography.bodyLarge, color = QriskuColors.SecondaryText)
                }
                item { PrimaryAction("Mulai Pengaturan", { step = 1 }) }
                item { Text("Tanpa akun · Diproses di perangkat", style = MaterialTheme.typography.bodyMedium) }
            }
            1 -> {
                item {
                    Text("Izinkan Qrisku membaca notifikasi.", style = MaterialTheme.typography.headlineMedium)
                    Spacer(Modifier.height(12.dp))
                    Text("Qrisku hanya memproses notifikasi DANA Bisnis yang sesuai pola pembayaran masuk.",
                        style = MaterialTheme.typography.bodyLarge)
                }
                item { InfoNotice("Akses Android secara teknis mencakup notifikasi aplikasi lain. " +
                    "Qrisku mengabaikan notifikasi lainnya dan memproses informasi secara lokal.") }
                item {
                    if (device.notificationAccess) {
                        InfoNotice("Akses notifikasi sudah diberikan.")
                        Spacer(Modifier.height(16.dp))
                        PrimaryAction("Lanjut ke Uji Suara", { step = 2 })
                    } else PrimaryAction("Buka Pengaturan Akses", onPermission)
                    Spacer(Modifier.height(12.dp))
                    SecondaryAction("Nanti Saja", onFinish)
                }
                item { Text("Setelah mengaktifkan akses di pengaturan Android, kembali ke Qrisku.",
                    style = MaterialTheme.typography.bodyMedium, color = QriskuColors.SecondaryText) }
            }
            2 -> {
                item {
                    Text("Coba suara Qrisku.", style = MaterialTheme.typography.headlineMedium)
                    Spacer(Modifier.height(12.dp))
                    Text("Pastikan volume media terdengar.", style = MaterialTheme.typography.bodyLarge)
                }
                item { VoiceTestCard(speech, device.mediaVolume, onTest, onStop, onRetryVoice, onVoiceSettings) }
                if (!device.notificationAccess) item { InfoNotice("Akses notifikasi belum aktif. " +
                    "Anda tetap dapat masuk Beranda dan menyelesaikan pengaturan nanti.", warning = true) }
                item {
                    Text("Bacakan Otomatis diaktifkan setelah akses diberikan dan contoh suara selesai dibacakan.",
                        style = MaterialTheme.typography.bodyMedium, color = QriskuColors.SecondaryText)
                    Spacer(Modifier.height(16.dp))
                    PrimaryAction("Selesai", onFinish)
                }
            }
        }
    }
}
