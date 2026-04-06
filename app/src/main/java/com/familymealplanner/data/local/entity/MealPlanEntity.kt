package com.familymealplanner.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(
    tableName = "meal_plans",
    foreignKeys = [
        ForeignKey(
            entity = MealEntity::class,
            parentColumns = ["id"],
            childColumns = ["meal_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("meal_id")
    ]
)
data class MealPlanEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "meal_id") val mealId: String,
    @ColumnInfo(name = "planned_date") val plannedDate: Long,
    @ColumnInfo(name = "meal_type") val mealType: String,
    val status: String,
    @ColumnInfo(name = "started_at") val startedAt: Long?,
    @ColumnInfo(name = "completed_at") val completedAt: Long?,
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "updated_at") val updatedAt: Long,
    @ColumnInfo(name = "ingredient_substitutions", defaultValue = "{}") val ingredientSubstitutions: String = "{}",
    @ColumnInfo(name = "planned_servings") val plannedServings: Int? = null,
    @ColumnInfo(name = "adjusted_prep_time_minutes") val adjustedPrepTimeMinutes: Int? = null,
    @ColumnInfo(name = "adjusted_cook_time_minutes") val adjustedCookTimeMinutes: Int? = null
)
