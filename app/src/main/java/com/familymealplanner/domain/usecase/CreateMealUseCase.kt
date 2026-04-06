package com.familymealplanner.domain.usecase

import com.familymealplanner.domain.model.Meal
import com.familymealplanner.domain.repository.MealIngredientInput
import com.familymealplanner.domain.repository.MealRepository
import java.util.UUID
import javax.inject.Inject

class CreateMealUseCase @Inject constructor(
    private val mealRepository: MealRepository
) {
    suspend operator fun invoke(
        name: String,
        instructions: String?,
        prepTimeMinutes: Int?,
        cookTimeMinutes: Int?,
        servings: Int?,
        mealType: com.familymealplanner.domain.model.MealType?,
        dishCategory: com.familymealplanner.domain.model.DishCategory?,
        ingredients: List<Pair<String, Double>>
    ): Result<Meal> {
        val now = System.currentTimeMillis()
        val meal = Meal(
            id = UUID.randomUUID().toString(),
            name = name,
            instructions = instructions,
            prepTimeMinutes = prepTimeMinutes,
            cookTimeMinutes = cookTimeMinutes,
            servings = servings,
            mealType = mealType,
            dishCategory = dishCategory,
            createdAt = now,
            updatedAt = now
        )
        
        // Convert Pair to MealIngredientInput (default isStarIngredient to false)
        val ingredientInputs = ingredients.map { (id, qty) ->
            MealIngredientInput(id, qty, false)
        }
        
        return mealRepository.createMeal(meal, ingredientInputs).map { meal }
    }
}
