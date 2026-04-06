package com.familymealplanner.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(
    tableName = "meal_ingredients",
    foreignKeys = [
        ForeignKey(
            entity = MealEntity::class,
            parentColumns = ["id"],
            childColumns = ["meal_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = IngredientEntity::class,
            parentColumns = ["id"],
            childColumns = ["ingredient_id"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index("meal_id"),
        Index("ingredient_id")
    ]
)
data class MealIngredientEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "meal_id") val mealId: String,
    @ColumnInfo(name = "ingredient_id") val ingredientId: String,
    val quantity: Double,
    val unit: String?,
    @ColumnInfo(name = "is_star_ingredient", defaultValue = "0")
    val isStarIngredient: Boolean = false
)
