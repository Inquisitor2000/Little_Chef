package com.familymealplanner.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(
    tableName = "ingredient_substitutes",
    foreignKeys = [
        ForeignKey(
            entity = IngredientEntity::class,
            parentColumns = ["id"],
            childColumns = ["ingredient_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = IngredientEntity::class,
            parentColumns = ["id"],
            childColumns = ["substitute_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("ingredient_id"),
        Index("substitute_id")
    ]
)
data class IngredientSubstituteEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "ingredient_id") val ingredientId: String,
    @ColumnInfo(name = "substitute_id") val substituteId: String,
    val notes: String?,
    @ColumnInfo(name = "created_at") val createdAt: Long
)
