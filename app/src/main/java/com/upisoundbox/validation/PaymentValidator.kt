package com.upisoundbox.validation

import com.upisoundbox.core.model.Direction

object PaymentValidator {

    private val REJECT_KEYWORDS = arrayOf(
        "otp",
        "verification code",
        "do not share",
        "cashback",
        "win up to",
        "offer valid",
        "claim your reward",
        "scratch card",
        "available balance",
        "account balance is",
        "requested money",
        "pay request",
        "approve request",
        "failed",
        "declined",
        "reversed",
        "unsuccessful",
        "cancelled",
        "canceled",
        "timed out",
        "mandate"
    )

    private val CREDIT_PATTERNS = arrayOf(
        Regex("(?i)\\bpaid\\s+you\\b"),
        Regex("(?i)\\bpaid\\s+to\\s+you\\b"),
        Regex("(?i)\\bsent\\s+you\\b"),
        Regex("(?i)\\breceived\\b"),
        Regex("(?i)\\bcredited\\b"),
        Regex("(?i)\\bpayment\\s+received\\b"),
        Regex("(?i)\\bmoney\\s+received\\b"),
        Regex("(?i)\\breceived\\s+from\\b"),
        Regex("(?i)\\bcredited\\s+with\\b"),
        Regex("(?i)\\bupi\\s+credited\\b")
    )

    private val DEBIT_PATTERNS = arrayOf(
        Regex("(?i)\\byou\\s+paid\\b"),
        Regex("(?i)\\bpaid\\s+to\\b"),
        Regex("(?i)\\bpaid\\b.*\\bfor\\b"),
        Regex("(?i)\\bpaid\\s+for\\b"),
        Regex("(?i)\\bdebited\\b"),
        Regex("(?i)\\bsent\\s+to\\b"),
        Regex("(?i)\\btransfer\\s+completed\\b"),
        Regex("(?i)\\bpayment\\s+made\\b"),
        Regex("(?i)\\bbill\\s+paid\\b"),
        Regex("(?i)\\bmoney\\s+sent\\b")
    )

    fun classifyDirection(lowercaseText: String): Direction {
        // Guard 1: Reject keywords (OTP/offer/promotions) take immediate precedence
        for (kw in REJECT_KEYWORDS) {
            if (lowercaseText.contains(kw)) {
                return Direction.UNKNOWN
            }
        }

        // Guard 2: Explicit debit check if text clearly indicates money was paid out (excluding "paid to you")
        if (lowercaseText.contains("you paid") || (lowercaseText.contains("paid to ") && !lowercaseText.contains("paid to you")) || lowercaseText.contains("debited from")) {
            return Direction.DEBIT
        }

        // Guard 3: Explicit credit patterns ("paid you", "sent you", "received", "credited")
        for (pattern in CREDIT_PATTERNS) {
            if (pattern.containsMatchIn(lowercaseText)) {
                return Direction.CREDIT
            }
        }

        // Guard 4: General debit patterns
        for (pattern in DEBIT_PATTERNS) {
            if (pattern.containsMatchIn(lowercaseText)) {
                return Direction.DEBIT
            }
        }

        return Direction.UNKNOWN
    }

    fun isNonPaymentOrPromotion(lowercaseText: String): Boolean {
        for (kw in REJECT_KEYWORDS) {
            if (lowercaseText.contains(kw)) return true
        }
        return false
    }
}
