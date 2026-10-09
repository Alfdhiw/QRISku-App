package id.qrisku.app

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    private var listenerEnabled by mutableStateOf(false)
    private var previewVoice: VoiceEngine? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        previewVoice = VoiceEngine(applicationContext)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("Qrisku App — PoC", style = MaterialTheme.typography.headlineMedium)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(if (listenerEnabled) "Akses notifikasi: AKTIF" else "Akses notifikasi: BELUM AKTIF")
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("Buka Pengaturan Akses Notifikasi") }
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                previewVoice?.speak(
                                    "Contoh suara Qrisku. Pembayaran masuk sebesar dua puluh sembilan ribu rupiah."
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("Tes Suara (Simulasi)") }
                        Spacer(modifier = Modifier.height(24.dp))
                        Text("Peristiwa terakhir:", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(PocState.lastEvent)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "Data berasal dari notifikasi Android, bukan verifikasi langsung ke DANA.",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val enabled = Settings.Secure.getString(
            contentResolver, "enabled_notification_listeners"
        ) ?: ""
        val myComponent = ComponentName(this, QriskuNotificationListener::class.java)
        listenerEnabled = enabled.split(':').any {
            ComponentName.unflattenFromString(it) == myComponent
        }
    }

    override fun onDestroy() {
        previewVoice?.shutdown()
        previewVoice = null
        super.onDestroy()
    }
}
