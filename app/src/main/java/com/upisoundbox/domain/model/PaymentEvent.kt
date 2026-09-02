package com.upisoundbox.domain.model

import com.upisoundbox.core.model.AnnouncementState
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
    val rawSnippet: String? = null,
    val durableIdentity: String = generateDurableIdentity(
        provider = provider,
        amountMinor = amountMinor,
        payerName = payerName,
        transactionReference = transactionReference,
        sourceNotificationKey = sourceNotificationKey
    ),
    val announcementState: AnnouncementState = AnnouncementState.ANNOUNCED,
    val announcedAt: Long = System.currentTimeMillis()
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

    companion object {
        fun cleanPayer(raw: String?): String? {
            if (raw.isNullOrBlank()) return null
            return raw.replace(Regex("(?i)[“”\"']"), "")
                .replace(Regex("[^A-Za-z0-9\\s]"), " ")
                .replace(Regex("\\s+"), " ")
                .trim()
                .takeIf { it.isNotEmpty() }
        }

        fun generateDurableIdentity(
            provider: Provider,
            amountMinor: Long,
            payerName: String?,
            transactionReference: String?,
            sourceNotificationKey: String?
        ): String {
            val cleanRef = transactionReference?.trim()?.takeIf { it.isNotEmpty() }
            if (cleanRef != null) {
                return "TXN_REF:${provider.name}:${cleanRef}:${amountMinor}"
            }

            val cleanKey = sourceNotificationKey?.trim()?.takeIf { it.isNotEmpty() }
            val cleanPayerName = cleanPayer(payerName) ?: "UNKNOWN"
            if (cleanKey != null) {
                return "NOTIF_KEY:${provider.name}:${cleanKey}:${amountMinor}:${cleanPayerName}"
            }

            return "SEMANTIC:${provider.name}:${amountMinor}:${cleanPayerName}"
        }
    }
}
