package com.littlechef.app.domain.usecase

import com.littlechef.app.domain.model.Allergen
import com.littlechef.app.domain.repository.AllergenRepository
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
