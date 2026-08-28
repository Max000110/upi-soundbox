package com.upisoundbox.speech

import com.upisoundbox.core.model.Provider
import com.upisoundbox.domain.model.PaymentEvent

object AnnouncementFormatter {

    fun format(
        event: PaymentEvent,
        language: String = "en",
        includePayer: Boolean = true,
        includeProvider: Boolean = false
    ): String {
        val rupees = event.amountMinor / 100
        val paise = event.amountMinor % 100

        return if (language.equals("hi", ignoreCase = true)) {
            formatHindi(rupees, paise, event.payerName?.takeIf { includePayer }, event.provider.takeIf { includeProvider })
        } else {
            formatEnglish(rupees, paise, event.payerName?.takeIf { includePayer }, event.provider.takeIf { includeProvider })
        }
    }

    private fun formatEnglish(rupees: Long, paise: Long, payer: String?, provider: Provider?): String {
        val amountWords = numberToEnglishWords(rupees)
        val paiseText = if (paise > 0) " and ${numberToEnglishWords(paise)} paise" else ""
        val currencyWord = if (rupees == 1L && paise == 0L) "rupee" else "rupees"

        val payerPart = if (!payer.isNullOrBlank()) " from $payer" else ""
        val providerPart = if (provider != null && provider != Provider.GENERIC) " on ${provider.displayName}" else ""

        return if (payerPart.isNotEmpty()) {
            "Received $amountWords$paiseText $currencyWord$payerPart$providerPart."
        } else if (providerPart.isNotEmpty()) {
            "$amountWords$paiseText $currencyWord received$providerPart."
        } else {
            "Payment received. $amountWords$paiseText $currencyWord."
        }
    }

    private fun formatHindi(rupees: Long, paise: Long, payer: String?, provider: Provider?): String {
        val amountWords = numberToHindiWords(rupees)
        val paiseText = if (paise > 0) " ${numberToHindiWords(paise)} पैसे" else ""

        val payerPart = if (!payer.isNullOrBlank()) "$payer से " else ""
        val providerPart = if (provider != null && provider != Provider.GENERIC) "${provider.displayName} पर " else ""

        return "$providerPart$payerPart$amountWords$paiseText रुपये प्राप्त हुए।"
    }

    fun numberToEnglishWords(n: Long): String {
        if (n == 0L) return "zero"
        if (n < 0L) return "minus " + numberToEnglishWords(-n)
        return formatPositiveEnglish(n)
    }

    private fun formatPositiveEnglish(n: Long): String {
        val units = arrayOf(
            "", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine",
            "ten", "eleven", "twelve", "thirteen", "fourteen", "fifteen", "sixteen",
            "seventeen", "eighteen", "nineteen"
        )
        val tens = arrayOf(
            "", "", "twenty", "thirty", "forty", "fifty", "sixty", "seventy", "eighty", "ninety"
        )

        return when {
            n < 20 -> units[n.toInt()]
            n < 100 -> (tens[(n / 10).toInt()] + " " + units[(n % 10).toInt()]).trim()
            n < 1000 -> (units[(n / 100).toInt()] + " hundred" + if (n % 100 > 0) " " + formatPositiveEnglish(n % 100) else "").trim()
            n < 100000 -> (formatPositiveEnglish(n / 1000) + " thousand" + if (n % 1000 > 0) " " + formatPositiveEnglish(n % 1000) else "").trim()
            n < 10000000 -> (formatPositiveEnglish(n / 100000) + " lakh" + if (n % 100000 > 0) " " + formatPositiveEnglish(n % 100000) else "").trim()
            else -> (formatPositiveEnglish(n / 10000000) + " crore" + if (n % 10000000 > 0) " " + formatPositiveEnglish(n % 10000000) else "").trim()
        }
    }

    fun numberToHindiWords(n: Long): String {
        if (n == 0L) return "शून्य"
        if (n < 0L) return "माइनस " + numberToHindiWords(-n)

        val hindiMap = mapOf(
            1L to "एक", 2L to "दो", 3L to "तीन", 4L to "चार", 5L to "पाँच",
            6L to "छह", 7L to "सात", 8L to "आठ", 9L to "नौ", 10L to "दस",
            11L to "ग्यारह", 12L to "बारह", 13L to "तेरह", 14L to "चौदह", 15L to "पंद्रह",
            16L to "सोलह", 17L to "सत्रह", 18L to "अठारह", 19L to "उन्नीस", 20L to "बीस",
            25L to "पच्चीस", 30L to "तीस", 40L to "चालीस", 50L to "पचास", 60L to "साठ",
            70L to "सत्तर", 80L to "अस्सी", 90L to "नब्बे", 100L to "सौ"
        )

        hindiMap[n]?.let { return it }

        return when {
            n < 100 -> "$n"
            n < 1000 -> (numberToHindiWords(n / 100) + " सौ " + if (n % 100 > 0) numberToHindiWords(n % 100) else "").trim()
            n < 100000 -> (numberToHindiWords(n / 1000) + " हजार " + if (n % 1000 > 0) numberToHindiWords(n % 1000) else "").trim()
            n < 10000000 -> (numberToHindiWords(n / 100000) + " लाख " + if (n % 100000 > 0) numberToHindiWords(n % 100000) else "").trim()
            else -> (numberToHindiWords(n / 10000000) + " करोड़ " + if (n % 10000000 > 0) numberToHindiWords(n % 10000000) else "").trim()
        }
    }
}
