package com.familymealplanner.di;

import com.familymealplanner.data.local.AppDatabase;
import com.familymealplanner.data.local.dao.MealIngredientDao;
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
public final class DatabaseModule_ProvideMealIngredientDaoFactory implements Factory<MealIngredientDao> {
  private final Provider<AppDatabase> databaseProvider;

  public DatabaseModule_ProvideMealIngredientDaoFactory(Provider<AppDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public MealIngredientDao get() {
    return provideMealIngredientDao(databaseProvider.get());
  }

  public static DatabaseModule_ProvideMealIngredientDaoFactory create(
      Provider<AppDatabase> databaseProvider) {
    return new DatabaseModule_ProvideMealIngredientDaoFactory(databaseProvider);
  }

  public static MealIngredientDao provideMealIngredientDao(AppDatabase database) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideMealIngredientDao(database));
  }
}
