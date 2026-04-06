package com.familymealplanner.domain.repository

import com.familymealplanner.domain.model.MealPlan
import kotlinx.coroutines.flow.Flow

interface MealPlanRepository {
    suspend fun create(mealPlan: MealPlan)
    suspend fun update(mealPlan: MealPlan)
    suspend fun delete(mealPlan: MealPlan)
    suspend fun getById(id: String): MealPlan?
    fun observeById(id: String): Flow<MealPlan?>
    suspend fun getAll(): List<MealPlan>
    fun observeAll(): Flow<List<MealPlan>>
    suspend fun getByDateRange(startDate: Long, endDate: Long): List<MealPlan>
    suspend fun getByDate(date: Long): List<MealPlan>
}
