package com.upisoundbox

import com.upisoundbox.core.model.Provider
import com.upisoundbox.dedupe.PaymentDeduplicator
import com.upisoundbox.domain.model.PaymentEvent
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PaymentDeduplicatorTest {

    private val deduplicator = PaymentDeduplicator(windowSeconds = 60)

    @Test
    fun testDuplicateSuppressionWithReference() {
        val event1 = PaymentEvent(
            sourcePackage = "com.phonepe.app",
            provider = Provider.PHONEPE,
            amountMinor = 50000L,
            transactionReference = "REF123456"
        )
        val event2 = PaymentEvent(
            sourcePackage = "com.phonepe.app",
            provider = Provider.PHONEPE,
            amountMinor = 50000L,
            transactionReference = "REF123456"
        )

        assertFalse(deduplicator.isDuplicate(event1, now = 1000000L))
        assertTrue(deduplicator.isDuplicate(event2, now = 1005000L))
    }

    @Test
    fun testDifferentAmountsAllowed() {
        val event1 = PaymentEvent(
            sourcePackage = "net.one97.paytm",
            provider = Provider.PAYTM,
            amountMinor = 50000L
        )
        val event2 = PaymentEvent(
            sourcePackage = "net.one97.paytm",
            provider = Provider.PAYTM,
            amountMinor = 100000L
        )

        assertFalse(deduplicator.isDuplicate(event1, now = 1000000L))
        assertFalse(deduplicator.isDuplicate(event2, now = 1005000L))
    }
}
