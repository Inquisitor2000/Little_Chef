package com.familymealplanner.di;

import com.familymealplanner.data.local.AppDatabase;
import com.familymealplanner.data.local.dao.IngredientSubstituteDao;
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
public final class DatabaseModule_ProvideIngredientSubstituteDaoFactory implements Factory<IngredientSubstituteDao> {
  private final Provider<AppDatabase> databaseProvider;

  public DatabaseModule_ProvideIngredientSubstituteDaoFactory(
      Provider<AppDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public IngredientSubstituteDao get() {
    return provideIngredientSubstituteDao(databaseProvider.get());
  }

  public static DatabaseModule_ProvideIngredientSubstituteDaoFactory create(
      Provider<AppDatabase> databaseProvider) {
    return new DatabaseModule_ProvideIngredientSubstituteDaoFactory(databaseProvider);
  }

  public static IngredientSubstituteDao provideIngredientSubstituteDao(AppDatabase database) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideIngredientSubstituteDao(database));
  }
}
