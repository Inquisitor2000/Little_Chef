package com.familymealplanner.domain.util;

import com.familymealplanner.data.local.TranslationSystem;
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
public final class IngredientMatcher_Factory implements Factory<IngredientMatcher> {
  private final Provider<TranslationSystem> translationSystemProvider;

  public IngredientMatcher_Factory(Provider<TranslationSystem> translationSystemProvider) {
    this.translationSystemProvider = translationSystemProvider;
  }

  @Override
  public IngredientMatcher get() {
    return newInstance(translationSystemProvider.get());
  }

  public static IngredientMatcher_Factory create(
      Provider<TranslationSystem> translationSystemProvider) {
    return new IngredientMatcher_Factory(translationSystemProvider);
  }

  public static IngredientMatcher newInstance(TranslationSystem translationSystem) {
    return new IngredientMatcher(translationSystem);
  }
}
