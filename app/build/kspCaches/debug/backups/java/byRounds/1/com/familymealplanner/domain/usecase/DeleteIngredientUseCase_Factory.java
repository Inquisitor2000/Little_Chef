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
public final class DeleteIngredientUseCase_Factory implements Factory<DeleteIngredientUseCase> {
  private final Provider<IngredientRepository> ingredientRepositoryProvider;

  public DeleteIngredientUseCase_Factory(
      Provider<IngredientRepository> ingredientRepositoryProvider) {
    this.ingredientRepositoryProvider = ingredientRepositoryProvider;
  }

  @Override
  public DeleteIngredientUseCase get() {
    return newInstance(ingredientRepositoryProvider.get());
  }

  public static DeleteIngredientUseCase_Factory create(
      Provider<IngredientRepository> ingredientRepositoryProvider) {
    return new DeleteIngredientUseCase_Factory(ingredientRepositoryProvider);
  }

  public static DeleteIngredientUseCase newInstance(IngredientRepository ingredientRepository) {
    return new DeleteIngredientUseCase(ingredientRepository);
  }
}
