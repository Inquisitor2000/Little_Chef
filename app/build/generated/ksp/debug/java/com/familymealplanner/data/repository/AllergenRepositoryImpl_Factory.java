package com.familymealplanner.data.repository;

import com.familymealplanner.data.local.dao.AllergenDao;
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
public final class AllergenRepositoryImpl_Factory implements Factory<AllergenRepositoryImpl> {
  private final Provider<AllergenDao> allergenDaoProvider;

  public AllergenRepositoryImpl_Factory(Provider<AllergenDao> allergenDaoProvider) {
    this.allergenDaoProvider = allergenDaoProvider;
  }

  @Override
  public AllergenRepositoryImpl get() {
    return newInstance(allergenDaoProvider.get());
  }

  public static AllergenRepositoryImpl_Factory create(Provider<AllergenDao> allergenDaoProvider) {
    return new AllergenRepositoryImpl_Factory(allergenDaoProvider);
  }

  public static AllergenRepositoryImpl newInstance(AllergenDao allergenDao) {
    return new AllergenRepositoryImpl(allergenDao);
  }
}
