package com.upisoundbox.parser

import com.upisoundbox.core.model.Direction
import com.upisoundbox.core.model.Provider
import com.upisoundbox.domain.model.PaymentEvent
import com.upisoundbox.notification.NormalizedNotification
import com.upisoundbox.validation.PaymentValidator

class BankUpiParser : PaymentNotificationParser {
    override val provider: Provider = Provider.GENERIC

    companion object {
        private val BANK_SMS_PACKAGES = setOf(
            "com.google.android.apps.messaging",
            "com.samsung.android.messaging",
            "com.android.mms",
            "com.kotak811mobilebankingapp.instantsavingsupiscanandpayrecharge",
            "com.snapwork.hdfc",
            "com.csam.icici.bank.imobile",
            "com.sbi.lotusintouch",
            "com.axis.mobile"
        )

        private val REGEX_FROM = Regex("(?i)\\bfrom\\s+([A-Za-z0-9\\s]{2,30}?)(?:\\s*\\(|\\s*on\\s+\\d|\\s*to\\s+XX|\\s*ref|\\.|$)")
        private val REGEX_REFERENCE = Regex("(?i)(?:upi\\s*ref|rrn|txn|ref)[:\\s]*([0-9A-Za-z]{6,25})")
        private val REGEX_QUOTES = Regex("(?i)[“”\"']")
        private val REGEX_NON_ALPHANUM = Regex("[^A-Za-z0-9\\s]")
        private val REGEX_WHITESPACE = Regex("\\s+")
    }

    override fun supports(packageName: String): Boolean {
        return BANK_SMS_PACKAGES.contains(packageName)
    }

    override fun parse(notification: NormalizedNotification): ParseResult {
        val text = notification.canonicalText
        val lower = notification.lowercaseText

        // Filter out non-payment messages, OTPs, promotions
        if (PaymentValidator.isNonPaymentOrPromotion(lower)) {
            return ParseResult.Ignored("Bank/SMS message contains OTP or non-payment keywords")
        }

        val direction = PaymentValidator.classifyDirection(lower)
        if (direction != Direction.CREDIT) {
            return ParseResult.Ignored("Bank/SMS message is not an incoming credit ($direction)")
        }

        val amountMinor = AmountParser.extractAmountMinor(text)
            ?: return ParseResult.Failed("Could not extract amount from bank/SMS notification")

        val payerName = extractPayer(notification)
        val ref = extractReference(text)

        val event = PaymentEvent(
            sourcePackage = notification.raw.packageName,
            provider = Provider.GENERIC,
            direction = Direction.CREDIT,
            amountMinor = amountMinor,
            payerName = payerName,
            transactionReference = ref,
            eventTime = notification.raw.postedAt,
            sourceNotificationKey = notification.raw.notificationKey,
            confidence = 0.90f,
            rawSnippet = text.take(120)
        )

        return ParseResult.Success(event)
    }

    private fun extractPayer(notification: NormalizedNotification): String? {
        val candidates = listOfNotNull(
            notification.raw.title,
            notification.raw.text,
            notification.raw.bigText,
            notification.canonicalText
        )

        for (t in candidates) {
            val fromMatch = REGEX_FROM.find(t)
            if (fromMatch != null) {
                val candidate = cleanPayerName(fromMatch.groupValues[1])
                if (candidate.isNotEmpty() && !candidate.equals("your", ignoreCase = true)) {
                    return candidate
                }
            }
        }
        return null
    }

    private fun extractReference(text: String): String? {
        val match = REGEX_REFERENCE.find(text)
        return match?.groupValues?.get(1)
    }

    private fun cleanPayerName(raw: String): String {
        return raw.replace(REGEX_QUOTES, "")
            .replace(REGEX_NON_ALPHANUM, " ")
            .replace(REGEX_WHITESPACE, " ")
            .trim()
    }
}
