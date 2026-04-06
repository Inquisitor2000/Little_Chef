package com.familymealplanner.data.preferences

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages language preferences and locale configuration for the application.
 * 
 * This class handles:
 * - Storing and retrieving language preferences using SharedPreferences
 * - Applying locale configuration to Android contexts
 * - Triggering activity recreation when language changes
 */
@Singleton
class LocaleManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val TAG = "LocaleManager"
        private const val PREF_NAME = "app_preferences"
        private const val PREF_LANGUAGE = "app_language"
        private const val DEFAULT_LANGUAGE = "en"
    }

    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Saves the language preference to SharedPreferences.
     * 
     * @param languageCode The language code to save (e.g., "en", "ru", "ro")
     */
    fun setLanguage(languageCode: String) {
        Log.d(TAG, "Saving language: $languageCode")
        
        sharedPreferences.edit()
            .putString(PREF_LANGUAGE, languageCode)
            .apply() // Use apply() instead of commit() for async operation
        
        Log.d(TAG, "✓ Language saved: $languageCode")
    }

    /**
     * Retrieves the saved language preference from SharedPreferences.
     * 
     * @return The saved language code, or "en" if no preference is saved
     */
    fun getLanguage(): String {
        val language = sharedPreferences.getString(PREF_LANGUAGE, DEFAULT_LANGUAGE) ?: DEFAULT_LANGUAGE
        return language
    }

    /**
     * Creates a new context with the configured locale applied.
     * 
     * This method should be called in Activity.attachBaseContext() to ensure
     * the locale is applied before the activity's views are created.
     * 
     * @param context The base context to apply the locale to
     * @return A new context with the configured locale
     */
    fun applyLocale(context: Context): Context {
        val languageCode = getLanguage()
        
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val configuration = context.resources.configuration
        configuration.setLocale(locale)

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.createConfigurationContext(configuration)
        } else {
            @Suppress("DEPRECATION")
            context.resources.updateConfiguration(configuration, context.resources.displayMetrics)
            context
        }
    }

    /**
     * Recreates the given activity to apply locale changes.
     * 
     * This should be called after changing the language preference to ensure
     * all UI elements are updated with the new language.
     * 
     * @param activity The activity to recreate
     */
    fun recreateActivity(activity: Activity) {
        Log.d(TAG, "recreateActivity() called for activity: ${activity.javaClass.simpleName}")
        activity.recreate()
    }
}
