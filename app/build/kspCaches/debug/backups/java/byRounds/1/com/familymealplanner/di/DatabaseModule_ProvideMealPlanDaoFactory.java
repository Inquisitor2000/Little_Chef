package com.familymealplanner.di;

import com.familymealplanner.data.local.AppDatabase;
import com.familymealplanner.data.local.dao.MealPlanDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class DatabaseModule_ProvideMealPlanDaoFactory implements Factory<MealPlanDao> {
  private final Provider<AppDatabase> databaseProvider;

  public DatabaseModule_ProvideMealPlanDaoFactory(Provider<AppDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public MealPlanDao get() {
    return provideMealPlanDao(databaseProvider.get());
  }

  public static DatabaseModule_ProvideMealPlanDaoFactory create(
      Provider<AppDatabase> databaseProvider) {
    return new DatabaseModule_ProvideMealPlanDaoFactory(databaseProvider);
  }

  public static MealPlanDao provideMealPlanDao(AppDatabase database) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideMealPlanDao(database));
  }
}
