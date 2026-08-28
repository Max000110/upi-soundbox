package com.upisoundbox.domain.model

import com.upisoundbox.core.model.Currency
import com.upisoundbox.core.model.Direction
import com.upisoundbox.core.model.Provider

data class PaymentEvent(
    val id: String = java.util.UUID.randomUUID().toString(),
    val sourcePackage: String,
    val provider: Provider,
    val direction: Direction = Direction.CREDIT,
    val amountMinor: Long, // Integer paise (e.g. 50000 = ₹500.00)
    val currency: Currency = Currency.INR,
    val payerName: String? = null,
    val transactionReference: String? = null,
    val eventTime: Long = System.currentTimeMillis(),
    val sourceNotificationKey: String? = null,
    val confidence: Float = 1.0f,
    val rawSnippet: String? = null
) {
    val amountMajorFormatted: String
        get() {
            val rupees = amountMinor / 100
            val paise = amountMinor % 100
            return if (paise == 0L) {
                "${currency.symbol}$rupees"
            } else {
                "${currency.symbol}$rupees.${paise.toString().padStart(2, '0')}"
            }
        }
}
