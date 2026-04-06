package com.familymealplanner.ui.screens;

import com.familymealplanner.data.local.BundledRecipeLoader;
import com.familymealplanner.data.local.SubstituteInitializer;
import com.familymealplanner.data.preferences.OnboardingPreferences;
import com.familymealplanner.domain.repository.IngredientRepository;
import com.familymealplanner.domain.repository.InventoryRepository;
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
public final class SuggestionViewModel_Factory implements Factory<SuggestionViewModel> {
  private final Provider<MealRepository> mealRepositoryProvider;

  private final Provider<BundledRecipeLoader> bundledRecipeLoaderProvider;

  private final Provider<InventoryRepository> inventoryRepositoryProvider;

  private final Provider<IngredientRepository> ingredientRepositoryProvider;

  private final Provider<OnboardingPreferences> preferencesProvider;

  private final Provider<SubstituteInitializer> substituteInitializerProvider;

  public SuggestionViewModel_Factory(Provider<MealRepository> mealRepositoryProvider,
      Provider<BundledRecipeLoader> bundledRecipeLoaderProvider,
      Provider<InventoryRepository> inventoryRepositoryProvider,
      Provider<IngredientRepository> ingredientRepositoryProvider,
      Provider<OnboardingPreferences> preferencesProvider,
      Provider<SubstituteInitializer> substituteInitializerProvider) {
    this.mealRepositoryProvider = mealRepositoryProvider;
    this.bundledRecipeLoaderProvider = bundledRecipeLoaderProvider;
    this.inventoryRepositoryProvider = inventoryRepositoryProvider;
    this.ingredientRepositoryProvider = ingredientRepositoryProvider;
    this.preferencesProvider = preferencesProvider;
    this.substituteInitializerProvider = substituteInitializerProvider;
  }

  @Override
  public SuggestionViewModel get() {
    return newInstance(mealRepositoryProvider.get(), bundledRecipeLoaderProvider.get(), inventoryRepositoryProvider.get(), ingredientRepositoryProvider.get(), preferencesProvider.get(), substituteInitializerProvider.get());
  }

  public static SuggestionViewModel_Factory create(Provider<MealRepository> mealRepositoryProvider,
      Provider<BundledRecipeLoader> bundledRecipeLoaderProvider,
      Provider<InventoryRepository> inventoryRepositoryProvider,
      Provider<IngredientRepository> ingredientRepositoryProvider,
      Provider<OnboardingPreferences> preferencesProvider,
      Provider<SubstituteInitializer> substituteInitializerProvider) {
    return new SuggestionViewModel_Factory(mealRepositoryProvider, bundledRecipeLoaderProvider, inventoryRepositoryProvider, ingredientRepositoryProvider, preferencesProvider, substituteInitializerProvider);
  }

  public static SuggestionViewModel newInstance(MealRepository mealRepository,
      BundledRecipeLoader bundledRecipeLoader, InventoryRepository inventoryRepository,
      IngredientRepository ingredientRepository, OnboardingPreferences preferences,
      SubstituteInitializer substituteInitializer) {
    return new SuggestionViewModel(mealRepository, bundledRecipeLoader, inventoryRepository, ingredientRepository, preferences, substituteInitializer);
  }
}
