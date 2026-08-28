package com.upisoundbox.parser

import com.upisoundbox.notification.NormalizedNotification

object ParserRegistry {

    private val parsers: List<PaymentNotificationParser> = listOf(
        PhonePeParser(),
        GooglePayParser(),
        PaytmParser(),
        BhimParser(),
        CredParser(),
        AmazonPayParser(),
        BankUpiParser(),
        GenericUpiParser()
    )

    fun parse(normalized: NormalizedNotification): ParseResult {
        val pkg = normalized.raw.packageName

        // 1. Try dedicated parser matching package ID
        val dedicatedParser = parsers.firstOrNull { it !is GenericUpiParser && it.supports(pkg) }
        if (dedicatedParser != null) {
            return dedicatedParser.parse(normalized)
        }

        // 2. Fallback to generic parser
        val genericParser = parsers.firstOrNull { it is GenericUpiParser }
        return genericParser?.parse(normalized) ?: ParseResult.Failed("No parser available for package: $pkg")
    }
}
