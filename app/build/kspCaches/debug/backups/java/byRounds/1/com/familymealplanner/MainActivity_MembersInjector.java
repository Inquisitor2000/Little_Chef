package com.familymealplanner;

import com.familymealplanner.data.local.TranslationSystem;
import com.familymealplanner.data.preferences.LocaleManager;
import com.familymealplanner.domain.usecase.PreloadCuisineAllergensUseCase;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class MainActivity_MembersInjector implements MembersInjector<MainActivity> {
  private final Provider<LocaleManager> localeManagerProvider;

  private final Provider<TranslationSystem> translationSystemProvider;

  private final Provider<PreloadCuisineAllergensUseCase> preloadCuisineAllergensUseCaseProvider;

  public MainActivity_MembersInjector(Provider<LocaleManager> localeManagerProvider,
      Provider<TranslationSystem> translationSystemProvider,
      Provider<PreloadCuisineAllergensUseCase> preloadCuisineAllergensUseCaseProvider) {
    this.localeManagerProvider = localeManagerProvider;
    this.translationSystemProvider = translationSystemProvider;
    this.preloadCuisineAllergensUseCaseProvider = preloadCuisineAllergensUseCaseProvider;
  }

  public static MembersInjector<MainActivity> create(Provider<LocaleManager> localeManagerProvider,
      Provider<TranslationSystem> translationSystemProvider,
      Provider<PreloadCuisineAllergensUseCase> preloadCuisineAllergensUseCaseProvider) {
    return new MainActivity_MembersInjector(localeManagerProvider, translationSystemProvider, preloadCuisineAllergensUseCaseProvider);
  }

  @Override
  public void injectMembers(MainActivity instance) {
    injectLocaleManager(instance, localeManagerProvider.get());
    injectTranslationSystem(instance, translationSystemProvider.get());
    injectPreloadCuisineAllergensUseCase(instance, preloadCuisineAllergensUseCaseProvider.get());
  }

  @InjectedFieldSignature("com.familymealplanner.MainActivity.localeManager")
  public static void injectLocaleManager(MainActivity instance, LocaleManager localeManager) {
    instance.localeManager = localeManager;
  }

  @InjectedFieldSignature("com.familymealplanner.MainActivity.translationSystem")
  public static void injectTranslationSystem(MainActivity instance,
      TranslationSystem translationSystem) {
    instance.translationSystem = translationSystem;
  }

  @InjectedFieldSignature("com.familymealplanner.MainActivity.preloadCuisineAllergensUseCase")
  public static void injectPreloadCuisineAllergensUseCase(MainActivity instance,
      PreloadCuisineAllergensUseCase preloadCuisineAllergensUseCase) {
    instance.preloadCuisineAllergensUseCase = preloadCuisineAllergensUseCase;
  }
}
