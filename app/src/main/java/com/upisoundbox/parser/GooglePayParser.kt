package com.upisoundbox.parser

import com.upisoundbox.core.model.Direction
import com.upisoundbox.core.model.Provider
import com.upisoundbox.domain.model.PaymentEvent
import com.upisoundbox.notification.NormalizedNotification
import com.upisoundbox.validation.PaymentValidator

class GooglePayParser : PaymentNotificationParser {
    override val provider: Provider = Provider.GOOGLE_PAY

    companion object {
        private val REGEX_PAID_YOU = Regex("(?i)(?:^|\\b)([A-Za-z0-9\\s]{2,40}?)\\s+(?:paid|sent)\\s+you")
        private val REGEX_FROM = Regex("(?i)\\bfrom\\s+([A-Za-z0-9\\s]{2,40}?)(?:\\s*\\(|\\s*on\\s+Google|\\s*on\\s+GPay|\\s*ref|\\.|$)")
        private val REGEX_PAYMENT_FROM = Regex("(?i)\\bpayment\\s+from\\s+([A-Za-z0-9\\s]{2,40}?)(?:\\s*\\(|\\s*on|\\s*ref|\\.|$)")
        private val REGEX_REFERENCE = Regex("(?i)(?:upi\\s*ref|rrn|txn|ref)[:\\s]*([0-9A-Za-z]{6,25})")
        private val REGEX_QUOTES = Regex("[\"']")
        private val REGEX_NON_ALPHANUM = Regex("[^\\p{L}\\p{Nd}\\s₹.,-]")
        private val REGEX_WHITESPACE = Regex("\\s+")
    }

    override fun supports(packageName: String): Boolean {
        return provider.defaultPackageIds.contains(packageName)
    }

    override fun parse(notification: NormalizedNotification): ParseResult {
        val text = notification.canonicalText
        val lower = notification.lowercaseText

        // Filter out Google Pay promotions, scratch cards, cashbacks, and bill reminders
        if (PaymentValidator.isNonPaymentOrPromotion(lower) || lower.contains("scratch card") || lower.contains("reward")) {
            return ParseResult.Ignored("Google Pay promotion/reward/non-payment notification ignored")
        }

        val direction = PaymentValidator.classifyDirection(lower)
        if (direction != Direction.CREDIT) {
            return ParseResult.Ignored("Google Pay notification is not an incoming credit ($direction)")
        }

        val amountMinor = AmountParser.extractAmountMinor(text)
            ?: return ParseResult.Failed("Could not extract amount from Google Pay notification")

        val payerName = extractPayer(notification)
        val ref = extractReference(text)

        val event = PaymentEvent(
            sourcePackage = notification.raw.packageName,
            provider = provider,
            direction = Direction.CREDIT,
            amountMinor = amountMinor,
            payerName = payerName,
            transactionReference = ref,
            eventTime = notification.raw.postedAt,
            sourceNotificationKey = notification.raw.notificationKey,
            confidence = 0.99f,
            rawSnippet = text.take(120)
        )

        return ParseResult.Success(event)
    }

    private fun extractPayer(notification: NormalizedNotification): String? {
        val candidates = listOfNotNull(
            notification.raw.title,
            notification.raw.bigTitle,
            notification.raw.text,
            notification.raw.bigText,
            notification.canonicalText
        )

        for (rawStr in candidates) {
            val t = cleanQuotesAndEmojis(rawStr)

            // Pattern 1: "<Payer> paid you ₹500" or "<Payer> sent you ₹500"
            val paidYouMatch = REGEX_PAID_YOU.find(t)
            if (paidYouMatch != null) {
                val candidate = sanitizeCandidate(paidYouMatch.groupValues[1])
                if (candidate.isNotEmpty()) return candidate
            }

            // Pattern 2: "received ₹500 from <Payer>" or "from <Payer> on Google Pay"
            val fromMatch = REGEX_FROM.find(t)
            if (fromMatch != null) {
                val candidate = sanitizeCandidate(fromMatch.groupValues[1])
                if (candidate.isNotEmpty()) return candidate
            }

            // Pattern 3: "Payment from <Payer>"
            val paymentFromMatch = REGEX_PAYMENT_FROM.find(t)
            if (paymentFromMatch != null) {
                val candidate = sanitizeCandidate(paymentFromMatch.groupValues[1])
                if (candidate.isNotEmpty()) return candidate
            }
        }

        return null
    }

    private fun extractReference(text: String): String? {
        val match = REGEX_REFERENCE.find(text)
        return match?.groupValues?.get(1)
    }

    private fun cleanQuotesAndEmojis(s: String): String {
        return s.replace('“', '"')
            .replace('”', '"')
            .replace('‘', '\'')
            .replace('’', '\'')
            .replace(REGEX_QUOTES, "")
            .replace(REGEX_NON_ALPHANUM, " ")
            .replace(REGEX_WHITESPACE, " ")
            .trim()
    }

    private fun sanitizeCandidate(raw: String): String {
        return raw.replace("Google Pay", "", ignoreCase = true)
            .replace("GPay", "", ignoreCase = true)
            .replace("for Business", "", ignoreCase = true)
            .trim()
    }
}
