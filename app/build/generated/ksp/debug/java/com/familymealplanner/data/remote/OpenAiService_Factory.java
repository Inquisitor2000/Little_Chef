package com.familymealplanner.data.remote;

import com.familymealplanner.data.preferences.LocaleManager;
import com.familymealplanner.data.preferences.OnboardingPreferences;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import io.ktor.client.HttpClient;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import kotlinx.serialization.json.Json;

@ScopeMetadata("javax.inject.Singleton")
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
public final class OpenAiService_Factory implements Factory<OpenAiService> {
  private final Provider<HttpClient> httpClientProvider;

  private final Provider<OnboardingPreferences> preferencesProvider;

  private final Provider<LocaleManager> localeManagerProvider;

  private final Provider<Json> jsonProvider;

  public OpenAiService_Factory(Provider<HttpClient> httpClientProvider,
      Provider<OnboardingPreferences> preferencesProvider,
      Provider<LocaleManager> localeManagerProvider, Provider<Json> jsonProvider) {
    this.httpClientProvider = httpClientProvider;
    this.preferencesProvider = preferencesProvider;
    this.localeManagerProvider = localeManagerProvider;
    this.jsonProvider = jsonProvider;
  }

  @Override
  public OpenAiService get() {
    return newInstance(httpClientProvider.get(), preferencesProvider.get(), localeManagerProvider.get(), jsonProvider.get());
  }

  public static OpenAiService_Factory create(Provider<HttpClient> httpClientProvider,
      Provider<OnboardingPreferences> preferencesProvider,
      Provider<LocaleManager> localeManagerProvider, Provider<Json> jsonProvider) {
    return new OpenAiService_Factory(httpClientProvider, preferencesProvider, localeManagerProvider, jsonProvider);
  }

  public static OpenAiService newInstance(HttpClient httpClient, OnboardingPreferences preferences,
      LocaleManager localeManager, Json json) {
    return new OpenAiService(httpClient, preferences, localeManager, json);
  }
}
