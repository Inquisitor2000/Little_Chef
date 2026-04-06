package com.familymealplanner.ui.screens;

import com.familymealplanner.data.local.TranslationSystem;
import com.familymealplanner.domain.repository.MealRepository;
import com.familymealplanner.domain.usecase.CreateMealUseCase;
import com.familymealplanner.domain.usecase.DeleteMealUseCase;
import com.familymealplanner.domain.usecase.UpdateMealUseCase;
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
public final class MealsViewModel_Factory implements Factory<MealsViewModel> {
  private final Provider<MealRepository> mealRepositoryProvider;

  private final Provider<TranslationSystem> translationSystemProvider;

  private final Provider<CreateMealUseCase> createMealUseCaseProvider;

  private final Provider<UpdateMealUseCase> updateMealUseCaseProvider;

  private final Provider<DeleteMealUseCase> deleteMealUseCaseProvider;

  public MealsViewModel_Factory(Provider<MealRepository> mealRepositoryProvider,
      Provider<TranslationSystem> translationSystemProvider,
      Provider<CreateMealUseCase> createMealUseCaseProvider,
      Provider<UpdateMealUseCase> updateMealUseCaseProvider,
      Provider<DeleteMealUseCase> deleteMealUseCaseProvider) {
    this.mealRepositoryProvider = mealRepositoryProvider;
    this.translationSystemProvider = translationSystemProvider;
    this.createMealUseCaseProvider = createMealUseCaseProvider;
    this.updateMealUseCaseProvider = updateMealUseCaseProvider;
    this.deleteMealUseCaseProvider = deleteMealUseCaseProvider;
  }

  @Override
  public MealsViewModel get() {
    return newInstance(mealRepositoryProvider.get(), translationSystemProvider.get(), createMealUseCaseProvider.get(), updateMealUseCaseProvider.get(), deleteMealUseCaseProvider.get());
  }

  public static MealsViewModel_Factory create(Provider<MealRepository> mealRepositoryProvider,
      Provider<TranslationSystem> translationSystemProvider,
      Provider<CreateMealUseCase> createMealUseCaseProvider,
      Provider<UpdateMealUseCase> updateMealUseCaseProvider,
      Provider<DeleteMealUseCase> deleteMealUseCaseProvider) {
    return new MealsViewModel_Factory(mealRepositoryProvider, translationSystemProvider, createMealUseCaseProvider, updateMealUseCaseProvider, deleteMealUseCaseProvider);
  }

  public static MealsViewModel newInstance(MealRepository mealRepository,
      TranslationSystem translationSystem, CreateMealUseCase createMealUseCase,
      UpdateMealUseCase updateMealUseCase, DeleteMealUseCase deleteMealUseCase) {
    return new MealsViewModel(mealRepository, translationSystem, createMealUseCase, updateMealUseCase, deleteMealUseCase);
  }
}
