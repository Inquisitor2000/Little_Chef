package com.familymealplanner.ui.screens;

import com.familymealplanner.data.local.TranslationSystem;
import com.familymealplanner.data.preferences.OnboardingPreferences;
import com.familymealplanner.domain.repository.IngredientRepository;
import com.familymealplanner.domain.repository.InventoryRepository;
import com.familymealplanner.domain.usecase.AdjustInventoryUseCase;
import com.familymealplanner.domain.usecase.RestockIngredientUseCase;
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
public final class PantryViewModel_Factory implements Factory<PantryViewModel> {
  private final Provider<InventoryRepository> inventoryRepositoryProvider;

  private final Provider<TranslationSystem> translationSystemProvider;

  private final Provider<RestockIngredientUseCase> restockIngredientUseCaseProvider;

  private final Provider<AdjustInventoryUseCase> adjustInventoryUseCaseProvider;

  private final Provider<IngredientRepository> ingredientRepositoryProvider;

  private final Provider<OnboardingPreferences> preferencesProvider;

  public PantryViewModel_Factory(Provider<InventoryRepository> inventoryRepositoryProvider,
      Provider<TranslationSystem> translationSystemProvider,
      Provider<RestockIngredientUseCase> restockIngredientUseCaseProvider,
      Provider<AdjustInventoryUseCase> adjustInventoryUseCaseProvider,
      Provider<IngredientRepository> ingredientRepositoryProvider,
      Provider<OnboardingPreferences> preferencesProvider) {
    this.inventoryRepositoryProvider = inventoryRepositoryProvider;
    this.translationSystemProvider = translationSystemProvider;
    this.restockIngredientUseCaseProvider = restockIngredientUseCaseProvider;
    this.adjustInventoryUseCaseProvider = adjustInventoryUseCaseProvider;
    this.ingredientRepositoryProvider = ingredientRepositoryProvider;
    this.preferencesProvider = preferencesProvider;
  }

  @Override
  public PantryViewModel get() {
    return newInstance(inventoryRepositoryProvider.get(), translationSystemProvider.get(), restockIngredientUseCaseProvider.get(), adjustInventoryUseCaseProvider.get(), ingredientRepositoryProvider.get(), preferencesProvider.get());
  }

  public static PantryViewModel_Factory create(
      Provider<InventoryRepository> inventoryRepositoryProvider,
      Provider<TranslationSystem> translationSystemProvider,
      Provider<RestockIngredientUseCase> restockIngredientUseCaseProvider,
      Provider<AdjustInventoryUseCase> adjustInventoryUseCaseProvider,
      Provider<IngredientRepository> ingredientRepositoryProvider,
      Provider<OnboardingPreferences> preferencesProvider) {
    return new PantryViewModel_Factory(inventoryRepositoryProvider, translationSystemProvider, restockIngredientUseCaseProvider, adjustInventoryUseCaseProvider, ingredientRepositoryProvider, preferencesProvider);
  }

  public static PantryViewModel newInstance(InventoryRepository inventoryRepository,
      TranslationSystem translationSystem, RestockIngredientUseCase restockIngredientUseCase,
      AdjustInventoryUseCase adjustInventoryUseCase, IngredientRepository ingredientRepository,
      OnboardingPreferences preferences) {
    return new PantryViewModel(inventoryRepository, translationSystem, restockIngredientUseCase, adjustInventoryUseCase, ingredientRepository, preferences);
  }
}
