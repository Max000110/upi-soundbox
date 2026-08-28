package com.upisoundbox.security

import android.app.Activity
import android.content.Context
import android.os.Build
import android.view.WindowManager
import java.io.File

object SecurityManager {

    /**
     * Enables or disables WindowManager.LayoutParams.FLAG_SECURE.
     * When enabled, prevents Android OS from capturing screenshots or previews in the recent apps switcher,
     * and blocks background screen-recording apps from capturing payment data.
     */
    fun applyWindowSecurity(activity: Activity, secureScreenEnabled: Boolean) {
        if (secureScreenEnabled) {
            activity.window.setFlags(
                WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE
            )
        } else {
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }
    }

    /**
     * Checks if the device environment exhibits root binaries or known dangerous environments.
     */
    fun isDeviceRooted(): Boolean {
        val buildTags = Build.TAGS
        if (buildTags != null && buildTags.contains("test-keys")) {
            return true
        }

        val paths = arrayOf(
            "/system/app/Superuser.apk",
            "/sbin/su",
            "/system/bin/su",
            "/system/xbin/su",
            "/data/local/xbin/su",
            "/data/local/bin/su",
            "/system/sd/xbin/su",
            "/system/bin/failsafe/su",
            "/data/local/su",
            "/su/bin/su"
        )

        for (path in paths) {
            if (File(path).exists()) return true
        }

        return false
    }

    /**
     * Sanitizes strings to prevent format string vulnerabilities or UI spoofing.
     */
    fun sanitizeText(input: String): String {
        return input.replace(Regex("[\\x00-\\x1F\\x7F]"), "")
            .replace(Regex("[<>]"), "")
            .take(200)
    }
}
