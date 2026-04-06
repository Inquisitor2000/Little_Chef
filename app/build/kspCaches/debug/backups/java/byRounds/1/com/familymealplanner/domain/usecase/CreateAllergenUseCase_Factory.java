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
public final class CreateAllergenUseCase_Factory implements Factory<CreateAllergenUseCase> {
  private final Provider<AllergenRepository> allergenRepositoryProvider;

  public CreateAllergenUseCase_Factory(Provider<AllergenRepository> allergenRepositoryProvider) {
    this.allergenRepositoryProvider = allergenRepositoryProvider;
  }

  @Override
  public CreateAllergenUseCase get() {
    return newInstance(allergenRepositoryProvider.get());
  }

  public static CreateAllergenUseCase_Factory create(
      Provider<AllergenRepository> allergenRepositoryProvider) {
    return new CreateAllergenUseCase_Factory(allergenRepositoryProvider);
  }

  public static CreateAllergenUseCase newInstance(AllergenRepository allergenRepository) {
    return new CreateAllergenUseCase(allergenRepository);
  }
}
