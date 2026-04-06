package com.familymealplanner.domain.usecase

import android.graphics.Bitmap
import com.familymealplanner.data.local.ImageStorage
import com.familymealplanner.data.local.TranslationSystem
import com.familymealplanner.data.remote.ScrapedRecipe
import com.familymealplanner.data.remote.ScrapedIngredient
import com.familymealplanner.domain.model.Meal
import com.familymealplanner.domain.model.UnitConversion
import com.familymealplanner.domain.repository.IngredientRepository
import com.familymealplanner.domain.repository.MealRepository
import com.familymealplanner.domain.util.IngredientMatcher
import java.util.UUID
import javax.inject.Inject

class CreateScrapedMealUseCase @Inject constructor(
    private val mealRepository: MealRepository,
    private val ingredientRepository: IngredientRepository,
    private val imageStorage: ImageStorage,
    private val ingredientMatcher: IngredientMatcher,
    private val translationSystem: TranslationSystem
) {
    /**
     * Create a meal from scraped recipe data.
     * @param scrapedRecipe The scraped recipe data
     * @param finalName The final name for the meal
     * @param mealType The meal type category
     * @param dishCategory The dish category
     * @param dishImage Optional bitmap of the dish photo to save
     */
    suspend operator fun invoke(
        scrapedRecipe: ScrapedRecipe,
        finalName: String,
        mealType: com.familymealplanner.domain.model.MealType? = null,
        dishCategory: com.familymealplanner.domain.model.DishCategory? = null,
        dishImage: Bitmap? = null
    ): Result<Meal> {
        val now = System.currentTimeMillis()
        val mealId = UUID.randomUUID().toString()
        
        // Save dish image if provided
        val imagePath = dishImage?.let { imageStorage.saveBitmap(it, mealId) }
        
        // Create ingredients that don't exist and collect ingredient IDs with quantities and star status
        val ingredientInputs = mutableListOf<com.familymealplanner.domain.repository.MealIngredientInput>()
        
        for (scrapedIngredient in scrapedRecipe.ingredients) {
            // Convert to storage unit (g or ml) if needed
            val (storageQuantity, storageUnit) = convertToStorageUnit(
                scrapedIngredient.quantity, 
                scrapedIngredient.unit
            )
            
            // Try to find existing ingredient by name
            val existingIngredient = ingredientRepository.getIngredientByName(scrapedIngredient.name)
            
            val ingredientId = if (existingIngredient != null) {
                existingIngredient.id
            } else {
                // For scraped recipes, ingredient names are already in the user's language
                // Use fuzzy matching directly without translation
                val matchResult = ingredientMatcher.findMatch(scrapedIngredient.name, threshold = 0.6)
                val catalogIngredient = matchResult?.catalogIngredient
                
                val category = catalogIngredient?.category?.displayName ?: "Other"
                val subcategory = catalogIngredient?.subcategory ?: "Other"
                val allergenNames = catalogIngredient?.allergens?.map { it.displayName } ?: emptyList()
                
                // Create new ingredient with storage unit
                val createResult = ingredientRepository.createIngredient(
                    name = scrapedIngredient.name,
                    unit = storageUnit,
                    category = category,
                    subcategory = subcategory
                )
                
                if (createResult.isFailure) {
                    throw createResult.exceptionOrNull() ?: Exception("Failed to create ingredient")
                }
                
                val newIngredient = createResult.getOrThrow()
                
                // Update ingredient with allergens if any
                if (allergenNames.isNotEmpty()) {
                    ingredientRepository.updateIngredient(newIngredient, allergenNames)
                }
                
                newIngredient.id
            }
            
            ingredientInputs.add(
                com.familymealplanner.domain.repository.MealIngredientInput(
                    ingredientId = ingredientId,
                    quantity = storageQuantity,
                    isStarIngredient = scrapedIngredient.isStarIngredient
                )
            )
        }
        
        // Create the meal
        val meal = Meal(
            id = mealId,
            name = finalName,
            instructions = scrapedRecipe.instructions,
            simpleInstructions = scrapedRecipe.simpleInstructions,
            prepTimeMinutes = scrapedRecipe.prepTimeMinutes,
            cookTimeMinutes = scrapedRecipe.cookTimeMinutes,
            servings = scrapedRecipe.servings,
            isScraped = true,
            mealType = mealType,
            dishCategory = dishCategory,
            imagePath = imagePath,
            createdAt = now,
            updatedAt = now
        )
        
        return mealRepository.createMeal(meal, ingredientInputs).map { meal }
    }
    
    /**
     * Convert a quantity and unit to storage format (g, ml, or pcs).
     * Handles unit normalization for external/API units.
     */
    private fun convertToStorageUnit(quantity: Double, unit: String): Pair<Double, String> {
        // Convert to storage unit using UnitConversion (handles normalization)
        return UnitConversion.toStorageUnit(quantity, unit) ?: (quantity to unit)
    }
}
