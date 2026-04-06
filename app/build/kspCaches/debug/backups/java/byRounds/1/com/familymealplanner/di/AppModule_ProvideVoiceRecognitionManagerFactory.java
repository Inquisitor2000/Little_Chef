package com.familymealplanner.di;

import android.content.Context;
import com.familymealplanner.data.preferences.LocaleManager;
import com.familymealplanner.domain.util.VoiceRecognitionManager;
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
public final class AppModule_ProvideVoiceRecognitionManagerFactory implements Factory<VoiceRecognitionManager> {
  private final Provider<Context> contextProvider;

  private final Provider<LocaleManager> localeManagerProvider;

  public AppModule_ProvideVoiceRecognitionManagerFactory(Provider<Context> contextProvider,
      Provider<LocaleManager> localeManagerProvider) {
    this.contextProvider = contextProvider;
    this.localeManagerProvider = localeManagerProvider;
  }

  @Override
  public VoiceRecognitionManager get() {
    return provideVoiceRecognitionManager(contextProvider.get(), localeManagerProvider.get());
  }

  public static AppModule_ProvideVoiceRecognitionManagerFactory create(
      Provider<Context> contextProvider, Provider<LocaleManager> localeManagerProvider) {
    return new AppModule_ProvideVoiceRecognitionManagerFactory(contextProvider, localeManagerProvider);
  }

  public static VoiceRecognitionManager provideVoiceRecognitionManager(Context context,
      LocaleManager localeManager) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideVoiceRecognitionManager(context, localeManager));
  }
}
