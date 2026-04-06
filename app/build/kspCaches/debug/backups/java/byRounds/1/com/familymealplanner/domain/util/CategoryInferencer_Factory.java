package com.familymealplanner.domain.util;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
public final class CategoryInferencer_Factory implements Factory<CategoryInferencer> {
  @Override
  public CategoryInferencer get() {
    return newInstance();
  }

  public static CategoryInferencer_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static CategoryInferencer newInstance() {
    return new CategoryInferencer();
  }

  private static final class InstanceHolder {
    private static final CategoryInferencer_Factory INSTANCE = new CategoryInferencer_Factory();
  }
}
