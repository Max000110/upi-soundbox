package com.upisoundbox

import com.upisoundbox.parser.AmountParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AmountParserTest {

    @Test
    fun testStandardAmounts() {
        assertEquals(5000L, AmountParser.extractAmountMinor("Received ₹50"))
        assertEquals(5000L, AmountParser.extractAmountMinor("Received ₹50.00"))
        assertEquals(5050L, AmountParser.extractAmountMinor("Received ₹50.50"))
        assertEquals(100000L, AmountParser.extractAmountMinor("Payment of ₹1,000 received"))
        assertEquals(10000000L, AmountParser.extractAmountMinor("₹1,00,000 credited to your account"))
    }

    @Test
    fun testAlternateRupeeMarkers() {
        assertEquals(50000L, AmountParser.extractAmountMinor("Received 500 rupees from Rahul"))
        assertEquals(25000L, AmountParser.extractAmountMinor("250 ₹ received"))
    }

    @Test
    fun testNegativeNonAmounts() {
        assertNull(AmountParser.extractAmountMinor("Your OTP is 482910"))
        assertNull(AmountParser.extractAmountMinor("Hello world, thanks for calling"))
    }
}
