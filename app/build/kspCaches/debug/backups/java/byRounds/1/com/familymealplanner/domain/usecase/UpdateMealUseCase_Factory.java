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
public final class UpdateMealUseCase_Factory implements Factory<UpdateMealUseCase> {
  private final Provider<MealRepository> mealRepositoryProvider;

  public UpdateMealUseCase_Factory(Provider<MealRepository> mealRepositoryProvider) {
    this.mealRepositoryProvider = mealRepositoryProvider;
  }

  @Override
  public UpdateMealUseCase get() {
    return newInstance(mealRepositoryProvider.get());
  }

  public static UpdateMealUseCase_Factory create(Provider<MealRepository> mealRepositoryProvider) {
    return new UpdateMealUseCase_Factory(mealRepositoryProvider);
  }

  public static UpdateMealUseCase newInstance(MealRepository mealRepository) {
    return new UpdateMealUseCase(mealRepository);
  }
}
