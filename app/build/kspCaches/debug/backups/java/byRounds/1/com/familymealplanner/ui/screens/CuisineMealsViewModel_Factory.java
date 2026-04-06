package com.familymealplanner.ui.screens;

import com.familymealplanner.data.local.BundledRecipeLoader;
import com.familymealplanner.data.local.TranslationSystem;
import com.familymealplanner.data.preferences.FavoriteRecipesPreferences;
import com.familymealplanner.data.preferences.OnboardingPreferences;
import com.familymealplanner.domain.repository.IngredientRepository;
import com.familymealplanner.domain.usecase.PreloadCuisineAllergensUseCase;
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
public final class CuisineMealsViewModel_Factory implements Factory<CuisineMealsViewModel> {
  private final Provider<BundledRecipeLoader> bundledRecipeLoaderProvider;

  private final Provider<TranslationSystem> translationSystemProvider;

  private final Provider<IngredientRepository> ingredientRepositoryProvider;

  private final Provider<OnboardingPreferences> onboardingPreferencesProvider;

  private final Provider<IngredientMatcher> ingredientMatcherProvider;

  private final Provider<PreloadCuisineAllergensUseCase> preloadCuisineAllergensUseCaseProvider;

  private final Provider<FavoriteRecipesPreferences> favoriteRecipesPreferencesProvider;

  public CuisineMealsViewModel_Factory(Provider<BundledRecipeLoader> bundledRecipeLoaderProvider,
      Provider<TranslationSystem> translationSystemProvider,
      Provider<IngredientRepository> ingredientRepositoryProvider,
      Provider<OnboardingPreferences> onboardingPreferencesProvider,
      Provider<IngredientMatcher> ingredientMatcherProvider,
      Provider<PreloadCuisineAllergensUseCase> preloadCuisineAllergensUseCaseProvider,
      Provider<FavoriteRecipesPreferences> favoriteRecipesPreferencesProvider) {
    this.bundledRecipeLoaderProvider = bundledRecipeLoaderProvider;
    this.translationSystemProvider = translationSystemProvider;
    this.ingredientRepositoryProvider = ingredientRepositoryProvider;
    this.onboardingPreferencesProvider = onboardingPreferencesProvider;
    this.ingredientMatcherProvider = ingredientMatcherProvider;
    this.preloadCuisineAllergensUseCaseProvider = preloadCuisineAllergensUseCaseProvider;
    this.favoriteRecipesPreferencesProvider = favoriteRecipesPreferencesProvider;
  }

  @Override
  public CuisineMealsViewModel get() {
    return newInstance(bundledRecipeLoaderProvider.get(), translationSystemProvider.get(), ingredientRepositoryProvider.get(), onboardingPreferencesProvider.get(), ingredientMatcherProvider.get(), preloadCuisineAllergensUseCaseProvider.get(), favoriteRecipesPreferencesProvider.get());
  }

  public static CuisineMealsViewModel_Factory create(
      Provider<BundledRecipeLoader> bundledRecipeLoaderProvider,
      Provider<TranslationSystem> translationSystemProvider,
      Provider<IngredientRepository> ingredientRepositoryProvider,
      Provider<OnboardingPreferences> onboardingPreferencesProvider,
      Provider<IngredientMatcher> ingredientMatcherProvider,
      Provider<PreloadCuisineAllergensUseCase> preloadCuisineAllergensUseCaseProvider,
      Provider<FavoriteRecipesPreferences> favoriteRecipesPreferencesProvider) {
    return new CuisineMealsViewModel_Factory(bundledRecipeLoaderProvider, translationSystemProvider, ingredientRepositoryProvider, onboardingPreferencesProvider, ingredientMatcherProvider, preloadCuisineAllergensUseCaseProvider, favoriteRecipesPreferencesProvider);
  }

  public static CuisineMealsViewModel newInstance(BundledRecipeLoader bundledRecipeLoader,
      TranslationSystem translationSystem, IngredientRepository ingredientRepository,
      OnboardingPreferences onboardingPreferences, IngredientMatcher ingredientMatcher,
      PreloadCuisineAllergensUseCase preloadCuisineAllergensUseCase,
      FavoriteRecipesPreferences favoriteRecipesPreferences) {
    return new CuisineMealsViewModel(bundledRecipeLoader, translationSystem, ingredientRepository, onboardingPreferences, ingredientMatcher, preloadCuisineAllergensUseCase, favoriteRecipesPreferences);
  }
}
