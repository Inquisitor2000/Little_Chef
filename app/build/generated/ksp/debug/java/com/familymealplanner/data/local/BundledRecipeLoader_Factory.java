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
public final class BundledRecipeLoader_Factory implements Factory<BundledRecipeLoader> {
  private final Provider<Context> contextProvider;

  public BundledRecipeLoader_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public BundledRecipeLoader get() {
    return newInstance(contextProvider.get());
  }

  public static BundledRecipeLoader_Factory create(Provider<Context> contextProvider) {
    return new BundledRecipeLoader_Factory(contextProvider);
  }

  public static BundledRecipeLoader newInstance(Context context) {
    return new BundledRecipeLoader(context);
  }
}
