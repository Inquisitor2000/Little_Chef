package com.familymealplanner.ui.screens;

import com.familymealplanner.data.local.SubstituteInitializer;
import com.familymealplanner.data.local.TranslationSystem;
import com.familymealplanner.data.preferences.OnboardingPreferences;
import com.familymealplanner.domain.repository.GroceryRepository;
import com.familymealplanner.domain.repository.InventoryRepository;
import com.familymealplanner.domain.repository.MealPlanRepository;
import com.familymealplanner.domain.repository.MealRepository;
import com.familymealplanner.domain.usecase.AbortCookingUseCase;
import com.familymealplanner.domain.usecase.CompleteCookingUseCase;
import com.familymealplanner.domain.usecase.CreateMealPlanUseCase;
import com.familymealplanner.domain.usecase.StartCookingUseCase;
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
public final class PlanViewModel_Factory implements Factory<PlanViewModel> {
  private final Provider<MealPlanRepository> mealPlanRepositoryProvider;

  private final Provider<MealRepository> mealRepositoryProvider;

  private final Provider<InventoryRepository> inventoryRepositoryProvider;

  private final Provider<GroceryRepository> groceryRepositoryProvider;

  private final Provider<CreateMealPlanUseCase> createMealPlanUseCaseProvider;

  private final Provider<StartCookingUseCase> startCookingUseCaseProvider;

  private final Provider<CompleteCookingUseCase> completeCookingUseCaseProvider;

  private final Provider<AbortCookingUseCase> abortCookingUseCaseProvider;

  private final Provider<OnboardingPreferences> preferencesProvider;

  private final Provider<SubstituteInitializer> substituteInitializerProvider;

  private final Provider<TranslationSystem> translationSystemProvider;

  public PlanViewModel_Factory(Provider<MealPlanRepository> mealPlanRepositoryProvider,
      Provider<MealRepository> mealRepositoryProvider,
      Provider<InventoryRepository> inventoryRepositoryProvider,
      Provider<GroceryRepository> groceryRepositoryProvider,
      Provider<CreateMealPlanUseCase> createMealPlanUseCaseProvider,
      Provider<StartCookingUseCase> startCookingUseCaseProvider,
      Provider<CompleteCookingUseCase> completeCookingUseCaseProvider,
      Provider<AbortCookingUseCase> abortCookingUseCaseProvider,
      Provider<OnboardingPreferences> preferencesProvider,
      Provider<SubstituteInitializer> substituteInitializerProvider,
      Provider<TranslationSystem> translationSystemProvider) {
    this.mealPlanRepositoryProvider = mealPlanRepositoryProvider;
    this.mealRepositoryProvider = mealRepositoryProvider;
    this.inventoryRepositoryProvider = inventoryRepositoryProvider;
    this.groceryRepositoryProvider = groceryRepositoryProvider;
    this.createMealPlanUseCaseProvider = createMealPlanUseCaseProvider;
    this.startCookingUseCaseProvider = startCookingUseCaseProvider;
    this.completeCookingUseCaseProvider = completeCookingUseCaseProvider;
    this.abortCookingUseCaseProvider = abortCookingUseCaseProvider;
    this.preferencesProvider = preferencesProvider;
    this.substituteInitializerProvider = substituteInitializerProvider;
    this.translationSystemProvider = translationSystemProvider;
  }

  @Override
  public PlanViewModel get() {
    return newInstance(mealPlanRepositoryProvider.get(), mealRepositoryProvider.get(), inventoryRepositoryProvider.get(), groceryRepositoryProvider.get(), createMealPlanUseCaseProvider.get(), startCookingUseCaseProvider.get(), completeCookingUseCaseProvider.get(), abortCookingUseCaseProvider.get(), preferencesProvider.get(), substituteInitializerProvider.get(), translationSystemProvider.get());
  }

  public static PlanViewModel_Factory create(
      Provider<MealPlanRepository> mealPlanRepositoryProvider,
      Provider<MealRepository> mealRepositoryProvider,
      Provider<InventoryRepository> inventoryRepositoryProvider,
      Provider<GroceryRepository> groceryRepositoryProvider,
      Provider<CreateMealPlanUseCase> createMealPlanUseCaseProvider,
      Provider<StartCookingUseCase> startCookingUseCaseProvider,
      Provider<CompleteCookingUseCase> completeCookingUseCaseProvider,
      Provider<AbortCookingUseCase> abortCookingUseCaseProvider,
      Provider<OnboardingPreferences> preferencesProvider,
      Provider<SubstituteInitializer> substituteInitializerProvider,
      Provider<TranslationSystem> translationSystemProvider) {
    return new PlanViewModel_Factory(mealPlanRepositoryProvider, mealRepositoryProvider, inventoryRepositoryProvider, groceryRepositoryProvider, createMealPlanUseCaseProvider, startCookingUseCaseProvider, completeCookingUseCaseProvider, abortCookingUseCaseProvider, preferencesProvider, substituteInitializerProvider, translationSystemProvider);
  }

  public static PlanViewModel newInstance(MealPlanRepository mealPlanRepository,
      MealRepository mealRepository, InventoryRepository inventoryRepository,
      GroceryRepository groceryRepository, CreateMealPlanUseCase createMealPlanUseCase,
      StartCookingUseCase startCookingUseCase, CompleteCookingUseCase completeCookingUseCase,
      AbortCookingUseCase abortCookingUseCase, OnboardingPreferences preferences,
      SubstituteInitializer substituteInitializer, TranslationSystem translationSystem) {
    return new PlanViewModel(mealPlanRepository, mealRepository, inventoryRepository, groceryRepository, createMealPlanUseCase, startCookingUseCase, completeCookingUseCase, abortCookingUseCase, preferences, substituteInitializer, translationSystem);
  }
}
