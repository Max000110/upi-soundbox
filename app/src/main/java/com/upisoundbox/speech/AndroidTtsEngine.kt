package com.upisoundbox.speech

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.speech.tts.Voice
import android.util.Log
import com.upisoundbox.core.model.TtsStatus
import com.upisoundbox.domain.model.SpeechRequest
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeoutOrNull
import java.util.Locale

class AndroidTtsEngine(
    private val context: Context,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default + Job())
) : SpeechEngine, TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager

    private val _status = MutableStateFlow(TtsStatus.UNINITIALIZED)
    val status: StateFlow<TtsStatus> = _status.asStateFlow()

    private var initDeferred = CompletableDeferred<Boolean>()
    private val initMutex = Mutex()
    private var retryCount = 0
    private val maxRetries = 3

    var lastInitTime: Long = 0L
        private set
    var lastSpeechTime: Long = 0L
        private set
    var lastErrorMessage: String? = null
        private set

    init {
        initialize()
    }

    fun initialize() {
        scope.launch {
            initMutex.withLock {
                if (_status.value == TtsStatus.READY || _status.value == TtsStatus.INITIALIZING) {
                    return@withLock
                }
                doInitializeLocked()
            }
        }
    }

    private fun doInitializeLocked() {
        try {
            _status.value = TtsStatus.INITIALIZING
            initDeferred = CompletableDeferred()
            Log.i("UpiSoundbox", ">>> Initializing TextToSpeech engine (Attempt ${retryCount + 1})...")

            val preferredEngine = getBestTtsEngine()
            tts = if (preferredEngine != null) {
                Log.d("UpiSoundbox", "Binding to preferred engine: $preferredEngine")
                TextToSpeech(context.applicationContext, this, preferredEngine)
            } else {
                TextToSpeech(context.applicationContext, this)
            }

            scope.launch {
                delay(7000L)
                if (_status.value == TtsStatus.INITIALIZING) {
                    Log.w("UpiSoundbox", "TTS initialization timed out after 7s")
                    _status.value = TtsStatus.ERROR
                    lastErrorMessage = "Initialization timed out"
                    if (!initDeferred.isCompleted) {
                        initDeferred.complete(false)
                    }
                    scheduleRetry()
                }
            }
        } catch (e: Exception) {
            Log.e("UpiSoundbox", "Exception creating TextToSpeech", e)
            _status.value = TtsStatus.ERROR
            lastErrorMessage = e.localizedMessage
            if (!initDeferred.isCompleted) {
                initDeferred.complete(false)
            }
            scheduleRetry()
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            _status.value = TtsStatus.READY
            retryCount = 0
            lastInitTime = System.currentTimeMillis()
            lastErrorMessage = null
            Log.i("UpiSoundbox", ">>> TextToSpeech Initialized successfully (Status: READY)")
            if (!initDeferred.isCompleted) {
                initDeferred.complete(true)
            }
        } else {
            _status.value = TtsStatus.ERROR
            lastErrorMessage = "onInit returned error code $status"
            Log.e("UpiSoundbox", "TextToSpeech onInit failed with status code $status")
            if (!initDeferred.isCompleted) {
                initDeferred.complete(false)
            }
            scheduleRetry()
        }
    }

    private fun scheduleRetry() {
        if (retryCount < maxRetries) {
            retryCount++
            val backoffMs = retryCount * 2000L
            Log.i("UpiSoundbox", "Scheduling TTS auto-recovery retry $retryCount in ${backoffMs}ms...")
            scope.launch {
                delay(backoffMs)
                reinitialize()
            }
        } else {
            _status.value = TtsStatus.UNAVAILABLE
            Log.e("UpiSoundbox", "TTS initialization reached max retries. Engine unavailable.")
        }
    }

    fun reinitialize() {
        scope.launch {
            initMutex.withLock {
                try {
                    _status.value = TtsStatus.RETRYING
                    Log.i("UpiSoundbox", ">>> Reinitializing TextToSpeech engine...")
                    tts?.stop()
                    tts?.shutdown()
                } catch (e: Exception) {
                    Log.w("UpiSoundbox", "Error shutting down stale TTS engine", e)
                } finally {
                    tts = null
                }
                doInitializeLocked()
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
            withTimeoutOrNull(8000L) { initDeferred.await() } ?: false
        }

        if (!ready || tts == null) {
            Log.e("UpiSoundbox", "TTS speak called but engine not ready (ready=$ready, tts=$tts)")
            reinitialize()
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

        val targetLocale = if (request.language.equals("hi", ignoreCase = true)) {
            Locale("hi", "IN")
        } else {
            Locale("en", "IN")
        }

        val langResult = engine.setLanguage(targetLocale)
        if (langResult == TextToSpeech.LANG_MISSING_DATA || langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
            Log.w("UpiSoundbox", "Language $targetLocale not supported, falling back to default English")
            engine.language = Locale.ENGLISH
        } else {
            // Apply high quality Indian acoustic voice profile if available
            selectBestVoice(engine, targetLocale)
        }

        engine.setSpeechRate(request.speechRate.coerceIn(0.5f, 2.0f))
        engine.setPitch(request.speechPitch.coerceIn(0.5f, 2.0f))

        val utteranceId = request.id
        val completionDeferred = CompletableDeferred<Boolean>()

        var focusRequest: AudioFocusRequest? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && audioManager != null) {
            val playbackAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build()

            focusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK)
                .setAudioAttributes(playbackAttributes)
                .setAcceptsDelayedFocusGain(false)
                .build()

            val focusResult = audioManager.requestAudioFocus(focusRequest)
            Log.d("UpiSoundbox", "Audio focus requested (ducking enabled): result=$focusResult")
        }

        engine.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(id: String?) {
                if (id == utteranceId) {
                    _status.value = TtsStatus.SPEAKING
                    lastSpeechTime = System.currentTimeMillis()
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
                    lastErrorMessage = "Utterance playback error on id $id"
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
        try {
            val result = engine.speak(request.text, TextToSpeech.QUEUE_FLUSH, params, utteranceId)
            if (result != TextToSpeech.SUCCESS) {
                _status.value = TtsStatus.ERROR
                lastErrorMessage = "engine.speak returned error code $result"
                Log.e("UpiSoundbox", "engine.speak returned error code $result")
                abandonFocus(focusRequest)
                reinitialize()
                return false
            }
        } catch (e: Exception) {
            Log.e("UpiSoundbox", "Exception during engine.speak", e)
            _status.value = TtsStatus.ERROR
            lastErrorMessage = e.localizedMessage
            abandonFocus(focusRequest)
            reinitialize()
            return false
        }

        return withTimeoutOrNull(15000L) { completionDeferred.await() } ?: run {
            Log.w("UpiSoundbox", "TTS speech timed out after 15s")
            abandonFocus(focusRequest)
            false
        }
    }

    private fun selectBestVoice(engine: TextToSpeech, targetLocale: Locale) {
        try {
            val availableVoices = engine.voices ?: return
            val matchingVoices = availableVoices.filter {
                it.locale.language.equals(targetLocale.language, ignoreCase = true) &&
                        !it.isNetworkConnectionRequired
            }

            if (matchingVoices.isNotEmpty()) {
                val selectedVoice = matchingVoices.firstOrNull { it.name.contains("local", ignoreCase = true) }
                    ?: matchingVoices.first()
                engine.voice = selectedVoice
                Log.d("UpiSoundbox", "Applied high-quality acoustic voice: ${selectedVoice.name} (locale=${selectedVoice.locale})")
            }
        } catch (e: Exception) {
            Log.w("UpiSoundbox", "Could not query or apply custom voice profile", e)
        }
    }

    private fun abandonFocus(focusRequest: AudioFocusRequest?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && focusRequest != null && audioManager != null) {
            audioManager.abandonAudioFocusRequest(focusRequest)
        }
    }

    private fun getBestTtsEngine(): String? {
        val engines = try {
            val pm = context.packageManager
            val intent = android.content.Intent(TextToSpeech.Engine.INTENT_ACTION_TTS_SERVICE)
            pm.queryIntentServices(intent, 0).map { it.serviceInfo.packageName }
        } catch (e: Exception) {
            emptyList()
        }

        return when {
            engines.contains("com.google.android.tts") -> "com.google.android.tts"
            engines.isNotEmpty() -> engines.first()
            else -> null
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
