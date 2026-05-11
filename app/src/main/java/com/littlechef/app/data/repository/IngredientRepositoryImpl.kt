package com.littlechef.app.data.repository

import com.littlechef.app.data.local.dao.AllergenDao
import com.littlechef.app.data.local.dao.IngredientAllergenDao
import com.littlechef.app.data.local.dao.IngredientDao
import com.littlechef.app.data.local.dao.IngredientSubstituteDao
import com.littlechef.app.data.local.dao.MealIngredientDao
import com.littlechef.app.data.local.entity.AllergenEntity
import com.littlechef.app.data.local.entity.IngredientAllergenEntity
import com.littlechef.app.data.local.entity.IngredientEntity
import com.littlechef.app.data.local.entity.IngredientSubstituteEntity
import com.littlechef.app.domain.model.Allergen
import com.littlechef.app.domain.model.Ingredient
import com.littlechef.app.domain.model.IngredientSubstitute
import com.littlechef.app.domain.repository.IngredientRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class IngredientRepositoryImpl @Inject constructor(
    private val ingredientDao: IngredientDao,
    private val allergenDao: AllergenDao,
    private val ingredientAllergenDao: IngredientAllergenDao,
    private val ingredientSubstituteDao: IngredientSubstituteDao,
    private val mealIngredientDao: MealIngredientDao,
    private val localeManager: com.littlechef.app.data.preferences.LocaleManager
) : IngredientRepository {

    override suspend fun createIngredient(
        name: String,
        unit: String,
        category: String?,
        subcategory: String?,
        preferredDisplayUnit: String?
    ): Result<Ingredient> {
        return try {
            val currentLanguage = localeManager.getLanguage()
            val ingredient = Ingredient(
                id = UUID.randomUUID().toString(),
                name = name,
                unit = unit,
                category = category,
                subcategory = subcategory,
                preferredDisplayUnit = preferredDisplayUnit,
                createdInLanguage = currentLanguage,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            
            ingredientDao.insert(ingredient.toEntity())
            Result.success(ingredient)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getIngredientById(id: String): Ingredient? {
        val entity = ingredientDao.getById(id) ?: return null
        return entity.toDomainWithDetails()
    }

    override suspend fun getIngredientByName(name: String): Ingredient? {
        val entity = ingredientDao.getByName(name) ?: return null
        return entity.toDomainWithDetails()
    }

    override suspend fun getAllIngredients(): List<Ingredient> {
        return ingredientDao.getAll().map { it.toDomainWithDetails() }
    }

    override fun observeAllIngredients(): Flow<List<Ingredient>> {
        return ingredientDao.observeAll().map { list -> 
            list.map { it.toDomainWithDetails() }
        }
    }

    override suspend fun getIngredientsByCategory(category: String): List<Ingredient> {
        return ingredientDao.getByCategory(category).map { it.toDomainWithDetails() }
    }

    override suspend fun searchIngredients(query: String): List<Ingredient> {
        return ingredientDao.search(query).map { it.toDomainWithDetails() }
    }

    override suspend fun updateIngredient(ingredient: Ingredient, allergenNames: List<String>) {
        ingredientDao.update(ingredient.toEntity())
        
        // Update allergen associations
        ingredientAllergenDao.deleteByIngredientId(ingredient.id)
        if (allergenNames.isNotEmpty()) {
            val associations = allergenNames.mapNotNull { name ->
                val allergenId = name.lowercase().replace(" ", "_")
                val allergen = allergenDao.getById(allergenId)
                if (allergen != null) {
                    IngredientAllergenEntity(
                        id = UUID.randomUUID().toString(),
                        ingredientId = ingredient.id,
                        allergenId = allergenId
                    )
                } else null
            }
            if (associations.isNotEmpty()) {
                ingredientAllergenDao.insertAll(associations)
            }
        }
    }

    override suspend fun deleteIngredient(ingredient: Ingredient): Result<Unit> {
        // Check if ingredient is used in any meals
        val usageCount = mealIngredientDao.countByIngredientId(ingredient.id)
        if (usageCount > 0) {
            return Result.failure(
                IllegalStateException("Cannot delete ingredient that is used in $usageCount meal(s)")
            )
        }
        
        ingredientDao.delete(ingredient.toEntity())
        return Result.success(Unit)
    }

    override suspend fun addSubstitute(ingredientId: String, substituteId: String, notes: String?) {
        val substitute = IngredientSubstituteEntity(
            id = UUID.randomUUID().toString(),
            ingredientId = ingredientId,
            substituteId = substituteId,
            notes = notes,
            createdAt = System.currentTimeMillis()
        )
        ingredientSubstituteDao.insert(substitute)
    }

    override suspend fun removeSubstitute(ingredientId: String, substituteId: String) {
        val substitutes = ingredientSubstituteDao.getByIngredientId(ingredientId)
        val toDelete = substitutes.find { it.substituteId == substituteId }
        if (toDelete != null) {
            ingredientSubstituteDao.delete(toDelete)
        }
    }

    override suspend fun getSubstitutesForIngredient(ingredientId: String): List<IngredientSubstitute> {
        val substituteEntities = ingredientSubstituteDao.getByIngredientId(ingredientId)
        return substituteEntities.mapNotNull { entity ->
            val substituteIngredient = ingredientDao.getById(entity.substituteId)?.toDomainWithDetails()
            substituteIngredient?.let {
                IngredientSubstitute(
                    id = entity.id,
                    substituteIngredient = it,
                    notes = entity.notes,
                    createdAt = entity.createdAt
                )
            }
        }
    }

    private suspend fun IngredientEntity.toDomainWithDetails(): Ingredient {
        val allergenAssociations = ingredientAllergenDao.getByIngredientId(id)
        val allergens = allergenAssociations.mapNotNull { association ->
            allergenDao.getById(association.allergenId)?.toDomain()
        }
        
        val substituteEntities = ingredientSubstituteDao.getByIngredientId(id)
        val substitutes = substituteEntities.mapNotNull { entity ->
            // Load substitute ingredient WITHOUT its own substitutes to avoid infinite recursion
            val substituteIngredient = ingredientDao.getById(entity.substituteId)?.toDomain()
            substituteIngredient?.let {
                IngredientSubstitute(
                    id = entity.id,
                    substituteIngredient = it,
                    notes = entity.notes,
                    createdAt = entity.createdAt
                )
            }
        }
        
        return Ingredient(
            id = id,
            name = name,
            unit = unit,
            category = category,
            subcategory = subcategory,
            preferredDisplayUnit = preferredDisplayUnit,
            createdInLanguage = createdInLanguage,
            createdAt = createdAt,
            updatedAt = updatedAt,
            allergens = allergens,
            substitutes = substitutes
        )
    }

    private fun IngredientEntity.toDomain(): Ingredient {
        return Ingredient(
            id = id,
            name = name,
            unit = unit,
            category = category,
            subcategory = subcategory,
            preferredDisplayUnit = preferredDisplayUnit,
            createdInLanguage = createdInLanguage,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    private fun Ingredient.toEntity(): IngredientEntity {
        return IngredientEntity(
            id = id,
            name = name,
            unit = unit,
            category = category,
            subcategory = subcategory,
            preferredDisplayUnit = preferredDisplayUnit,
            createdInLanguage = createdInLanguage,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    private fun AllergenEntity.toDomain(): Allergen {
        return Allergen(
            id = id,
            name = name,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}
