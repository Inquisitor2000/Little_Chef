package com.familymealplanner.domain.repository

import com.familymealplanner.domain.model.Allergen
import kotlinx.coroutines.flow.Flow

interface AllergenRepository {
    suspend fun createAllergen(allergen: Allergen)
    suspend fun getAllergenById(id: String): Allergen?
    suspend fun getAllAllergens(): List<Allergen>
    fun observeAllAllergens(): Flow<List<Allergen>>
    suspend fun updateAllergen(allergen: Allergen)
    suspend fun deleteAllergen(allergen: Allergen)
}
