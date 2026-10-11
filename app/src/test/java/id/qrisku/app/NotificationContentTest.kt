package id.qrisku.app

import org.junit.Assert.*
import org.junit.Test

class NotificationContentTest {
    private val payment = "Rp29.000 diterima DANA Bisnis."

    @Test fun readsExpandedPaymentWhenCollapsedTextIsEmpty() {
        assertEquals(29_000L, PaymentParser.parse("Pembayaran Masuk",
            NotificationContent.body("  ", payment)))
    }

    @Test fun readsFullMessageInsteadOfTruncatedPreview() {
        assertEquals(29_000L, PaymentParser.parse("Pembayaran Masuk",
            NotificationContent.body("Rp29.000 diterima…", "  $payment  ")))
    }

    @Test fun fallsBackToCollapsedTextWhenExpandedTextIsBlank() {
        assertEquals(payment, NotificationContent.body(" $payment ", " \n "))
        assertEquals(payment, NotificationContent.body(payment, null))
    }

    @Test fun neverCombinesMessagesOrAcceptsAnInvalidExpandedPayment() {
        assertNull(PaymentParser.parse("Pembayaran Masuk",
            NotificationContent.body(payment, "Transfer berhasil Rp29.000")))
        assertNull(NotificationContent.body(null, "  "))
    }
}
