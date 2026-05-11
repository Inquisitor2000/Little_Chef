package com.littlechef.app.domain.usecase

import com.littlechef.app.domain.model.Allergen
import com.littlechef.app.domain.repository.AllergenRepository
import java.util.UUID
import javax.inject.Inject

class CreateAllergenUseCase @Inject constructor(
    private val allergenRepository: AllergenRepository
) {
    suspend operator fun invoke(name: String): Result<Allergen> {
        if (name.isBlank()) {
            return Result.failure(IllegalArgumentException("Allergen name cannot be blank"))
        }

        val allergen = Allergen(
            id = UUID.randomUUID().toString(),
            name = name.trim(),
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )

        return try {
            allergenRepository.createAllergen(allergen)
            Result.success(allergen)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
