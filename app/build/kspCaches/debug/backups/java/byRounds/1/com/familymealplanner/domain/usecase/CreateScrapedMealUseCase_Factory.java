package com.familymealplanner.domain.usecase;

import com.familymealplanner.data.local.ImageStorage;
import com.familymealplanner.data.local.TranslationSystem;
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
public final class CreateScrapedMealUseCase_Factory implements Factory<CreateScrapedMealUseCase> {
  private final Provider<MealRepository> mealRepositoryProvider;

  private final Provider<IngredientRepository> ingredientRepositoryProvider;

  private final Provider<ImageStorage> imageStorageProvider;

  private final Provider<IngredientMatcher> ingredientMatcherProvider;

  private final Provider<TranslationSystem> translationSystemProvider;

  public CreateScrapedMealUseCase_Factory(Provider<MealRepository> mealRepositoryProvider,
      Provider<IngredientRepository> ingredientRepositoryProvider,
      Provider<ImageStorage> imageStorageProvider,
      Provider<IngredientMatcher> ingredientMatcherProvider,
      Provider<TranslationSystem> translationSystemProvider) {
    this.mealRepositoryProvider = mealRepositoryProvider;
    this.ingredientRepositoryProvider = ingredientRepositoryProvider;
    this.imageStorageProvider = imageStorageProvider;
    this.ingredientMatcherProvider = ingredientMatcherProvider;
    this.translationSystemProvider = translationSystemProvider;
  }

  @Override
  public CreateScrapedMealUseCase get() {
    return newInstance(mealRepositoryProvider.get(), ingredientRepositoryProvider.get(), imageStorageProvider.get(), ingredientMatcherProvider.get(), translationSystemProvider.get());
  }

  public static CreateScrapedMealUseCase_Factory create(
      Provider<MealRepository> mealRepositoryProvider,
      Provider<IngredientRepository> ingredientRepositoryProvider,
      Provider<ImageStorage> imageStorageProvider,
      Provider<IngredientMatcher> ingredientMatcherProvider,
      Provider<TranslationSystem> translationSystemProvider) {
    return new CreateScrapedMealUseCase_Factory(mealRepositoryProvider, ingredientRepositoryProvider, imageStorageProvider, ingredientMatcherProvider, translationSystemProvider);
  }

  public static CreateScrapedMealUseCase newInstance(MealRepository mealRepository,
      IngredientRepository ingredientRepository, ImageStorage imageStorage,
      IngredientMatcher ingredientMatcher, TranslationSystem translationSystem) {
    return new CreateScrapedMealUseCase(mealRepository, ingredientRepository, imageStorage, ingredientMatcher, translationSystem);
  }
}
