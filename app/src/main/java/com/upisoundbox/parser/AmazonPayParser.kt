package com.upisoundbox.parser

import com.upisoundbox.core.model.Direction
import com.upisoundbox.core.model.Provider
import com.upisoundbox.domain.model.PaymentEvent
import com.upisoundbox.notification.NormalizedNotification
import com.upisoundbox.validation.PaymentValidator

class AmazonPayParser : PaymentNotificationParser {
    override val provider: Provider = Provider.AMAZON_PAY

    override fun supports(packageName: String): Boolean {
        return provider.defaultPackageIds.contains(packageName)
    }

    override fun parse(notification: NormalizedNotification): ParseResult {
        val text = notification.canonicalText
        val lower = notification.lowercaseText

        val direction = PaymentValidator.classifyDirection(lower)
        if (direction != Direction.CREDIT) {
            return ParseResult.Ignored("Amazon Pay notification is not an incoming credit")
        }

        val amountMinor = AmountParser.extractAmountMinor(text)
            ?: return ParseResult.Failed("Could not extract amount from Amazon Pay notification")

        val event = PaymentEvent(
            sourcePackage = notification.raw.packageName,
            provider = provider,
            direction = Direction.CREDIT,
            amountMinor = amountMinor,
            eventTime = notification.raw.postedAt,
            sourceNotificationKey = notification.raw.notificationKey,
            confidence = 0.95f,
            rawSnippet = text.take(120)
        )
        return ParseResult.Success(event)
    }
}
