package com.familymealplanner.data.repository;

import com.familymealplanner.data.local.dao.AllergenDao;
import com.familymealplanner.data.local.dao.IngredientAllergenDao;
import com.familymealplanner.data.local.dao.IngredientDao;
import com.familymealplanner.data.local.dao.IngredientSubstituteDao;
import com.familymealplanner.data.local.dao.MealIngredientDao;
import com.familymealplanner.data.preferences.LocaleManager;
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
public final class IngredientRepositoryImpl_Factory implements Factory<IngredientRepositoryImpl> {
  private final Provider<IngredientDao> ingredientDaoProvider;

  private final Provider<AllergenDao> allergenDaoProvider;

  private final Provider<IngredientAllergenDao> ingredientAllergenDaoProvider;

  private final Provider<IngredientSubstituteDao> ingredientSubstituteDaoProvider;

  private final Provider<MealIngredientDao> mealIngredientDaoProvider;

  private final Provider<LocaleManager> localeManagerProvider;

  public IngredientRepositoryImpl_Factory(Provider<IngredientDao> ingredientDaoProvider,
      Provider<AllergenDao> allergenDaoProvider,
      Provider<IngredientAllergenDao> ingredientAllergenDaoProvider,
      Provider<IngredientSubstituteDao> ingredientSubstituteDaoProvider,
      Provider<MealIngredientDao> mealIngredientDaoProvider,
      Provider<LocaleManager> localeManagerProvider) {
    this.ingredientDaoProvider = ingredientDaoProvider;
    this.allergenDaoProvider = allergenDaoProvider;
    this.ingredientAllergenDaoProvider = ingredientAllergenDaoProvider;
    this.ingredientSubstituteDaoProvider = ingredientSubstituteDaoProvider;
    this.mealIngredientDaoProvider = mealIngredientDaoProvider;
    this.localeManagerProvider = localeManagerProvider;
  }

  @Override
  public IngredientRepositoryImpl get() {
    return newInstance(ingredientDaoProvider.get(), allergenDaoProvider.get(), ingredientAllergenDaoProvider.get(), ingredientSubstituteDaoProvider.get(), mealIngredientDaoProvider.get(), localeManagerProvider.get());
  }

  public static IngredientRepositoryImpl_Factory create(
      Provider<IngredientDao> ingredientDaoProvider, Provider<AllergenDao> allergenDaoProvider,
      Provider<IngredientAllergenDao> ingredientAllergenDaoProvider,
      Provider<IngredientSubstituteDao> ingredientSubstituteDaoProvider,
      Provider<MealIngredientDao> mealIngredientDaoProvider,
      Provider<LocaleManager> localeManagerProvider) {
    return new IngredientRepositoryImpl_Factory(ingredientDaoProvider, allergenDaoProvider, ingredientAllergenDaoProvider, ingredientSubstituteDaoProvider, mealIngredientDaoProvider, localeManagerProvider);
  }

  public static IngredientRepositoryImpl newInstance(IngredientDao ingredientDao,
      AllergenDao allergenDao, IngredientAllergenDao ingredientAllergenDao,
      IngredientSubstituteDao ingredientSubstituteDao, MealIngredientDao mealIngredientDao,
      LocaleManager localeManager) {
    return new IngredientRepositoryImpl(ingredientDao, allergenDao, ingredientAllergenDao, ingredientSubstituteDao, mealIngredientDao, localeManager);
  }
}
