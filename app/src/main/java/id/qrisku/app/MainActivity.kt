package id.qrisku.app

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.media.AudioManager
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import id.qrisku.app.data.PaymentSource
import id.qrisku.app.ui.DeviceActions
import id.qrisku.app.ui.QriskuApp
import id.qrisku.app.ui.theme.QriskuTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val controller get() = (application as QriskuApplication).controller

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.rgb(15, 23, 42)))
        volumeControlStream = AudioManager.STREAM_MUSIC
        val actions = DeviceActions(
            permission = { openSettings(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)) },
            voiceSettings = { openSettings(Intent("com.android.settings.TTS_SETTINGS")) },
            volumeSettings = { openSettings(Intent(Settings.ACTION_SOUND_SETTINGS)) },
            batterySettings = { openSettings(Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)) },
            openDana = {
                val intent = packageManager.getLaunchIntentForPackage(PaymentSource.PACKAGE)
                if (intent == null) Toast.makeText(this, "DANA belum terpasang di perangkat ini.", Toast.LENGTH_LONG).show()
                else openSettings(intent)
            }
        )
        setContent { QriskuTheme { QriskuApp(controller, actions) } }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                while (isActive) {
                    // Refresh hardware volume and permission only while the screen is visible.
                    controller.refreshDevice()
                    delay(2_000)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        controller.refreshDevice()
    }

    private fun openSettings(intent: Intent) {
        try {
            startActivity(intent)
        } catch (_: ActivityNotFoundException) {
            Toast.makeText(this, "Pengaturan ini tidak tersedia pada perangkat. Buka Pengaturan Android.", Toast.LENGTH_LONG).show()
        } catch (_: SecurityException) {
            Toast.makeText(this, "Pengaturan belum dapat dibuka. Buka Pengaturan Android secara manual.", Toast.LENGTH_LONG).show()
        }
    }
}
