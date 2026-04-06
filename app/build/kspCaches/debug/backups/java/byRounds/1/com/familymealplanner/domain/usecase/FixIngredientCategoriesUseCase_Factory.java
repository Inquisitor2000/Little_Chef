package com.familymealplanner.domain.usecase;

import com.familymealplanner.domain.repository.IngredientRepository;
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
public final class FixIngredientCategoriesUseCase_Factory implements Factory<FixIngredientCategoriesUseCase> {
  private final Provider<IngredientRepository> ingredientRepositoryProvider;

  public FixIngredientCategoriesUseCase_Factory(
      Provider<IngredientRepository> ingredientRepositoryProvider) {
    this.ingredientRepositoryProvider = ingredientRepositoryProvider;
  }

  @Override
  public FixIngredientCategoriesUseCase get() {
    return newInstance(ingredientRepositoryProvider.get());
  }

  public static FixIngredientCategoriesUseCase_Factory create(
      Provider<IngredientRepository> ingredientRepositoryProvider) {
    return new FixIngredientCategoriesUseCase_Factory(ingredientRepositoryProvider);
  }

  public static FixIngredientCategoriesUseCase newInstance(
      IngredientRepository ingredientRepository) {
    return new FixIngredientCategoriesUseCase(ingredientRepository);
  }
}
