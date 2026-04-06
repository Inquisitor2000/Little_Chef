package com.familymealplanner;

import com.familymealplanner.data.local.ImagePreloader;
import com.familymealplanner.data.local.SubstituteInitializer;
import com.familymealplanner.data.local.TranslationSystem;
import com.familymealplanner.data.preferences.LocaleManager;
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
public final class MealPlannerApp_MembersInjector implements MembersInjector<MealPlannerApp> {
  private final Provider<ImagePreloader> imagePreloaderProvider;

  private final Provider<SubstituteInitializer> substituteInitializerProvider;

  private final Provider<TranslationSystem> translationSystemProvider;

  private final Provider<LocaleManager> localeManagerProvider;

  public MealPlannerApp_MembersInjector(Provider<ImagePreloader> imagePreloaderProvider,
      Provider<SubstituteInitializer> substituteInitializerProvider,
      Provider<TranslationSystem> translationSystemProvider,
      Provider<LocaleManager> localeManagerProvider) {
    this.imagePreloaderProvider = imagePreloaderProvider;
    this.substituteInitializerProvider = substituteInitializerProvider;
    this.translationSystemProvider = translationSystemProvider;
    this.localeManagerProvider = localeManagerProvider;
  }

  public static MembersInjector<MealPlannerApp> create(
      Provider<ImagePreloader> imagePreloaderProvider,
      Provider<SubstituteInitializer> substituteInitializerProvider,
      Provider<TranslationSystem> translationSystemProvider,
      Provider<LocaleManager> localeManagerProvider) {
    return new MealPlannerApp_MembersInjector(imagePreloaderProvider, substituteInitializerProvider, translationSystemProvider, localeManagerProvider);
  }

  @Override
  public void injectMembers(MealPlannerApp instance) {
    injectImagePreloader(instance, imagePreloaderProvider.get());
    injectSubstituteInitializer(instance, substituteInitializerProvider.get());
    injectTranslationSystem(instance, translationSystemProvider.get());
    injectLocaleManager(instance, localeManagerProvider.get());
  }

  @InjectedFieldSignature("com.familymealplanner.MealPlannerApp.imagePreloader")
  public static void injectImagePreloader(MealPlannerApp instance, ImagePreloader imagePreloader) {
    instance.imagePreloader = imagePreloader;
  }

  @InjectedFieldSignature("com.familymealplanner.MealPlannerApp.substituteInitializer")
  public static void injectSubstituteInitializer(MealPlannerApp instance,
      SubstituteInitializer substituteInitializer) {
    instance.substituteInitializer = substituteInitializer;
  }

  @InjectedFieldSignature("com.familymealplanner.MealPlannerApp.translationSystem")
  public static void injectTranslationSystem(MealPlannerApp instance,
      TranslationSystem translationSystem) {
    instance.translationSystem = translationSystem;
  }

  @InjectedFieldSignature("com.familymealplanner.MealPlannerApp.localeManager")
  public static void injectLocaleManager(MealPlannerApp instance, LocaleManager localeManager) {
    instance.localeManager = localeManager;
  }
}
