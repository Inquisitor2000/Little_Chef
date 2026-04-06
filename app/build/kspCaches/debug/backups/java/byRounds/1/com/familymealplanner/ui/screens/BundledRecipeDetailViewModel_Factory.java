package com.familymealplanner.ui.screens;

import android.content.Context;
import com.familymealplanner.data.local.BundledRecipeLoader;
import com.familymealplanner.data.local.TranslationSystem;
import com.familymealplanner.data.preferences.LocaleManager;
import com.familymealplanner.data.preferences.OnboardingPreferences;
import com.familymealplanner.domain.repository.GroceryRepository;
import com.familymealplanner.domain.repository.IngredientRepository;
import com.familymealplanner.domain.repository.InventoryRepository;
import com.familymealplanner.domain.repository.MealPlanRepository;
import com.familymealplanner.domain.repository.MealRepository;
import com.familymealplanner.domain.usecase.CheckRecipeIngredientsUseCase;
import com.familymealplanner.domain.usecase.CreateMealPlanUseCase;
import com.familymealplanner.domain.usecase.CreateScrapedMealUseCase;
import com.familymealplanner.domain.usecase.StartCookingUseCase;
import com.familymealplanner.domain.util.IngredientMatcher;
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
public final class BundledRecipeDetailViewModel_Factory implements Factory<BundledRecipeDetailViewModel> {
  private final Provider<Context> contextProvider;

  private final Provider<LocaleManager> localeManagerProvider;

  private final Provider<BundledRecipeLoader> bundledRecipeLoaderProvider;

  private final Provider<TranslationSystem> translationSystemProvider;

  private final Provider<CheckRecipeIngredientsUseCase> checkRecipeIngredientsUseCaseProvider;

  private final Provider<GroceryRepository> groceryRepositoryProvider;

  private final Provider<OnboardingPreferences> preferencesProvider;

  private final Provider<CreateScrapedMealUseCase> createScrapedMealUseCaseProvider;

  private final Provider<CreateMealPlanUseCase> createMealPlanUseCaseProvider;

  private final Provider<StartCookingUseCase> startCookingUseCaseProvider;

  private final Provider<MealRepository> mealRepositoryProvider;

  private final Provider<MealPlanRepository> mealPlanRepositoryProvider;

  private final Provider<InventoryRepository> inventoryRepositoryProvider;

  private final Provider<IngredientRepository> ingredientRepositoryProvider;

  private final Provider<IngredientMatcher> ingredientMatcherProvider;

  public BundledRecipeDetailViewModel_Factory(Provider<Context> contextProvider,
      Provider<LocaleManager> localeManagerProvider,
      Provider<BundledRecipeLoader> bundledRecipeLoaderProvider,
      Provider<TranslationSystem> translationSystemProvider,
      Provider<CheckRecipeIngredientsUseCase> checkRecipeIngredientsUseCaseProvider,
      Provider<GroceryRepository> groceryRepositoryProvider,
      Provider<OnboardingPreferences> preferencesProvider,
      Provider<CreateScrapedMealUseCase> createScrapedMealUseCaseProvider,
      Provider<CreateMealPlanUseCase> createMealPlanUseCaseProvider,
      Provider<StartCookingUseCase> startCookingUseCaseProvider,
      Provider<MealRepository> mealRepositoryProvider,
      Provider<MealPlanRepository> mealPlanRepositoryProvider,
      Provider<InventoryRepository> inventoryRepositoryProvider,
      Provider<IngredientRepository> ingredientRepositoryProvider,
      Provider<IngredientMatcher> ingredientMatcherProvider) {
    this.contextProvider = contextProvider;
    this.localeManagerProvider = localeManagerProvider;
    this.bundledRecipeLoaderProvider = bundledRecipeLoaderProvider;
    this.translationSystemProvider = translationSystemProvider;
    this.checkRecipeIngredientsUseCaseProvider = checkRecipeIngredientsUseCaseProvider;
    this.groceryRepositoryProvider = groceryRepositoryProvider;
    this.preferencesProvider = preferencesProvider;
    this.createScrapedMealUseCaseProvider = createScrapedMealUseCaseProvider;
    this.createMealPlanUseCaseProvider = createMealPlanUseCaseProvider;
    this.startCookingUseCaseProvider = startCookingUseCaseProvider;
    this.mealRepositoryProvider = mealRepositoryProvider;
    this.mealPlanRepositoryProvider = mealPlanRepositoryProvider;
    this.inventoryRepositoryProvider = inventoryRepositoryProvider;
    this.ingredientRepositoryProvider = ingredientRepositoryProvider;
    this.ingredientMatcherProvider = ingredientMatcherProvider;
  }

  @Override
  public BundledRecipeDetailViewModel get() {
    return newInstance(contextProvider.get(), localeManagerProvider.get(), bundledRecipeLoaderProvider.get(), translationSystemProvider.get(), checkRecipeIngredientsUseCaseProvider.get(), groceryRepositoryProvider.get(), preferencesProvider.get(), createScrapedMealUseCaseProvider.get(), createMealPlanUseCaseProvider.get(), startCookingUseCaseProvider.get(), mealRepositoryProvider.get(), mealPlanRepositoryProvider.get(), inventoryRepositoryProvider.get(), ingredientRepositoryProvider.get(), ingredientMatcherProvider.get());
  }

  public static BundledRecipeDetailViewModel_Factory create(Provider<Context> contextProvider,
      Provider<LocaleManager> localeManagerProvider,
      Provider<BundledRecipeLoader> bundledRecipeLoaderProvider,
      Provider<TranslationSystem> translationSystemProvider,
      Provider<CheckRecipeIngredientsUseCase> checkRecipeIngredientsUseCaseProvider,
      Provider<GroceryRepository> groceryRepositoryProvider,
      Provider<OnboardingPreferences> preferencesProvider,
      Provider<CreateScrapedMealUseCase> createScrapedMealUseCaseProvider,
      Provider<CreateMealPlanUseCase> createMealPlanUseCaseProvider,
      Provider<StartCookingUseCase> startCookingUseCaseProvider,
      Provider<MealRepository> mealRepositoryProvider,
      Provider<MealPlanRepository> mealPlanRepositoryProvider,
      Provider<InventoryRepository> inventoryRepositoryProvider,
      Provider<IngredientRepository> ingredientRepositoryProvider,
      Provider<IngredientMatcher> ingredientMatcherProvider) {
    return new BundledRecipeDetailViewModel_Factory(contextProvider, localeManagerProvider, bundledRecipeLoaderProvider, translationSystemProvider, checkRecipeIngredientsUseCaseProvider, groceryRepositoryProvider, preferencesProvider, createScrapedMealUseCaseProvider, createMealPlanUseCaseProvider, startCookingUseCaseProvider, mealRepositoryProvider, mealPlanRepositoryProvider, inventoryRepositoryProvider, ingredientRepositoryProvider, ingredientMatcherProvider);
  }

  public static BundledRecipeDetailViewModel newInstance(Context context,
      LocaleManager localeManager, BundledRecipeLoader bundledRecipeLoader,
      TranslationSystem translationSystem,
      CheckRecipeIngredientsUseCase checkRecipeIngredientsUseCase,
      GroceryRepository groceryRepository, OnboardingPreferences preferences,
      CreateScrapedMealUseCase createScrapedMealUseCase,
      CreateMealPlanUseCase createMealPlanUseCase, StartCookingUseCase startCookingUseCase,
      MealRepository mealRepository, MealPlanRepository mealPlanRepository,
      InventoryRepository inventoryRepository, IngredientRepository ingredientRepository,
      IngredientMatcher ingredientMatcher) {
    return new BundledRecipeDetailViewModel(context, localeManager, bundledRecipeLoader, translationSystem, checkRecipeIngredientsUseCase, groceryRepository, preferences, createScrapedMealUseCase, createMealPlanUseCase, startCookingUseCase, mealRepository, mealPlanRepository, inventoryRepository, ingredientRepository, ingredientMatcher);
  }
}
