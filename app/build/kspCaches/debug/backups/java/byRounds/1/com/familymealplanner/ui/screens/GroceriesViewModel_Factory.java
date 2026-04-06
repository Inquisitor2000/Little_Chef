package com.familymealplanner.ui.screens;

import com.familymealplanner.data.local.TranslationSystem;
import com.familymealplanner.data.preferences.OnboardingPreferences;
import com.familymealplanner.domain.repository.GroceryRepository;
import com.familymealplanner.domain.repository.IngredientRepository;
import com.familymealplanner.domain.repository.InventoryRepository;
import com.familymealplanner.domain.usecase.AddGroceryItemToPantryUseCase;
import com.familymealplanner.domain.util.IngredientMatcher;
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
public final class GroceriesViewModel_Factory implements Factory<GroceriesViewModel> {
  private final Provider<GroceryRepository> groceryRepositoryProvider;

  private final Provider<AddGroceryItemToPantryUseCase> addGroceryItemToPantryUseCaseProvider;

  private final Provider<OnboardingPreferences> preferencesProvider;

  private final Provider<IngredientRepository> ingredientRepositoryProvider;

  private final Provider<InventoryRepository> inventoryRepositoryProvider;

  private final Provider<TranslationSystem> translationSystemProvider;

  private final Provider<IngredientMatcher> ingredientMatcherProvider;

  public GroceriesViewModel_Factory(Provider<GroceryRepository> groceryRepositoryProvider,
      Provider<AddGroceryItemToPantryUseCase> addGroceryItemToPantryUseCaseProvider,
      Provider<OnboardingPreferences> preferencesProvider,
      Provider<IngredientRepository> ingredientRepositoryProvider,
      Provider<InventoryRepository> inventoryRepositoryProvider,
      Provider<TranslationSystem> translationSystemProvider,
      Provider<IngredientMatcher> ingredientMatcherProvider) {
    this.groceryRepositoryProvider = groceryRepositoryProvider;
    this.addGroceryItemToPantryUseCaseProvider = addGroceryItemToPantryUseCaseProvider;
    this.preferencesProvider = preferencesProvider;
    this.ingredientRepositoryProvider = ingredientRepositoryProvider;
    this.inventoryRepositoryProvider = inventoryRepositoryProvider;
    this.translationSystemProvider = translationSystemProvider;
    this.ingredientMatcherProvider = ingredientMatcherProvider;
  }

  @Override
  public GroceriesViewModel get() {
    return newInstance(groceryRepositoryProvider.get(), addGroceryItemToPantryUseCaseProvider.get(), preferencesProvider.get(), ingredientRepositoryProvider.get(), inventoryRepositoryProvider.get(), translationSystemProvider.get(), ingredientMatcherProvider.get());
  }

  public static GroceriesViewModel_Factory create(
      Provider<GroceryRepository> groceryRepositoryProvider,
      Provider<AddGroceryItemToPantryUseCase> addGroceryItemToPantryUseCaseProvider,
      Provider<OnboardingPreferences> preferencesProvider,
      Provider<IngredientRepository> ingredientRepositoryProvider,
      Provider<InventoryRepository> inventoryRepositoryProvider,
      Provider<TranslationSystem> translationSystemProvider,
      Provider<IngredientMatcher> ingredientMatcherProvider) {
    return new GroceriesViewModel_Factory(groceryRepositoryProvider, addGroceryItemToPantryUseCaseProvider, preferencesProvider, ingredientRepositoryProvider, inventoryRepositoryProvider, translationSystemProvider, ingredientMatcherProvider);
  }

  public static GroceriesViewModel newInstance(GroceryRepository groceryRepository,
      AddGroceryItemToPantryUseCase addGroceryItemToPantryUseCase,
      OnboardingPreferences preferences, IngredientRepository ingredientRepository,
      InventoryRepository inventoryRepository, TranslationSystem translationSystem,
      IngredientMatcher ingredientMatcher) {
    return new GroceriesViewModel(groceryRepository, addGroceryItemToPantryUseCase, preferences, ingredientRepository, inventoryRepository, translationSystem, ingredientMatcher);
  }
}
