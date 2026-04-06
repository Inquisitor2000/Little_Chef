package com.familymealplanner.ui.screens;

import com.familymealplanner.data.local.ImageStorage;
import com.familymealplanner.data.preferences.OnboardingPreferences;
import com.familymealplanner.domain.repository.IngredientRepository;
import com.familymealplanner.domain.repository.MealRepository;
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
public final class ManualRecipeViewModel_Factory implements Factory<ManualRecipeViewModel> {
  private final Provider<MealRepository> mealRepositoryProvider;

  private final Provider<IngredientRepository> ingredientRepositoryProvider;

  private final Provider<ImageStorage> imageStorageProvider;

  private final Provider<OnboardingPreferences> preferencesProvider;

  private final Provider<IngredientMatcher> ingredientMatcherProvider;

  public ManualRecipeViewModel_Factory(Provider<MealRepository> mealRepositoryProvider,
      Provider<IngredientRepository> ingredientRepositoryProvider,
      Provider<ImageStorage> imageStorageProvider,
      Provider<OnboardingPreferences> preferencesProvider,
      Provider<IngredientMatcher> ingredientMatcherProvider) {
    this.mealRepositoryProvider = mealRepositoryProvider;
    this.ingredientRepositoryProvider = ingredientRepositoryProvider;
    this.imageStorageProvider = imageStorageProvider;
    this.preferencesProvider = preferencesProvider;
    this.ingredientMatcherProvider = ingredientMatcherProvider;
  }

  @Override
  public ManualRecipeViewModel get() {
    return newInstance(mealRepositoryProvider.get(), ingredientRepositoryProvider.get(), imageStorageProvider.get(), preferencesProvider.get(), ingredientMatcherProvider.get());
  }

  public static ManualRecipeViewModel_Factory create(
      Provider<MealRepository> mealRepositoryProvider,
      Provider<IngredientRepository> ingredientRepositoryProvider,
      Provider<ImageStorage> imageStorageProvider,
      Provider<OnboardingPreferences> preferencesProvider,
      Provider<IngredientMatcher> ingredientMatcherProvider) {
    return new ManualRecipeViewModel_Factory(mealRepositoryProvider, ingredientRepositoryProvider, imageStorageProvider, preferencesProvider, ingredientMatcherProvider);
  }

  public static ManualRecipeViewModel newInstance(MealRepository mealRepository,
      IngredientRepository ingredientRepository, ImageStorage imageStorage,
      OnboardingPreferences preferences, IngredientMatcher ingredientMatcher) {
    return new ManualRecipeViewModel(mealRepository, ingredientRepository, imageStorage, preferences, ingredientMatcher);
  }
}
