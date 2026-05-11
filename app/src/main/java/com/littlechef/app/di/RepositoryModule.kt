package com.littlechef.app.di

import com.littlechef.app.data.repository.AllergenRepositoryImpl
import com.littlechef.app.data.repository.IngredientRepositoryImpl
import com.littlechef.app.data.repository.InventoryRepositoryImpl
import com.littlechef.app.data.repository.MealPlanRepositoryImpl
import com.littlechef.app.data.repository.MealRepositoryImpl
import com.littlechef.app.domain.repository.AllergenRepository
import com.littlechef.app.domain.repository.IngredientRepository
import com.littlechef.app.domain.repository.InventoryRepository
import com.littlechef.app.domain.repository.MealPlanRepository
import com.littlechef.app.domain.repository.MealRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAllergenRepository(
        allergenRepositoryImpl: AllergenRepositoryImpl
    ): AllergenRepository

    @Binds
    @Singleton
    abstract fun bindIngredientRepository(
        ingredientRepositoryImpl: IngredientRepositoryImpl
    ): IngredientRepository

    @Binds
    @Singleton
    abstract fun bindMealRepository(
        mealRepositoryImpl: MealRepositoryImpl
    ): MealRepository

    @Binds
    @Singleton
    abstract fun bindMealPlanRepository(
        mealPlanRepositoryImpl: MealPlanRepositoryImpl
    ): MealPlanRepository

    @Binds
    @Singleton
    abstract fun bindInventoryRepository(
        inventoryRepositoryImpl: InventoryRepositoryImpl
    ): InventoryRepository

    @Binds
    @Singleton
    abstract fun bindGroceryRepository(
        groceryRepositoryImpl: com.littlechef.app.data.repository.GroceryRepositoryImpl
    ): com.littlechef.app.domain.repository.GroceryRepository
}
