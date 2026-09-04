package com.upisoundbox.notification

import com.upisoundbox.domain.model.RawNotification

data class NormalizedNotification(
    val raw: RawNotification,
    val canonicalText: String,
    val lowercaseText: String
)

object NotificationNormalizer {

    private val DEVANAGARI_DIGITS = mapOf(
        '०' to '0', '१' to '1', '२' to '2', '३' to '3', '४' to '4',
        '५' to '5', '६' to '6', '७' to '7', '८' to '8', '९' to '9'
    )

    private val REGEX_NEWLINES = Regex("[\\r\\n\\t]+")
    private val REGEX_CURRENCY_MARKERS = Regex("(?i)\\b(?:rs|inr|re)\\.?(?=[\\s\\d]|$)")
    private val REGEX_COLLAPSE_WHITESPACES = Regex("\\s+")

    fun normalize(raw: RawNotification): NormalizedNotification {
        val combined = raw.fullText()

        // 1. Fast char replacement for common Unicode spaces & curly quotes
        var text = combined
            .replace('\u00A0', ' ')
            .replace('\u2007', ' ')
            .replace('\u202F', ' ')
            .replace('\uFEFF', ' ')
            .replace('“', '"')
            .replace('”', '"')
            .replace('‘', '\'')
            .replace('’', '\'')
            .replace(REGEX_NEWLINES, " ")

        // 2. Convert Indic/Devanagari digits to standard ASCII digits
        val digitConverted = StringBuilder(text.length)
        for (ch in text) {
            val converted = DEVANAGARI_DIGITS[ch] ?: ch
            digitConverted.append(converted)
        }
        text = digitConverted.toString()

        // 3. Normalize currency markers: "Rs.", "Rs", "INR", "inr" -> "₹"
        text = text.replace(REGEX_CURRENCY_MARKERS, "₹")

        // 4. Collapse repeated whitespaces
        text = text.replace(REGEX_COLLAPSE_WHITESPACES, " ").trim()

        val lowercase = text.lowercase()

        return NormalizedNotification(
            raw = raw,
            canonicalText = text,
            lowercaseText = lowercase
        )
    }
}
