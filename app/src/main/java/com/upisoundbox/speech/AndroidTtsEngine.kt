package com.upisoundbox.speech

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import com.upisoundbox.core.model.TtsStatus
import com.upisoundbox.domain.model.SpeechRequest
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withTimeoutOrNull
import java.util.Locale

class AndroidTtsEngine(private val context: Context) : SpeechEngine, TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager

    private val _status = MutableStateFlow(TtsStatus.INITIALIZING)
    val status: StateFlow<TtsStatus> = _status.asStateFlow()

    private val initDeferred = CompletableDeferred<Boolean>()

    init {
        initialize()
    }

    private fun initialize() {
        try {
            _status.value = TtsStatus.INITIALIZING
            Log.d("UpiSoundbox", "Initializing TextToSpeech engine...")
            tts = TextToSpeech(context.applicationContext, this)
        } catch (e: Exception) {
            Log.e("UpiSoundbox", "Exception creating TextToSpeech", e)
            _status.value = TtsStatus.ERROR
            if (!initDeferred.isCompleted) {
                initDeferred.complete(false)
            }
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            _status.value = TtsStatus.READY
            Log.i("UpiSoundbox", ">>> TextToSpeech Initialized successfully (Status: READY)")
            if (!initDeferred.isCompleted) {
                initDeferred.complete(true)
            }
        } else {
            _status.value = TtsStatus.UNAVAILABLE
            Log.e("UpiSoundbox", "TextToSpeech onInit failed with status code $status")
            if (!initDeferred.isCompleted) {
                initDeferred.complete(false)
            }
        }
    }

    override fun isAvailable(): Boolean {
        return _status.value == TtsStatus.READY || _status.value == TtsStatus.SPEAKING
    }

    override suspend fun speak(request: SpeechRequest): Boolean {
        val ready = if (initDeferred.isCompleted) {
            @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
            initDeferred.getCompleted()
        } else {
            withTimeoutOrNull(5000L) { initDeferred.await() } ?: false
        }

        if (!ready || tts == null) {
            Log.e("UpiSoundbox", "TTS speak called but engine not ready (ready=$ready, tts=$tts)")
            return false
        }

        val engine = tts ?: return false

        try {
            engine.setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build()
            )
        } catch (e: Exception) {
            Log.w("UpiSoundbox", "Could not set audio attributes on TTS engine", e)
        }

        // Configure language
        val locale = if (request.language.equals("hi", ignoreCase = true)) {
            Locale("hi", "IN")
        } else {
            Locale("en", "IN")
        }

        val langResult = engine.setLanguage(locale)
        if (langResult == TextToSpeech.LANG_MISSING_DATA || langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
            Log.w("UpiSoundbox", "Language $locale not supported, falling back to English")
            engine.language = Locale.ENGLISH
        }

        engine.setSpeechRate(request.speechRate.coerceIn(0.5f, 2.0f))
        engine.setPitch(request.speechPitch.coerceIn(0.5f, 2.0f))

        val utteranceId = request.id
        val completionDeferred = CompletableDeferred<Boolean>()

        // Request transient audio focus to pause background playback cleanly for the announcement
        var focusRequest: AudioFocusRequest? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && audioManager != null) {
            val playbackAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build()

            focusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
                .setAudioAttributes(playbackAttributes)
                .setAcceptsDelayedFocusGain(false)
                .build()

            val focusResult = audioManager.requestAudioFocus(focusRequest)
            Log.d("UpiSoundbox", "Audio focus requested: result=$focusResult")
        }

        engine.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(id: String?) {
                if (id == utteranceId) {
                    _status.value = TtsStatus.SPEAKING
                    Log.i("UpiSoundbox", ">>> TTS UTTERANCE STARTED: $id (text='${request.text}')")
                }
            }

            override fun onDone(id: String?) {
                if (id == utteranceId) {
                    _status.value = TtsStatus.READY
                    Log.i("UpiSoundbox", ">>> TTS UTTERANCE COMPLETED: $id")
                    abandonFocus(focusRequest)
                    completionDeferred.complete(true)
                }
            }

            override fun onError(id: String?) {
                if (id == utteranceId) {
                    _status.value = TtsStatus.READY
                    Log.e("UpiSoundbox", ">>> TTS UTTERANCE ERROR: $id")
                    abandonFocus(focusRequest)
                    completionDeferred.complete(false)
                }
            }
        })

        val params = Bundle().apply {
            putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, request.requestedVolume.coerceIn(0.1f, 1.0f))
            putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId)
            putInt(TextToSpeech.Engine.KEY_PARAM_STREAM, AudioManager.STREAM_MUSIC)
        }

        Log.i("UpiSoundbox", "Calling TextToSpeech.speak text='${request.text}' id=$utteranceId")
        val result = engine.speak(request.text, TextToSpeech.QUEUE_FLUSH, params, utteranceId)
        if (result != TextToSpeech.SUCCESS) {
            _status.value = TtsStatus.ERROR
            Log.e("UpiSoundbox", "engine.speak returned error code $result")
            abandonFocus(focusRequest)
            return false
        }

        return withTimeoutOrNull(15000L) { completionDeferred.await() } ?: run {
            Log.w("UpiSoundbox", "TTS speech timed out after 15s")
            abandonFocus(focusRequest)
            false
        }
    }

    private fun abandonFocus(focusRequest: AudioFocusRequest?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && focusRequest != null && audioManager != null) {
            audioManager.abandonAudioFocusRequest(focusRequest)
        }
    }

    override fun stop() {
        tts?.stop()
        _status.value = TtsStatus.READY
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        _status.value = TtsStatus.UNAVAILABLE
    }
}
