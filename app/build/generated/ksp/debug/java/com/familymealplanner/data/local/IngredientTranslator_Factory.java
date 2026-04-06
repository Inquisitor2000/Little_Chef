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
public final class IngredientTranslator_Factory implements Factory<IngredientTranslator> {
  private final Provider<Context> contextProvider;

  public IngredientTranslator_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public IngredientTranslator get() {
    return newInstance(contextProvider.get());
  }

  public static IngredientTranslator_Factory create(Provider<Context> contextProvider) {
    return new IngredientTranslator_Factory(contextProvider);
  }

  public static IngredientTranslator newInstance(Context context) {
    return new IngredientTranslator(context);
  }
}
