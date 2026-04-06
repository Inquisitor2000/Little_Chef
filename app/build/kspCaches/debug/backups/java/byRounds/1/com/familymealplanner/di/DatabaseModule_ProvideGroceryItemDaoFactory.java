package com.familymealplanner.di;

import com.familymealplanner.data.local.AppDatabase;
import com.familymealplanner.data.local.dao.GroceryItemDao;
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
public final class DatabaseModule_ProvideGroceryItemDaoFactory implements Factory<GroceryItemDao> {
  private final Provider<AppDatabase> databaseProvider;

  public DatabaseModule_ProvideGroceryItemDaoFactory(Provider<AppDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public GroceryItemDao get() {
    return provideGroceryItemDao(databaseProvider.get());
  }

  public static DatabaseModule_ProvideGroceryItemDaoFactory create(
      Provider<AppDatabase> databaseProvider) {
    return new DatabaseModule_ProvideGroceryItemDaoFactory(databaseProvider);
  }

  public static GroceryItemDao provideGroceryItemDao(AppDatabase database) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideGroceryItemDao(database));
  }
}
