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

    @Test
    fun testHindiNumbers1To99() {
        assertEquals("एक", AnnouncementFormatter.numberToHindiWords(1L))
        assertEquals("पच्चीस", AnnouncementFormatter.numberToHindiWords(25L))
        assertEquals("पैंतीस", AnnouncementFormatter.numberToHindiWords(35L))
        assertEquals("पचास", AnnouncementFormatter.numberToHindiWords(50L))
        assertEquals("अड़तालीस", AnnouncementFormatter.numberToHindiWords(48L))
        assertEquals("अठहत्तर", AnnouncementFormatter.numberToHindiWords(78L))
        assertEquals("निन्यानवे", AnnouncementFormatter.numberToHindiWords(99L))
    }

    @Test
    fun testHindiLargeNumbers() {
        assertEquals("एक सौ", AnnouncementFormatter.numberToHindiWords(100L))
        assertEquals("पाँच सौ", AnnouncementFormatter.numberToHindiWords(500L))
        assertEquals("एक हजार", AnnouncementFormatter.numberToHindiWords(1000L))
        assertEquals("एक लाख", AnnouncementFormatter.numberToHindiWords(100000L))
        assertEquals("एक करोड़", AnnouncementFormatter.numberToHindiWords(10000000L))
    }

    @Test
    fun testEnglishNumbers() {
        assertEquals("one", AnnouncementFormatter.numberToEnglishWords(1L))
        assertEquals("forty five", AnnouncementFormatter.numberToEnglishWords(45L))
        assertEquals("five hundred", AnnouncementFormatter.numberToEnglishWords(500L))
        assertEquals("one thousand", AnnouncementFormatter.numberToEnglishWords(1000L))
    }
}
