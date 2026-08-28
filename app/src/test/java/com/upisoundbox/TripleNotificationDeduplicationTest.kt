package com.upisoundbox

import com.upisoundbox.core.model.Direction
import com.upisoundbox.core.model.Provider
import com.upisoundbox.dedupe.PaymentDeduplicator
import com.upisoundbox.domain.model.PaymentEvent
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class TripleNotificationDeduplicationTest {

    private lateinit var deduplicator: PaymentDeduplicator

    @Before
    fun setup() {
        deduplicator = PaymentDeduplicator(windowSeconds = 60)
    }

    @Test
    fun testGooglePay_Plus_KotakApp_Plus_BankSms_AreDeduplicatedToSingleAnnouncement() {
        val now = 1700000000000L

        // 1. Google Pay Notification arrives first
        val gpayEvent = PaymentEvent(
            sourcePackage = "com.google.android.apps.nbu.paisa.user",
            provider = Provider.GOOGLE_PAY,
            direction = Direction.CREDIT,
            amountMinor = 100L, // ₹1.00
            payerName = "AFZAL KASAM MANSURI",
            transactionReference = null,
            eventTime = now,
            sourceNotificationKey = "0|com.google.android.apps.nbu.paisa.user|1|gpay_123|10575",
            confidence = 0.99f,
            rawSnippet = "AFZAL KASAM MANSURI paid you ₹1.00"
        )

        val isGpayDuplicate = deduplicator.isDuplicate(gpayEvent, now = now)
        // First notification MUST NOT be duplicate (it must be announced!)
        assertFalse("First Google Pay notification must be processed", isGpayDuplicate)

        // 2. Kotak Bank Mobile App notification arrives 800ms later
        val kotakAppEvent = PaymentEvent(
            sourcePackage = "com.kotak811mobilebankingapp.instantsavingsupiscanandpayrecharge",
            provider = Provider.GENERIC,
            direction = Direction.CREDIT,
            amountMinor = 100L, // ₹1.00
            payerName = "AFZAL KASAM MANSURI",
            transactionReference = null,
            eventTime = now + 800L,
            sourceNotificationKey = "0|com.kotak811mobilebankingapp|0|FCM:478127961|10562",
            confidence = 0.90f,
            rawSnippet = "₹1.00 received from AFZAL KASAM MANSURI"
        )

        val isKotakAppDuplicate = deduplicator.isDuplicate(kotakAppEvent, now = now + 800L)
        // Kotak App notification MUST BE SUPPRESSED (already announced by Google Pay!)
        assertTrue("Kotak App duplicate notification must be suppressed", isKotakAppDuplicate)

        // 3. Bank SMS arrives 1500ms later
        val smsEvent = PaymentEvent(
            sourcePackage = "com.google.android.apps.messaging",
            provider = Provider.GENERIC,
            direction = Direction.CREDIT,
            amountMinor = 100L, // ₹1.00
            payerName = "AFZAL KASAM MANSURI",
            transactionReference = "128644995392",
            eventTime = now + 1500L,
            sourceNotificationKey = "0|com.google.android.apps.messaging|2|sms_381|10191",
            confidence = 0.90f,
            rawSnippet = "Received Rs.1.00 in your Kotak Bank AC 2413 from AFZAL KASAM MANSURI on 28-08-26.UPI Ref:128644995392"
        )

        val isSmsDuplicate = deduplicator.isDuplicate(smsEvent, now = now + 1500L)
        // Bank SMS notification MUST BE SUPPRESSED (already announced by Google Pay!)
        assertTrue("Bank SMS duplicate notification must be suppressed", isSmsDuplicate)
    }

    @Test
    fun testDistinctPaymentAfterWindowIsAllowed() {
        val now = 1700000000000L

        val payment1 = PaymentEvent(
            sourcePackage = "com.google.android.apps.nbu.paisa.user",
            provider = Provider.GOOGLE_PAY,
            direction = Direction.CREDIT,
            amountMinor = 100L,
            payerName = "AFZAL KASAM MANSURI",
            transactionReference = null,
            eventTime = now,
            sourceNotificationKey = "key_1",
            confidence = 0.99f
        )
        assertFalse(deduplicator.isDuplicate(payment1, now = now))

        // Same amount and payer 2 minutes (120s) later -> NEW PAYMENT!
        val payment2 = PaymentEvent(
            sourcePackage = "com.google.android.apps.nbu.paisa.user",
            provider = Provider.GOOGLE_PAY,
            direction = Direction.CREDIT,
            amountMinor = 100L,
            payerName = "AFZAL KASAM MANSURI",
            transactionReference = null,
            eventTime = now + 120_000L,
            sourceNotificationKey = "key_2",
            confidence = 0.99f
        )
        assertFalse("New transaction after 60s window must be processed", deduplicator.isDuplicate(payment2, now = now + 120_000L))
    }
}
