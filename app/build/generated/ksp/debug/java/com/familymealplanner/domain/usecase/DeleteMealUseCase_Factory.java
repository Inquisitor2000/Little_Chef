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
public final class DeleteMealUseCase_Factory implements Factory<DeleteMealUseCase> {
  private final Provider<MealRepository> mealRepositoryProvider;

  public DeleteMealUseCase_Factory(Provider<MealRepository> mealRepositoryProvider) {
    this.mealRepositoryProvider = mealRepositoryProvider;
  }

  @Override
  public DeleteMealUseCase get() {
    return newInstance(mealRepositoryProvider.get());
  }

  public static DeleteMealUseCase_Factory create(Provider<MealRepository> mealRepositoryProvider) {
    return new DeleteMealUseCase_Factory(mealRepositoryProvider);
  }

  public static DeleteMealUseCase newInstance(MealRepository mealRepository) {
    return new DeleteMealUseCase(mealRepository);
  }
}
