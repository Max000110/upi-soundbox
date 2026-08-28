package com.upisoundbox.parser

import com.upisoundbox.core.model.Provider
import com.upisoundbox.notification.NormalizedNotification

interface PaymentNotificationParser {
    val provider: Provider
    fun supports(packageName: String): Boolean
    fun parse(notification: NormalizedNotification): ParseResult
}
