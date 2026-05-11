package com.littlechef.app.domain.usecase

import com.littlechef.app.domain.model.IngredientCatalog
import com.littlechef.app.domain.repository.IngredientRepository
import javax.inject.Inject

class FixIngredientCategoriesUseCase @Inject constructor(
    private val ingredientRepository: IngredientRepository
) {
    suspend operator fun invoke(): Result<Int> {
        return try {
            android.util.Log.d("FixIngredientCategories", "=== Starting ingredient category correction ===")
            
            val allIngredients = ingredientRepository.getAllIngredients()
            var correctedCount = 0
            
            android.util.Log.d("FixIngredientCategories", "Found ${allIngredients.size} ingredients to check")
            
            allIngredients.forEach { ingredient ->
                // Look up the ingredient in catalog
                val catalogIngredient = IngredientCatalog.allIngredients.find { 
                    it.nameKey.equals(ingredient.name, ignoreCase = true) 
                }
                
                if (catalogIngredient != null) {
                    val correctCategory = catalogIngredient.category.displayName
                    val correctSubcategory = catalogIngredient.subcategory
                    
                    if (ingredient.category != correctCategory || ingredient.subcategory != correctSubcategory) {
                        android.util.Log.d("FixIngredientCategories", "Correcting ${ingredient.name}:")
                        android.util.Log.d("FixIngredientCategories", "  From: ${ingredient.category} / ${ingredient.subcategory}")
                        android.util.Log.d("FixIngredientCategories", "  To: $correctCategory / $correctSubcategory")
                        
                        val correctedIngredient = ingredient.copy(
                            category = correctCategory,
                            subcategory = correctSubcategory,
                            updatedAt = System.currentTimeMillis()
                        )
                        
                        val allergenNames = ingredient.allergens.map { it.name }
                        ingredientRepository.updateIngredient(correctedIngredient, allergenNames)
                        correctedCount++
                    }
                } else {
                    android.util.Log.w("FixIngredientCategories", "No catalog entry found for: ${ingredient.name}")
                }
            }
            
            android.util.Log.d("FixIngredientCategories", "=== Correction completed: $correctedCount ingredients fixed ===")
            Result.success(correctedCount)
        } catch (e: Exception) {
            android.util.Log.e("FixIngredientCategories", "Error fixing categories", e)
            Result.failure(e)
        }
    }
}