package com.familymealplanner.domain.usecase;

import com.familymealplanner.domain.repository.AllergenRepository;
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
public final class DeleteAllergenUseCase_Factory implements Factory<DeleteAllergenUseCase> {
  private final Provider<AllergenRepository> allergenRepositoryProvider;

  public DeleteAllergenUseCase_Factory(Provider<AllergenRepository> allergenRepositoryProvider) {
    this.allergenRepositoryProvider = allergenRepositoryProvider;
  }

  @Override
  public DeleteAllergenUseCase get() {
    return newInstance(allergenRepositoryProvider.get());
  }

  public static DeleteAllergenUseCase_Factory create(
      Provider<AllergenRepository> allergenRepositoryProvider) {
    return new DeleteAllergenUseCase_Factory(allergenRepositoryProvider);
  }

  public static DeleteAllergenUseCase newInstance(AllergenRepository allergenRepository) {
    return new DeleteAllergenUseCase(allergenRepository);
  }
}
