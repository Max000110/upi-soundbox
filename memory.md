# UPI Voice Soundbox — `memory.md`

**Document Type:** Persistent Project Memory / Current-State Ledger  
**Project:** UPI Voice Soundbox — Android local-first application  
**Version:** 1.0.6  
**Status:** Triple-Notification & Cross-Provider Deduplication Fixed and Verified  
**Last Updated:** 2026-08-28  

---

# 1. TRIPLE NOTIFICATION ROOT CAUSE & RESOLUTION

### Root Cause
When you receive 1 UPI payment on your Android phone, Android receives up to **3 separate notifications within ~1.5 seconds**:
1. **Google Pay / PhonePe App Notification** (`com.google.android.apps.nbu.paisa.user`)
2. **Kotak / Banking App Push Notification** (`com.kotak811mobilebankingapp...`)
3. **Bank SMS via Messages App** (`com.google.android.apps.messaging`)

Previously, `PaymentDeduplicator.kt` generated separate fingerprints using the app's `provider.name` and static time buckets. Consequently:
- Google Pay created `GOOGLE_PAY|100|afzal kasam mansuri`
- Kotak Bank App created `GENERIC|100|afzal kasam mansuri`
- Bank SMS created `REF:GENERIC:128644995392`

Because the fingerprints differed, all 3 notifications were treated as 3 distinct payments, resulting in **3 voice announcements** and **3 duplicate records in history** for a single ₹1 transaction.

---

# 2. THE MULTI-LAYER DEDUPLICATION FIX

In [`PaymentDeduplicator.kt`](file:///home/ubuntu/app/src/main/java/com/upisoundbox/dedupe/PaymentDeduplicator.kt), we replaced the isolated provider hash with a **Universal Semantic Cross-Provider Deduplicator**:
1. **Notification Key Cache**: Re-posted/updated notifications from the same app are instantly suppressed.
2. **Global UPI Reference / RRN Cache**: Identical transaction references (e.g. `128644995392`) across SMS and banking apps are instantly suppressed.
3. **Cross-Provider Rolling Window (60s)**:
   - Matches `amountMinor` (e.g. 100 paise) + `cleanPayerName` (e.g. "afzal kasam mansuri") across ALL incoming notifications within the 60-second window.
   - The first notification (e.g. Google Pay) triggers the **single voice announcement** and **single history record**.
   - All subsequent notifications from the Banking App and SMS within 60s are **silently suppressed**.

---

# 3. VERIFICATION & EMPIRICAL EVIDENCE

- **Unit Regression Test:** [`TripleNotificationDeduplicationTest.kt`](file:///home/ubuntu/app/src/test/java/com/upisoundbox/TripleNotificationDeduplicationTest.kt) (Passed)
  - `gpayEvent` ➔ `isDuplicate = false` (Announced & Logged)
  - `kotakAppEvent` (+800ms) ➔ `isDuplicate = true` (Suppressed)
  - `smsEvent` (+1500ms) ➔ `isDuplicate = true` (Suppressed)
- **Target Device:** Samsung Galaxy M14 5G (`SM-M146B`, Android 15)
- **Status:** Fresh APK deployed with clean history buffer.
