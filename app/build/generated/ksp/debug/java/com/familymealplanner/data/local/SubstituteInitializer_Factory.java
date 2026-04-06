package com.familymealplanner.data.local;

import com.familymealplanner.data.local.dao.IngredientDao;
import com.familymealplanner.data.local.dao.IngredientSubstituteDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class SubstituteInitializer_Factory implements Factory<SubstituteInitializer> {
  private final Provider<IngredientDao> ingredientDaoProvider;

  private final Provider<IngredientSubstituteDao> substituteDaoProvider;

  public SubstituteInitializer_Factory(Provider<IngredientDao> ingredientDaoProvider,
      Provider<IngredientSubstituteDao> substituteDaoProvider) {
    this.ingredientDaoProvider = ingredientDaoProvider;
    this.substituteDaoProvider = substituteDaoProvider;
  }

  @Override
  public SubstituteInitializer get() {
    return newInstance(ingredientDaoProvider.get(), substituteDaoProvider.get());
  }

  public static SubstituteInitializer_Factory create(Provider<IngredientDao> ingredientDaoProvider,
      Provider<IngredientSubstituteDao> substituteDaoProvider) {
    return new SubstituteInitializer_Factory(ingredientDaoProvider, substituteDaoProvider);
  }

  public static SubstituteInitializer newInstance(IngredientDao ingredientDao,
      IngredientSubstituteDao substituteDao) {
    return new SubstituteInitializer(ingredientDao, substituteDao);
  }
}
