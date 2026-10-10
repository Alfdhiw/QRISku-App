package id.qrisku.app

import id.qrisku.app.data.NotificationIdentity

fun main() {
    val accepted = listOf(
        Triple("Pembayaran Masuk", "Rp29.000 diterima DANA Bisnis.", 29000L),
        Triple("Pembayaran Masuk", "Rp100 diterima DANA Bisnis.", 100L),
        Triple("Pembayaran Masuk", "Rp1.250.000 diterima DANA Bisnis.", 1250000L),
        Triple("Pembayaran Masuk", "Rp20000 diterima DANA Bisnis", 20000L)
    )
    accepted.forEach { (title, text, expected) ->
        check(PaymentParser.parse(title, text) == expected) { "Gagal: $text" }
    }
    val rejected = listOf(
        "Transfer Berhasil" to "Rp29.000 diterima DANA Bisnis.",
        "Pembayaran Masuk" to "Rp29.000 berhasil dikirim.",
        "Pembayaran Masuk" to "Rp29.00 diterima DANA Bisnis.",
        "Pembayaran Masuk" to "Rp0 diterima DANA Bisnis."
    )
    rejected.forEach { (title, text) ->
        check(PaymentParser.parse(title, text) == null) { "Harus ditolak: $text" }
    }
    check(IndonesianNumberWords.spell(29_000L) == "dua puluh sembilan ribu")
    check(IndonesianNumberWords.spell(125_000L) == "seratus dua puluh lima ribu")
    check(IndonesianNumberWords.spell(1_000_000L) == "satu juta")
    check(IndonesianNumberWords.spell(9_999_999_999L) == "sembilan miliar sembilan ratus sembilan puluh sembilan juta sembilan ratus sembilan puluh sembilan ribu sembilan ratus sembilan puluh sembilan")
    val first = NotificationIdentity.create("id.dana", "key", 123, "Pembayaran Masuk", "Rp29.000 diterima DANA Bisnis.")
    check(first == NotificationIdentity.create("id.dana", "key", 123, "Pembayaran Masuk", "Rp29.000 diterima DANA Bisnis."))
    check(first != NotificationIdentity.create("id.dana", "key", 124, "Pembayaran Masuk", "Rp29.000 diterima DANA Bisnis."))
    check(first.length == 64)
    println("LULUS: 4 format diterima, 4 ditolak, 4 pelafalan, 3 identitas notifikasi.")
}
