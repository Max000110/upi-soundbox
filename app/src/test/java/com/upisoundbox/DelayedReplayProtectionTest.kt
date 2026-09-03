package com.upisoundbox

import com.upisoundbox.core.model.Direction
import com.upisoundbox.core.model.Provider
import com.upisoundbox.dedupe.PaymentDeduplicator
import com.upisoundbox.domain.model.PaymentEvent
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
    fun testRealWorldKotakPlusGooglePayDelayed23MinutesReconciliation() {
        val t0 = 1788200000000L // 06:32 PM

        // 1. Kotak 811 Mobile Banking app sends push notification at 06:32 PM
        val kotakPush = PaymentEvent(
            sourcePackage = "com.kotak811mobilebankingapp.instantsavingsupiscanandpayrecharge",
            provider = Provider.GENERIC,
            direction = Direction.CREDIT,
            amountMinor = 2000L, // ₹20.00
            payerName = "GUDIYA JAGDISH CHOUDHARY",
            transactionReference = null,
            eventTime = t0,
            sourceNotificationKey = "0|com.kotak811mobilebankingapp|0|FCM-Notification:814334555|10562",
            confidence = 0.90f,
            rawSnippet = "₹20.00 received from GUDIYA JAGDISH CHOUDHARY"
        )

        val isKotakDuplicate = deduplicator.isDuplicate(kotakPush, now = t0)
        assertFalse("First Kotak bank notification must be announced", isKotakDuplicate)

        // 2. 23 minutes later (06:55 PM), Google Pay syncs and posts delayed notification
        val t23min = t0 + (23 * 60 * 1000L)
        val googlePayPush = PaymentEvent(
            sourcePackage = "com.google.android.apps.nbu.paisa.user",
            provider = Provider.GOOGLE_PAY,
            direction = Direction.CREDIT,
            amountMinor = 2000L, // ₹20.00
            payerName = "GUDIYA JAGDISH CHOUDHARY",
            transactionReference = null,
            eventTime = t23min,
            sourceNotificationKey = "0|com.google.android.apps.nbu.paisa.user|0|1354135901::client_fetch:0eefafe3|10575",
            confidence = 0.99f,
            rawSnippet = "GUDIYA JAGDISH CHOUDHARY paid you ₹20.00"
        )

        val isGpayDuplicate = deduplicator.isDuplicate(googlePayPush, now = t23min)
        assertTrue("Google Pay notification arriving 23 mins after Kotak for same payment MUST BE SUPPRESSED", isGpayDuplicate)

        // 3. 40 minutes later (07:12 PM), Bank SMS arrives with UPI Reference
        val t40min = t0 + (40 * 60 * 1000L)
        val bankSms = PaymentEvent(
            sourcePackage = "com.google.android.apps.messaging",
            provider = Provider.GENERIC,
            direction = Direction.CREDIT,
            amountMinor = 2000L, // ₹20.00
            payerName = "GUDIYA JAGDISH CHOUD",
            transactionReference = "142912391924",
            eventTime = t40min,
            sourceNotificationKey = "0|com.google.android.apps.messaging|2|sms:381|10191",
            confidence = 0.90f,
            rawSnippet = "Received Rs.20.00 in your Kotak Bank AC 2413 from GUDIYA JAGDISH CHOUD on 03-09-26.UPI Ref:142912391924"
        )

        val isSmsDuplicate = deduplicator.isDuplicate(bankSms, now = t40min)
        assertTrue("Bank SMS arriving 40 mins later for same payment MUST BE SUPPRESSED", isSmsDuplicate)
    }

    @Test
    fun testGenuinelyNewCustomerPaymentAfter2HoursIsAnnounced() {
        val t0 = 1788200000000L

        // Customer 1 pays ₹20 at 06:00 PM
        val cust1 = PaymentEvent(
            sourcePackage = "com.google.android.apps.nbu.paisa.user",
            provider = Provider.GOOGLE_PAY,
            direction = Direction.CREDIT,
            amountMinor = 2000L,
            payerName = "GUDIYA JAGDISH CHOUDHARY",
            transactionReference = "REF_111",
            eventTime = t0,
            sourceNotificationKey = "key_111"
        )
        assertFalse(deduplicator.isDuplicate(cust1, now = t0))

        // Customer 2 pays ₹20 at 06:15 PM (Different payer -> instant announcement!)
        val cust2 = PaymentEvent(
            sourcePackage = "com.google.android.apps.nbu.paisa.user",
            provider = Provider.GOOGLE_PAY,
            direction = Direction.CREDIT,
            amountMinor = 2000L,
            payerName = "Mr IMRAN KARIMULLA SHAIKH",
            transactionReference = "REF_222",
            eventTime = t0 + 15 * 60 * 1000L,
            sourceNotificationKey = "key_222"
        )
        assertFalse("Different payer for same amount must be announced", deduplicator.isDuplicate(cust2, now = t0 + 15 * 60 * 1000L))
    }
}
