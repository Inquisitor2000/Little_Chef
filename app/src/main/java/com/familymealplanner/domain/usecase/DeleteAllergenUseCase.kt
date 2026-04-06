package com.familymealplanner.domain.usecase

import com.familymealplanner.domain.model.Allergen
import com.familymealplanner.domain.repository.AllergenRepository
import javax.inject.Inject

class DeleteAllergenUseCase @Inject constructor(
    private val allergenRepository: AllergenRepository
) {
    suspend operator fun invoke(allergen: Allergen): Result<Unit> {
        return try {
            allergenRepository.deleteAllergen(allergen)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
