package com.familymealplanner.di

import com.familymealplanner.data.repository.AllergenRepositoryImpl
import com.familymealplanner.data.repository.IngredientRepositoryImpl
import com.familymealplanner.data.repository.InventoryRepositoryImpl
import com.familymealplanner.data.repository.MealPlanRepositoryImpl
import com.familymealplanner.data.repository.MealRepositoryImpl
import com.familymealplanner.domain.repository.AllergenRepository
import com.familymealplanner.domain.repository.IngredientRepository
import com.familymealplanner.domain.repository.InventoryRepository
import com.familymealplanner.domain.repository.MealPlanRepository
import com.familymealplanner.domain.repository.MealRepository
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
        groceryRepositoryImpl: com.familymealplanner.data.repository.GroceryRepositoryImpl
    ): com.familymealplanner.domain.repository.GroceryRepository
}
