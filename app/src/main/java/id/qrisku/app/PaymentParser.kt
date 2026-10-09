package id.qrisku.app

/** Parser sengaja ketat: terima hanya format notifikasi DANA Bisnis yang sudah diamati. */
object PaymentParser {
    private val bodyPattern = Regex(
        pattern = "^Rp\\s*([0-9]+(?:\\.[0-9]{3})*)\\s+diterima DANA Bisnis\\.?$",
        option = RegexOption.IGNORE_CASE
    )

    fun parse(title: String?, body: String?): Long? {
        if (title?.trim() != "Pembayaran Masuk") return null
        val text = body?.trim() ?: return null
        val match = bodyPattern.matchEntire(text) ?: return null
        val amount = match.groupValues[1].replace(".", "").toLongOrNull() ?: return null
        return amount.takeIf { it in 1L..999_999_999_999L }
    }
}
