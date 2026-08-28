package com.upisoundbox.parser

import com.upisoundbox.domain.model.PaymentEvent

sealed class ParseResult {
    data class Success(val event: PaymentEvent) : ParseResult()
    data class Ignored(val reason: String) : ParseResult()
    data class Failed(val reason: String) : ParseResult()
}
