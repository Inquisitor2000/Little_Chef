package com.familymealplanner.di;

import com.familymealplanner.data.local.AppDatabase;
import com.familymealplanner.data.local.dao.InventoryTransactionDao;
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
public final class DatabaseModule_ProvideInventoryTransactionDaoFactory implements Factory<InventoryTransactionDao> {
  private final Provider<AppDatabase> databaseProvider;

  public DatabaseModule_ProvideInventoryTransactionDaoFactory(
      Provider<AppDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public InventoryTransactionDao get() {
    return provideInventoryTransactionDao(databaseProvider.get());
  }

  public static DatabaseModule_ProvideInventoryTransactionDaoFactory create(
      Provider<AppDatabase> databaseProvider) {
    return new DatabaseModule_ProvideInventoryTransactionDaoFactory(databaseProvider);
  }

  public static InventoryTransactionDao provideInventoryTransactionDao(AppDatabase database) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideInventoryTransactionDao(database));
  }
}
