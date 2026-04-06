package com.familymealplanner.domain.usecase;

import com.familymealplanner.domain.repository.MealRepository;
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
public final class CreateMealUseCase_Factory implements Factory<CreateMealUseCase> {
  private final Provider<MealRepository> mealRepositoryProvider;

  public CreateMealUseCase_Factory(Provider<MealRepository> mealRepositoryProvider) {
    this.mealRepositoryProvider = mealRepositoryProvider;
  }

  @Override
  public CreateMealUseCase get() {
    return newInstance(mealRepositoryProvider.get());
  }

  public static CreateMealUseCase_Factory create(Provider<MealRepository> mealRepositoryProvider) {
    return new CreateMealUseCase_Factory(mealRepositoryProvider);
  }

  public static CreateMealUseCase newInstance(MealRepository mealRepository) {
    return new CreateMealUseCase(mealRepository);
  }
}
