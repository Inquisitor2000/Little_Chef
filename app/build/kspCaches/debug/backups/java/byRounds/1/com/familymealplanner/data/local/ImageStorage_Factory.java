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
public final class ImageStorage_Factory implements Factory<ImageStorage> {
  private final Provider<Context> contextProvider;

  public ImageStorage_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public ImageStorage get() {
    return newInstance(contextProvider.get());
  }

  public static ImageStorage_Factory create(Provider<Context> contextProvider) {
    return new ImageStorage_Factory(contextProvider);
  }

  public static ImageStorage newInstance(Context context) {
    return new ImageStorage(context);
  }
}
