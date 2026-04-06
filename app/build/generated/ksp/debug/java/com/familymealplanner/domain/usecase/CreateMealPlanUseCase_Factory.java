package com.familymealplanner.domain.usecase;

import com.familymealplanner.domain.repository.InventoryRepository;
import com.familymealplanner.domain.repository.MealPlanRepository;
import com.familymealplanner.domain.repository.MealRepository;
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
public final class CreateMealPlanUseCase_Factory implements Factory<CreateMealPlanUseCase> {
  private final Provider<MealPlanRepository> mealPlanRepositoryProvider;

  private final Provider<MealRepository> mealRepositoryProvider;

  private final Provider<InventoryRepository> inventoryRepositoryProvider;

  public CreateMealPlanUseCase_Factory(Provider<MealPlanRepository> mealPlanRepositoryProvider,
      Provider<MealRepository> mealRepositoryProvider,
      Provider<InventoryRepository> inventoryRepositoryProvider) {
    this.mealPlanRepositoryProvider = mealPlanRepositoryProvider;
    this.mealRepositoryProvider = mealRepositoryProvider;
    this.inventoryRepositoryProvider = inventoryRepositoryProvider;
  }

  @Override
  public CreateMealPlanUseCase get() {
    return newInstance(mealPlanRepositoryProvider.get(), mealRepositoryProvider.get(), inventoryRepositoryProvider.get());
  }

  public static CreateMealPlanUseCase_Factory create(
      Provider<MealPlanRepository> mealPlanRepositoryProvider,
      Provider<MealRepository> mealRepositoryProvider,
      Provider<InventoryRepository> inventoryRepositoryProvider) {
    return new CreateMealPlanUseCase_Factory(mealPlanRepositoryProvider, mealRepositoryProvider, inventoryRepositoryProvider);
  }

  public static CreateMealPlanUseCase newInstance(MealPlanRepository mealPlanRepository,
      MealRepository mealRepository, InventoryRepository inventoryRepository) {
    return new CreateMealPlanUseCase(mealPlanRepository, mealRepository, inventoryRepository);
  }
}
