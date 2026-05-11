package com.littlechef.app.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.littlechef.app.data.local.AppDatabase
import com.littlechef.app.data.local.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        // Rename old database file if upgrading from previous version
        migrateDatabaseFile(context, "family_meal_planner_db", AppDatabase.DATABASE_NAME)
        
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        )
        .fallbackToDestructiveMigration() // Allow destructive migration during development
        .addCallback(object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // Prepopulate allergens table with common allergens
                val currentTime = System.currentTimeMillis()
                val allergens = listOf(
                    "gluten" to "Gluten",
                    "dairy" to "Dairy",
                    "eggs" to "Eggs",
                    "tree_nuts" to "Tree Nuts",
                    "peanuts" to "Peanuts",
                    "soy" to "Soy",
                    "fish" to "Fish",
                    "shellfish" to "Shellfish",
                    "sesame" to "Sesame"
                )
                allergens.forEach { (id, name) ->
                    db.execSQL(
                        "INSERT INTO allergens (id, name, created_at, updated_at) VALUES (?, ?, ?, ?)",
                        arrayOf(id, name, currentTime, currentTime)
                    )
                }
            }
        })
        .build()
    }

    /**
     * Renames database files from old name to new name, preserving user data.
     * Handles the main .db file along with -wal and -shm WAL mode files.
     */
    private fun migrateDatabaseFile(context: Context, oldName: String, newName: String) {
        if (oldName == newName) return
        for (ext in listOf("", "-wal", "-shm")) {
            val oldFile = context.getDatabasePath("$oldName$ext")
            val newFile = context.getDatabasePath("$newName$ext")
            if (oldFile.exists() && !newFile.exists()) {
                oldFile.renameTo(newFile)
            }
        }
    }

    @Provides
    fun provideAllergenDao(database: AppDatabase): AllergenDao = database.allergenDao()

    @Provides
    fun provideIngredientDao(database: AppDatabase): IngredientDao = database.ingredientDao()

    @Provides
    fun provideIngredientAllergenDao(database: AppDatabase): IngredientAllergenDao = database.ingredientAllergenDao()

    @Provides
    fun provideIngredientSubstituteDao(database: AppDatabase): IngredientSubstituteDao = database.ingredientSubstituteDao()

    @Provides
    fun provideMealDao(database: AppDatabase): MealDao = database.mealDao()

    @Provides
    fun provideMealIngredientDao(database: AppDatabase): MealIngredientDao = database.mealIngredientDao()

    @Provides
    fun provideMealPlanDao(database: AppDatabase): MealPlanDao = database.mealPlanDao()

    @Provides
    fun provideInventoryTransactionDao(database: AppDatabase): InventoryTransactionDao = database.inventoryTransactionDao()

    @Provides
    fun provideGroceryItemDao(database: AppDatabase): GroceryItemDao = database.groceryItemDao()
}
