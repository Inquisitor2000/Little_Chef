package com.familymealplanner.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "ingredients")
data class IngredientEntity(
    @PrimaryKey val id: String,
    val name: String,
    val unit: String,
    val category: String?,
    val subcategory: String?,
    @ColumnInfo(name = "preferred_display_unit") val preferredDisplayUnit: String? = null,
    @ColumnInfo(name = "created_in_language", defaultValue = "en") val createdInLanguage: String = "en",
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "updated_at") val updatedAt: Long
)
