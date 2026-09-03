package com.upisoundbox.dedupe

import android.content.Context
import android.util.Log
import com.upisoundbox.domain.model.PaymentEvent
import com.upisoundbox.storage.HistoryRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.math.abs

class PaymentDeduplicator(
    private val context: Context? = null,
    private var windowSeconds: Int = 60,
    private val ioScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {
    data class CachedPayment(
        val durableIdentity: String,
        val sourcePackage: String,
        val notificationKey: String?,
        val transactionReference: String?,
        val amountMinor: Long,
        val cleanPayer: String?,
        val timestamp: Long
    )

    private val prefs = context?.getSharedPreferences("upi_dedupe_cache_store", Context.MODE_PRIVATE)

    private val seenDurableIdentities = ConcurrentHashMap<String, Long>()
    private val seenNotificationKeys = ConcurrentHashMap<String, Long>()
    private val seenReferences = ConcurrentHashMap<String, Long>()
    private val recentPayments = CopyOnWriteArrayList<CachedPayment>()

    init {
        loadPersistedDedupeCache()
    }

    private fun loadPersistedDedupeCache() {
        if (prefs == null) return
        try {
            val savedIdentities = prefs.getStringSet("seen_identities", null) ?: emptySet()
            val now = System.currentTimeMillis()
            for (id in savedIdentities) {
                seenDurableIdentities[id] = now
            }
            val savedRefs = prefs.getStringSet("seen_refs", null) ?: emptySet()
            for (ref in savedRefs) {
                seenReferences[ref] = now
            }
            val savedKeys = prefs.getStringSet("seen_keys", null) ?: emptySet()
            for (key in savedKeys) {
                seenNotificationKeys[key] = now
            }
            Log.d("UpiSoundbox", "Loaded persisted dedupe cache: ${savedIdentities.size} identities, ${savedRefs.size} refs, ${savedKeys.size} keys")
        } catch (e: Exception) {
            Log.e("UpiSoundbox", "Error loading persisted dedupe cache", e)
        }
    }

    private fun persistDedupeCacheAsync() {
        if (prefs == null) return
        ioScope.launch {
            try {
                prefs.edit()
                    .putStringSet("seen_identities", seenDurableIdentities.keys.take(500).toSet())
                    .putStringSet("seen_refs", seenReferences.keys.take(500).toSet())
                    .putStringSet("seen_keys", seenNotificationKeys.keys.take(500).toSet())
                    .apply()
            } catch (e: Exception) {
                Log.e("UpiSoundbox", "Error persisting dedupe cache", e)
            }
        }
    }

    fun setWindowSeconds(seconds: Int) {
        this.windowSeconds = seconds.coerceIn(10, 300)
    }

    /**
     * Checks if the event is a duplicate using cross-provider multi-app reconciliation:
     * 1. Durable History Store (persisted on disk across app lifecycles and cross-app reconciliation)
     * 2. Universal Transaction Reference (RRN / UPI Ref) Cache (universal across Bank SMS & Apps)
     * 3. Direct Notification Key Cache (Android status bar update/reposts)
     * 4. Multi-App Reconciliation:
     *    - Different packages (Bank App vs Google Pay / PhonePe): Reconciled across 2-hour window
     *    - Same package (Multiple payments in same app): Separated by configured windowSeconds
     */
    @Synchronized
    fun isDuplicate(
        event: PaymentEvent,
        historyRepository: HistoryRepository? = null,
        now: Long = System.currentTimeMillis()
    ): Boolean {
        cleanupExpired(now)

        // 1. LAYER 1: Durable History Store Verification
        if (historyRepository != null && historyRepository.isAlreadyAnnounced(event)) {
            Log.w("UpiSoundbox", "Deduplicator: Suppressed duplicate via persistent history check (durableId=${event.durableIdentity})")
            recordKeysOnly(event, now)
            return true
        }

        // 2. LAYER 2: Universal Transaction Reference Match (Bank SMS / RRN)
        val cleanRef = event.transactionReference?.trim()?.takeIf { it.isNotEmpty() }
        if (cleanRef != null) {
            val refTime = seenReferences[cleanRef]
            if (refTime != null) {
                Log.w("UpiSoundbox", "Deduplicator: Suppressed duplicate via seen transactionReference: $cleanRef")
                recordKeysOnly(event, now)
                return true
            }
        }

        // 3. LAYER 3: Direct Notification Key Match (Android status bar update/re-post)
        val cleanKey = event.sourceNotificationKey?.trim()?.takeIf { it.isNotEmpty() }
        if (cleanKey != null) {
            val keyTime = seenNotificationKeys[cleanKey]
            if (keyTime != null) {
                Log.w("UpiSoundbox", "Deduplicator: Suppressed duplicate via seen sourceNotificationKey: $cleanKey")
                recordKeysOnly(event, now)
                return true
            }
        }

        // 4. LAYER 4: Cross-App & Same-App Semantic Matching
        val cleanPayer = PaymentEvent.cleanPayer(event.payerName)
        val sameAppWindowMillis = windowSeconds * 1000L
        val crossAppWindowMillis = 2 * 3600 * 1000L // 2 hours for Bank App + UPI App delayed pairs

        for (cached in recentPayments) {
            if (cached.amountMinor == event.amountMinor) {
                val payerMatch = arePayersMatching(cached.cleanPayer, cleanPayer)
                if (payerMatch) {
                    val timeDiff = abs(now - cached.timestamp)
                    val isDifferentApp = cached.sourcePackage != event.sourcePackage

                    if (isDifferentApp && timeDiff < crossAppWindowMillis) {
                        // Different app reporting same payment (e.g. Kotak Bank Push + Google Pay Push)
                        Log.w("UpiSoundbox", "Deduplicator: Suppressed cross-app duplicate (${event.amountMajorFormatted} from $cleanPayer within ${timeDiff / 1000}s)")
                        recordKeysOnly(event, now)
                        return true
                    } else if (!isDifferentApp && timeDiff < sameAppWindowMillis) {
                        // Same app rapid burst duplicate
                        Log.w("UpiSoundbox", "Deduplicator: Suppressed same-app burst duplicate (${event.amountMajorFormatted} from $cleanPayer within ${timeDiff / 1000}s)")
                        recordKeysOnly(event, now)
                        return true
                    }
                }
            }
        }

        // Event is unique - record it across all caches
        seenDurableIdentities[event.durableIdentity] = now
        if (cleanRef != null) {
            seenReferences[cleanRef] = now
        }
        if (cleanKey != null) {
            seenNotificationKeys[cleanKey] = now
        }

        recentPayments.add(
            CachedPayment(
                durableIdentity = event.durableIdentity,
                sourcePackage = event.sourcePackage,
                notificationKey = cleanKey,
                transactionReference = cleanRef,
                amountMinor = event.amountMinor,
                cleanPayer = cleanPayer,
                timestamp = now
            )
        )

        persistDedupeCacheAsync()
        return false
    }

    private fun recordKeysOnly(event: PaymentEvent, now: Long) {
        seenDurableIdentities[event.durableIdentity] = now
        val cleanRef = event.transactionReference?.trim()?.takeIf { it.isNotEmpty() }
        if (cleanRef != null) {
            seenReferences[cleanRef] = now
        }
        val cleanKey = event.sourceNotificationKey?.trim()?.takeIf { it.isNotEmpty() }
        if (cleanKey != null) {
            seenNotificationKeys[cleanKey] = now
        }
        persistDedupeCacheAsync()
    }

    private fun arePayersMatching(p1: String?, p2: String?): Boolean {
        if (p1.isNullOrBlank() && p2.isNullOrBlank()) return true
        if (p1.isNullOrBlank() || p2.isNullOrBlank()) return true

        val s1 = PaymentEvent.cleanPayer(p1)?.lowercase() ?: ""
        val s2 = PaymentEvent.cleanPayer(p2)?.lowercase() ?: ""

        if (s1.isEmpty() || s2.isEmpty()) return true
        return s1 == s2 || s1.contains(s2) || s2.contains(s1)
    }

    private fun cleanupExpired(now: Long) {
        val burstThreshold = now - (2 * 3600 * 1000L)
        recentPayments.removeIf { it.timestamp < burstThreshold }

        val longTermThreshold = now - (7 * 24 * 3600 * 1000L)
        seenDurableIdentities.entries.removeIf { it.value < longTermThreshold }
        seenReferences.entries.removeIf { it.value < longTermThreshold }
        seenNotificationKeys.entries.removeIf { it.value < longTermThreshold }
    }

    fun clear() {
        seenDurableIdentities.clear()
        seenNotificationKeys.clear()
        seenReferences.clear()
        recentPayments.clear()
        prefs?.edit()?.clear()?.apply()
    }
}
