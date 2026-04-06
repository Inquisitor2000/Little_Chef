package com.familymealplanner.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.familymealplanner.data.preferences.OnboardingPreferences
import com.familymealplanner.domain.model.VoiceIngredientItem
import com.familymealplanner.domain.util.IngredientMatcher
import com.familymealplanner.domain.util.VoiceInputParser
import com.familymealplanner.domain.util.VoiceRecognitionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for voice input feature.
 * 
 * New simplified flow:
 * 1. User speaks ingredient names only (no quantities/units)
 * 2. Voice recognition continues until stop button or 5 seconds of silence
 * 3. Parse transcription to extract ingredient names
 * 4. Match each name against catalog
 * 5. Return list of VoiceIngredientItem with default units and quantity=1
 */
@HiltViewModel
class VoiceInputViewModel @Inject constructor(
    private val voiceRecognitionManager: VoiceRecognitionManager,
    private val ingredientMatcher: IngredientMatcher,
    private val onboardingPreferences: OnboardingPreferences
) : ViewModel() {

    /**
     * UI state for voice input screen.
     * 
     * @param isRecording True if currently recording
     * @param transcription Current transcription text
     * @param elapsedTime Elapsed recording time in seconds
     * @param error Error message if any
     * @param isProcessing True if processing transcription
     * @param showTutorial True if tutorial should be shown
     */
    data class VoiceInputState(
        val isRecording: Boolean = false,
        val transcription: String = "",
        val elapsedTime: Long = 0,
        val error: String? = null,
        val isProcessing: Boolean = false,
        val showTutorial: Boolean = false
    )

    private val _uiState = MutableStateFlow(VoiceInputState())
    val uiState: StateFlow<VoiceInputState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    init {
        // Check if tutorial should be shown
        viewModelScope.launch {
            onboardingPreferences.voiceInputTutorialShown.collect { tutorialShown ->
                _uiState.value = _uiState.value.copy(showTutorial = !tutorialShown)
            }
        }
        
        // Observe voice recognition state and update UI state
        viewModelScope.launch {
            voiceRecognitionManager.recognitionState.collect { recognitionState ->
                when (recognitionState) {
                    is VoiceRecognitionManager.RecognitionState.Idle -> {
                        _uiState.value = _uiState.value.copy(
                            isRecording = false,
                            isProcessing = false,
                            error = null
                        )
                    }
                    is VoiceRecognitionManager.RecognitionState.Listening -> {
                        _uiState.value = _uiState.value.copy(
                            isRecording = true,
                            error = null
                        )
                    }
                    is VoiceRecognitionManager.RecognitionState.Transcribing -> {
                        _uiState.value = _uiState.value.copy(
                            isRecording = true,
                            transcription = recognitionState.partialText,
                            error = null
                        )
                    }
                    is VoiceRecognitionManager.RecognitionState.Completed -> {
                        _uiState.value = _uiState.value.copy(
                            isRecording = false,
                            transcription = recognitionState.finalText,
                            error = null
                        )
                        stopTimer()
                    }
                    is VoiceRecognitionManager.RecognitionState.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isRecording = false,
                            error = recognitionState.message,
                            isProcessing = false
                        )
                        stopTimer()
                    }
                }
            }
        }
    }

    /**
     * Start voice recording.
     * Initializes voice recognition and starts the timer.
     */
    fun startRecording() {
        voiceRecognitionManager.startListening()
        startTimer()
    }

    /**
     * Stop voice recording.
     * Stops voice recognition and the timer.
     */
    fun stopRecording() {
        voiceRecognitionManager.stopListening()
        stopTimer()
    }

    /**
     * Cancel voice recording.
     * Cancels voice recognition, stops the timer, and resets state.
     */
    fun cancelRecording() {
        voiceRecognitionManager.cancelListening()
        stopTimer()
        _uiState.value = VoiceInputState()
    }

    /**
     * Start the recording timer.
     * Updates elapsedTime every second.
     */
    private fun startTimer() {
        // Cancel any existing timer
        timerJob?.cancel()
        
        // Reset elapsed time
        _uiState.value = _uiState.value.copy(elapsedTime = 0)
        
        // Start new timer
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000) // Wait 1 second
                _uiState.value = _uiState.value.copy(
                    elapsedTime = _uiState.value.elapsedTime + 1
                )
            }
        }
    }

    /**
     * Stop the recording timer.
     */
    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    /**
     * Process the transcription and match ingredients.
     * 
     * New simplified flow:
     * 1. Parse transcription to extract ingredient names
     * 2. For each name, try to match against catalog
     * 3. Return VoiceIngredientItem with matched catalog data or unrecognized flag
     * 4. Each item has quantity=1 by default (user will adjust in review screen)
     * 
     * @return List of VoiceIngredientItem ready for quantity selection
     */
    fun processTranscription(): List<VoiceIngredientItem> {
        val transcription = _uiState.value.transcription
        
        if (transcription.isBlank()) {
            return emptyList()
        }
        
        _uiState.value = _uiState.value.copy(isProcessing = true)
        
        try {
            // Parse transcription into ingredient names
            val parsedIngredients = VoiceInputParser.parse(transcription)
            
            // Match each ingredient name to catalog
            val ingredientItems = parsedIngredients
                .filter { it.name.isNotBlank() }
                .map { parsedIngredient ->
                    // Try to match to catalog
                    val matchResult = ingredientMatcher.findMatch(parsedIngredient.name)
                    
                    VoiceIngredientItem(
                        parsedName = parsedIngredient.name,
                        matchedIngredient = matchResult?.catalogIngredient,
                        quantity = 1, // Default quantity
                        isRecognized = matchResult != null
                    )
                }
            
            return ingredientItems
        } finally {
            _uiState.value = _uiState.value.copy(isProcessing = false)
        }
    }
    
    /**
     * Dismiss the tutorial without saving preference.
     */
    fun dismissTutorial() {
        _uiState.value = _uiState.value.copy(showTutorial = false)
    }
    
    /**
     * Dismiss the tutorial and save preference to not show again.
     */
    fun dismissTutorialPermanently() {
        viewModelScope.launch {
            onboardingPreferences.setVoiceInputTutorialShown()
            _uiState.value = _uiState.value.copy(showTutorial = false)
        }
    }

    override fun onCleared() {
        super.onCleared()
        voiceRecognitionManager.destroy()
        stopTimer()
    }
}
