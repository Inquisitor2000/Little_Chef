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
public final class AddGroceryItemToPantryUseCase_Factory implements Factory<AddGroceryItemToPantryUseCase> {
  private final Provider<IngredientRepository> ingredientRepositoryProvider;

  private final Provider<RestockIngredientUseCase> restockIngredientUseCaseProvider;

  private final Provider<CreateIngredientUseCase> createIngredientUseCaseProvider;

  public AddGroceryItemToPantryUseCase_Factory(
      Provider<IngredientRepository> ingredientRepositoryProvider,
      Provider<RestockIngredientUseCase> restockIngredientUseCaseProvider,
      Provider<CreateIngredientUseCase> createIngredientUseCaseProvider) {
    this.ingredientRepositoryProvider = ingredientRepositoryProvider;
    this.restockIngredientUseCaseProvider = restockIngredientUseCaseProvider;
    this.createIngredientUseCaseProvider = createIngredientUseCaseProvider;
  }

  @Override
  public AddGroceryItemToPantryUseCase get() {
    return newInstance(ingredientRepositoryProvider.get(), restockIngredientUseCaseProvider.get(), createIngredientUseCaseProvider.get());
  }

  public static AddGroceryItemToPantryUseCase_Factory create(
      Provider<IngredientRepository> ingredientRepositoryProvider,
      Provider<RestockIngredientUseCase> restockIngredientUseCaseProvider,
      Provider<CreateIngredientUseCase> createIngredientUseCaseProvider) {
    return new AddGroceryItemToPantryUseCase_Factory(ingredientRepositoryProvider, restockIngredientUseCaseProvider, createIngredientUseCaseProvider);
  }

  public static AddGroceryItemToPantryUseCase newInstance(IngredientRepository ingredientRepository,
      RestockIngredientUseCase restockIngredientUseCase,
      CreateIngredientUseCase createIngredientUseCase) {
    return new AddGroceryItemToPantryUseCase(ingredientRepository, restockIngredientUseCase, createIngredientUseCase);
  }
}
