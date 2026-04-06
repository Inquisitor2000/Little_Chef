package com.familymealplanner.data.local;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class TranslationSystem_Factory implements Factory<TranslationSystem> {
  private final Provider<Context> contextProvider;

  private final Provider<IngredientTranslator> ingredientTranslatorProvider;

  private final Provider<RecipeTranslator> recipeTranslatorProvider;

  private final Provider<CategoryTranslator> categoryTranslatorProvider;

  public TranslationSystem_Factory(Provider<Context> contextProvider,
      Provider<IngredientTranslator> ingredientTranslatorProvider,
      Provider<RecipeTranslator> recipeTranslatorProvider,
      Provider<CategoryTranslator> categoryTranslatorProvider) {
    this.contextProvider = contextProvider;
    this.ingredientTranslatorProvider = ingredientTranslatorProvider;
    this.recipeTranslatorProvider = recipeTranslatorProvider;
    this.categoryTranslatorProvider = categoryTranslatorProvider;
  }

  @Override
  public TranslationSystem get() {
    return newInstance(contextProvider.get(), ingredientTranslatorProvider.get(), recipeTranslatorProvider.get(), categoryTranslatorProvider.get());
  }

  public static TranslationSystem_Factory create(Provider<Context> contextProvider,
      Provider<IngredientTranslator> ingredientTranslatorProvider,
      Provider<RecipeTranslator> recipeTranslatorProvider,
      Provider<CategoryTranslator> categoryTranslatorProvider) {
    return new TranslationSystem_Factory(contextProvider, ingredientTranslatorProvider, recipeTranslatorProvider, categoryTranslatorProvider);
  }

  public static TranslationSystem newInstance(Context context,
      IngredientTranslator ingredientTranslator, RecipeTranslator recipeTranslator,
      CategoryTranslator categoryTranslator) {
    return new TranslationSystem(context, ingredientTranslator, recipeTranslator, categoryTranslator);
  }
}
