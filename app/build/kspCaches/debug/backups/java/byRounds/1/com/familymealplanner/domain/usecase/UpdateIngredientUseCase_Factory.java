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
public final class UpdateIngredientUseCase_Factory implements Factory<UpdateIngredientUseCase> {
  private final Provider<IngredientRepository> ingredientRepositoryProvider;

  public UpdateIngredientUseCase_Factory(
      Provider<IngredientRepository> ingredientRepositoryProvider) {
    this.ingredientRepositoryProvider = ingredientRepositoryProvider;
  }

  @Override
  public UpdateIngredientUseCase get() {
    return newInstance(ingredientRepositoryProvider.get());
  }

  public static UpdateIngredientUseCase_Factory create(
      Provider<IngredientRepository> ingredientRepositoryProvider) {
    return new UpdateIngredientUseCase_Factory(ingredientRepositoryProvider);
  }

  public static UpdateIngredientUseCase newInstance(IngredientRepository ingredientRepository) {
    return new UpdateIngredientUseCase(ingredientRepository);
  }
}
