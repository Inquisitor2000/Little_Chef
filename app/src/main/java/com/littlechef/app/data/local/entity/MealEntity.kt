package com.littlechef.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "meals")
data class MealEntity(
    @PrimaryKey val id: String,
    val name: String,
    val instructions: String?,
    @ColumnInfo(name = "simple_instructions") val simpleInstructions: String? = null,
    @ColumnInfo(name = "prep_time_minutes") val prepTimeMinutes: Int?,
    @ColumnInfo(name = "cook_time_minutes") val cookTimeMinutes: Int?,
    val servings: Int?,
    @ColumnInfo(name = "is_scraped", defaultValue = "0") val isScraped: Boolean = false,
    @ColumnInfo(name = "is_bundled", defaultValue = "0") val isBundled: Boolean = false,
    @ColumnInfo(name = "image_path") val imagePath: String? = null,
    @ColumnInfo(name = "meal_type") val mealType: String? = null,
    @ColumnInfo(name = "dish_category") val dishCategory: String? = null,
    @ColumnInfo(name = "created_in_language", defaultValue = "en") val createdInLanguage: String = "en",
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "updated_at") val updatedAt: Long
)
