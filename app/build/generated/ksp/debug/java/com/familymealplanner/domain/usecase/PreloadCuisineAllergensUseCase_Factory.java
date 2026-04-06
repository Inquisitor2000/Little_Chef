package com.familymealplanner.domain.usecase;

import com.familymealplanner.data.local.BundledRecipeLoader;
import com.familymealplanner.data.local.TranslationSystem;
import com.familymealplanner.domain.util.IngredientMatcher;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class PreloadCuisineAllergensUseCase_Factory implements Factory<PreloadCuisineAllergensUseCase> {
  private final Provider<BundledRecipeLoader> bundledRecipeLoaderProvider;

  private final Provider<TranslationSystem> translationSystemProvider;

  private final Provider<IngredientMatcher> ingredientMatcherProvider;

  public PreloadCuisineAllergensUseCase_Factory(
      Provider<BundledRecipeLoader> bundledRecipeLoaderProvider,
      Provider<TranslationSystem> translationSystemProvider,
      Provider<IngredientMatcher> ingredientMatcherProvider) {
    this.bundledRecipeLoaderProvider = bundledRecipeLoaderProvider;
    this.translationSystemProvider = translationSystemProvider;
    this.ingredientMatcherProvider = ingredientMatcherProvider;
  }

  @Override
  public PreloadCuisineAllergensUseCase get() {
    return newInstance(bundledRecipeLoaderProvider.get(), translationSystemProvider.get(), ingredientMatcherProvider.get());
  }

  public static PreloadCuisineAllergensUseCase_Factory create(
      Provider<BundledRecipeLoader> bundledRecipeLoaderProvider,
      Provider<TranslationSystem> translationSystemProvider,
      Provider<IngredientMatcher> ingredientMatcherProvider) {
    return new PreloadCuisineAllergensUseCase_Factory(bundledRecipeLoaderProvider, translationSystemProvider, ingredientMatcherProvider);
  }

  public static PreloadCuisineAllergensUseCase newInstance(BundledRecipeLoader bundledRecipeLoader,
      TranslationSystem translationSystem, IngredientMatcher ingredientMatcher) {
    return new PreloadCuisineAllergensUseCase(bundledRecipeLoader, translationSystem, ingredientMatcher);
  }
}
