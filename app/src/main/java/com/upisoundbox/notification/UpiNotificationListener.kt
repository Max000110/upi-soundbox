package com.upisoundbox.notification

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.upisoundbox.UpiSoundboxApp
import com.upisoundbox.core.model.ListenerState
import com.upisoundbox.domain.model.RawNotification

class UpiNotificationListener : NotificationListenerService() {

    override fun onListenerConnected() {
        super.onListenerConnected()
        Log.i("UpiSoundbox", ">>> NotificationListenerService CONNECTED")
        try {
            UpiSoundboxApp.instance.container.diagnosticsRepository.setListenerState(ListenerState.CONNECTED)
        } catch (e: Exception) {
            Log.e("UpiSoundbox", "Error setting listener connected state", e)
        }
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        Log.w("UpiSoundbox", ">>> NotificationListenerService DISCONNECTED")
        try {
            UpiSoundboxApp.instance.container.diagnosticsRepository.setListenerState(ListenerState.DISCONNECTED)
        } catch (e: Exception) {
            Log.e("UpiSoundbox", "Error setting listener disconnected state", e)
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            try {
                Log.i("UpiSoundbox", "Requesting auto-rebind for UpiNotificationListener...")
                requestRebind(android.content.ComponentName(this, UpiNotificationListener::class.java))
            } catch (e: Exception) {
                Log.e("UpiSoundbox", "Failed to request rebind", e)
            }
        }
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        sbn ?: return

        val notif = sbn.notification ?: return
        val packageName = sbn.packageName ?: return
        val extras = notif.extras ?: return

        // Skip non-actionable group summary containers (e.g. "5 new notifications")
        val isGroupSummary = (notif.flags and Notification.FLAG_GROUP_SUMMARY) != 0
        val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString()
        val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString()

        if (isGroupSummary && (text?.contains("new notification", ignoreCase = true) == true || (title.isNullOrBlank() && text.isNullOrBlank()))) {
            Log.d("UpiSoundbox", "Skipping group summary notification for $packageName")
            return
        }

        val bigTitle = extras.getCharSequence(Notification.EXTRA_TITLE_BIG)?.toString()
        val bigText = extras.getCharSequence(Notification.EXTRA_BIG_TEXT)?.toString()
        val subText = extras.getCharSequence(Notification.EXTRA_SUB_TEXT)?.toString()
        val summaryText = extras.getCharSequence(Notification.EXTRA_SUMMARY_TEXT)?.toString()

        val textLines = mutableListOf<String>()
        val linesArray = extras.getCharSequenceArray(Notification.EXTRA_TEXT_LINES)
        if (linesArray != null) {
            for (line in linesArray) {
                line?.toString()?.let { textLines.add(it) }
            }
        }
        subText?.let { textLines.add(it) }
        summaryText?.let { textLines.add(it) }
        notif.tickerText?.toString()?.let { textLines.add(it) }

        val raw = RawNotification(
            packageName = packageName,
            notificationKey = sbn.key,
            postedAt = sbn.postTime,
            title = title,
            text = text,
            bigTitle = bigTitle,
            bigText = bigText,
            textLines = textLines,
            category = notif.category
        )

        Log.d("UpiSoundbox", ">>> NotificationPosted pkg=$packageName key=${sbn.key} title='$title' text='$text' bigText='$bigText'")

        try {
            UpiSoundboxApp.instance.container.processNotification(raw)
        } catch (e: Exception) {
            Log.e("UpiSoundbox", "Error in processNotification", e)
        }
    }
}
