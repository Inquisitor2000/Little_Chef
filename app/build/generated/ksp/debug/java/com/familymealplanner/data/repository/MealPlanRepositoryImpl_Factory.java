package com.familymealplanner.data.repository;

import com.familymealplanner.data.local.dao.MealPlanDao;
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
public final class MealPlanRepositoryImpl_Factory implements Factory<MealPlanRepositoryImpl> {
  private final Provider<MealPlanDao> mealPlanDaoProvider;

  private final Provider<MealRepository> mealRepositoryProvider;

  public MealPlanRepositoryImpl_Factory(Provider<MealPlanDao> mealPlanDaoProvider,
      Provider<MealRepository> mealRepositoryProvider) {
    this.mealPlanDaoProvider = mealPlanDaoProvider;
    this.mealRepositoryProvider = mealRepositoryProvider;
  }

  @Override
  public MealPlanRepositoryImpl get() {
    return newInstance(mealPlanDaoProvider.get(), mealRepositoryProvider.get());
  }

  public static MealPlanRepositoryImpl_Factory create(Provider<MealPlanDao> mealPlanDaoProvider,
      Provider<MealRepository> mealRepositoryProvider) {
    return new MealPlanRepositoryImpl_Factory(mealPlanDaoProvider, mealRepositoryProvider);
  }

  public static MealPlanRepositoryImpl newInstance(MealPlanDao mealPlanDao,
      MealRepository mealRepository) {
    return new MealPlanRepositoryImpl(mealPlanDao, mealRepository);
  }
}
