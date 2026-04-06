package com.familymealplanner.ui.navigation;

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
public final class NavViewModel_Factory implements Factory<NavViewModel> {
  private final Provider<OnboardingPreferences> onboardingPreferencesProvider;

  public NavViewModel_Factory(Provider<OnboardingPreferences> onboardingPreferencesProvider) {
    this.onboardingPreferencesProvider = onboardingPreferencesProvider;
  }

  @Override
  public NavViewModel get() {
    return newInstance(onboardingPreferencesProvider.get());
  }

  public static NavViewModel_Factory create(
      Provider<OnboardingPreferences> onboardingPreferencesProvider) {
    return new NavViewModel_Factory(onboardingPreferencesProvider);
  }

  public static NavViewModel newInstance(OnboardingPreferences onboardingPreferences) {
    return new NavViewModel(onboardingPreferences);
  }
}
