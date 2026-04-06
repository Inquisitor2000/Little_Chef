package com.familymealplanner.ui.onboarding;

import com.familymealplanner.data.preferences.LocaleManager;
import com.familymealplanner.data.preferences.OnboardingPreferences;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class OnboardingViewModel_Factory implements Factory<OnboardingViewModel> {
  private final Provider<OnboardingPreferences> onboardingPreferencesProvider;

  private final Provider<LocaleManager> localeManagerProvider;

  public OnboardingViewModel_Factory(Provider<OnboardingPreferences> onboardingPreferencesProvider,
      Provider<LocaleManager> localeManagerProvider) {
    this.onboardingPreferencesProvider = onboardingPreferencesProvider;
    this.localeManagerProvider = localeManagerProvider;
  }

  @Override
  public OnboardingViewModel get() {
    return newInstance(onboardingPreferencesProvider.get(), localeManagerProvider.get());
  }

  public static OnboardingViewModel_Factory create(
      Provider<OnboardingPreferences> onboardingPreferencesProvider,
      Provider<LocaleManager> localeManagerProvider) {
    return new OnboardingViewModel_Factory(onboardingPreferencesProvider, localeManagerProvider);
  }

  public static OnboardingViewModel newInstance(OnboardingPreferences onboardingPreferences,
      LocaleManager localeManager) {
    return new OnboardingViewModel(onboardingPreferences, localeManager);
  }
}
