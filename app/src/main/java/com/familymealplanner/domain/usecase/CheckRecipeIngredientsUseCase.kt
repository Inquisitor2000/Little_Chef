package com.familymealplanner.domain.usecase

import com.familymealplanner.domain.model.Ingredient
import com.familymealplanner.domain.repository.IngredientRepository
import com.familymealplanner.domain.repository.InventoryRepository
import javax.inject.Inject

class CheckRecipeIngredientsUseCase @Inject constructor(
    private val inventoryRepository: InventoryRepository,
    private val ingredientRepository: IngredientRepository
) {
    data class RecipeIngredient(
        val name: String,
        val quantity: Double,
        val unit: String
    )

    data class MissingIngredient(
        val name: String,
        val required: Double,
        val available: Double,
        val unit: String,
        val substitute: Ingredient? = null
    )

    sealed class Result {
        data class AllAvailable(val ingredients: List<RecipeIngredient>) : Result()
        data class MissingIngredients(
            val missing: List<MissingIngredient>,
            val available: List<RecipeIngredient>
        ) : Result()
    }

    suspend operator fun invoke(ingredients: List<RecipeIngredient>): Result {
        val missing = mutableListOf<MissingIngredient>()
        val available = mutableListOf<RecipeIngredient>()

        for (recipeIngredient in ingredients) {
            // Try to find the ingredient in the database
            val ingredient = ingredientRepository.getIngredientByName(recipeIngredient.name)
            
            if (ingredient == null) {
                // Ingredient not in database, mark as missing
                missing.add(
                    MissingIngredient(
                        name = recipeIngredient.name,
                        required = recipeIngredient.quantity,
                        available = 0.0,
                        unit = recipeIngredient.unit,
                        substitute = null
                    )
                )
                continue
            }

            // Check available quantity
            val availableQty = inventoryRepository.getAvailableQuantity(ingredient.id)
            
            if (availableQty < recipeIngredient.quantity) {
                // Check if there's a substitute available
                val substitute = ingredient.substitutes.firstOrNull { sub ->
                    val subAvailable = inventoryRepository.getAvailableQuantity(sub.substituteIngredient.id)
                    subAvailable >= recipeIngredient.quantity
                }?.substituteIngredient

                missing.add(
                    MissingIngredient(
                        name = recipeIngredient.name,
                        required = recipeIngredient.quantity,
                        available = availableQty,
                        unit = ingredient.unit,
                        substitute = substitute
                    )
                )
            } else {
                available.add(recipeIngredient)
            }
        }

        return if (missing.isEmpty()) {
            Result.AllAvailable(ingredients)
        } else {
            Result.MissingIngredients(missing, available)
        }
    }
}
