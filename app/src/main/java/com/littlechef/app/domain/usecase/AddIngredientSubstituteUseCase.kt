package com.littlechef.app.domain.usecase

import com.littlechef.app.domain.repository.IngredientRepository
import javax.inject.Inject

class AddIngredientSubstituteUseCase @Inject constructor(
    private val ingredientRepository: IngredientRepository
) {
    suspend operator fun invoke(
        ingredientId: String,
        substituteId: String,
        notes: String?
    ): Result<Unit> {
        if (ingredientId == substituteId) {
            return Result.failure(IllegalArgumentException("An ingredient cannot be a substitute for itself"))
        }

        return try {
            ingredientRepository.addSubstitute(ingredientId, substituteId, notes)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
