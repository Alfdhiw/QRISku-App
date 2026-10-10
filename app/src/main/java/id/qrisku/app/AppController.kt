package id.qrisku.app

import android.content.ComponentName
import android.content.Context
import android.media.AudioManager
import android.provider.Settings
import id.qrisku.app.data.NotificationIdentity
import id.qrisku.app.data.NotificationRecord
import id.qrisku.app.data.NotificationRepository
import id.qrisku.app.data.PaymentSource
import id.qrisku.app.data.PlaybackStatus
import id.qrisku.app.data.PreferencesRepository
import id.qrisku.app.data.SpeechKind
import id.qrisku.app.data.SpeechRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.cancel
import java.util.UUID

data class DeviceState(
    val notificationAccess: Boolean = false,
    val listenerConnected: Boolean = false,
    val mediaVolume: Int = 0
)

class AppController(private val context: Context,
                    databaseName: String = "qrisku_notifications.db",
                    preferencesName: String = "qrisku_voice",
                    private val notificationAccessReader: () -> Boolean = {
                        val component = ComponentName(context, QriskuNotificationListener::class.java)
                        Settings.Secure.getString(context.contentResolver, "enabled_notification_listeners")
                            .orEmpty().split(':').any { ComponentName.unflattenFromString(it) == component }
                    }) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    val preferences = PreferencesRepository(context, preferencesName)
    val history = NotificationRepository(context, scope, databaseName)
    private val mutableDevice = MutableStateFlow(DeviceState())
    private var observedListenerConnection = false
    val device = mutableDevice.asStateFlow()
    val voice = VoiceEngine(context) { request, status ->
        if (request.kind == SpeechKind.AUTOMATIC && request.recordId != null) {
            scope.launch { history.updatePlayback(request.recordId, status) }
        }
    }

    init {
        voice.setSpeechRate(preferences.state.value.speechRate)
        refreshDevice()
    }

    fun refreshDevice() {
        val granted = notificationAccessReader()
        val audio = context.getSystemService(AudioManager::class.java)
        val maximum = audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC).coerceAtLeast(1)
        val volume = audio.getStreamVolume(AudioManager.STREAM_MUSIC) * 100 / maximum
        mutableDevice.value = mutableDevice.value.copy(notificationAccess = granted,
            listenerConnected = granted && observedListenerConnection, mediaVolume = volume)
        if (!granted) voice.stopAutomatic()
    }

    fun listenerConnected(connected: Boolean) {
        // Android may deliver this callback before its permission setting becomes visible.
        // Keep the observed connection independently so the next refresh can reconcile both.
        observedListenerConnection = connected
        refreshDevice()
        if (!connected) voice.stopAutomatic()
    }

    fun notificationReceived(packageName: String, key: String, postedAt: Long, title: String?, body: String?) {
        if (packageName != PaymentSource.PACKAGE) return
        val amount = PaymentParser.parse(title, body) ?: return
        val id = NotificationIdentity.create(packageName, key, postedAt, title!!.trim(), body!!.trim())
        val detectedAt = System.currentTimeMillis()
        scope.launch {
            val status = if (preferences.state.value.automatic) PlaybackStatus.QUEUED else PlaybackStatus.MUTED
            val record = NotificationRecord(id, amount, detectedAt, postedAt, playbackStatus = status)
            if (history.record(record) != true || status == PlaybackStatus.MUTED) return@launch
            // Preferences or permission may change while the SQLite insert is in progress.
            if (!preferences.state.value.automatic || !device.value.notificationAccess || !device.value.listenerConnected) {
                history.updatePlayback(id, PlaybackStatus.INTERRUPTED)
                return@launch
            }
            voice.speak(SpeechRequest(UUID.randomUUID().toString(), paymentSpeech(amount), SpeechKind.AUTOMATIC, id))
        }
    }

    fun setAutomatic(enabled: Boolean) {
        preferences.setAutomatic(enabled)
        if (!enabled) voice.stopAutomatic()
    }

    fun setSpeechRate(rate: Float) {
        preferences.setSpeechRate(rate)
        voice.setSpeechRate(preferences.state.value.speechRate)
    }

    fun completeOnboarding() {
        preferences.completeOnboarding(device.value.notificationAccess &&
            voice.state.value.testStatus == PlaybackStatus.SPOKEN)
    }

    fun testVoice() {
        refreshDevice()
        if (!voice.state.value.busy) {
            voice.speak(SpeechRequest(UUID.randomUUID().toString(),
                "Contoh suara Qrisku. " + paymentSpeech(29_000), SpeechKind.TEST))
        }
    }

    fun replay(record: NotificationRecord) {
        refreshDevice()
        if (!voice.state.value.busy) {
            // Replay is an explicit user action, independent of automatic mode and original delivery status.
            voice.speak(SpeechRequest(UUID.randomUUID().toString(), paymentSpeech(record.amount), SpeechKind.REPLAY, record.id))
        }
    }

    fun clearHistory() {
        voice.stopAll()
        scope.launch { history.clearHistory() }
    }

    fun reloadHistory() { scope.launch { history.reload() } }
    fun loadMoreHistory() { scope.launch { history.loadMore() } }

    suspend fun close() {
        voice.shutdown()
        scope.cancel()
        history.close()
    }

    private fun paymentSpeech(amount: Long) =
        "Pembayaran masuk sebesar ${IndonesianNumberWords.spell(amount)} rupiah."
}
