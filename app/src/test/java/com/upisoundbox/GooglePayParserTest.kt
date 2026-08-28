package com.upisoundbox

import com.upisoundbox.core.model.Direction
import com.upisoundbox.core.model.Provider
import com.upisoundbox.domain.model.RawNotification
import com.upisoundbox.notification.NotificationNormalizer
import com.upisoundbox.parser.GooglePayParser
import com.upisoundbox.parser.ParseResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GooglePayParserTest {

    private val parser = GooglePayParser()

    @Test
    fun testGooglePay_PaidYou_Standard() {
        val raw = RawNotification(
            packageName = "com.google.android.apps.nbu.paisa.user",
            notificationKey = "gpay_1",
            postedAt = 1700000000000L,
            title = "AFZAL KASAM MANSURI paid you ₹1.00",
            text = "Tap to view."
        )
        val normalized = NotificationNormalizer.normalize(raw)
        val result = parser.parse(normalized)

        assertTrue(result is ParseResult.Success)
        val event = (result as ParseResult.Success).event
        assertEquals(100L, event.amountMinor)
        assertEquals(Direction.CREDIT, event.direction)
        assertEquals("AFZAL KASAM MANSURI", event.payerName)
        assertEquals(Provider.GOOGLE_PAY, event.provider)
    }

    @Test
    fun testGooglePay_StylizedQuotedName() {
        val raw = RawNotification(
            packageName = "com.google.android.apps.nbu.paisa.user",
            notificationKey = "gpay_2",
            postedAt = 1700000000000L,
            title = "ROYAL “Afzu” GAMING paid you ₹100.00",
            text = "Tap to view."
        )
        val normalized = NotificationNormalizer.normalize(raw)
        val result = parser.parse(normalized)

        assertTrue(result is ParseResult.Success)
        val event = (result as ParseResult.Success).event
        assertEquals(10000L, event.amountMinor)
        assertEquals("ROYAL Afzu GAMING", event.payerName)
    }

    @Test
    fun testGooglePay_SentYou() {
        val raw = RawNotification(
            packageName = "com.google.android.apps.nbu.paisa.user",
            notificationKey = "gpay_3",
            postedAt = 1700000000000L,
            title = "Google Pay",
            text = "Rahul Sharma sent you ₹1,250.00"
        )
        val normalized = NotificationNormalizer.normalize(raw)
        val result = parser.parse(normalized)

        assertTrue(result is ParseResult.Success)
        val event = (result as ParseResult.Success).event
        assertEquals(125000L, event.amountMinor)
        assertEquals("Rahul Sharma", event.payerName)
    }

    @Test
    fun testGooglePay_ForBusinessApp() {
        val raw = RawNotification(
            packageName = "com.google.android.apps.nbu.paisa.merchant",
            notificationKey = "gpay_biz_1",
            postedAt = 1700000000000L,
            title = "Payment received",
            text = "₹500.00 received from Amit Patel on Google Pay for Business"
        )
        val normalized = NotificationNormalizer.normalize(raw)
        val result = parser.parse(normalized)

        assertTrue(result is ParseResult.Success)
        val event = (result as ParseResult.Success).event
        assertEquals(50000L, event.amountMinor)
        assertEquals("Amit Patel", event.payerName)
        assertEquals(Provider.GOOGLE_PAY, event.provider)
    }

    @Test
    fun testGooglePay_DebitRejection() {
        val raw = RawNotification(
            packageName = "com.google.android.apps.nbu.paisa.user",
            notificationKey = "gpay_debit",
            postedAt = 1700000000000L,
            title = "Google Pay",
            text = "You paid ₹500 to Store"
        )
        val normalized = NotificationNormalizer.normalize(raw)
        val result = parser.parse(normalized)
        assertTrue(result is ParseResult.Ignored)
    }

    @Test
    fun testGooglePay_RewardRejection() {
        val raw = RawNotification(
            packageName = "com.google.android.apps.nbu.paisa.user",
            notificationKey = "gpay_reward",
            postedAt = 1700000000000L,
            title = "You earned a scratch card!",
            text = "Win up to ₹1,000 cashback on your next payment."
        )
        val normalized = NotificationNormalizer.normalize(raw)
        val result = parser.parse(normalized)
        assertTrue(result is ParseResult.Ignored)
    }
}
