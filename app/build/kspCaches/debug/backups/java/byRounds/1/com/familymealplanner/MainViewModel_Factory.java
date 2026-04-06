package com.familymealplanner;

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
public final class MainViewModel_Factory implements Factory<MainViewModel> {
  private final Provider<OnboardingPreferences> onboardingPreferencesProvider;

  public MainViewModel_Factory(Provider<OnboardingPreferences> onboardingPreferencesProvider) {
    this.onboardingPreferencesProvider = onboardingPreferencesProvider;
  }

  @Override
  public MainViewModel get() {
    return newInstance(onboardingPreferencesProvider.get());
  }

  public static MainViewModel_Factory create(
      Provider<OnboardingPreferences> onboardingPreferencesProvider) {
    return new MainViewModel_Factory(onboardingPreferencesProvider);
  }

  public static MainViewModel newInstance(OnboardingPreferences onboardingPreferences) {
    return new MainViewModel(onboardingPreferences);
  }
}
