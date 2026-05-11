package com.littlechef.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.littlechef.app.domain.model.GroceryItem
import com.littlechef.app.domain.model.MealType

@Entity(tableName = "grocery_items")
data class GroceryItemEntity(
    @PrimaryKey
    val id: String,
    val ingredientName: String,
    val ingredientId: String?, // ID of the ingredient in the database (null for custom items)
    val category: String?, // Category of the ingredient (from catalog or database)
    val quantity: Double,
    val unit: String,
    val mealName: String,
    val mealType: String?, // Stored as string, converted to MealType enum
    val plannedDate: Long?,
    val isChecked: Boolean,
    val checkedAt: Long?,
    val createdAt: Long
) {
    fun toDomain(): GroceryItem {
        return GroceryItem(
            id = id,
            ingredientName = ingredientName,
            ingredientId = ingredientId,
            category = category,
            quantity = quantity,
            unit = unit,
            mealName = mealName,
            mealType = mealType?.let { MealType.valueOf(it) },
            plannedDate = plannedDate,
            isChecked = isChecked,
            checkedAt = checkedAt,
            createdAt = createdAt
        )
    }
}

fun GroceryItem.toEntity(): GroceryItemEntity {
    return GroceryItemEntity(
        id = id,
        ingredientName = ingredientName,
        ingredientId = ingredientId,
        category = category,
        quantity = quantity,
        unit = unit,
        mealName = mealName,
        mealType = mealType?.name,
        plannedDate = plannedDate,
        isChecked = isChecked,
        checkedAt = checkedAt,
        createdAt = createdAt
    )
}
