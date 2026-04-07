package com.familymealplanner.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "onboarding_prefs")

@Singleton
class OnboardingPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val HOUSEHOLD_NAME = stringPreferencesKey("household_name")
    private val ONBOARDING_COMPLETED = stringPreferencesKey("onboarding_completed")
    private val PENDING_ONBOARDING_COMPLETION = stringPreferencesKey("pending_onboarding_completion")
    private val FIRST_LAUNCH_AFTER_ONBOARDING = stringPreferencesKey("first_launch_after_onboarding")
    private val OPENAI_API_KEY = stringPreferencesKey("openai_api_key")
    private val TEXT_SCALE = stringPreferencesKey("text_scale")
    private val APP_FONT = stringPreferencesKey("app_font")
    private val ACCENT_COLOR_LIGHT = stringPreferencesKey("accent_color_light")
    private val ACCENT_COLOR_DARK = stringPreferencesKey("accent_color_dark")
    private val DEFAULT_SERVING_SIZE = stringPreferencesKey("default_serving_size")
    private val VOICE_INPUT_TUTORIAL_SHOWN = stringPreferencesKey("voice_input_tutorial_shown")
    private val PCS_WARNING_DISMISSED = stringPreferencesKey("pcs_warning_dismissed")
    private val CUSTOM_GROCERY_HEADER = stringPreferencesKey("custom_grocery_header")

    val householdName: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[HOUSEHOLD_NAME]
    }

    val hasCompletedOnboarding: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[ONBOARDING_COMPLETED] == "true"
    }
    
    val hasPendingOnboardingCompletion: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PENDING_ONBOARDING_COMPLETION] == "true"
    }
    
    val isFirstLaunchAfterOnboarding: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[FIRST_LAUNCH_AFTER_ONBOARDING] == "true"
    }

    val openAiApiKey: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[OPENAI_API_KEY]
    }
    
    val textScale: Flow<Float> = context.dataStore.data.map { preferences ->
        preferences[TEXT_SCALE]?.toFloatOrNull() ?: 0.95f
    }
    
    val appFont: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[APP_FONT] ?: "Roboto Medium"
    }
    
    val accentColorLight: Flow<Long> = context.dataStore.data.map { preferences ->
        preferences[ACCENT_COLOR_LIGHT]?.toLongOrNull() ?: 0xFFD68C45 // Toasted Almond default
    }
    
    val accentColorDark: Flow<Long> = context.dataStore.data.map { preferences ->
        preferences[ACCENT_COLOR_DARK]?.toLongOrNull() ?: 0xFF5398be // Blue Bell default
    }
    
    val defaultServingSize: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[DEFAULT_SERVING_SIZE]?.toIntOrNull() ?: 2 // Default to 2 servings
    }
    
    val voiceInputTutorialShown: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[VOICE_INPUT_TUTORIAL_SHOWN] == "true"
    }
    
    val pcsWarningDismissed: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PCS_WARNING_DISMISSED] == "true"
    }
    
    val customGroceryHeader: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[CUSTOM_GROCERY_HEADER]
    }

    suspend fun getOpenAiApiKeySync(): String? {
        return context.dataStore.data.first()[OPENAI_API_KEY]
    }

    suspend fun setOpenAiApiKey(apiKey: String?) {
        context.dataStore.edit { preferences ->
            if (apiKey.isNullOrBlank()) {
                preferences.remove(OPENAI_API_KEY)
            } else {
                preferences[OPENAI_API_KEY] = apiKey
            }
        }
    }
    
    suspend fun setTextScale(scale: Float) {
        context.dataStore.edit { preferences ->
            preferences[TEXT_SCALE] = scale.toString()
        }
    }
    
    suspend fun setAppFont(fontName: String) {
        context.dataStore.edit { preferences ->
            preferences[APP_FONT] = fontName
        }
    }
    
    suspend fun setAccentColorLight(color: Long) {
        context.dataStore.edit { preferences ->
            preferences[ACCENT_COLOR_LIGHT] = color.toString()
        }
    }
    
    suspend fun setAccentColorDark(color: Long) {
        context.dataStore.edit { preferences ->
            preferences[ACCENT_COLOR_DARK] = color.toString()
        }
    }
    
    suspend fun setDefaultServingSize(servingSize: Int) {
        context.dataStore.edit { preferences ->
            preferences[DEFAULT_SERVING_SIZE] = servingSize.toString()
        }
    }
    
    suspend fun setHouseholdName(name: String?) {
        context.dataStore.edit { preferences ->
            if (name.isNullOrBlank()) {
                preferences.remove(HOUSEHOLD_NAME)
            } else {
                preferences[HOUSEHOLD_NAME] = name
            }
        }
    }
    
    suspend fun setVoiceInputTutorialShown() {
        context.dataStore.edit { preferences ->
            preferences[VOICE_INPUT_TUTORIAL_SHOWN] = "true"
        }
    }
    
    suspend fun setPcsWarningDismissed() {
        context.dataStore.edit { preferences ->
            preferences[PCS_WARNING_DISMISSED] = "true"
        }
    }
    
    suspend fun setCustomGroceryHeader(header: String?) {
        context.dataStore.edit { preferences ->
            if (header.isNullOrBlank()) {
                preferences.remove(CUSTOM_GROCERY_HEADER)
            } else {
                // Limit to 100 characters
                val trimmedHeader = header.take(100)
                preferences[CUSTOM_GROCERY_HEADER] = trimmedHeader
            }
        }
    }

    suspend fun setOnboardingCompleted() {
        context.dataStore.edit { preferences ->
            preferences[ONBOARDING_COMPLETED] = "true"
            preferences[PENDING_ONBOARDING_COMPLETION] = "false" // Clear pending flag
            preferences[FIRST_LAUNCH_AFTER_ONBOARDING] = "true" // Set flag for first launch
        }
    }
    
    suspend fun setPendingOnboardingCompletion(pending: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PENDING_ONBOARDING_COMPLETION] = pending.toString()
        }
    }
    
    suspend fun clearFirstLaunchFlag() {
        context.dataStore.edit { preferences ->
            preferences[FIRST_LAUNCH_AFTER_ONBOARDING] = "false"
        }
    }

    suspend fun clearOnboarding() {
        context.dataStore.edit { preferences ->
            preferences.remove(HOUSEHOLD_NAME)
            preferences.remove(ONBOARDING_COMPLETED)
        }
    }
}
