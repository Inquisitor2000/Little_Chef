package com.familymealplanner.domain.usecase

import com.familymealplanner.domain.model.Ingredient
import com.familymealplanner.domain.repository.IngredientRepository
import javax.inject.Inject

class DeleteIngredientUseCase @Inject constructor(
    private val ingredientRepository: IngredientRepository
) {
    suspend operator fun invoke(ingredient: Ingredient): Result<Unit> {
        return ingredientRepository.deleteIngredient(ingredient)
    }
}
