package com.upisoundbox.speech

import com.upisoundbox.core.model.Provider
import com.upisoundbox.domain.model.PaymentEvent

object AnnouncementFormatter {

    private val HINDI_NUMBERS = arrayOf(
        "शून्य", "एक", "दो", "तीन", "चार", "पाँच", "छह", "सात", "आठ", "नौ", "दस",
        "ग्यारह", "बारह", "तेरह", "चौदह", "पंद्रह", "सोलह", "सत्रह", "अठारह", "उन्नीस", "बीस",
        "इक्कीस", "बाईस", "तेईस", "चौबीस", "पच्चीस", "छब्बीस", "सत्ताईस", "अट्ठाईस", "उनतीस", "तीस",
        "इकतीस", "बत्तीस", "तैंतीस", "चौंतीस", "पैंतीस", "छत्तीस", "सैंतीस", "अड़तीस", "उनतालीस", "चालीस",
        "इकतालीस", "बयालीस", "तैंतालीस", "चवालीस", "पैंतालीस", "छियालीस", "सैंतालीस", "अड़तालीस", "उनचास", "पचास",
        "इक्यावन", "बावन", "तिरपन", "चौवन", "पचपन", "छप्पन", "सत्तावन", "अट्ठावन", "उनसठ", "साठ",
        "इकसठ", "बासठ", "तिरसठ", "चौंसठ", "पैंसठ", "छियासठ", "सड़सठ", "अड़सठ", "उनहत्तर", "सत्तर",
        "इकहत्तर", "बहत्तर", "तिहत्तर", "चौहत्तर", "पचहत्तर", "छिहत्तर", "सतहत्तर", "अठहत्तर", "उन्यासी", "अस्सी",
        "इक्यासी", "बयासी", "तिरासी", "चौरासी", "पचासी", "छियासी", "सत्तासी", "अट्ठासी", "नवासी", "नब्बे",
        "इक्यानवे", "बानवे", "तिरानवे", "चौरानवे", "पचानवे", "छियानवे", "सत्तानवे", "अट्ठानवे", "निन्यानवे"
    )

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

        return when {
            n < 100 -> HINDI_NUMBERS[n.toInt()]
            n < 1000 -> (numberToHindiWords(n / 100) + " सौ " + if (n % 100 > 0) numberToHindiWords(n % 100) else "").trim()
            n < 100000 -> (numberToHindiWords(n / 1000) + " हजार " + if (n % 1000 > 0) numberToHindiWords(n % 1000) else "").trim()
            n < 10000000 -> (numberToHindiWords(n / 100000) + " लाख " + if (n % 100000 > 0) numberToHindiWords(n % 100000) else "").trim()
            else -> (numberToHindiWords(n / 10000000) + " करोड़ " + if (n % 10000000 > 0) numberToHindiWords(n % 10000000) else "").trim()
        }
    }
}
