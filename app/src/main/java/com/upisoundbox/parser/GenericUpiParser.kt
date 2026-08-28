package com.upisoundbox.parser

import com.upisoundbox.core.model.Direction
import com.upisoundbox.core.model.Provider
import com.upisoundbox.domain.model.PaymentEvent
import com.upisoundbox.notification.NormalizedNotification
import com.upisoundbox.validation.PaymentValidator

class GenericUpiParser : PaymentNotificationParser {
    override val provider: Provider = Provider.GENERIC

    override fun supports(packageName: String): Boolean {
        return true // Fallback parser
    }

    override fun parse(notification: NormalizedNotification): ParseResult {
        val text = notification.canonicalText
        val lower = notification.lowercaseText

        // Strict validation: Must contain explicit credit keywords and no reject keywords
        if (PaymentValidator.isNonPaymentOrPromotion(lower)) {
            return ParseResult.Ignored("Generic notification contains promotion/OTP/non-payment keywords")
        }

        val direction = PaymentValidator.classifyDirection(lower)
        if (direction != Direction.CREDIT) {
            return ParseResult.Ignored("Generic notification does not have incoming credit semantics")
        }

        val amountMinor = AmountParser.extractAmountMinor(text)
            ?: return ParseResult.Failed("Generic notification has credit keywords but missing valid amount")

        val event = PaymentEvent(
            sourcePackage = notification.raw.packageName,
            provider = Provider.fromPackageName(notification.raw.packageName),
            direction = Direction.CREDIT,
            amountMinor = amountMinor,
            eventTime = notification.raw.postedAt,
            sourceNotificationKey = notification.raw.notificationKey,
            confidence = 0.85f,
            rawSnippet = text.take(120)
        )
        return ParseResult.Success(event)
    }
}
