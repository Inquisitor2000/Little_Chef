package com.littlechef.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.littlechef.app.data.local.dao.*
import com.littlechef.app.data.local.entity.*

@Database(
    entities = [
        AllergenEntity::class,
        IngredientEntity::class,
        IngredientAllergenEntity::class,
        IngredientSubstituteEntity::class,
        MealEntity::class,
        MealIngredientEntity::class,
        MealPlanEntity::class,
        InventoryTransactionEntity::class,
        GroceryItemEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun allergenDao(): AllergenDao
    abstract fun ingredientDao(): IngredientDao
    abstract fun ingredientAllergenDao(): IngredientAllergenDao
    abstract fun ingredientSubstituteDao(): IngredientSubstituteDao
    abstract fun mealDao(): MealDao
    abstract fun mealIngredientDao(): MealIngredientDao
    abstract fun mealPlanDao(): MealPlanDao
    abstract fun inventoryTransactionDao(): InventoryTransactionDao
    abstract fun groceryItemDao(): GroceryItemDao

    companion object {
        const val DATABASE_NAME = "little_chef_db"
    }
}
