package com.littlechef.app.domain.usecase

import com.littlechef.app.domain.model.Meal
import com.littlechef.app.domain.repository.MealIngredientInput
import com.littlechef.app.domain.repository.MealRepository
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
        mealType: com.littlechef.app.domain.model.MealType?,
        dishCategory: com.littlechef.app.domain.model.DishCategory?,
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
