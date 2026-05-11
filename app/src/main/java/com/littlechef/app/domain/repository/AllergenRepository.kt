package com.littlechef.app.domain.repository

import com.littlechef.app.domain.model.Allergen
import kotlinx.coroutines.flow.Flow

interface AllergenRepository {
    suspend fun createAllergen(allergen: Allergen)
    suspend fun getAllergenById(id: String): Allergen?
    suspend fun getAllAllergens(): List<Allergen>
    fun observeAllAllergens(): Flow<List<Allergen>>
    suspend fun updateAllergen(allergen: Allergen)
    suspend fun deleteAllergen(allergen: Allergen)
}
