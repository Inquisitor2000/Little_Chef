package com.familymealplanner.data.local;

import android.content.Context;
import coil.ImageLoader;
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
public final class ImagePreloader_Factory implements Factory<ImagePreloader> {
  private final Provider<Context> contextProvider;

  private final Provider<BundledRecipeLoader> bundledRecipeLoaderProvider;

  private final Provider<ImageLoader> imageLoaderProvider;

  public ImagePreloader_Factory(Provider<Context> contextProvider,
      Provider<BundledRecipeLoader> bundledRecipeLoaderProvider,
      Provider<ImageLoader> imageLoaderProvider) {
    this.contextProvider = contextProvider;
    this.bundledRecipeLoaderProvider = bundledRecipeLoaderProvider;
    this.imageLoaderProvider = imageLoaderProvider;
  }

  @Override
  public ImagePreloader get() {
    return newInstance(contextProvider.get(), bundledRecipeLoaderProvider.get(), imageLoaderProvider.get());
  }

  public static ImagePreloader_Factory create(Provider<Context> contextProvider,
      Provider<BundledRecipeLoader> bundledRecipeLoaderProvider,
      Provider<ImageLoader> imageLoaderProvider) {
    return new ImagePreloader_Factory(contextProvider, bundledRecipeLoaderProvider, imageLoaderProvider);
  }

  public static ImagePreloader newInstance(Context context, BundledRecipeLoader bundledRecipeLoader,
      ImageLoader imageLoader) {
    return new ImagePreloader(context, bundledRecipeLoader, imageLoader);
  }
}
