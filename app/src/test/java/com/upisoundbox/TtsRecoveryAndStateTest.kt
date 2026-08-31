package com.upisoundbox

import com.upisoundbox.core.model.TtsStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TtsRecoveryAndStateTest {

    @Test
    fun testTtsStatusHierarchy() {
        val ready = TtsStatus.READY
        val error = TtsStatus.ERROR
        val retrying = TtsStatus.RETRYING
        val initializing = TtsStatus.INITIALIZING

        assertEquals("Ready", ready.displayName)
        assertEquals("Error", error.displayName)
        assertEquals("Retrying", retrying.displayName)
        assertEquals("Initializing", initializing.displayName)

        assertTrue(ready != initializing)
        assertTrue(error != initializing)
    }
}
