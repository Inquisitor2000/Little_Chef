package com.familymealplanner.data.local

import android.content.Context
import com.familymealplanner.domain.model.NutritionInfo
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

/**
 * Loads and caches the ingredient nutrition database from assets.
 * The data maps ingredient names (English, lowercase) to [NutritionInfo] per 100g.
 */
@Singleton
class NutritionLoader @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var cache: Map<String, NutritionInfo>? = null

    /**
     * Returns the nutrition info for a given ingredient name (case-insensitive).
     * Returns null if no data is available for that ingredient.
     */
    fun getNutrition(ingredientName: String): NutritionInfo? {
        val map = cache ?: return null
        return map[ingredientName.lowercase().trim()]
    }

    /**
     * Loads the nutrition database from the assets JSON file.
     * Safe to call multiple times — only loads once.
     */
    suspend fun load(): Map<String, NutritionInfo> = withContext(Dispatchers.IO) {
        cache?.let { return@withContext it }

        val jsonText = context.assets.open("nutrition/ingredient_nutrition.json")
            .bufferedReader()
            .use { it.readText() }

        val root = JSONObject(jsonText)
        val result = mutableMapOf<String, NutritionInfo>()

        for (key in root.keys()) {
            val obj = root.getJSONObject(key)
            val info = NutritionInfo(
                calories = obj.optDouble("kcal", 0.0),
                fatsG = obj.optDouble("fats", 0.0),
                carbsG = obj.optDouble("carbs", 0.0),
                proteinG = obj.optDouble("protein", 0.0),
                pieceG = if (obj.has("pieceG")) obj.optDouble("pieceG") else null
            )
            result[key.lowercase().trim()] = info
        }

        cache = result
        result
    }

    /** Whether the database has been loaded. */
    val isLoaded: Boolean get() = cache != null
}
