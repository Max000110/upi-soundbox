package com.upisoundbox.core.model

enum class Provider(val displayName: String, val defaultPackageIds: Set<String>) {
    PHONEPE(
        displayName = "PhonePe",
        defaultPackageIds = setOf("com.phonepe.app", "com.phonepe.app.business")
    ),
    GOOGLE_PAY(
        displayName = "Google Pay",
        defaultPackageIds = setOf("com.google.android.apps.nbu.paisa.user", "com.google.android.apps.nbu.paisa.merchant")
    ),
    PAYTM(
        displayName = "Paytm",
        defaultPackageIds = setOf("net.one97.paytm", "com.paytm.business")
    ),
    BHIM(
        displayName = "BHIM",
        defaultPackageIds = setOf("in.org.npci.upiapp")
    ),
    CRED(
        displayName = "CRED",
        defaultPackageIds = setOf("com.dreamplug.androidapp")
    ),
    AMAZON_PAY(
        displayName = "Amazon Pay",
        defaultPackageIds = setOf("in.amazon.mShop.android.shopping")
    ),
    GENERIC(
        displayName = "Other UPI Apps",
        defaultPackageIds = emptySet()
    );

    companion object {
        fun fromPackageName(packageName: String): Provider {
            return entries.firstOrNull { it.defaultPackageIds.contains(packageName) } ?: GENERIC
        }
    }
}
