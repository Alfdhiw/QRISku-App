package id.qrisku.app.data

/** Foreground polling may request a bind, but must not flood Android or bypass consent. */
class ListenerReconnectPolicy(private val intervalMs: Long = 30_000) {
    private var lastAttempt: Long? = null

    fun shouldRequest(permission: Boolean, connected: Boolean, now: Long): Boolean {
        if (!permission || connected) {
            lastAttempt = null
            return false
        }
        val previous = lastAttempt
        if (previous != null && now >= previous && now - previous < intervalMs) return false
        lastAttempt = now
        return true
    }
}
