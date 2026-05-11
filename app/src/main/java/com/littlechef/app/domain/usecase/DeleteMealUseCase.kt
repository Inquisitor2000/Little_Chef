package com.littlechef.app.domain.usecase

import com.littlechef.app.domain.model.Meal
import com.littlechef.app.domain.repository.MealRepository
import javax.inject.Inject

class DeleteMealUseCase @Inject constructor(
    private val mealRepository: MealRepository
) {
    suspend operator fun invoke(meal: Meal) {
        mealRepository.deleteMeal(meal)
    }
}
