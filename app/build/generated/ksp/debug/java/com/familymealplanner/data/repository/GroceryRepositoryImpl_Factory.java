package com.familymealplanner.data.repository;

import com.familymealplanner.data.local.dao.GroceryItemDao;
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
public final class GroceryRepositoryImpl_Factory implements Factory<GroceryRepositoryImpl> {
  private final Provider<GroceryItemDao> groceryItemDaoProvider;

  public GroceryRepositoryImpl_Factory(Provider<GroceryItemDao> groceryItemDaoProvider) {
    this.groceryItemDaoProvider = groceryItemDaoProvider;
  }

  @Override
  public GroceryRepositoryImpl get() {
    return newInstance(groceryItemDaoProvider.get());
  }

  public static GroceryRepositoryImpl_Factory create(
      Provider<GroceryItemDao> groceryItemDaoProvider) {
    return new GroceryRepositoryImpl_Factory(groceryItemDaoProvider);
  }

  public static GroceryRepositoryImpl newInstance(GroceryItemDao groceryItemDao) {
    return new GroceryRepositoryImpl(groceryItemDao);
  }
}
