package id.qrisku.app

/** BigTextStyle contains the full message; the collapsed text may be empty or truncated. */
object NotificationContent {
    fun body(collapsed: CharSequence?, expanded: CharSequence?): String? =
        expanded?.toString()?.trim()?.takeIf { it.isNotEmpty() }
            ?: collapsed?.toString()?.trim()?.takeIf { it.isNotEmpty() }
}
