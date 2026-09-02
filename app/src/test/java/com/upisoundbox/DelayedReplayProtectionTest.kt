package com.upisoundbox

import com.upisoundbox.core.model.Direction
import com.upisoundbox.core.model.Provider
import com.upisoundbox.dedupe.PaymentDeduplicator
import com.upisoundbox.domain.model.PaymentEvent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class DelayedReplayProtectionTest {

    private lateinit var deduplicator: PaymentDeduplicator

    @Before
    fun setup() {
        deduplicator = PaymentDeduplicator(context = null, windowSeconds = 60)
    }

    @Test
    fun testPaymentAnnouncedOnce_AndDelayedNotificationRepostAfter45MinutesIsSuppressed() {
        val t0 = 1788200000000L // 10:00 AM

        // ₹30 payment arrives at 10:00 AM
        val initialPayment = PaymentEvent(
            sourcePackage = "com.phonepe.app",
            provider = Provider.PHONEPE,
            direction = Direction.CREDIT,
            amountMinor = 3000L, // ₹30.00
            payerName = "Rahul Sharma",
            transactionReference = "TXN_PHONEPE_884920",
            eventTime = t0,
            sourceNotificationKey = "0|com.phonepe.app|1|merchant_30|10588",
            confidence = 0.98f
        )

        // 1. Initial notification MUST be announced
        val isFirstDuplicate = deduplicator.isDuplicate(initialPayment, now = t0)
        assertFalse("Initial payment must be announced", isFirstDuplicate)

        // 2. 45 minutes later (T0 + 45 min): PhonePe updates notification summary or Android redispatches onNotificationPosted
        val t45min = t0 + (45 * 60 * 1000L)
        val repostedPayment = PaymentEvent(
            sourcePackage = "com.phonepe.app",
            provider = Provider.PHONEPE,
            direction = Direction.CREDIT,
            amountMinor = 3000L, // ₹30.00
            payerName = "Rahul Sharma",
            transactionReference = "TXN_PHONEPE_884920",
            eventTime = t45min,
            sourceNotificationKey = "0|com.phonepe.app|1|merchant_30|10588",
            confidence = 0.98f
        )

        // Delayed repost MUST be suppressed permanently!
        val isRepostDuplicate = deduplicator.isDuplicate(repostedPayment, now = t45min)
        assertTrue("45-minute delayed repost of same transaction ref must be suppressed", isRepostDuplicate)
    }

    @Test
    fun testDelayedNotificationKeyRepostWithoutRefIsSuppressedAfter60Minutes() {
        val t0 = 1788200000000L

        val initialPayment = PaymentEvent(
            sourcePackage = "com.google.android.apps.nbu.paisa.user",
            provider = Provider.GOOGLE_PAY,
            direction = Direction.CREDIT,
            amountMinor = 5000L, // ₹50.00
            payerName = "Amit Kumar",
            transactionReference = null, // No ref in brief push
            eventTime = t0,
            sourceNotificationKey = "0|com.google.android.apps.nbu.paisa.user|1|gpay_notif_99|10590",
            confidence = 0.95f
        )

        assertFalse(deduplicator.isDuplicate(initialPayment, now = t0))

        // 60 minutes later, Android re-delivers the existing notification key from the shade
        val t60min = t0 + (60 * 60 * 1000L)
        val delayedPayment = PaymentEvent(
            sourcePackage = "com.google.android.apps.nbu.paisa.user",
            provider = Provider.GOOGLE_PAY,
            direction = Direction.CREDIT,
            amountMinor = 5000L,
            payerName = "Amit Kumar",
            transactionReference = null,
            eventTime = t60min,
            sourceNotificationKey = "0|com.google.android.apps.nbu.paisa.user|1|gpay_notif_99|10590",
            confidence = 0.95f
        )

        assertTrue("Delayed notification key repost after 60 mins must be suppressed", deduplicator.isDuplicate(delayedPayment, now = t60min))
    }

    @Test
    fun testGenuinelyNewPaymentOfSameAmountAfter45MinutesIsAnnounced() {
        val t0 = 1788200000000L

        // Customer 1 pays ₹30 at 10:00 AM
        val payment1 = PaymentEvent(
            sourcePackage = "com.phonepe.app",
            provider = Provider.PHONEPE,
            direction = Direction.CREDIT,
            amountMinor = 3000L, // ₹30.00
            payerName = "Customer One",
            transactionReference = "REF_ORDER_001",
            eventTime = t0,
            sourceNotificationKey = "notif_key_cust1",
            confidence = 0.98f
        )
        assertFalse("First ₹30 payment must be announced", deduplicator.isDuplicate(payment1, now = t0))

        // Customer 2 pays ₹30 at 10:45 AM (New transaction reference + new notification key)
        val t45min = t0 + (45 * 60 * 1000L)
        val payment2 = PaymentEvent(
            sourcePackage = "com.phonepe.app",
            provider = Provider.PHONEPE,
            direction = Direction.CREDIT,
            amountMinor = 3000L, // ₹30.00
            payerName = "Customer Two",
            transactionReference = "REF_ORDER_002",
            eventTime = t45min,
            sourceNotificationKey = "notif_key_cust2",
            confidence = 0.98f
        )
        assertFalse("Genuinely new ₹30 payment from Customer 2 must be announced", deduplicator.isDuplicate(payment2, now = t45min))
    }

    @Test
    fun testDurableIdentityFormat() {
        val eventWithRef = PaymentEvent(
            sourcePackage = "com.phonepe.app",
            provider = Provider.PHONEPE,
            amountMinor = 10000L,
            payerName = "Deepak",
            transactionReference = "RRN998877",
            sourceNotificationKey = "key_998877"
        )
        assertEquals("TXN_REF:PHONEPE:RRN998877:10000", eventWithRef.durableIdentity)

        val eventWithoutRef = PaymentEvent(
            sourcePackage = "com.google.android.apps.nbu.paisa.user",
            provider = Provider.GOOGLE_PAY,
            amountMinor = 2500L,
            payerName = "Suresh Raina",
            transactionReference = null,
            sourceNotificationKey = "gpay_shade_key_44"
        )
        assertEquals("NOTIF_KEY:GOOGLE_PAY:gpay_shade_key_44:2500:Suresh Raina", eventWithoutRef.durableIdentity)
    }
}
