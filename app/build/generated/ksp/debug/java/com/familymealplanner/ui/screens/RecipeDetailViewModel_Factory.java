package com.familymealplanner.ui.screens;

import android.content.Context;
import com.familymealplanner.data.local.ImageStorage;
import com.familymealplanner.data.local.TranslationSystem;
import com.familymealplanner.data.preferences.LocaleManager;
import com.familymealplanner.data.preferences.OnboardingPreferences;
import com.familymealplanner.domain.repository.GroceryRepository;
import com.familymealplanner.domain.repository.InventoryRepository;
import com.familymealplanner.domain.repository.MealPlanRepository;
import com.familymealplanner.domain.repository.MealRepository;
import com.familymealplanner.domain.usecase.CheckRecipeIngredientsUseCase;
import com.familymealplanner.domain.usecase.CreateMealPlanUseCase;
import com.familymealplanner.domain.usecase.DeleteMealUseCase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class RecipeDetailViewModel_Factory implements Factory<RecipeDetailViewModel> {
  private final Provider<Context> contextProvider;

  private final Provider<LocaleManager> localeManagerProvider;

  private final Provider<MealRepository> mealRepositoryProvider;

  private final Provider<DeleteMealUseCase> deleteMealUseCaseProvider;

  private final Provider<ImageStorage> imageStorageProvider;

  private final Provider<CheckRecipeIngredientsUseCase> checkRecipeIngredientsUseCaseProvider;

  private final Provider<GroceryRepository> groceryRepositoryProvider;

  private final Provider<CreateMealPlanUseCase> createMealPlanUseCaseProvider;

  private final Provider<OnboardingPreferences> preferencesProvider;

  private final Provider<MealPlanRepository> mealPlanRepositoryProvider;

  private final Provider<InventoryRepository> inventoryRepositoryProvider;

  private final Provider<TranslationSystem> translationSystemProvider;

  public RecipeDetailViewModel_Factory(Provider<Context> contextProvider,
      Provider<LocaleManager> localeManagerProvider,
      Provider<MealRepository> mealRepositoryProvider,
      Provider<DeleteMealUseCase> deleteMealUseCaseProvider,
      Provider<ImageStorage> imageStorageProvider,
      Provider<CheckRecipeIngredientsUseCase> checkRecipeIngredientsUseCaseProvider,
      Provider<GroceryRepository> groceryRepositoryProvider,
      Provider<CreateMealPlanUseCase> createMealPlanUseCaseProvider,
      Provider<OnboardingPreferences> preferencesProvider,
      Provider<MealPlanRepository> mealPlanRepositoryProvider,
      Provider<InventoryRepository> inventoryRepositoryProvider,
      Provider<TranslationSystem> translationSystemProvider) {
    this.contextProvider = contextProvider;
    this.localeManagerProvider = localeManagerProvider;
    this.mealRepositoryProvider = mealRepositoryProvider;
    this.deleteMealUseCaseProvider = deleteMealUseCaseProvider;
    this.imageStorageProvider = imageStorageProvider;
    this.checkRecipeIngredientsUseCaseProvider = checkRecipeIngredientsUseCaseProvider;
    this.groceryRepositoryProvider = groceryRepositoryProvider;
    this.createMealPlanUseCaseProvider = createMealPlanUseCaseProvider;
    this.preferencesProvider = preferencesProvider;
    this.mealPlanRepositoryProvider = mealPlanRepositoryProvider;
    this.inventoryRepositoryProvider = inventoryRepositoryProvider;
    this.translationSystemProvider = translationSystemProvider;
  }

  @Override
  public RecipeDetailViewModel get() {
    return newInstance(contextProvider.get(), localeManagerProvider.get(), mealRepositoryProvider.get(), deleteMealUseCaseProvider.get(), imageStorageProvider.get(), checkRecipeIngredientsUseCaseProvider.get(), groceryRepositoryProvider.get(), createMealPlanUseCaseProvider.get(), preferencesProvider.get(), mealPlanRepositoryProvider.get(), inventoryRepositoryProvider.get(), translationSystemProvider.get());
  }

  public static RecipeDetailViewModel_Factory create(Provider<Context> contextProvider,
      Provider<LocaleManager> localeManagerProvider,
      Provider<MealRepository> mealRepositoryProvider,
      Provider<DeleteMealUseCase> deleteMealUseCaseProvider,
      Provider<ImageStorage> imageStorageProvider,
      Provider<CheckRecipeIngredientsUseCase> checkRecipeIngredientsUseCaseProvider,
      Provider<GroceryRepository> groceryRepositoryProvider,
      Provider<CreateMealPlanUseCase> createMealPlanUseCaseProvider,
      Provider<OnboardingPreferences> preferencesProvider,
      Provider<MealPlanRepository> mealPlanRepositoryProvider,
      Provider<InventoryRepository> inventoryRepositoryProvider,
      Provider<TranslationSystem> translationSystemProvider) {
    return new RecipeDetailViewModel_Factory(contextProvider, localeManagerProvider, mealRepositoryProvider, deleteMealUseCaseProvider, imageStorageProvider, checkRecipeIngredientsUseCaseProvider, groceryRepositoryProvider, createMealPlanUseCaseProvider, preferencesProvider, mealPlanRepositoryProvider, inventoryRepositoryProvider, translationSystemProvider);
  }

  public static RecipeDetailViewModel newInstance(Context context, LocaleManager localeManager,
      MealRepository mealRepository, DeleteMealUseCase deleteMealUseCase, ImageStorage imageStorage,
      CheckRecipeIngredientsUseCase checkRecipeIngredientsUseCase,
      GroceryRepository groceryRepository, CreateMealPlanUseCase createMealPlanUseCase,
      OnboardingPreferences preferences, MealPlanRepository mealPlanRepository,
      InventoryRepository inventoryRepository, TranslationSystem translationSystem) {
    return new RecipeDetailViewModel(context, localeManager, mealRepository, deleteMealUseCase, imageStorage, checkRecipeIngredientsUseCase, groceryRepository, createMealPlanUseCase, preferences, mealPlanRepository, inventoryRepository, translationSystem);
  }
}
