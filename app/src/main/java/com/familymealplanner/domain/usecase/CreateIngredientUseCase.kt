package com.familymealplanner.domain.usecase

import com.familymealplanner.domain.model.Ingredient
import com.familymealplanner.domain.repository.IngredientRepository
import javax.inject.Inject

class CreateIngredientUseCase @Inject constructor(
    private val ingredientRepository: IngredientRepository
) {
    suspend operator fun invoke(
        name: String,
        unit: String,
        category: String?,
        subcategory: String?,
        allergenIds: List<String>
    ): Result<Ingredient> {
        if (name.isBlank()) {
            return Result.failure(IllegalArgumentException("Ingredient name cannot be blank"))
        }
        if (unit.isBlank()) {
            return Result.failure(IllegalArgumentException("Unit cannot be blank"))
        }

        return ingredientRepository.createIngredient(
            name = name.trim(),
            unit = unit.trim(),
            category = category?.trim(),
            subcategory = subcategory?.trim()
        )
    }
}
