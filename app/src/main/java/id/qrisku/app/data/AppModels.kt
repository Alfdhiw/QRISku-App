package id.qrisku.app.data

import java.security.MessageDigest

object PaymentSource {
    const val PACKAGE = "id.dana"
    const val LABEL = "DANA Bisnis"
}

enum class PlaybackStatus(val label: String) {
    QUEUED("Menunggu dibacakan"),
    SPEAKING("Sedang dibacakan"),
    SPOKEN("Sudah dibacakan"),
    MUTED("Suara nonaktif"),
    FAILED("Gagal dibacakan"),
    INTERRUPTED("Pembacaan terhenti");

    val inFlight: Boolean get() = this == QUEUED || this == SPEAKING
}

object PlaybackRules {
    fun canTransition(from: PlaybackStatus, to: PlaybackStatus): Boolean =
        from.inFlight && (to == PlaybackStatus.SPEAKING || !to.inFlight) && from != to

    fun afterRestart(status: PlaybackStatus): PlaybackStatus =
        if (status.inFlight) PlaybackStatus.INTERRUPTED else status
}

data class NotificationRecord(
    val id: String,
    val amount: Long,
    val detectedAt: Long,
    val postedAt: Long,
    val sourcePackage: String = PaymentSource.PACKAGE,
    val playbackStatus: PlaybackStatus
)

data class HistoryState(
    val records: List<NotificationRecord> = emptyList(),
    val totalCount: Int = 0,
    val loading: Boolean = true,
    val error: String? = null
)

data class VoicePreferences(
    val automatic: Boolean = false,
    val speechRate: Float = 1f,
    val onboardingComplete: Boolean = false
)

enum class MonitoringStatus {
    PERMISSION_REQUIRED, NEEDS_ATTENTION, ACTIVE, ACTIVE_MUTED
}

fun monitoringStatus(permissionGranted: Boolean, listenerConnected: Boolean, automatic: Boolean) = when {
    !permissionGranted -> MonitoringStatus.PERMISSION_REQUIRED
    !listenerConnected -> MonitoringStatus.NEEDS_ATTENTION
    !automatic -> MonitoringStatus.ACTIVE_MUTED
    else -> MonitoringStatus.ACTIVE
}

/** Persist a hash of event identity, never raw text or Android notification keys. */
object NotificationIdentity {
    fun create(packageName: String, key: String, postedAt: Long, title: String, body: String): String {
        val identity = listOf(packageName, key, postedAt.toString(), title, body)
            .joinToString("\u0000")
        return MessageDigest.getInstance("SHA-256").digest(identity.toByteArray(Charsets.UTF_8))
            .joinToString("") { "%02x".format(it) }
    }
}
