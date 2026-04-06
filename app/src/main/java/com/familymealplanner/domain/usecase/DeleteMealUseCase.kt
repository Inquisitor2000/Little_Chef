package com.familymealplanner.domain.usecase

import com.familymealplanner.domain.model.Meal
import com.familymealplanner.domain.repository.MealRepository
import javax.inject.Inject

class DeleteMealUseCase @Inject constructor(
    private val mealRepository: MealRepository
) {
    suspend operator fun invoke(meal: Meal) {
        mealRepository.deleteMeal(meal)
    }
}
