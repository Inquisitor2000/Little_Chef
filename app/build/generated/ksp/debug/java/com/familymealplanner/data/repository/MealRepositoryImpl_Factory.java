package com.familymealplanner.data.repository;

import com.familymealplanner.data.local.TranslationSystem;
import com.familymealplanner.data.local.dao.AllergenDao;
import com.familymealplanner.data.local.dao.IngredientAllergenDao;
import com.familymealplanner.data.local.dao.IngredientDao;
import com.familymealplanner.data.local.dao.IngredientSubstituteDao;
import com.familymealplanner.data.local.dao.MealDao;
import com.familymealplanner.data.local.dao.MealIngredientDao;
import com.familymealplanner.data.preferences.LocaleManager;
import com.familymealplanner.domain.util.IngredientMatcher;
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
public final class MealRepositoryImpl_Factory implements Factory<MealRepositoryImpl> {
  private final Provider<MealDao> mealDaoProvider;

  private final Provider<MealIngredientDao> mealIngredientDaoProvider;

  private final Provider<IngredientDao> ingredientDaoProvider;

  private final Provider<IngredientAllergenDao> ingredientAllergenDaoProvider;

  private final Provider<AllergenDao> allergenDaoProvider;

  private final Provider<IngredientSubstituteDao> ingredientSubstituteDaoProvider;

  private final Provider<LocaleManager> localeManagerProvider;

  private final Provider<TranslationSystem> translationSystemProvider;

  private final Provider<IngredientMatcher> ingredientMatcherProvider;

  public MealRepositoryImpl_Factory(Provider<MealDao> mealDaoProvider,
      Provider<MealIngredientDao> mealIngredientDaoProvider,
      Provider<IngredientDao> ingredientDaoProvider,
      Provider<IngredientAllergenDao> ingredientAllergenDaoProvider,
      Provider<AllergenDao> allergenDaoProvider,
      Provider<IngredientSubstituteDao> ingredientSubstituteDaoProvider,
      Provider<LocaleManager> localeManagerProvider,
      Provider<TranslationSystem> translationSystemProvider,
      Provider<IngredientMatcher> ingredientMatcherProvider) {
    this.mealDaoProvider = mealDaoProvider;
    this.mealIngredientDaoProvider = mealIngredientDaoProvider;
    this.ingredientDaoProvider = ingredientDaoProvider;
    this.ingredientAllergenDaoProvider = ingredientAllergenDaoProvider;
    this.allergenDaoProvider = allergenDaoProvider;
    this.ingredientSubstituteDaoProvider = ingredientSubstituteDaoProvider;
    this.localeManagerProvider = localeManagerProvider;
    this.translationSystemProvider = translationSystemProvider;
    this.ingredientMatcherProvider = ingredientMatcherProvider;
  }

  @Override
  public MealRepositoryImpl get() {
    return newInstance(mealDaoProvider.get(), mealIngredientDaoProvider.get(), ingredientDaoProvider.get(), ingredientAllergenDaoProvider.get(), allergenDaoProvider.get(), ingredientSubstituteDaoProvider.get(), localeManagerProvider.get(), translationSystemProvider.get(), ingredientMatcherProvider.get());
  }

  public static MealRepositoryImpl_Factory create(Provider<MealDao> mealDaoProvider,
      Provider<MealIngredientDao> mealIngredientDaoProvider,
      Provider<IngredientDao> ingredientDaoProvider,
      Provider<IngredientAllergenDao> ingredientAllergenDaoProvider,
      Provider<AllergenDao> allergenDaoProvider,
      Provider<IngredientSubstituteDao> ingredientSubstituteDaoProvider,
      Provider<LocaleManager> localeManagerProvider,
      Provider<TranslationSystem> translationSystemProvider,
      Provider<IngredientMatcher> ingredientMatcherProvider) {
    return new MealRepositoryImpl_Factory(mealDaoProvider, mealIngredientDaoProvider, ingredientDaoProvider, ingredientAllergenDaoProvider, allergenDaoProvider, ingredientSubstituteDaoProvider, localeManagerProvider, translationSystemProvider, ingredientMatcherProvider);
  }

  public static MealRepositoryImpl newInstance(MealDao mealDao, MealIngredientDao mealIngredientDao,
      IngredientDao ingredientDao, IngredientAllergenDao ingredientAllergenDao,
      AllergenDao allergenDao, IngredientSubstituteDao ingredientSubstituteDao,
      LocaleManager localeManager, TranslationSystem translationSystem,
      IngredientMatcher ingredientMatcher) {
    return new MealRepositoryImpl(mealDao, mealIngredientDao, ingredientDao, ingredientAllergenDao, allergenDao, ingredientSubstituteDao, localeManager, translationSystem, ingredientMatcher);
  }
}
