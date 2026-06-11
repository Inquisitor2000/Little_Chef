package com.littlechef.app.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.littlechef.app.data.preferences.LocaleManager
import com.littlechef.app.data.preferences.OnboardingPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferences: OnboardingPreferences,
    private val localeManager: LocaleManager
) : ViewModel() {

    private val _currentLanguage = MutableStateFlow(localeManager.getLanguage())
    val currentLanguage: StateFlow<String> = _currentLanguage.asStateFlow()

    val apiKey: StateFlow<String?> = preferences.openAiApiKey.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val textScale: StateFlow<Float> = preferences.textScale.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 1.0f
    )

    val appFont: StateFlow<String> = preferences.appFont.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = "Roboto Medium"
    )

    val accentColorLight: StateFlow<Long> = preferences.accentColorLight.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0xFFCB6565 // Lobster Pink
    )

    val accentColorDark: StateFlow<Long> = preferences.accentColorDark.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0xFF80A1D4 // Wisteria Blue
    )
    
    val customGroceryHeader: StateFlow<String?> = preferences.customGroceryHeader.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    fun setLanguage(languageCode: String) {
        localeManager.setLanguage(languageCode)
        _currentLanguage.value = languageCode
    }

    suspend fun saveApiKey(apiKey: String?) {
        preferences.setOpenAiApiKey(apiKey)
    }

    suspend fun saveTextScale(scale: Float) {
        preferences.setTextScale(scale)
    }

    suspend fun saveFont(fontName: String) {
        preferences.setAppFont(fontName)
    }

    suspend fun saveAccentColorLight(color: Long) {
        preferences.setAccentColorLight(color)
    }

    suspend fun saveAccentColorDark(color: Long) {
        preferences.setAccentColorDark(color)
    }
    
    suspend fun saveCustomGroceryHeader(header: String?) {
        preferences.setCustomGroceryHeader(header)
    }
}
