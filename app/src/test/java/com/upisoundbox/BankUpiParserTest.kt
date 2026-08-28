package com.upisoundbox

import com.upisoundbox.core.model.Direction
import com.upisoundbox.domain.model.RawNotification
import com.upisoundbox.notification.NotificationNormalizer
import com.upisoundbox.parser.BankUpiParser
import com.upisoundbox.parser.ParseResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BankUpiParserTest {

    private val parser = BankUpiParser()

    @Test
    fun testKotakBankSmsMessage() {
        val raw = RawNotification(
            packageName = "com.google.android.apps.messaging",
            notificationKey = "sms_kotak_1",
            postedAt = System.currentTimeMillis(),
            title = "JD-KOTAKB-S",
            text = "Received Rs.1.00 in your Kotak Bank AC 2413 from AFZAL KASAM MANSURI on 28-08-26.UPI Ref:128644995392"
        )
        val normalized = NotificationNormalizer.normalize(raw)
        val result = parser.parse(normalized)

        assertTrue(result is ParseResult.Success)
        val event = (result as ParseResult.Success).event
        assertEquals(100L, event.amountMinor)
        assertEquals(Direction.CREDIT, event.direction)
        assertEquals("AFZAL KASAM MANSURI", event.payerName)
        assertEquals("128644995392", event.transactionReference)
    }

    @Test
    fun testKotakMobileAppNotification() {
        val raw = RawNotification(
            packageName = "com.kotak811mobilebankingapp.instantsavingsupiscanandpayrecharge",
            notificationKey = "kotak_app_1",
            postedAt = System.currentTimeMillis(),
            title = "₹1.00 received from AFZAL KASAM MANSURI",
            text = "Amount credited to XX2413. Check out details."
        )
        val normalized = NotificationNormalizer.normalize(raw)
        val result = parser.parse(normalized)

        assertTrue(result is ParseResult.Success)
        val event = (result as ParseResult.Success).event
        assertEquals(100L, event.amountMinor)
        assertEquals(Direction.CREDIT, event.direction)
        assertEquals("AFZAL KASAM MANSURI", event.payerName)
    }

    @Test
    fun testBankOtpRejection() {
        val raw = RawNotification(
            packageName = "com.google.android.apps.messaging",
            notificationKey = "sms_otp_1",
            postedAt = System.currentTimeMillis(),
            title = "VK-KOTAKB",
            text = "Your OTP for transaction of Rs.500 is 492019. Do not share with anyone."
        )
        val normalized = NotificationNormalizer.normalize(raw)
        val result = parser.parse(normalized)
        assertTrue(result is ParseResult.Ignored)
    }
}
