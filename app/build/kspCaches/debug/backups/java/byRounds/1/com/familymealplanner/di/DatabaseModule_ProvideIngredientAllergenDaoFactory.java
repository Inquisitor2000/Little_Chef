package com.familymealplanner.di;

import com.familymealplanner.data.local.AppDatabase;
import com.familymealplanner.data.local.dao.IngredientAllergenDao;
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
public final class DatabaseModule_ProvideIngredientAllergenDaoFactory implements Factory<IngredientAllergenDao> {
  private final Provider<AppDatabase> databaseProvider;

  public DatabaseModule_ProvideIngredientAllergenDaoFactory(
      Provider<AppDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public IngredientAllergenDao get() {
    return provideIngredientAllergenDao(databaseProvider.get());
  }

  public static DatabaseModule_ProvideIngredientAllergenDaoFactory create(
      Provider<AppDatabase> databaseProvider) {
    return new DatabaseModule_ProvideIngredientAllergenDaoFactory(databaseProvider);
  }

  public static IngredientAllergenDao provideIngredientAllergenDao(AppDatabase database) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideIngredientAllergenDao(database));
  }
}
