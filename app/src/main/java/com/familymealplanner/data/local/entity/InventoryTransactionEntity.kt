package com.familymealplanner.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(
    tableName = "inventory_transactions",
    foreignKeys = [
        ForeignKey(
            entity = IngredientEntity::class,
            parentColumns = ["id"],
            childColumns = ["ingredient_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("ingredient_id"),
        Index("meal_plan_id")
    ]
)
data class InventoryTransactionEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "ingredient_id") val ingredientId: String,
    @ColumnInfo(name = "quantity_change") val quantityChange: Double,
    val status: String,
    val reason: String,
    @ColumnInfo(name = "meal_plan_id") val mealPlanId: String?,
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "updated_at") val updatedAt: Long
)
