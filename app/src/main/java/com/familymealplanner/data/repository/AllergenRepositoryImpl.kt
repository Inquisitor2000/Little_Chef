package com.familymealplanner.data.repository

import com.familymealplanner.data.local.dao.AllergenDao
import com.familymealplanner.data.local.entity.AllergenEntity
import com.familymealplanner.domain.model.Allergen
import com.familymealplanner.domain.repository.AllergenRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AllergenRepositoryImpl @Inject constructor(
    private val allergenDao: AllergenDao
) : AllergenRepository {

    override suspend fun createAllergen(allergen: Allergen) {
        allergenDao.insert(allergen.toEntity())
    }

    override suspend fun getAllergenById(id: String): Allergen? {
        return allergenDao.getById(id)?.toDomain()
    }

    override suspend fun getAllAllergens(): List<Allergen> {
        return allergenDao.getAll().map { it.toDomain() }
    }

    override fun observeAllAllergens(): Flow<List<Allergen>> {
        return allergenDao.observeAll().map { list -> list.map { it.toDomain() } }
    }

    override suspend fun updateAllergen(allergen: Allergen) {
        allergenDao.update(allergen.toEntity())
    }

    override suspend fun deleteAllergen(allergen: Allergen) {
        allergenDao.delete(allergen.toEntity())
    }

    private fun AllergenEntity.toDomain(): Allergen {
        return Allergen(
            id = id,
            name = name,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    private fun Allergen.toEntity(): AllergenEntity {
        return AllergenEntity(
            id = id,
            name = name,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}
