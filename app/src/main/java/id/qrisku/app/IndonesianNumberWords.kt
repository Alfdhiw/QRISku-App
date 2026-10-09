package id.qrisku.app

/** Konversi angka ke kata agar pelafalan nominal tidak bergantung pada cara TTS membaca digit. */
object IndonesianNumberWords {
    private val oneToEleven = listOf(
        "nol", "satu", "dua", "tiga", "empat", "lima", "enam",
        "tujuh", "delapan", "sembilan", "sepuluh", "sebelas"
    )

    fun spell(number: Long): String {
        require(number in 0L..999_999_999_999L)
        return when {
            number < 12L -> oneToEleven[number.toInt()]
            number < 20L -> "${spell(number - 10L)} belas"
            number < 100L -> parts(spell(number / 10L), "puluh", spellRemainder(number % 10L))
            number < 200L -> parts("seratus", spellRemainder(number - 100L))
            number < 1000L -> parts(spell(number / 100L), "ratus", spellRemainder(number % 100L))
            number < 2000L -> parts("seribu", spellRemainder(number - 1000L))
            number < 1_000_000L -> parts(spell(number / 1000L), "ribu", spellRemainder(number % 1000L))
            number < 1_000_000_000L -> parts(spell(number / 1_000_000L), "juta", spellRemainder(number % 1_000_000L))
            else -> parts(spell(number / 1_000_000_000L), "miliar", spellRemainder(number % 1_000_000_000L))
        }
    }

    private fun spellRemainder(value: Long): String = if (value == 0L) "" else spell(value)
    private fun parts(vararg items: String) = items.filter(String::isNotBlank).joinToString(" ")
}
