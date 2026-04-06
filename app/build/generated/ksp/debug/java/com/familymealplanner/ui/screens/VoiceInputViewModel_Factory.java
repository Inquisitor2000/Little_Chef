package com.familymealplanner.ui.screens;

import com.familymealplanner.data.preferences.OnboardingPreferences;
import com.familymealplanner.domain.util.IngredientMatcher;
import com.familymealplanner.domain.util.VoiceRecognitionManager;
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
public final class VoiceInputViewModel_Factory implements Factory<VoiceInputViewModel> {
  private final Provider<VoiceRecognitionManager> voiceRecognitionManagerProvider;

  private final Provider<IngredientMatcher> ingredientMatcherProvider;

  private final Provider<OnboardingPreferences> onboardingPreferencesProvider;

  public VoiceInputViewModel_Factory(
      Provider<VoiceRecognitionManager> voiceRecognitionManagerProvider,
      Provider<IngredientMatcher> ingredientMatcherProvider,
      Provider<OnboardingPreferences> onboardingPreferencesProvider) {
    this.voiceRecognitionManagerProvider = voiceRecognitionManagerProvider;
    this.ingredientMatcherProvider = ingredientMatcherProvider;
    this.onboardingPreferencesProvider = onboardingPreferencesProvider;
  }

  @Override
  public VoiceInputViewModel get() {
    return newInstance(voiceRecognitionManagerProvider.get(), ingredientMatcherProvider.get(), onboardingPreferencesProvider.get());
  }

  public static VoiceInputViewModel_Factory create(
      Provider<VoiceRecognitionManager> voiceRecognitionManagerProvider,
      Provider<IngredientMatcher> ingredientMatcherProvider,
      Provider<OnboardingPreferences> onboardingPreferencesProvider) {
    return new VoiceInputViewModel_Factory(voiceRecognitionManagerProvider, ingredientMatcherProvider, onboardingPreferencesProvider);
  }

  public static VoiceInputViewModel newInstance(VoiceRecognitionManager voiceRecognitionManager,
      IngredientMatcher ingredientMatcher, OnboardingPreferences onboardingPreferences) {
    return new VoiceInputViewModel(voiceRecognitionManager, ingredientMatcher, onboardingPreferences);
  }
}
