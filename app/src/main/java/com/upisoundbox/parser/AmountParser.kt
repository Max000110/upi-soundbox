package com.upisoundbox.parser

object AmountParser {

    // Matches ₹50, ₹.50, Rs. 50, Rs 50, INR 50, Re. 1, ₹1,000, ₹1,00,000, ₹50.50, etc.
    private val PREFIX_AMOUNT_REGEX = Regex(
        "(?i)(?:₹\\.?|rs\\.?|inr|re\\.?)\\s*([0-9]{1,3}(?:,[0-9]{2,3})*(?:\\.[0-9]{1,2})?|[0-9]+(?:\\.[0-9]{1,2})?)"
    )

    // Matches trailing markers like "50 ₹", "500 rupees", "1 rupee", "500.00 Rs.", "500 inr"
    private val SUFFIX_AMOUNT_REGEX = Regex(
        "(?i)([0-9]{1,3}(?:,[0-9]{2,3})*(?:\\.[0-9]{1,2})?|[0-9]+(?:\\.[0-9]{1,2})?)\\s*(?:₹|rupees?|rs\\.?|inr)"
    )

    fun extractAmountMinor(text: String): Long? {
        // Try prefix ₹ / Rs. / INR first
        val prefixMatch = PREFIX_AMOUNT_REGEX.find(text)
        if (prefixMatch != null) {
            val numStr = prefixMatch.groupValues[1].replace(",", "")
            val parsed = parseDecimalToMinor(numStr)
            if (parsed != null && parsed > 0) return parsed
        }

        // Try suffix ₹ / rupees / Rs.
        val suffixMatch = SUFFIX_AMOUNT_REGEX.find(text)
        if (suffixMatch != null) {
            val numStr = suffixMatch.groupValues[1].replace(",", "")
            val parsed = parseDecimalToMinor(numStr)
            if (parsed != null && parsed > 0) return parsed
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
