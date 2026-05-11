package com.littlechef.app.data.repository

import com.littlechef.app.data.local.dao.MealDao
import com.littlechef.app.data.local.dao.MealPlanDao
import com.littlechef.app.data.local.entity.MealPlanEntity
import com.littlechef.app.domain.model.MealPlan
import com.littlechef.app.domain.model.MealPlanStatus
import com.littlechef.app.domain.model.MealType
import com.littlechef.app.domain.repository.MealPlanRepository
import com.littlechef.app.domain.repository.MealRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

class MealPlanRepositoryImpl @Inject constructor(
    private val mealPlanDao: MealPlanDao,
    private val mealRepository: MealRepository
) : MealPlanRepository {

    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun create(mealPlan: MealPlan) {
        mealPlanDao.insert(mealPlan.toEntity())
    }

    override suspend fun update(mealPlan: MealPlan) {
        mealPlanDao.update(mealPlan.toEntity())
    }

    override suspend fun delete(mealPlan: MealPlan) {
        mealPlanDao.delete(mealPlan.toEntity())
    }

    override suspend fun getById(id: String): MealPlan? {
        val entity = mealPlanDao.getById(id) ?: return null
        return entity.toDomain(mealRepository)
    }

    override fun observeById(id: String): Flow<MealPlan?> {
        return mealPlanDao.observeById(id).map { entity ->
            entity?.toDomain(mealRepository)
        }
    }

    override suspend fun getAll(): List<MealPlan> {
        return mealPlanDao.getAll().map { it.toDomain(mealRepository) }
    }

    override fun observeAll(): Flow<List<MealPlan>> {
        return mealPlanDao.observeAll().map { entities ->
            entities.map { it.toDomain(mealRepository) }
        }
    }

    override suspend fun getByDateRange(
        startDate: Long,
        endDate: Long
    ): List<MealPlan> {
        return mealPlanDao.getByDateRange(startDate, endDate)
            .map { it.toDomain(mealRepository) }
    }

    override suspend fun getByDate(date: Long): List<MealPlan> {
        return mealPlanDao.getByDate(date)
            .map { it.toDomain(mealRepository) }
    }

    private suspend fun MealPlanEntity.toDomain(mealRepository: MealRepository): MealPlan {
        val meal = mealRepository.getMealById(mealId)
            ?: throw IllegalStateException("Meal not found: $mealId")
        
        // Parse ingredient substitutions from JSON
        val substitutions = try {
            json.decodeFromString<Map<String, String>>(ingredientSubstitutions)
        } catch (e: Exception) {
            emptyMap()
        }
        
        return MealPlan(
            id = id,
            meal = meal,
            plannedDate = plannedDate,
            mealType = MealType.valueOf(mealType),
            status = MealPlanStatus.valueOf(status),
            startedAt = startedAt,
            completedAt = completedAt,
            createdAt = createdAt,
            updatedAt = updatedAt,
            ingredientSubstitutions = substitutions,
            plannedServings = plannedServings,
            adjustedPrepTimeMinutes = adjustedPrepTimeMinutes,
            adjustedCookTimeMinutes = adjustedCookTimeMinutes
        )
    }

    private fun MealPlan.toEntity(): MealPlanEntity {
        // Encode ingredient substitutions to JSON
        val substitutionsJson = json.encodeToString(ingredientSubstitutions)
        
        return MealPlanEntity(
            id = id,
            mealId = meal.id,
            plannedDate = plannedDate,
            mealType = mealType.name,
            status = status.name,
            startedAt = startedAt,
            completedAt = completedAt,
            createdAt = createdAt,
            updatedAt = updatedAt,
            ingredientSubstitutions = substitutionsJson,
            plannedServings = plannedServings,
            adjustedPrepTimeMinutes = adjustedPrepTimeMinutes,
            adjustedCookTimeMinutes = adjustedCookTimeMinutes
        )
    }
}
