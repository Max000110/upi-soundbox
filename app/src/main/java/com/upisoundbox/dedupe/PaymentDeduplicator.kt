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

class PaymentDeduplicator(
    private val context: Context? = null,
    private var windowSeconds: Int = 60,
    private val ioScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {
    data class CachedPayment(
        val durableIdentity: String,
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
     * Checks if the event is a duplicate using a 5-layer defence:
     * 1. Durable History Store (persisted on disk across app lifecycles)
     * 2. Persistent Durable Identity Cache
     * 3. Persistent Transaction Reference (RRN / UPI Ref) Cache
     * 4. Persistent Notification Key Cache (Android status bar reposts)
     * 5. In-Memory Sliding Window for cross-provider simultaneous bursts (Bank SMS vs App Push)
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

        // 2. LAYER 2: Exact Durable Identity Match
        if (seenDurableIdentities.containsKey(event.durableIdentity)) {
            Log.w("UpiSoundbox", "Deduplicator: Suppressed duplicate via seen durableIdentity: ${event.durableIdentity}")
            recordKeysOnly(event, now)
            return true
        }

        // 3. LAYER 3: Exact Transaction Reference / UPI Ref / RRN Match
        val cleanRef = event.transactionReference?.trim()?.takeIf { it.isNotEmpty() }
        if (cleanRef != null) {
            val refTime = seenReferences[cleanRef]
            if (refTime != null) {
                Log.w("UpiSoundbox", "Deduplicator: Suppressed duplicate via seen transactionReference: $cleanRef")
                recordKeysOnly(event, now)
                return true
            }
        }

        // 4. LAYER 4: Direct Notification Key Match (Android notification update/re-post)
        val cleanKey = event.sourceNotificationKey?.trim()?.takeIf { it.isNotEmpty() }
        if (cleanKey != null) {
            val keyTime = seenNotificationKeys[cleanKey]
            if (keyTime != null) {
                Log.w("UpiSoundbox", "Deduplicator: Suppressed duplicate via seen sourceNotificationKey: $cleanKey")
                recordKeysOnly(event, now)
                return true
            }
        }

        // 5. LAYER 5: Cross-Provider Semantic Match within sliding window (Bank SMS + App Push burst)
        val windowMillis = windowSeconds * 1000L
        val cleanPayer = PaymentEvent.cleanPayer(event.payerName)

        for (cached in recentPayments) {
            val elapsed = now - cached.timestamp
            if (elapsed in 0..windowMillis) {
                if (cached.amountMinor == event.amountMinor) {
                    val payerMatch = arePayersMatching(cached.cleanPayer, cleanPayer)
                    if (payerMatch) {
                        Log.w("UpiSoundbox", "Deduplicator: Suppressed cross-provider burst duplicate (${event.amountMajorFormatted} from $cleanPayer)")
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
        if (p1.isNullOrBlank() || p2.isNullOrBlank()) {
            return true
        }

        val s1 = p1.lowercase().trim()
        val s2 = p2.lowercase().trim()

        return s1 == s2 || s1.contains(s2) || s2.contains(s1)
    }

    private fun cleanupExpired(now: Long) {
        val burstThreshold = now - (windowSeconds * 1000L)
        recentPayments.removeIf { it.timestamp < burstThreshold }

        // Long-term cache cleanup after 7 days
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
