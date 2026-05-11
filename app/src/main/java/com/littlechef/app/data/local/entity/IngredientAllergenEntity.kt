package com.littlechef.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(
    tableName = "ingredient_allergens",
    foreignKeys = [
        ForeignKey(
            entity = IngredientEntity::class,
            parentColumns = ["id"],
            childColumns = ["ingredient_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = AllergenEntity::class,
            parentColumns = ["id"],
            childColumns = ["allergen_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("ingredient_id"),
        Index("allergen_id")
    ]
)
data class IngredientAllergenEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "ingredient_id") val ingredientId: String,
    @ColumnInfo(name = "allergen_id") val allergenId: String
)
