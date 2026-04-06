package com.familymealplanner.di;

import com.familymealplanner.data.local.AppDatabase;
import com.familymealplanner.data.local.dao.IngredientDao;
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
public final class DatabaseModule_ProvideIngredientDaoFactory implements Factory<IngredientDao> {
  private final Provider<AppDatabase> databaseProvider;

  public DatabaseModule_ProvideIngredientDaoFactory(Provider<AppDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public IngredientDao get() {
    return provideIngredientDao(databaseProvider.get());
  }

  public static DatabaseModule_ProvideIngredientDaoFactory create(
      Provider<AppDatabase> databaseProvider) {
    return new DatabaseModule_ProvideIngredientDaoFactory(databaseProvider);
  }

  public static IngredientDao provideIngredientDao(AppDatabase database) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideIngredientDao(database));
  }
}
