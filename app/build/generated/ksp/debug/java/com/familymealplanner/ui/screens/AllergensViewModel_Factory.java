package com.familymealplanner.ui.screens;

import com.familymealplanner.domain.repository.AllergenRepository;
import com.familymealplanner.domain.usecase.CreateAllergenUseCase;
import com.familymealplanner.domain.usecase.DeleteAllergenUseCase;
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
public final class AllergensViewModel_Factory implements Factory<AllergensViewModel> {
  private final Provider<AllergenRepository> allergenRepositoryProvider;

  private final Provider<CreateAllergenUseCase> createAllergenUseCaseProvider;

  private final Provider<DeleteAllergenUseCase> deleteAllergenUseCaseProvider;

  public AllergensViewModel_Factory(Provider<AllergenRepository> allergenRepositoryProvider,
      Provider<CreateAllergenUseCase> createAllergenUseCaseProvider,
      Provider<DeleteAllergenUseCase> deleteAllergenUseCaseProvider) {
    this.allergenRepositoryProvider = allergenRepositoryProvider;
    this.createAllergenUseCaseProvider = createAllergenUseCaseProvider;
    this.deleteAllergenUseCaseProvider = deleteAllergenUseCaseProvider;
  }

  @Override
  public AllergensViewModel get() {
    return newInstance(allergenRepositoryProvider.get(), createAllergenUseCaseProvider.get(), deleteAllergenUseCaseProvider.get());
  }

  public static AllergensViewModel_Factory create(
      Provider<AllergenRepository> allergenRepositoryProvider,
      Provider<CreateAllergenUseCase> createAllergenUseCaseProvider,
      Provider<DeleteAllergenUseCase> deleteAllergenUseCaseProvider) {
    return new AllergensViewModel_Factory(allergenRepositoryProvider, createAllergenUseCaseProvider, deleteAllergenUseCaseProvider);
  }

  public static AllergensViewModel newInstance(AllergenRepository allergenRepository,
      CreateAllergenUseCase createAllergenUseCase, DeleteAllergenUseCase deleteAllergenUseCase) {
    return new AllergensViewModel(allergenRepository, createAllergenUseCase, deleteAllergenUseCase);
  }
}
