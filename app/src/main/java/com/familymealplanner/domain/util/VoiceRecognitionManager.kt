package com.familymealplanner.domain.util

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import com.familymealplanner.data.preferences.LocaleManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Manages Android SpeechRecognizer lifecycle and provides clean interface for voice recognition.
 * 
 * Features:
 * - StateFlow-based state management for reactive UI updates
 * - Automatic silence timeout (20 seconds)
 * - User-friendly error messages
 * - Proper resource cleanup
 * - Multi-language support based on app locale
 * 
 * Usage:
 * ```
 * val manager = VoiceRecognitionManager(context, localeManager)
 * manager.recognitionState.collect { state ->
 *     when (state) {
 *         is RecognitionState.Listening -> // Show recording UI
 *         is RecognitionState.Transcribing -> // Update transcription text
 *         is RecognitionState.Completed -> // Process final text
 *         is RecognitionState.Error -> // Show error message
 *     }
 * }
 * manager.startListening()
 * ```
 */
class VoiceRecognitionManager(
    private val context: Context,
    private val localeManager: LocaleManager
) {
    companion object {
        const val MAX_SILENCE_DURATION_MS = 5_000L  // 5 seconds of silence
        const val ERROR_NO_MATCH = SpeechRecognizer.ERROR_NO_MATCH
        const val ERROR_NETWORK = SpeechRecognizer.ERROR_NETWORK
        const val ERROR_AUDIO = SpeechRecognizer.ERROR_AUDIO
        const val ERROR_INSUFFICIENT_PERMISSIONS = SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS
    }

    /**
     * Represents the current state of voice recognition.
     */
    sealed class RecognitionState {
        /** Initial state, not recording */
        object Idle : RecognitionState()
        
        /** Recording started, waiting for speech */
        object Listening : RecognitionState()
        
        /** Speech detected, transcribing in real-time */
        data class Transcribing(val partialText: String) : RecognitionState()
        
        /** Recording completed successfully */
        data class Completed(val finalText: String) : RecognitionState()
        
        /** Error occurred during recognition */
        data class Error(val errorCode: Int, val message: String) : RecognitionState()
    }

    private val _recognitionState = MutableStateFlow<RecognitionState>(RecognitionState.Idle)
    val recognitionState: StateFlow<RecognitionState> = _recognitionState.asStateFlow()

    private var speechRecognizer: SpeechRecognizer? = null
    private var silenceTimeoutJob: Job? = null
    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    
    private var accumulatedText = StringBuilder()

    /**
     * Check if speech recognition is available on this device.
     */
    fun isRecognitionAvailable(): Boolean {
        return SpeechRecognizer.isRecognitionAvailable(context)
    }

    /**
     * Start listening for voice input.
     * Initializes SpeechRecognizer lazily on first use and begins recognition.
     * The SpeechRecognizer instance is reused across sessions for better performance.
     */
    fun startListening() {
        // Check if speech recognition is available
        if (!isRecognitionAvailable()) {
            _recognitionState.value = RecognitionState.Error(
                SpeechRecognizer.ERROR_CLIENT,
                "Voice recognition is not available on this device. Please ensure Google services are installed."
            )
            return
        }
        
        // Cancel any existing recognition
        cancelListening()
        
        // Reset accumulated text
        accumulatedText.clear()
        
        // Initialize SpeechRecognizer lazily on first use
        // Reuse existing instance for better performance (NFR-1.1)
        if (speechRecognizer == null) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
            speechRecognizer?.setRecognitionListener(createRecognitionListener())
        }
        
        // Create recognition intent with current app language
        val languageCode = localeManager.getLanguage()
        val recognitionLocale = mapLanguageCodeToRecognitionLocale(languageCode)
        
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, recognitionLocale)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }
        
        // Start recognition
        speechRecognizer?.startListening(intent)
        _recognitionState.value = RecognitionState.Listening
        
        // Start silence timeout
        startSilenceTimeout()
    }

    /**
     * Stop listening and process final results.
     * Triggers onResults callback with accumulated transcription.
     */
    fun stopListening() {
        cancelSilenceTimeout()
        speechRecognizer?.stopListening()
    }

    /**
     * Cancel listening and reset to idle state.
     * Discards any accumulated transcription.
     */
    fun cancelListening() {
        cancelSilenceTimeout()
        speechRecognizer?.cancel()
        accumulatedText.clear()
        _recognitionState.value = RecognitionState.Idle
    }

    /**
     * Clean up resources.
     * Should be called when the manager is no longer needed.
     */
    fun destroy() {
        cancelSilenceTimeout()
        speechRecognizer?.destroy()
        speechRecognizer = null
        coroutineScope.cancel()
    }

    /**
     * Start the silence timeout coroutine.
     * Automatically stops listening after MAX_SILENCE_DURATION_MS.
     */
    private fun startSilenceTimeout() {
        silenceTimeoutJob = coroutineScope.launch {
            delay(MAX_SILENCE_DURATION_MS)
            stopListening()
        }
    }

    /**
     * Cancel the silence timeout coroutine.
     */
    private fun cancelSilenceTimeout() {
        silenceTimeoutJob?.cancel()
        silenceTimeoutJob = null
    }

    /**
     * Create a RecognitionListener that updates the state flow.
     */
    private fun createRecognitionListener(): RecognitionListener {
        return object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                // Recognition is ready, already in Listening state
            }

            override fun onBeginningOfSpeech() {
                // Speech detected, cancel silence timeout
                cancelSilenceTimeout()
            }

            override fun onRmsChanged(rmsdB: Float) {
                // Audio level changed, not used
            }

            override fun onBufferReceived(buffer: ByteArray?) {
                // Audio buffer received, not used
            }

            override fun onEndOfSpeech() {
                // Speech ended, waiting for results
            }

            override fun onError(error: Int) {
                cancelSilenceTimeout()
                val errorMessage = mapErrorCodeToMessage(error)
                _recognitionState.value = RecognitionState.Error(error, errorMessage)
            }

            override fun onResults(results: Bundle?) {
                cancelSilenceTimeout()
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val finalText = matches?.firstOrNull() ?: accumulatedText.toString()
                _recognitionState.value = RecognitionState.Completed(finalText)
            }

            override fun onPartialResults(partialResults: Bundle?) {
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val partialText = matches?.firstOrNull() ?: ""
                
                if (partialText.isNotEmpty()) {
                    accumulatedText.clear()
                    accumulatedText.append(partialText)
                    _recognitionState.value = RecognitionState.Transcribing(partialText)
                }
            }

            override fun onEvent(eventType: Int, params: Bundle?) {
                // Custom events, not used
            }
        }
    }

    /**
     * Map Android SpeechRecognizer error codes to user-friendly messages.
     */
    private fun mapErrorCodeToMessage(errorCode: Int): String {
        return when (errorCode) {
            SpeechRecognizer.ERROR_NO_MATCH -> 
                "No speech detected. Please try again."
            SpeechRecognizer.ERROR_NETWORK, SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> 
                "Voice recognition requires internet connection."
            SpeechRecognizer.ERROR_AUDIO -> 
                "Microphone not available. Please check device."
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> 
                "Microphone permission required."
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> 
                "Voice recognition is busy. Please try again."
            SpeechRecognizer.ERROR_SERVER -> 
                "Voice recognition service error. Please try again."
            SpeechRecognizer.ERROR_CLIENT -> 
                "Voice recognition not available. This may occur on emulators without Google services. Try on a physical device."
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> 
                "No speech detected. Please try again."
            else -> 
                "Voice recognition failed. Please try again."
        }
    }

    /**
     * Maps app language codes to Android speech recognition locale strings.
     * 
     * @param languageCode The app language code (e.g., "en", "ru", "ro")
     * @return The locale string for speech recognition (e.g., "en-US", "ru-RU", "ro-RO")
     */
    private fun mapLanguageCodeToRecognitionLocale(languageCode: String): String {
        return when (languageCode) {
            "en" -> "en-US"
            "ru" -> "ru-RU"
            "ro" -> "ro-RO"
            else -> "en-US" // Default to English if unknown language
        }
    }
}
