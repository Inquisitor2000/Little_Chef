package com.familymealplanner.data.repository;

import com.familymealplanner.data.local.dao.IngredientDao;
import com.familymealplanner.data.local.dao.InventoryTransactionDao;
import com.familymealplanner.data.local.dao.MealDao;
import com.familymealplanner.data.local.dao.MealPlanDao;
import com.familymealplanner.domain.repository.IngredientRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava"
})
public final class InventoryRepositoryImpl_Factory implements Factory<InventoryRepositoryImpl> {
  private final Provider<InventoryTransactionDao> inventoryTransactionDaoProvider;

  private final Provider<IngredientDao> ingredientDaoProvider;

  private final Provider<MealPlanDao> mealPlanDaoProvider;

  private final Provider<IngredientRepository> ingredientRepositoryProvider;

  private final Provider<MealDao> mealDaoProvider;

  public InventoryRepositoryImpl_Factory(
      Provider<InventoryTransactionDao> inventoryTransactionDaoProvider,
      Provider<IngredientDao> ingredientDaoProvider, Provider<MealPlanDao> mealPlanDaoProvider,
      Provider<IngredientRepository> ingredientRepositoryProvider,
      Provider<MealDao> mealDaoProvider) {
    this.inventoryTransactionDaoProvider = inventoryTransactionDaoProvider;
    this.ingredientDaoProvider = ingredientDaoProvider;
    this.mealPlanDaoProvider = mealPlanDaoProvider;
    this.ingredientRepositoryProvider = ingredientRepositoryProvider;
    this.mealDaoProvider = mealDaoProvider;
  }

  @Override
  public InventoryRepositoryImpl get() {
    return newInstance(inventoryTransactionDaoProvider.get(), ingredientDaoProvider.get(), mealPlanDaoProvider.get(), ingredientRepositoryProvider.get(), mealDaoProvider.get());
  }

  public static InventoryRepositoryImpl_Factory create(
      Provider<InventoryTransactionDao> inventoryTransactionDaoProvider,
      Provider<IngredientDao> ingredientDaoProvider, Provider<MealPlanDao> mealPlanDaoProvider,
      Provider<IngredientRepository> ingredientRepositoryProvider,
      Provider<MealDao> mealDaoProvider) {
    return new InventoryRepositoryImpl_Factory(inventoryTransactionDaoProvider, ingredientDaoProvider, mealPlanDaoProvider, ingredientRepositoryProvider, mealDaoProvider);
  }

  public static InventoryRepositoryImpl newInstance(InventoryTransactionDao inventoryTransactionDao,
      IngredientDao ingredientDao, MealPlanDao mealPlanDao,
      IngredientRepository ingredientRepository, MealDao mealDao) {
    return new InventoryRepositoryImpl(inventoryTransactionDao, ingredientDao, mealPlanDao, ingredientRepository, mealDao);
  }
}
