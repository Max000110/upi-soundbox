package com.upisoundbox.speech

import android.util.Log
import com.upisoundbox.domain.model.SpeechRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SpeechQueue(
    private val speechEngine: SpeechEngine,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default + Job())
) {
    private val channel = Channel<SpeechRequest>(capacity = 50)

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing.asStateFlow()

    private var workerJob: Job? = null

    init {
        startWorker()
    }

    private fun startWorker() {
        workerJob?.cancel()
        workerJob = scope.launch {
            for (request in channel) {
                _isProcessing.value = true
                Log.d("UpiSoundbox", ">>> SpeechQueue dequeued request: '${request.text}'")
                try {
                    var success = false
                    for (attempt in 1..3) {
                        success = speechEngine.speak(request)
                        Log.d("UpiSoundbox", ">>> SpeechQueue speak attempt $attempt result: $success")
                        if (success) break
                        delay(600L)
                    }
                } catch (e: Exception) {
                    Log.e("UpiSoundbox", "Error in speech worker", e)
                } finally {
                    _isProcessing.value = false
                }
            }
        }
    }

    fun enqueue(request: SpeechRequest): Boolean {
        val enqueued = channel.trySend(request).isSuccess
        Log.d("UpiSoundbox", "SpeechQueue.enqueue(id=${request.id}): enqueued=$enqueued")
        return enqueued
    }

    fun stopCurrent() {
        speechEngine.stop()
    }
}
