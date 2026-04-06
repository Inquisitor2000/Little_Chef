package com.familymealplanner.domain.usecase;

import com.familymealplanner.domain.repository.InventoryRepository;
import com.familymealplanner.domain.repository.MealPlanRepository;
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
public final class AbortCookingUseCase_Factory implements Factory<AbortCookingUseCase> {
  private final Provider<MealPlanRepository> mealPlanRepositoryProvider;

  private final Provider<InventoryRepository> inventoryRepositoryProvider;

  public AbortCookingUseCase_Factory(Provider<MealPlanRepository> mealPlanRepositoryProvider,
      Provider<InventoryRepository> inventoryRepositoryProvider) {
    this.mealPlanRepositoryProvider = mealPlanRepositoryProvider;
    this.inventoryRepositoryProvider = inventoryRepositoryProvider;
  }

  @Override
  public AbortCookingUseCase get() {
    return newInstance(mealPlanRepositoryProvider.get(), inventoryRepositoryProvider.get());
  }

  public static AbortCookingUseCase_Factory create(
      Provider<MealPlanRepository> mealPlanRepositoryProvider,
      Provider<InventoryRepository> inventoryRepositoryProvider) {
    return new AbortCookingUseCase_Factory(mealPlanRepositoryProvider, inventoryRepositoryProvider);
  }

  public static AbortCookingUseCase newInstance(MealPlanRepository mealPlanRepository,
      InventoryRepository inventoryRepository) {
    return new AbortCookingUseCase(mealPlanRepository, inventoryRepository);
  }
}
