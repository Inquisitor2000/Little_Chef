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
public final class CreateIngredientUseCase_Factory implements Factory<CreateIngredientUseCase> {
  private final Provider<IngredientRepository> ingredientRepositoryProvider;

  public CreateIngredientUseCase_Factory(
      Provider<IngredientRepository> ingredientRepositoryProvider) {
    this.ingredientRepositoryProvider = ingredientRepositoryProvider;
  }

  @Override
  public CreateIngredientUseCase get() {
    return newInstance(ingredientRepositoryProvider.get());
  }

  public static CreateIngredientUseCase_Factory create(
      Provider<IngredientRepository> ingredientRepositoryProvider) {
    return new CreateIngredientUseCase_Factory(ingredientRepositoryProvider);
  }

  public static CreateIngredientUseCase newInstance(IngredientRepository ingredientRepository) {
    return new CreateIngredientUseCase(ingredientRepository);
  }
}
