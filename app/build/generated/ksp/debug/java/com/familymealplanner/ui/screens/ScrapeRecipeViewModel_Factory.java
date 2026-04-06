package com.familymealplanner.ui.screens;

import com.familymealplanner.data.preferences.OnboardingPreferences;
import com.familymealplanner.data.remote.OpenAiService;
import com.familymealplanner.domain.usecase.CreateScrapedMealUseCase;
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
public final class ScrapeRecipeViewModel_Factory implements Factory<ScrapeRecipeViewModel> {
  private final Provider<OpenAiService> openAiServiceProvider;

  private final Provider<CreateScrapedMealUseCase> createScrapedMealUseCaseProvider;

  private final Provider<OnboardingPreferences> preferencesProvider;

  public ScrapeRecipeViewModel_Factory(Provider<OpenAiService> openAiServiceProvider,
      Provider<CreateScrapedMealUseCase> createScrapedMealUseCaseProvider,
      Provider<OnboardingPreferences> preferencesProvider) {
    this.openAiServiceProvider = openAiServiceProvider;
    this.createScrapedMealUseCaseProvider = createScrapedMealUseCaseProvider;
    this.preferencesProvider = preferencesProvider;
  }

  @Override
  public ScrapeRecipeViewModel get() {
    return newInstance(openAiServiceProvider.get(), createScrapedMealUseCaseProvider.get(), preferencesProvider.get());
  }

  public static ScrapeRecipeViewModel_Factory create(Provider<OpenAiService> openAiServiceProvider,
      Provider<CreateScrapedMealUseCase> createScrapedMealUseCaseProvider,
      Provider<OnboardingPreferences> preferencesProvider) {
    return new ScrapeRecipeViewModel_Factory(openAiServiceProvider, createScrapedMealUseCaseProvider, preferencesProvider);
  }

  public static ScrapeRecipeViewModel newInstance(OpenAiService openAiService,
      CreateScrapedMealUseCase createScrapedMealUseCase, OnboardingPreferences preferences) {
    return new ScrapeRecipeViewModel(openAiService, createScrapedMealUseCase, preferences);
  }
}
