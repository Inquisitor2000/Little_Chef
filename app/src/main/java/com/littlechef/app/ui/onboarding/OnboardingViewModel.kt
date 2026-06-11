package com.littlechef.app.ui.onboarding

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.littlechef.app.data.preferences.LocaleManager
import com.littlechef.app.data.preferences.OnboardingPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

sealed class OnboardingStep {
    data object LanguageSelection : OnboardingStep()
    data object Welcome : OnboardingStep()
    data object ServingSize : OnboardingStep()
    data object Complete : OnboardingStep()
}

data class OnboardingState(
    val currentStep: OnboardingStep = OnboardingStep.LanguageSelection,
    val selectedLanguage: String = "en",
    val selectedServingSize: Int = 2,
    val isLoading: Boolean = false,
    val error: String? = null,
    val localeVersion: Int = 0 // Increment to force recomposition
)

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val onboardingPreferences: OnboardingPreferences,
    private val localeManager: LocaleManager
) : ViewModel() {

    companion object {
        private const val TAG = "OnboardingViewModel"
    }

    private val _state = MutableStateFlow(OnboardingState())
    val state: StateFlow<OnboardingState> = _state.asStateFlow()

    init {
        // ViewModel initialized
    }

    fun setLanguage(languageCode: String) {
        // Only update if language actually changed
        if (_state.value.selectedLanguage == languageCode) {
            return
        }
        
        // Update state immediately for UI responsiveness
        _state.update { 
            it.copy(
                selectedLanguage = languageCode,
                localeVersion = it.localeVersion + 1
            ) 
        }
        
        // Do I/O work in background
        viewModelScope.launch {
            localeManager.setLanguage(languageCode)
            
            val locale = Locale(languageCode)
            Locale.setDefault(locale)
        }
    }

    fun nextStep() {
        val previousStep = _state.value.currentStep
        _state.update { 
            it.copy(
                currentStep = when (it.currentStep) {
                    OnboardingStep.LanguageSelection -> {
                        OnboardingStep.Welcome
                    }
                    OnboardingStep.Welcome -> OnboardingStep.ServingSize
                    OnboardingStep.ServingSize -> OnboardingStep.Complete
                    OnboardingStep.Complete -> OnboardingStep.Complete
                }
            )
        }
    }

    fun setServingSize(size: Int) {
        _state.update { it.copy(selectedServingSize = size) }
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            // Save preferences
            
            onboardingPreferences.setDefaultServingSize(_state.value.selectedServingSize)
            
            if (shouldRecreateActivity()) {
                // Mark that we need to complete onboarding after recreation
                onboardingPreferences.setPendingOnboardingCompletion(true)
            } else {
                // No recreation needed, complete immediately
                onboardingPreferences.setOnboardingCompleted()
            }
            
            // Small delay to ensure preferences are written
            delay(100)
            
            _state.update { 
                it.copy(
                    currentStep = OnboardingStep.Complete,
                    isLoading = false
                )
            }
        }
    }

    fun shouldRecreateActivity(): Boolean {
        // Recreate activity if language was changed from default
        val currentLanguage = _state.value.selectedLanguage
        return currentLanguage != "en" // Default is English
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}
