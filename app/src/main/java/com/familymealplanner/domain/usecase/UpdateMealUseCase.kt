package com.familymealplanner.domain.usecase

import com.familymealplanner.domain.model.Meal
import com.familymealplanner.domain.repository.MealIngredientInput
import com.familymealplanner.domain.repository.MealRepository
import javax.inject.Inject

class UpdateMealUseCase @Inject constructor(
    private val mealRepository: MealRepository
) {
    suspend operator fun invoke(
        meal: Meal,
        name: String,
        instructions: String?,
        prepTimeMinutes: Int?,
        cookTimeMinutes: Int?,
        servings: Int?,
        mealType: com.familymealplanner.domain.model.MealType?,
        dishCategory: com.familymealplanner.domain.model.DishCategory?,
        ingredients: List<Pair<String, Double>>
    ): Result<Meal> {
        val updatedMeal = meal.copy(
            name = name,
            instructions = instructions,
            prepTimeMinutes = prepTimeMinutes,
            cookTimeMinutes = cookTimeMinutes,
            servings = servings,
            mealType = mealType,
            dishCategory = dishCategory,
            updatedAt = System.currentTimeMillis()
        )
        
        // Convert Pair to MealIngredientInput (preserve existing isStarIngredient from meal)
        val ingredientInputs = ingredients.map { (id, qty) ->
            val existingIngredient = meal.ingredients.find { it.ingredient.id == id }
            MealIngredientInput(id, qty, existingIngredient?.isStarIngredient ?: false)
        }
        
        return mealRepository.updateMeal(updatedMeal, ingredientInputs).map { updatedMeal }
    }
}
