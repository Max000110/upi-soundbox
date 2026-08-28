package com.upisoundbox

import android.app.Application

class UpiSoundboxApp : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        container = AppContainer(this)
        // Pre-warm and eagerly initialize TTS engine on app start
        container.ttsEngine
    }

    companion object {
        lateinit var instance: UpiSoundboxApp
            private set
    }
}
