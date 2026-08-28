package com.upisoundbox

import com.upisoundbox.core.model.Direction
import com.upisoundbox.domain.model.RawNotification
import com.upisoundbox.notification.NotificationNormalizer
import com.upisoundbox.parser.ParseResult
import com.upisoundbox.parser.PhonePeParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PhonePeParserTest {

    private val parser = PhonePeParser()

    @Test
    fun testPositiveCreditWithPayer() {
        val raw = RawNotification(
            packageName = "com.phonepe.app",
            notificationKey = "key_1",
            postedAt = 1700000000000L,
            title = "Payment Received",
            text = "You have received ₹500 from Rahul (UPI Ref: 423984920)"
        )
        val normalized = NotificationNormalizer.normalize(raw)
        val result = parser.parse(normalized)

        assertTrue(result is ParseResult.Success)
        val event = (result as ParseResult.Success).event
        assertEquals(50000L, event.amountMinor)
        assertEquals(Direction.CREDIT, event.direction)
        assertEquals("Rahul", event.payerName)
        assertEquals("423984920", event.transactionReference)
    }

    @Test
    fun testNegativeDebit() {
        val raw = RawNotification(
            packageName = "com.phonepe.app",
            notificationKey = "key_2",
            postedAt = 1700000000000L,
            title = "Paid to Merchant",
            text = "Paid ₹500 to Big Bazaar"
        )
        val normalized = NotificationNormalizer.normalize(raw)
        val result = parser.parse(normalized)
        assertTrue(result is ParseResult.Ignored)
    }
}
