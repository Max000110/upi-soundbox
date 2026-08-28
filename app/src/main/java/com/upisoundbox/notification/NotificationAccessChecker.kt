package com.upisoundbox.notification

import android.content.ComponentName
import android.content.Context
import android.provider.Settings
import androidx.core.app.NotificationManagerCompat

object NotificationAccessChecker {
    fun isAccessGranted(context: Context): Boolean {
        val packageName = context.packageName
        // Method 1: NotificationManagerCompat
        val enabledPackages = NotificationManagerCompat.getEnabledListenerPackages(context)
        if (enabledPackages.contains(packageName)) {
            return true
        }

        // Method 2: Settings.Secure check
        val flat = Settings.Secure.getString(
            context.contentResolver,
            "enabled_notification_listeners"
        ) ?: return false

        val componentName = ComponentName(context, UpiNotificationListener::class.java).flattenToString()
        return flat.contains(componentName) || flat.contains(packageName)
    }
}
