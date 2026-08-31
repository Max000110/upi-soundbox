package com.upisoundbox.parser

import com.upisoundbox.core.model.Direction
import com.upisoundbox.core.model.Provider
import com.upisoundbox.domain.model.PaymentEvent
import com.upisoundbox.notification.NormalizedNotification
import com.upisoundbox.validation.PaymentValidator

class PhonePeParser : PaymentNotificationParser {
    override val provider: Provider = Provider.PHONEPE

    companion object {
        private val REGEX_FROM = Regex("(?i)\\bfrom\\s+([A-Za-z0-9\\s]{2,30}?)(?:\\s*\\(|\\s*via|\\s*on|\\s*ref|\\s*upi|$)")
        private val REGEX_REFERENCE = Regex("(?i)(?:ref|rrn|txn|upi\\s*ref)[:\\s]*([0-9A-Za-z]{6,25})")
    }

    override fun supports(packageName: String): Boolean {
        return provider.defaultPackageIds.contains(packageName)
    }

    override fun parse(notification: NormalizedNotification): ParseResult {
        val text = notification.canonicalText
        val lower = notification.lowercaseText

        // Check direction
        val direction = PaymentValidator.classifyDirection(lower)
        if (direction != Direction.CREDIT) {
            return ParseResult.Ignored("PhonePe notification is not an incoming credit ($direction)")
        }

        // Extract amount
        val amountMinor = AmountParser.extractAmountMinor(text)
            ?: return ParseResult.Failed("Could not extract valid monetary amount from PhonePe notification")

        val payerName = extractPayer(text)
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
            confidence = 0.98f,
            rawSnippet = text.take(120)
        )

        return ParseResult.Success(event)
    }

    private fun extractPayer(text: String): String? {
        val match = REGEX_FROM.find(text)
        return match?.groupValues?.get(1)?.trim()?.takeIf { it.isNotEmpty() && !it.equals("you", ignoreCase = true) }
    }

    private fun extractReference(text: String): String? {
        val match = REGEX_REFERENCE.find(text)
        return match?.groupValues?.get(1)
    }
}
