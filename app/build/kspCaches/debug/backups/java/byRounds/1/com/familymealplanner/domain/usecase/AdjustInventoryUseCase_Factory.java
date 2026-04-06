package com.familymealplanner.domain.usecase;

import com.familymealplanner.domain.repository.IngredientRepository;
import com.familymealplanner.domain.repository.InventoryRepository;
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
public final class AdjustInventoryUseCase_Factory implements Factory<AdjustInventoryUseCase> {
  private final Provider<InventoryRepository> inventoryRepositoryProvider;

  private final Provider<IngredientRepository> ingredientRepositoryProvider;

  public AdjustInventoryUseCase_Factory(Provider<InventoryRepository> inventoryRepositoryProvider,
      Provider<IngredientRepository> ingredientRepositoryProvider) {
    this.inventoryRepositoryProvider = inventoryRepositoryProvider;
    this.ingredientRepositoryProvider = ingredientRepositoryProvider;
  }

  @Override
  public AdjustInventoryUseCase get() {
    return newInstance(inventoryRepositoryProvider.get(), ingredientRepositoryProvider.get());
  }

  public static AdjustInventoryUseCase_Factory create(
      Provider<InventoryRepository> inventoryRepositoryProvider,
      Provider<IngredientRepository> ingredientRepositoryProvider) {
    return new AdjustInventoryUseCase_Factory(inventoryRepositoryProvider, ingredientRepositoryProvider);
  }

  public static AdjustInventoryUseCase newInstance(InventoryRepository inventoryRepository,
      IngredientRepository ingredientRepository) {
    return new AdjustInventoryUseCase(inventoryRepository, ingredientRepository);
  }
}
