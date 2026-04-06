package com.familymealplanner.ui.screens;

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
public final class SettingsViewModel_Factory implements Factory<SettingsViewModel> {
  private final Provider<OnboardingPreferences> preferencesProvider;

  private final Provider<LocaleManager> localeManagerProvider;

  public SettingsViewModel_Factory(Provider<OnboardingPreferences> preferencesProvider,
      Provider<LocaleManager> localeManagerProvider) {
    this.preferencesProvider = preferencesProvider;
    this.localeManagerProvider = localeManagerProvider;
  }

  @Override
  public SettingsViewModel get() {
    return newInstance(preferencesProvider.get(), localeManagerProvider.get());
  }

  public static SettingsViewModel_Factory create(
      Provider<OnboardingPreferences> preferencesProvider,
      Provider<LocaleManager> localeManagerProvider) {
    return new SettingsViewModel_Factory(preferencesProvider, localeManagerProvider);
  }

  public static SettingsViewModel newInstance(OnboardingPreferences preferences,
      LocaleManager localeManager) {
    return new SettingsViewModel(preferences, localeManager);
  }
}
