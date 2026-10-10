package id.qrisku.app

import org.junit.Assert.*
import org.junit.Test

class PaymentParserTest {
    @Test fun acceptsObservedDanaBusinessFormat() {
        assertEquals(29_000L, PaymentParser.parse("Pembayaran Masuk", "Rp29.000 diterima DANA Bisnis."))
        assertEquals(1_250_000L, PaymentParser.parse("Pembayaran Masuk", "Rp1.250.000 diterima DANA Bisnis."))
    }

    @Test fun rejectsTransfersMalformedAmountsAndUnrelatedText() {
        assertNull(PaymentParser.parse("Transfer Berhasil", "Rp29.000 diterima DANA Bisnis."))
        assertNull(PaymentParser.parse("Pembayaran Masuk", "Rp29.00 diterima DANA Bisnis."))
        assertNull(PaymentParser.parse("Pembayaran Masuk", "Rp0 diterima DANA Bisnis."))
        assertNull(PaymentParser.parse("Pembayaran Masuk", "Rp29.000 berhasil dikirim."))
        assertNull(PaymentParser.parse(null, null))
    }

    @Test fun speaksPaymentAmountsInIndonesian() {
        assertEquals("dua puluh sembilan ribu", IndonesianNumberWords.spell(29_000))
        assertEquals("seratus dua puluh lima ribu", IndonesianNumberWords.spell(125_000))
        assertEquals("satu juta", IndonesianNumberWords.spell(1_000_000))
    }
}
