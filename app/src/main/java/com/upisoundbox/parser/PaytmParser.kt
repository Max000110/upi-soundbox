package com.upisoundbox.parser

import com.upisoundbox.core.model.Direction
import com.upisoundbox.core.model.Provider
import com.upisoundbox.domain.model.PaymentEvent
import com.upisoundbox.notification.NormalizedNotification
import com.upisoundbox.validation.PaymentValidator

class PaytmParser : PaymentNotificationParser {
    override val provider: Provider = Provider.PAYTM

    override fun supports(packageName: String): Boolean {
        return provider.defaultPackageIds.contains(packageName)
    }

    override fun parse(notification: NormalizedNotification): ParseResult {
        val text = notification.canonicalText
        val lower = notification.lowercaseText

        val direction = PaymentValidator.classifyDirection(lower)
        if (direction != Direction.CREDIT) {
            return ParseResult.Ignored("Paytm notification is not an incoming credit ($direction)")
        }

        val amountMinor = AmountParser.extractAmountMinor(text)
            ?: return ParseResult.Failed("Could not extract amount from Paytm notification")

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
        val fromMatch = Regex("(?i)\\bfrom\\s+([A-Za-z0-9\\s]{2,30}?)(?:\\s*in|\\s*to|\\s*ref|\\s*upi|$)").find(text)
        return fromMatch?.groupValues?.get(1)?.trim()
    }

    private fun extractReference(text: String): String? {
        val match = Regex("(?i)(?:upi\\s*ref|txn|ref|order)[:\\s]*([0-9A-Za-z]{6,25})").find(text)
        return match?.groupValues?.get(1)
    }
}
