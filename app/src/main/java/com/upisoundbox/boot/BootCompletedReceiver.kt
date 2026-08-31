package com.upisoundbox.boot

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.service.notification.NotificationListenerService
import android.util.Log
import com.upisoundbox.UpiSoundboxApp
import com.upisoundbox.domain.model.DiagnosticEvent
import com.upisoundbox.notification.NotificationAccessChecker
import com.upisoundbox.notification.UpiNotificationListener

class BootCompletedReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        context ?: return
        val action = intent?.action ?: return
        Log.i("UpiSoundbox", ">>> BootCompletedReceiver received action: $action")

        try {
            val app = UpiSoundboxApp.instance
            val container = app.container

            // 1. Eagerly warm up the TTS speech engine
            container.ttsEngine.initialize()

            // 2. Log boot recovery event
            container.diagnosticsRepository.logDiagnostic(
                DiagnosticEvent(
                    eventType = "BOOT_INITIALIZATION",
                    message = "System boot detected ($action). Runtime components initialized."
                )
            )

            // 3. If notification access is granted, request listener rebind
            if (NotificationAccessChecker.isAccessGranted(context)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    val component = ComponentName(context, UpiNotificationListener::class.java)
                    NotificationListenerService.requestRebind(component)
                    Log.i("UpiSoundbox", "Requested NotificationListenerService rebind after boot")
                }
            }
        } catch (e: Exception) {
            Log.e("UpiSoundbox", "Error during boot initialization", e)
        }
    }
}
