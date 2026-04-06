package com.familymealplanner.data.local

import android.util.Log
import com.familymealplanner.data.local.dao.IngredientDao
import com.familymealplanner.data.local.dao.IngredientSubstituteDao
import com.familymealplanner.data.local.entity.IngredientSubstituteEntity
import com.familymealplanner.domain.model.IngredientSubstitutions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubstituteInitializer @Inject constructor(
    private val ingredientDao: IngredientDao,
    private val substituteDao: IngredientSubstituteDao
) {
    companion object {
        private const val TAG = "SubstituteInitializer"
    }

    /**
     * Initialize substitutes in the database if they don't already exist.
     * Only creates substitutes where both the ingredient and substitute exist in the database.
     * 
     * @param forceCheck If true, always checks and adds missing substitutes even if some already exist
     */
    suspend fun initialize(forceCheck: Boolean = false) = withContext(Dispatchers.IO) {
        try {
            // Check if we already have substitutes (skip only if not forcing)
            if (!forceCheck) {
                val existingSubstitutes = substituteDao.getAll()
                if (existingSubstitutes.isNotEmpty()) {
                    Log.d(TAG, "Substitutes already initialized (${existingSubstitutes.size} found)")
                    return@withContext
                }
            }
            
            val allSubstitutions = IngredientSubstitutions.getAllSubstitutions()
            var successCount = 0
            var skipCount = 0
            val missingIngredients = mutableSetOf<String>()

            for ((ingredientName, substituteName) in allSubstitutions) {
                // Try case-insensitive matching
                val ingredient = ingredientDao.getByName(ingredientName)
                val substitute = ingredientDao.getByName(substituteName)

                if (ingredient != null && substitute != null) {
                    // Check if this substitute relationship already exists
                    val existing = substituteDao.getByIngredientId(ingredient.id)
                        .find { it.substituteId == substitute.id }
                    
                    if (existing == null) {
                        val substituteEntity = IngredientSubstituteEntity(
                            id = UUID.randomUUID().toString(),
                            ingredientId = ingredient.id,
                            substituteId = substitute.id,
                            notes = "Common substitution",
                            createdAt = System.currentTimeMillis()
                        )
                        
                        try {
                            substituteDao.insert(substituteEntity)
                            successCount++
                        } catch (e: Exception) {
                            Log.w(TAG, "Failed to insert substitute: $ingredientName -> $substituteName", e)
                        }
                    } else {
                        skipCount++
                    }
                } else {
                    skipCount++
                    if (ingredient == null) missingIngredients.add(ingredientName)
                    if (substitute == null) missingIngredients.add(substituteName)
                }
            }

            if (successCount > 0 || missingIngredients.isNotEmpty()) {
                if (missingIngredients.isNotEmpty() && missingIngredients.size <= 20) {
                    Log.d(TAG, "Missing ingredients (sample): ${missingIngredients.take(20).joinToString(", ")}")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing substitutes", e)
        }
    }
}
