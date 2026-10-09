package id.qrisku.app

/** Penanda duplikasi per callback. Bukan deduplikasi transaksi finansial yang terverifikasi. */
class NotificationDeduplicator(private val capacity: Int = 256) {
    private val seen = LinkedHashSet<String>()

    @Synchronized
    fun accept(fingerprint: String): Boolean {
        if (!seen.add(fingerprint)) return false
        if (seen.size > capacity) {
            val iterator = seen.iterator()
            if (iterator.hasNext()) {
                iterator.next()
                iterator.remove()
            }
        }
        return true
    }
}
