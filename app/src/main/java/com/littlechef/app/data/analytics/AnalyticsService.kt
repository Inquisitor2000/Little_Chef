package com.littlechef.app.data.analytics

import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Central analytics service wrapping Firebase Analytics.
 *
 * All user behavior tracking goes through this single gateway so we can:
 * - Swap / augment the analytics backend later (Mixpanel, Amplitude, etc.)
 * - Enforce consistent event naming and parameter schemas
 * - Filter or sample events in one place
 *
 * Methods are designed to be no-op safe — if Firebase isn't configured
 * (no google-services.json), calls degrade gracefully and log a warning.
 */
@Singleton
class AnalyticsService @Inject constructor() {

    companion object {
        private const val TAG = "AnalyticsService"

        // ── Event names ──────────────────────────────────────────────
        // Onboarding
        const val EVENT_ONBOARDING_STARTED = "onboarding_started"
        const val EVENT_ONBOARDING_COMPLETED = "onboarding_completed"
        const val EVENT_LANGUAGE_SELECTED = "language_selected"

        // Recipes
        const val EVENT_RECIPE_SCRAPED = "recipe_scraped"
        const val EVENT_RECIPE_CREATED_MANUAL = "recipe_created_manual"
        const val EVENT_RECIPE_VIEWED = "recipe_viewed"
        const val EVENT_CUISINE_BROWSED = "cuisine_browsed"
        const val EVENT_BUNDLED_RECIPE_VIEWED = "bundled_recipe_viewed"

        // Meal planning
        const val EVENT_MEAL_SUGGESTION_VIEWED = "meal_suggestion_viewed"
        const val EVENT_SUBSTITUTE_APPLIED = "substitute_applied"

        // ── Parameter keys ───────────────────────────────────────────
        const val PARAM_SCREEN_NAME = "screen_name"
        const val PARAM_CUISINE_NAME = "cuisine_name"
        const val PARAM_RECIPE_NAME = "recipe_name"
        const val PARAM_MEAL_TYPE = "meal_type"
        const val PARAM_LANGUAGE = "language"
        const val PARAM_SOURCE = "source"
        const val PARAM_MEAL_COUNT = "meal_count"
        const val PARAM_INGREDIENT_COUNT = "ingredient_count"
        const val PARAM_INGREDIENT_NAME = "ingredient_name"
        const val PARAM_SUCCESS = "success"
        const val PARAM_ERROR_MESSAGE = "error_message"
        const val PARAM_SERVING_SIZE = "serving_size"
        const val PARAM_TOKEN_USAGE = "token_usage"

        // ── Screen names (for screen_view events) ─────────────────────
        const val SCREEN_PLAN = "plan"
        const val SCREEN_MEALS = "meals"
        const val SCREEN_GROCERIES = "groceries"
        const val SCREEN_PANTRY = "pantry"
        const val SCREEN_RECIPE_DETAIL = "recipe_detail"
        const val SCREEN_CUISINE_MEALS = "cuisine_meals"
        const val SCREEN_BUNDLED_RECIPE = "bundled_recipe"
        const val SCREEN_SCRAPE_RECIPE = "scrape_recipe"
        const val SCREEN_MANUAL_RECIPE = "manual_recipe"
        const val SCREEN_SETTINGS = "settings"
        const val SCREEN_MEAL_PLAN_DETAIL = "meal_plan_detail"
        const val SCREEN_SUGGESTION = "suggestion"
        const val SCREEN_ONBOARDING = "onboarding"
        const val SCREEN_INGREDIENT_FORM = "ingredient_form"
    }

    private var isInitialized = false
    private var analytics: FirebaseAnalytics? = null

    /**
     * Must be called once from [android.app.Application.onCreate].
     * Safe to call multiple times — subsequent calls are ignored.
     */
    fun init(application: android.app.Application) {
        if (isInitialized) return
        isInitialized = true

        analytics = try {
            FirebaseAnalytics.getInstance(application).also {
                Log.i(TAG, "Firebase Analytics initialized")
            }
        } catch (e: Exception) {
            Log.w(TAG, "Firebase Analytics unavailable — check google-services.json", e)
            null
        }
    }

    // ── Screen tracking ──────────────────────────────────────────────

    /**
     * Track a screen view. Call this from a destination-change listener
     * in the NavHost or from onResume in each screen.
     */
    fun trackScreenView(screenName: String) {
        analytics?.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            param(FirebaseAnalytics.Param.SCREEN_CLASS, screenName)
        }
    }

    // ── Onboarding ───────────────────────────────────────────────────

    fun trackOnboardingStarted(preferredLanguage: String) {
        analytics?.logEvent(EVENT_ONBOARDING_STARTED) {
            param(PARAM_LANGUAGE, preferredLanguage)
        }
    }

    fun trackOnboardingCompleted(
        selectedLanguage: String,
        servingSize: Int,
        accentColorLight: String,
        accentColorDark: String,
        appFont: String,
        textScale: Float
    ) {
        analytics?.logEvent(EVENT_ONBOARDING_COMPLETED) {
            param(PARAM_LANGUAGE, selectedLanguage)
            param(PARAM_SERVING_SIZE, servingSize.toLong())
            param("accent_color_light", accentColorLight)
            param("accent_color_dark", accentColorDark)
            param("font", appFont)
            param("text_scale", textScale.toDouble())
        }
    }

    fun trackLanguageSelected(language: String) {
        analytics?.logEvent(EVENT_LANGUAGE_SELECTED) {
            param(PARAM_LANGUAGE, language)
        }
    }

    // ── Recipes ──────────────────────────────────────────────────────

    fun trackRecipeScraped(
        recipeName: String,
        ingredientCount: Int,
        tokenUsage: Int? = null,
        success: Boolean = true,
        errorMessage: String? = null
    ) {
        analytics?.logEvent(EVENT_RECIPE_SCRAPED) {
            param(PARAM_RECIPE_NAME, recipeName)
            param(PARAM_INGREDIENT_COUNT, ingredientCount.toLong())
            tokenUsage?.let { param(PARAM_TOKEN_USAGE, it.toLong()) }
            param(PARAM_SUCCESS, if (success) 1L else 0L)
            errorMessage?.let { param(PARAM_ERROR_MESSAGE, it) }
        }
    }

    fun trackRecipeCreatedManual(
        recipeName: String,
        ingredientCount: Int,
        mealType: String? = null
    ) {
        analytics?.logEvent(EVENT_RECIPE_CREATED_MANUAL) {
            param(PARAM_RECIPE_NAME, recipeName)
            param(PARAM_INGREDIENT_COUNT, ingredientCount.toLong())
            mealType?.let { param(PARAM_MEAL_TYPE, it) }
        }
    }

    fun trackRecipeViewed(recipeName: String, source: String = "user") {
        analytics?.logEvent(EVENT_RECIPE_VIEWED) {
            param(PARAM_RECIPE_NAME, recipeName)
            param(PARAM_SOURCE, source)
        }
    }

    fun trackCuisineBrowsed(cuisineName: String) {
        analytics?.logEvent(EVENT_CUISINE_BROWSED) {
            param(PARAM_CUISINE_NAME, cuisineName)
        }
    }

    fun trackBundledRecipeViewed(recipeName: String, cuisineName: String) {
        analytics?.logEvent(EVENT_BUNDLED_RECIPE_VIEWED) {
            param(PARAM_RECIPE_NAME, recipeName)
            param(PARAM_CUISINE_NAME, cuisineName)
        }
    }

    // ── Meal planning ────────────────────────────────────────────────

    fun trackSubstituteApplied(recipeName: String, ingredientName: String) {
        analytics?.logEvent(EVENT_SUBSTITUTE_APPLIED) {
            param(PARAM_RECIPE_NAME, recipeName)
            param(PARAM_INGREDIENT_NAME, ingredientName)
        }
    }

    fun trackSuggestionViewed(mealCount: Int) {
        analytics?.logEvent(EVENT_MEAL_SUGGESTION_VIEWED) {
            param(PARAM_MEAL_COUNT, mealCount.toLong())
        }
    }

    // ── User properties ──────────────────────────────────────────────

    /**
     * Set a user property that persists across sessions.
     */
    fun setUserProperty(name: String, value: String) {
        analytics?.setUserProperty(name, value)
    }

    /**
     * Set the user's current language as a persistent property.
     */
    fun setUserLanguage(languageCode: String) {
        setUserProperty("language", languageCode)
    }
}
