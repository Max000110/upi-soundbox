package com.upisoundbox

import com.upisoundbox.core.model.Provider
import com.upisoundbox.domain.model.PaymentEvent
import com.upisoundbox.speech.AnnouncementFormatter
import org.junit.Assert.assertEquals
import org.junit.Test

class AnnouncementFormatterTest {

    @Test
    fun testEnglishFormatting() {
        val event = PaymentEvent(
            sourcePackage = "com.phonepe.app",
            provider = Provider.PHONEPE,
            amountMinor = 50000L,
            payerName = "Rahul"
        )
        val text = AnnouncementFormatter.format(event, language = "en", includePayer = true, includeProvider = false)
        assertEquals("Received five hundred rupees from Rahul.", text)
    }

    @Test
    fun testHindiFormatting() {
        val event = PaymentEvent(
            sourcePackage = "net.one97.paytm",
            provider = Provider.PAYTM,
            amountMinor = 5000L,
            payerName = "अमित"
        )
        val text = AnnouncementFormatter.format(event, language = "hi", includePayer = true, includeProvider = false)
        assertEquals("अमित से पचास रुपये प्राप्त हुए।", text)
    }
}
