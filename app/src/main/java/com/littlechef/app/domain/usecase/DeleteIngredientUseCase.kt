package com.littlechef.app.domain.usecase

import com.littlechef.app.domain.model.Ingredient
import com.littlechef.app.domain.repository.IngredientRepository
import javax.inject.Inject

class DeleteIngredientUseCase @Inject constructor(
    private val ingredientRepository: IngredientRepository
) {
    suspend operator fun invoke(ingredient: Ingredient): Result<Unit> {
        return ingredientRepository.deleteIngredient(ingredient)
    }
}
