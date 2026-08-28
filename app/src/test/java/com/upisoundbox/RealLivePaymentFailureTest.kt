package com.upisoundbox

import com.upisoundbox.core.model.Direction
import com.upisoundbox.core.model.Provider
import com.upisoundbox.domain.model.RawNotification
import com.upisoundbox.notification.NotificationNormalizer
import com.upisoundbox.parser.GooglePayParser
import com.upisoundbox.parser.ParseResult
import com.upisoundbox.validation.PaymentValidator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RealLivePaymentFailureTest {

    @Test
    fun testRealGooglePayNotification_PaidYou_Direction() {
        val raw = "AFZAL KASAM MANSURI paid you ₹1.00. Tap to view."
        val normalized = NotificationNormalizer.normalize(
            RawNotification(
                packageName = "com.google.android.apps.nbu.paisa.user",
                notificationKey = "gpay_live_test",
                postedAt = System.currentTimeMillis(),
                title = "AFZAL KASAM MANSURI paid you ₹1.00",
                text = "Tap to view."
            )
        )

        val direction = PaymentValidator.classifyDirection(normalized.lowercaseText)
        assertEquals("Direction must be CREDIT for 'paid you'", Direction.CREDIT, direction)

        val parser = GooglePayParser()
        val result = parser.parse(normalized)
        assertTrue("Parse result must be Success", result is ParseResult.Success)

        val event = (result as ParseResult.Success).event
        assertEquals(Provider.GOOGLE_PAY, event.provider)
        assertEquals(100L, event.amountMinor)
        assertEquals("AFZAL KASAM MANSURI", event.payerName)
    }

    @Test
    fun testRealKotakNotification_Direction() {
        val normalized = NotificationNormalizer.normalize(
            RawNotification(
                packageName = "com.kotak811mobilebankingapp.instantsavingsupiscanandpayrecharge",
                notificationKey = "kotak_live_test",
                postedAt = System.currentTimeMillis(),
                title = "₹1.00 received from AFZAL KASAM MANSURI",
                text = "Amount credited to XX2413. Check out details."
            )
        )

        val direction = PaymentValidator.classifyDirection(normalized.lowercaseText)
        assertEquals(Direction.CREDIT, direction)
    }
}
