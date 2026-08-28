package com.upisoundbox.dedupe

import com.upisoundbox.domain.model.PaymentEvent
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

class PaymentDeduplicator(
    private var windowSeconds: Int = 60
) {
    data class CachedPayment(
        val notificationKey: String?,
        val transactionReference: String?,
        val amountMinor: Long,
        val cleanPayer: String?,
        val timestamp: Long
    )

    private val seenNotificationKeys = ConcurrentHashMap<String, Long>()
    private val seenReferences = ConcurrentHashMap<String, Long>()
    private val recentPayments = CopyOnWriteArrayList<CachedPayment>()

    fun setWindowSeconds(seconds: Int) {
        this.windowSeconds = seconds.coerceIn(10, 300)
    }

    /**
     * Checks if the event is a duplicate across notification keys, transaction references,
     * or cross-provider amount + payer combinations within the configured time window.
     *
     * @return true if it is a duplicate (should be suppressed), false if it is a new valid event.
     */
    @Synchronized
    fun isDuplicate(event: PaymentEvent, now: Long = System.currentTimeMillis()): Boolean {
        cleanupExpired(now)

        val windowMillis = windowSeconds * 1000L
        val cleanPayer = cleanPayerName(event.payerName)

        // 1. Direct Notification Key Match (Android notification shade update/re-post)
        if (!event.sourceNotificationKey.isNullOrBlank()) {
            val keyTime = seenNotificationKeys[event.sourceNotificationKey]
            if (keyTime != null && (now - keyTime) < windowMillis) {
                return true
            }
        }

        // 2. Exact Transaction Reference / UPI Ref / RRN Match (Across Bank SMS & App)
        if (!event.transactionReference.isNullOrBlank()) {
            val refTime = seenReferences[event.transactionReference.trim()]
            if (refTime != null && (now - refTime) < windowMillis) {
                return true
            }
        }

        // 3. Cross-Provider Semantic Match: Same Amount + Same Payer within window
        for (cached in recentPayments) {
            val elapsed = now - cached.timestamp
            if (elapsed in 0..windowMillis) {
                if (cached.amountMinor == event.amountMinor) {
                    val payerMatch = arePayersMatching(cached.cleanPayer, cleanPayer)
                    if (payerMatch) {
                        // Store the new notification key and ref if any so subsequent notifications match
                        recordKeysOnly(event, now)
                        return true
                    }
                }
            }
        }

        // Event is unique - record it
        if (!event.sourceNotificationKey.isNullOrBlank()) {
            seenNotificationKeys[event.sourceNotificationKey] = now
        }
        if (!event.transactionReference.isNullOrBlank()) {
            seenReferences[event.transactionReference.trim()] = now
        }

        recentPayments.add(
            CachedPayment(
                notificationKey = event.sourceNotificationKey,
                transactionReference = event.transactionReference?.trim(),
                amountMinor = event.amountMinor,
                cleanPayer = cleanPayer,
                timestamp = now
            )
        )

        return false
    }

    private fun recordKeysOnly(event: PaymentEvent, now: Long) {
        if (!event.sourceNotificationKey.isNullOrBlank()) {
            seenNotificationKeys[event.sourceNotificationKey] = now
        }
        if (!event.transactionReference.isNullOrBlank()) {
            seenReferences[event.transactionReference.trim()] = now
        }
    }

    private fun arePayersMatching(p1: String?, p2: String?): Boolean {
        if (p1.isNullOrBlank() && p2.isNullOrBlank()) return true
        if (p1.isNullOrBlank() || p2.isNullOrBlank()) {
            // If one has payer name and the other doesn't (e.g. Bank SMS vs Quick App Push),
            // matching amount within 60s is classified as the same transaction
            return true
        }

        val s1 = p1.lowercase().trim()
        val s2 = p2.lowercase().trim()

        return s1 == s2 || s1.contains(s2) || s2.contains(s1)
    }

    private fun cleanPayerName(raw: String?): String? {
        if (raw.isNullOrBlank()) return null
        return raw.replace(Regex("(?i)[“”\"']"), "")
            .replace(Regex("[^A-Za-z0-9\\s]"), " ")
            .replace(Regex("\\s+"), " ")
            .trim()
            .takeIf { it.isNotEmpty() }
    }

    private fun cleanupExpired(now: Long) {
        val expiryThreshold = now - (windowSeconds * 1000L)

        seenNotificationKeys.entries.removeIf { it.value < expiryThreshold }
        seenReferences.entries.removeIf { it.value < expiryThreshold }
        recentPayments.removeIf { it.timestamp < expiryThreshold }
    }

    fun clear() {
        seenNotificationKeys.clear()
        seenReferences.clear()
        recentPayments.clear()
    }
}
