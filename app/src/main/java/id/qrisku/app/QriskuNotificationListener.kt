package id.qrisku.app

import android.app.Notification
import android.os.Handler
import android.os.Looper
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import java.text.NumberFormat
import java.util.Locale

class QriskuNotificationListener : NotificationListenerService() {
    private val mainHandler = Handler(Looper.getMainLooper())
    private val dedupe = NotificationDeduplicator()
    private var voiceEngine: VoiceEngine? = null

    override fun onCreate() {
        super.onCreate()
        voiceEngine = VoiceEngine(applicationContext)
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        Log.i("QriskuPoC", "Notification Listener terhubung")
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        // Cek package asli, bukan ikon/nama tampilan yang mudah ditiru.
        if (sbn.packageName != "id.dana") return
        if (sbn.notification.flags and Notification.FLAG_GROUP_SUMMARY != 0) return

        val extras = sbn.notification.extras
        val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString()?.trim()
        val body = (extras.getCharSequence(Notification.EXTRA_TEXT)
            ?: extras.getCharSequence(Notification.EXTRA_BIG_TEXT))?.toString()?.trim()
        val amount = PaymentParser.parse(title, body) ?: return

        // Pembayaran sah yang jumlahnya sama tetap bisa diproses bila event-nya berbeda.
        val fingerprint = "${sbn.key}|${sbn.postTime}|$title|$body"
        if (!dedupe.accept(fingerprint)) return

        val formatted = NumberFormat.getNumberInstance(Locale.forLanguageTag("id-ID"))
            .format(amount)
        val spoken = "Pembayaran masuk sebesar ${IndonesianNumberWords.spell(amount)} rupiah."
        mainHandler.post {
            PocState.lastEvent = "Notifikasi dikenali: Rp$formatted"
            voiceEngine?.speak(spoken)
            Log.i("QriskuPoC", "Notifikasi DANA Bisnis dikenali, TTS diantrikan")
        }
    }

    override fun onDestroy() {
        voiceEngine?.shutdown()
        voiceEngine = null
        super.onDestroy()
    }
}
