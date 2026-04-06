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
public final class AddIngredientSubstituteUseCase_Factory implements Factory<AddIngredientSubstituteUseCase> {
  private final Provider<IngredientRepository> ingredientRepositoryProvider;

  public AddIngredientSubstituteUseCase_Factory(
      Provider<IngredientRepository> ingredientRepositoryProvider) {
    this.ingredientRepositoryProvider = ingredientRepositoryProvider;
  }

  @Override
  public AddIngredientSubstituteUseCase get() {
    return newInstance(ingredientRepositoryProvider.get());
  }

  public static AddIngredientSubstituteUseCase_Factory create(
      Provider<IngredientRepository> ingredientRepositoryProvider) {
    return new AddIngredientSubstituteUseCase_Factory(ingredientRepositoryProvider);
  }

  public static AddIngredientSubstituteUseCase newInstance(
      IngredientRepository ingredientRepository) {
    return new AddIngredientSubstituteUseCase(ingredientRepository);
  }
}
