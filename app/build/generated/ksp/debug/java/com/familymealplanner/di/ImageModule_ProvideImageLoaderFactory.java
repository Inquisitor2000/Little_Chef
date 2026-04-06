package com.familymealplanner.di;

import android.content.Context;
import coil.ImageLoader;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class ImageModule_ProvideImageLoaderFactory implements Factory<ImageLoader> {
  private final Provider<Context> contextProvider;

  public ImageModule_ProvideImageLoaderFactory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public ImageLoader get() {
    return provideImageLoader(contextProvider.get());
  }

  public static ImageModule_ProvideImageLoaderFactory create(Provider<Context> contextProvider) {
    return new ImageModule_ProvideImageLoaderFactory(contextProvider);
  }

  public static ImageLoader provideImageLoader(Context context) {
    return Preconditions.checkNotNullFromProvides(ImageModule.INSTANCE.provideImageLoader(context));
  }
}
