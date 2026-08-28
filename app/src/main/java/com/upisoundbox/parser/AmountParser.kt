package com.upisoundbox.parser

object AmountParser {

    // Matches ₹50, ₹ 50, ₹1,000, ₹1,00,000, ₹50.50, ₹ 1000.5, etc.
    // Also matches trailing rupee marker like "50 ₹", "500 rupees", "500.00 rupees"
    private val PREFIX_AMOUNT_REGEX = Regex("₹\\s*([0-9]{1,3}(?:,[0-9]{2,3})*(?:\\.[0-9]{1,2})?|[0-9]+(?:\\.[0-9]{1,2})?)")
    private val SUFFIX_AMOUNT_REGEX = Regex("([0-9]{1,3}(?:,[0-9]{2,3})*(?:\\.[0-9]{1,2})?|[0-9]+(?:\\.[0-9]{1,2})?)\\s*(?:₹|rupees)")

    fun extractAmountMinor(text: String): Long? {
        // Try prefix ₹ first
        val prefixMatch = PREFIX_AMOUNT_REGEX.find(text)
        if (prefixMatch != null) {
            val numStr = prefixMatch.groupValues[1].replace(",", "")
            return parseDecimalToMinor(numStr)
        }

        // Try suffix ₹ / rupees
        val suffixMatch = SUFFIX_AMOUNT_REGEX.find(text)
        if (suffixMatch != null) {
            val numStr = suffixMatch.groupValues[1].replace(",", "")
            return parseDecimalToMinor(numStr)
        }

        return null
    }

    private fun parseDecimalToMinor(numStr: String): Long? {
        return try {
            if (numStr.contains(".")) {
                val parts = numStr.split(".")
                val major = parts[0].toLong()
                val minorStr = parts[1].padEnd(2, '0').take(2)
                val minor = minorStr.toLong()
                (major * 100) + minor
            } else {
                numStr.toLong() * 100
            }
        } catch (e: Exception) {
            null
        }
    }
}
