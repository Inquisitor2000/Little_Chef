package com.familymealplanner.ui.screens;

import com.familymealplanner.domain.repository.IngredientRepository;
import com.familymealplanner.domain.usecase.AddIngredientSubstituteUseCase;
import com.familymealplanner.domain.usecase.CreateIngredientUseCase;
import com.familymealplanner.domain.usecase.DeleteIngredientUseCase;
import com.familymealplanner.domain.usecase.FixIngredientCategoriesUseCase;
import com.familymealplanner.domain.usecase.UpdateIngredientUseCase;
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
public final class IngredientsViewModel_Factory implements Factory<IngredientsViewModel> {
  private final Provider<IngredientRepository> ingredientRepositoryProvider;

  private final Provider<CreateIngredientUseCase> createIngredientUseCaseProvider;

  private final Provider<UpdateIngredientUseCase> updateIngredientUseCaseProvider;

  private final Provider<DeleteIngredientUseCase> deleteIngredientUseCaseProvider;

  private final Provider<AddIngredientSubstituteUseCase> addIngredientSubstituteUseCaseProvider;

  private final Provider<FixIngredientCategoriesUseCase> fixIngredientCategoriesUseCaseProvider;

  public IngredientsViewModel_Factory(Provider<IngredientRepository> ingredientRepositoryProvider,
      Provider<CreateIngredientUseCase> createIngredientUseCaseProvider,
      Provider<UpdateIngredientUseCase> updateIngredientUseCaseProvider,
      Provider<DeleteIngredientUseCase> deleteIngredientUseCaseProvider,
      Provider<AddIngredientSubstituteUseCase> addIngredientSubstituteUseCaseProvider,
      Provider<FixIngredientCategoriesUseCase> fixIngredientCategoriesUseCaseProvider) {
    this.ingredientRepositoryProvider = ingredientRepositoryProvider;
    this.createIngredientUseCaseProvider = createIngredientUseCaseProvider;
    this.updateIngredientUseCaseProvider = updateIngredientUseCaseProvider;
    this.deleteIngredientUseCaseProvider = deleteIngredientUseCaseProvider;
    this.addIngredientSubstituteUseCaseProvider = addIngredientSubstituteUseCaseProvider;
    this.fixIngredientCategoriesUseCaseProvider = fixIngredientCategoriesUseCaseProvider;
  }

  @Override
  public IngredientsViewModel get() {
    return newInstance(ingredientRepositoryProvider.get(), createIngredientUseCaseProvider.get(), updateIngredientUseCaseProvider.get(), deleteIngredientUseCaseProvider.get(), addIngredientSubstituteUseCaseProvider.get(), fixIngredientCategoriesUseCaseProvider.get());
  }

  public static IngredientsViewModel_Factory create(
      Provider<IngredientRepository> ingredientRepositoryProvider,
      Provider<CreateIngredientUseCase> createIngredientUseCaseProvider,
      Provider<UpdateIngredientUseCase> updateIngredientUseCaseProvider,
      Provider<DeleteIngredientUseCase> deleteIngredientUseCaseProvider,
      Provider<AddIngredientSubstituteUseCase> addIngredientSubstituteUseCaseProvider,
      Provider<FixIngredientCategoriesUseCase> fixIngredientCategoriesUseCaseProvider) {
    return new IngredientsViewModel_Factory(ingredientRepositoryProvider, createIngredientUseCaseProvider, updateIngredientUseCaseProvider, deleteIngredientUseCaseProvider, addIngredientSubstituteUseCaseProvider, fixIngredientCategoriesUseCaseProvider);
  }

  public static IngredientsViewModel newInstance(IngredientRepository ingredientRepository,
      CreateIngredientUseCase createIngredientUseCase,
      UpdateIngredientUseCase updateIngredientUseCase,
      DeleteIngredientUseCase deleteIngredientUseCase,
      AddIngredientSubstituteUseCase addIngredientSubstituteUseCase,
      FixIngredientCategoriesUseCase fixIngredientCategoriesUseCase) {
    return new IngredientsViewModel(ingredientRepository, createIngredientUseCase, updateIngredientUseCase, deleteIngredientUseCase, addIngredientSubstituteUseCase, fixIngredientCategoriesUseCase);
  }
}
