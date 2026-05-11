package com.littlechef.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.littlechef.app.data.local.entity.MealPlanEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MealPlanDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(mealPlan: MealPlanEntity)

    @Update
    suspend fun update(mealPlan: MealPlanEntity)

    @Delete
    suspend fun delete(mealPlan: MealPlanEntity)

    @Query("SELECT * FROM meal_plans WHERE id = :id")
    suspend fun getById(id: String): MealPlanEntity?

    @Query("SELECT * FROM meal_plans WHERE id = :id")
    fun observeById(id: String): Flow<MealPlanEntity?>

    @Query("SELECT * FROM meal_plans")
    suspend fun getAll(): List<MealPlanEntity>

    @Query("SELECT * FROM meal_plans")
    fun observeAll(): Flow<List<MealPlanEntity>>

    @Query("SELECT * FROM meal_plans WHERE planned_date BETWEEN :startDate AND :endDate")
    suspend fun getByDateRange(startDate: Long, endDate: Long): List<MealPlanEntity>

    @Query("SELECT * FROM meal_plans WHERE planned_date = :date")
    suspend fun getByDate(date: Long): List<MealPlanEntity>

    @Query("SELECT * FROM meal_plans WHERE status = :status")
    suspend fun getByStatus(status: String): List<MealPlanEntity>

    @Query("SELECT * FROM meal_plans WHERE updated_at > :timestamp")
    suspend fun getChangedSince(timestamp: Long): List<MealPlanEntity>
}
