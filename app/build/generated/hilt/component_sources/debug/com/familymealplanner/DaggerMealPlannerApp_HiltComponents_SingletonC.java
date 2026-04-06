package com.familymealplanner;

import android.app.Activity;
import android.app.Service;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import coil.ImageLoader;
import com.familymealplanner.data.local.AppDatabase;
import com.familymealplanner.data.local.BundledRecipeLoader;
import com.familymealplanner.data.local.CategoryTranslator;
import com.familymealplanner.data.local.ImagePreloader;
import com.familymealplanner.data.local.ImageStorage;
import com.familymealplanner.data.local.IngredientTranslator;
import com.familymealplanner.data.local.RecipeTranslator;
import com.familymealplanner.data.local.SubstituteInitializer;
import com.familymealplanner.data.local.TranslationSystem;
import com.familymealplanner.data.local.dao.AllergenDao;
import com.familymealplanner.data.local.dao.GroceryItemDao;
import com.familymealplanner.data.local.dao.IngredientAllergenDao;
import com.familymealplanner.data.local.dao.IngredientDao;
import com.familymealplanner.data.local.dao.IngredientSubstituteDao;
import com.familymealplanner.data.local.dao.InventoryTransactionDao;
import com.familymealplanner.data.local.dao.MealDao;
import com.familymealplanner.data.local.dao.MealIngredientDao;
import com.familymealplanner.data.local.dao.MealPlanDao;
import com.familymealplanner.data.preferences.FavoriteRecipesPreferences;
import com.familymealplanner.data.preferences.LocaleManager;
import com.familymealplanner.data.preferences.OnboardingPreferences;
import com.familymealplanner.data.remote.OpenAiService;
import com.familymealplanner.data.repository.AllergenRepositoryImpl;
import com.familymealplanner.data.repository.GroceryRepositoryImpl;
import com.familymealplanner.data.repository.IngredientRepositoryImpl;
import com.familymealplanner.data.repository.InventoryRepositoryImpl;
import com.familymealplanner.data.repository.MealPlanRepositoryImpl;
import com.familymealplanner.data.repository.MealRepositoryImpl;
import com.familymealplanner.di.AppModule;
import com.familymealplanner.di.AppModule_ProvideVoiceRecognitionManagerFactory;
import com.familymealplanner.di.DatabaseModule;
import com.familymealplanner.di.DatabaseModule_ProvideAllergenDaoFactory;
import com.familymealplanner.di.DatabaseModule_ProvideAppDatabaseFactory;
import com.familymealplanner.di.DatabaseModule_ProvideGroceryItemDaoFactory;
import com.familymealplanner.di.DatabaseModule_ProvideIngredientAllergenDaoFactory;
import com.familymealplanner.di.DatabaseModule_ProvideIngredientDaoFactory;
import com.familymealplanner.di.DatabaseModule_ProvideIngredientSubstituteDaoFactory;
import com.familymealplanner.di.DatabaseModule_ProvideInventoryTransactionDaoFactory;
import com.familymealplanner.di.DatabaseModule_ProvideMealDaoFactory;
import com.familymealplanner.di.DatabaseModule_ProvideMealIngredientDaoFactory;
import com.familymealplanner.di.DatabaseModule_ProvideMealPlanDaoFactory;
import com.familymealplanner.di.ImageModule;
import com.familymealplanner.di.ImageModule_ProvideImageLoaderFactory;
import com.familymealplanner.di.NetworkModule;
import com.familymealplanner.di.NetworkModule_ProvideHttpClientFactory;
import com.familymealplanner.di.NetworkModule_ProvideJsonFactory;
import com.familymealplanner.domain.repository.AllergenRepository;
import com.familymealplanner.domain.repository.GroceryRepository;
import com.familymealplanner.domain.repository.IngredientRepository;
import com.familymealplanner.domain.repository.InventoryRepository;
import com.familymealplanner.domain.repository.MealPlanRepository;
import com.familymealplanner.domain.repository.MealRepository;
import com.familymealplanner.domain.usecase.AbortCookingUseCase;
import com.familymealplanner.domain.usecase.AddGroceryItemToPantryUseCase;
import com.familymealplanner.domain.usecase.AddIngredientSubstituteUseCase;
import com.familymealplanner.domain.usecase.AdjustInventoryUseCase;
import com.familymealplanner.domain.usecase.CheckRecipeIngredientsUseCase;
import com.familymealplanner.domain.usecase.CompleteCookingUseCase;
import com.familymealplanner.domain.usecase.CreateAllergenUseCase;
import com.familymealplanner.domain.usecase.CreateIngredientUseCase;
import com.familymealplanner.domain.usecase.CreateMealPlanUseCase;
import com.familymealplanner.domain.usecase.CreateMealUseCase;
import com.familymealplanner.domain.usecase.CreateScrapedMealUseCase;
import com.familymealplanner.domain.usecase.DeleteAllergenUseCase;
import com.familymealplanner.domain.usecase.DeleteIngredientUseCase;
import com.familymealplanner.domain.usecase.DeleteMealUseCase;
import com.familymealplanner.domain.usecase.FixIngredientCategoriesUseCase;
import com.familymealplanner.domain.usecase.PreloadCuisineAllergensUseCase;
import com.familymealplanner.domain.usecase.RestockIngredientUseCase;
import com.familymealplanner.domain.usecase.StartCookingUseCase;
import com.familymealplanner.domain.usecase.UpdateIngredientUseCase;
import com.familymealplanner.domain.usecase.UpdateMealUseCase;
import com.familymealplanner.domain.util.IngredientMatcher;
import com.familymealplanner.domain.util.VoiceRecognitionManager;
import com.familymealplanner.ui.navigation.NavViewModel;
import com.familymealplanner.ui.navigation.NavViewModel_HiltModules_KeyModule_ProvideFactory;
import com.familymealplanner.ui.onboarding.OnboardingViewModel;
import com.familymealplanner.ui.onboarding.OnboardingViewModel_HiltModules_KeyModule_ProvideFactory;
import com.familymealplanner.ui.screens.AllergensViewModel;
import com.familymealplanner.ui.screens.AllergensViewModel_HiltModules_KeyModule_ProvideFactory;
import com.familymealplanner.ui.screens.BundledRecipeDetailViewModel;
import com.familymealplanner.ui.screens.BundledRecipeDetailViewModel_HiltModules_KeyModule_ProvideFactory;
import com.familymealplanner.ui.screens.CuisineMealsViewModel;
import com.familymealplanner.ui.screens.CuisineMealsViewModel_HiltModules_KeyModule_ProvideFactory;
import com.familymealplanner.ui.screens.GroceriesViewModel;
import com.familymealplanner.ui.screens.GroceriesViewModel_HiltModules_KeyModule_ProvideFactory;
import com.familymealplanner.ui.screens.IngredientsViewModel;
import com.familymealplanner.ui.screens.IngredientsViewModel_HiltModules_KeyModule_ProvideFactory;
import com.familymealplanner.ui.screens.ManualRecipeViewModel;
import com.familymealplanner.ui.screens.ManualRecipeViewModel_HiltModules_KeyModule_ProvideFactory;
import com.familymealplanner.ui.screens.MealsViewModel;
import com.familymealplanner.ui.screens.MealsViewModel_HiltModules_KeyModule_ProvideFactory;
import com.familymealplanner.ui.screens.PantryViewModel;
import com.familymealplanner.ui.screens.PantryViewModel_HiltModules_KeyModule_ProvideFactory;
import com.familymealplanner.ui.screens.PlanViewModel;
import com.familymealplanner.ui.screens.PlanViewModel_HiltModules_KeyModule_ProvideFactory;
import com.familymealplanner.ui.screens.RecipeDetailViewModel;
import com.familymealplanner.ui.screens.RecipeDetailViewModel_HiltModules_KeyModule_ProvideFactory;
import com.familymealplanner.ui.screens.ScrapeRecipeViewModel;
import com.familymealplanner.ui.screens.ScrapeRecipeViewModel_HiltModules_KeyModule_ProvideFactory;
import com.familymealplanner.ui.screens.SettingsViewModel;
import com.familymealplanner.ui.screens.SettingsViewModel_HiltModules_KeyModule_ProvideFactory;
import com.familymealplanner.ui.screens.SuggestionViewModel;
import com.familymealplanner.ui.screens.SuggestionViewModel_HiltModules_KeyModule_ProvideFactory;
import com.familymealplanner.ui.screens.VoiceInputViewModel;
import com.familymealplanner.ui.screens.VoiceInputViewModel_HiltModules_KeyModule_ProvideFactory;
import dagger.hilt.android.ActivityRetainedLifecycle;
import dagger.hilt.android.ViewModelLifecycle;
import dagger.hilt.android.flags.HiltWrapper_FragmentGetContextFix_FragmentGetContextFixModule;
import dagger.hilt.android.internal.builders.ActivityComponentBuilder;
import dagger.hilt.android.internal.builders.ActivityRetainedComponentBuilder;
import dagger.hilt.android.internal.builders.FragmentComponentBuilder;
import dagger.hilt.android.internal.builders.ServiceComponentBuilder;
import dagger.hilt.android.internal.builders.ViewComponentBuilder;
import dagger.hilt.android.internal.builders.ViewModelComponentBuilder;
import dagger.hilt.android.internal.builders.ViewWithFragmentComponentBuilder;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories_InternalFactoryFactory_Factory;
import dagger.hilt.android.internal.managers.ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory;
import dagger.hilt.android.internal.modules.ApplicationContextModule;
import dagger.hilt.android.internal.modules.ApplicationContextModule_ProvideContextFactory;
import dagger.internal.DaggerGenerated;
import dagger.internal.DoubleCheck;
import dagger.internal.MapBuilder;
import dagger.internal.Preconditions;
import dagger.internal.SetBuilder;
import io.ktor.client.HttpClient;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import kotlinx.serialization.json.Json;

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
public final class DaggerMealPlannerApp_HiltComponents_SingletonC {
  private DaggerMealPlannerApp_HiltComponents_SingletonC() {
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private ApplicationContextModule applicationContextModule;

    private Builder() {
    }

    /**
     * @deprecated This module is declared, but an instance is not used in the component. This method is a no-op. For more, see https://dagger.dev/unused-modules.
     */
    @Deprecated
    public Builder appModule(AppModule appModule) {
      Preconditions.checkNotNull(appModule);
      return this;
    }

    public Builder applicationContextModule(ApplicationContextModule applicationContextModule) {
      this.applicationContextModule = Preconditions.checkNotNull(applicationContextModule);
      return this;
    }

    /**
     * @deprecated This module is declared, but an instance is not used in the component. This method is a no-op. For more, see https://dagger.dev/unused-modules.
     */
    @Deprecated
    public Builder databaseModule(DatabaseModule databaseModule) {
      Preconditions.checkNotNull(databaseModule);
      return this;
    }

    /**
     * @deprecated This module is declared, but an instance is not used in the component. This method is a no-op. For more, see https://dagger.dev/unused-modules.
     */
    @Deprecated
    public Builder hiltWrapper_FragmentGetContextFix_FragmentGetContextFixModule(
        HiltWrapper_FragmentGetContextFix_FragmentGetContextFixModule hiltWrapper_FragmentGetContextFix_FragmentGetContextFixModule) {
      Preconditions.checkNotNull(hiltWrapper_FragmentGetContextFix_FragmentGetContextFixModule);
      return this;
    }

    /**
     * @deprecated This module is declared, but an instance is not used in the component. This method is a no-op. For more, see https://dagger.dev/unused-modules.
     */
    @Deprecated
    public Builder imageModule(ImageModule imageModule) {
      Preconditions.checkNotNull(imageModule);
      return this;
    }

    /**
     * @deprecated This module is declared, but an instance is not used in the component. This method is a no-op. For more, see https://dagger.dev/unused-modules.
     */
    @Deprecated
    public Builder networkModule(NetworkModule networkModule) {
      Preconditions.checkNotNull(networkModule);
      return this;
    }

    public MealPlannerApp_HiltComponents.SingletonC build() {
      Preconditions.checkBuilderRequirement(applicationContextModule, ApplicationContextModule.class);
      return new SingletonCImpl(applicationContextModule);
    }
  }

  private static final class ActivityRetainedCBuilder implements MealPlannerApp_HiltComponents.ActivityRetainedC.Builder {
    private final SingletonCImpl singletonCImpl;

    private ActivityRetainedCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public MealPlannerApp_HiltComponents.ActivityRetainedC build() {
      return new ActivityRetainedCImpl(singletonCImpl);
    }
  }

  private static final class ActivityCBuilder implements MealPlannerApp_HiltComponents.ActivityC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private Activity activity;

    private ActivityCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ActivityCBuilder activity(Activity activity) {
      this.activity = Preconditions.checkNotNull(activity);
      return this;
    }

    @Override
    public MealPlannerApp_HiltComponents.ActivityC build() {
      Preconditions.checkBuilderRequirement(activity, Activity.class);
      return new ActivityCImpl(singletonCImpl, activityRetainedCImpl, activity);
    }
  }

  private static final class FragmentCBuilder implements MealPlannerApp_HiltComponents.FragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private Fragment fragment;

    private FragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public FragmentCBuilder fragment(Fragment fragment) {
      this.fragment = Preconditions.checkNotNull(fragment);
      return this;
    }

    @Override
    public MealPlannerApp_HiltComponents.FragmentC build() {
      Preconditions.checkBuilderRequirement(fragment, Fragment.class);
      return new FragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragment);
    }
  }

  private static final class ViewWithFragmentCBuilder implements MealPlannerApp_HiltComponents.ViewWithFragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private View view;

    private ViewWithFragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;
    }

    @Override
    public ViewWithFragmentCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public MealPlannerApp_HiltComponents.ViewWithFragmentC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewWithFragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl, view);
    }
  }

  private static final class ViewCBuilder implements MealPlannerApp_HiltComponents.ViewC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private View view;

    private ViewCBuilder(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public ViewCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public MealPlannerApp_HiltComponents.ViewC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, view);
    }
  }

  private static final class ViewModelCBuilder implements MealPlannerApp_HiltComponents.ViewModelC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private SavedStateHandle savedStateHandle;

    private ViewModelLifecycle viewModelLifecycle;

    private ViewModelCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ViewModelCBuilder savedStateHandle(SavedStateHandle handle) {
      this.savedStateHandle = Preconditions.checkNotNull(handle);
      return this;
    }

    @Override
    public ViewModelCBuilder viewModelLifecycle(ViewModelLifecycle viewModelLifecycle) {
      this.viewModelLifecycle = Preconditions.checkNotNull(viewModelLifecycle);
      return this;
    }

    @Override
    public MealPlannerApp_HiltComponents.ViewModelC build() {
      Preconditions.checkBuilderRequirement(savedStateHandle, SavedStateHandle.class);
      Preconditions.checkBuilderRequirement(viewModelLifecycle, ViewModelLifecycle.class);
      return new ViewModelCImpl(singletonCImpl, activityRetainedCImpl, savedStateHandle, viewModelLifecycle);
    }
  }

  private static final class ServiceCBuilder implements MealPlannerApp_HiltComponents.ServiceC.Builder {
    private final SingletonCImpl singletonCImpl;

    private Service service;

    private ServiceCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ServiceCBuilder service(Service service) {
      this.service = Preconditions.checkNotNull(service);
      return this;
    }

    @Override
    public MealPlannerApp_HiltComponents.ServiceC build() {
      Preconditions.checkBuilderRequirement(service, Service.class);
      return new ServiceCImpl(singletonCImpl, service);
    }
  }

  private static final class ViewWithFragmentCImpl extends MealPlannerApp_HiltComponents.ViewWithFragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private final ViewWithFragmentCImpl viewWithFragmentCImpl = this;

    private ViewWithFragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;


    }
  }

  private static final class FragmentCImpl extends MealPlannerApp_HiltComponents.FragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl = this;

    private FragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        Fragment fragmentParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return activityCImpl.getHiltInternalFactoryFactory();
    }

    @Override
    public ViewWithFragmentComponentBuilder viewWithFragmentComponentBuilder() {
      return new ViewWithFragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl);
    }
  }

  private static final class ViewCImpl extends MealPlannerApp_HiltComponents.ViewC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final ViewCImpl viewCImpl = this;

    private ViewCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }
  }

  private static final class ActivityCImpl extends MealPlannerApp_HiltComponents.ActivityC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl = this;

    private ActivityCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, Activity activityParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;


    }

    @Override
    public void injectMainActivity(MainActivity mainActivity) {
      injectMainActivity2(mainActivity);
    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return DefaultViewModelFactories_InternalFactoryFactory_Factory.newInstance(getViewModelKeys(), new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl));
    }

    @Override
    public Set<String> getViewModelKeys() {
      return SetBuilder.<String>newSetBuilder(17).add(AllergensViewModel_HiltModules_KeyModule_ProvideFactory.provide()).add(BundledRecipeDetailViewModel_HiltModules_KeyModule_ProvideFactory.provide()).add(CuisineMealsViewModel_HiltModules_KeyModule_ProvideFactory.provide()).add(GroceriesViewModel_HiltModules_KeyModule_ProvideFactory.provide()).add(IngredientsViewModel_HiltModules_KeyModule_ProvideFactory.provide()).add(MainViewModel_HiltModules_KeyModule_ProvideFactory.provide()).add(ManualRecipeViewModel_HiltModules_KeyModule_ProvideFactory.provide()).add(MealsViewModel_HiltModules_KeyModule_ProvideFactory.provide()).add(NavViewModel_HiltModules_KeyModule_ProvideFactory.provide()).add(OnboardingViewModel_HiltModules_KeyModule_ProvideFactory.provide()).add(PantryViewModel_HiltModules_KeyModule_ProvideFactory.provide()).add(PlanViewModel_HiltModules_KeyModule_ProvideFactory.provide()).add(RecipeDetailViewModel_HiltModules_KeyModule_ProvideFactory.provide()).add(ScrapeRecipeViewModel_HiltModules_KeyModule_ProvideFactory.provide()).add(SettingsViewModel_HiltModules_KeyModule_ProvideFactory.provide()).add(SuggestionViewModel_HiltModules_KeyModule_ProvideFactory.provide()).add(VoiceInputViewModel_HiltModules_KeyModule_ProvideFactory.provide()).build();
    }

    @Override
    public ViewModelComponentBuilder getViewModelComponentBuilder() {
      return new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public FragmentComponentBuilder fragmentComponentBuilder() {
      return new FragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @Override
    public ViewComponentBuilder viewComponentBuilder() {
      return new ViewCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    private MainActivity injectMainActivity2(MainActivity instance) {
      MainActivity_MembersInjector.injectLocaleManager(instance, singletonCImpl.localeManagerProvider.get());
      MainActivity_MembersInjector.injectTranslationSystem(instance, singletonCImpl.translationSystemProvider.get());
      MainActivity_MembersInjector.injectPreloadCuisineAllergensUseCase(instance, singletonCImpl.preloadCuisineAllergensUseCaseProvider.get());
      return instance;
    }
  }

  private static final class ViewModelCImpl extends MealPlannerApp_HiltComponents.ViewModelC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ViewModelCImpl viewModelCImpl = this;

    private Provider<AllergensViewModel> allergensViewModelProvider;

    private Provider<BundledRecipeDetailViewModel> bundledRecipeDetailViewModelProvider;

    private Provider<CuisineMealsViewModel> cuisineMealsViewModelProvider;

    private Provider<GroceriesViewModel> groceriesViewModelProvider;

    private Provider<IngredientsViewModel> ingredientsViewModelProvider;

    private Provider<MainViewModel> mainViewModelProvider;

    private Provider<ManualRecipeViewModel> manualRecipeViewModelProvider;

    private Provider<MealsViewModel> mealsViewModelProvider;

    private Provider<NavViewModel> navViewModelProvider;

    private Provider<OnboardingViewModel> onboardingViewModelProvider;

    private Provider<PantryViewModel> pantryViewModelProvider;

    private Provider<PlanViewModel> planViewModelProvider;

    private Provider<RecipeDetailViewModel> recipeDetailViewModelProvider;

    private Provider<ScrapeRecipeViewModel> scrapeRecipeViewModelProvider;

    private Provider<SettingsViewModel> settingsViewModelProvider;

    private Provider<SuggestionViewModel> suggestionViewModelProvider;

    private Provider<VoiceInputViewModel> voiceInputViewModelProvider;

    private ViewModelCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, SavedStateHandle savedStateHandleParam,
        ViewModelLifecycle viewModelLifecycleParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;

      initialize(savedStateHandleParam, viewModelLifecycleParam);

    }

    private CreateAllergenUseCase createAllergenUseCase() {
      return new CreateAllergenUseCase(singletonCImpl.bindAllergenRepositoryProvider.get());
    }

    private DeleteAllergenUseCase deleteAllergenUseCase() {
      return new DeleteAllergenUseCase(singletonCImpl.bindAllergenRepositoryProvider.get());
    }

    private CheckRecipeIngredientsUseCase checkRecipeIngredientsUseCase() {
      return new CheckRecipeIngredientsUseCase(singletonCImpl.bindInventoryRepositoryProvider.get(), singletonCImpl.bindIngredientRepositoryProvider.get());
    }

    private CreateScrapedMealUseCase createScrapedMealUseCase() {
      return new CreateScrapedMealUseCase(singletonCImpl.bindMealRepositoryProvider.get(), singletonCImpl.bindIngredientRepositoryProvider.get(), singletonCImpl.imageStorageProvider.get(), singletonCImpl.ingredientMatcherProvider.get(), singletonCImpl.translationSystemProvider.get());
    }

    private CreateMealPlanUseCase createMealPlanUseCase() {
      return new CreateMealPlanUseCase(singletonCImpl.bindMealPlanRepositoryProvider.get(), singletonCImpl.bindMealRepositoryProvider.get(), singletonCImpl.bindInventoryRepositoryProvider.get());
    }

    private StartCookingUseCase startCookingUseCase() {
      return new StartCookingUseCase(singletonCImpl.bindMealPlanRepositoryProvider.get(), singletonCImpl.bindInventoryRepositoryProvider.get());
    }

    private RestockIngredientUseCase restockIngredientUseCase() {
      return new RestockIngredientUseCase(singletonCImpl.bindInventoryRepositoryProvider.get(), singletonCImpl.bindIngredientRepositoryProvider.get());
    }

    private CreateIngredientUseCase createIngredientUseCase() {
      return new CreateIngredientUseCase(singletonCImpl.bindIngredientRepositoryProvider.get());
    }

    private AddGroceryItemToPantryUseCase addGroceryItemToPantryUseCase() {
      return new AddGroceryItemToPantryUseCase(singletonCImpl.bindIngredientRepositoryProvider.get(), restockIngredientUseCase(), createIngredientUseCase());
    }

    private UpdateIngredientUseCase updateIngredientUseCase() {
      return new UpdateIngredientUseCase(singletonCImpl.bindIngredientRepositoryProvider.get());
    }

    private DeleteIngredientUseCase deleteIngredientUseCase() {
      return new DeleteIngredientUseCase(singletonCImpl.bindIngredientRepositoryProvider.get());
    }

    private AddIngredientSubstituteUseCase addIngredientSubstituteUseCase() {
      return new AddIngredientSubstituteUseCase(singletonCImpl.bindIngredientRepositoryProvider.get());
    }

    private FixIngredientCategoriesUseCase fixIngredientCategoriesUseCase() {
      return new FixIngredientCategoriesUseCase(singletonCImpl.bindIngredientRepositoryProvider.get());
    }

    private CreateMealUseCase createMealUseCase() {
      return new CreateMealUseCase(singletonCImpl.bindMealRepositoryProvider.get());
    }

    private UpdateMealUseCase updateMealUseCase() {
      return new UpdateMealUseCase(singletonCImpl.bindMealRepositoryProvider.get());
    }

    private DeleteMealUseCase deleteMealUseCase() {
      return new DeleteMealUseCase(singletonCImpl.bindMealRepositoryProvider.get());
    }

    private AdjustInventoryUseCase adjustInventoryUseCase() {
      return new AdjustInventoryUseCase(singletonCImpl.bindInventoryRepositoryProvider.get(), singletonCImpl.bindIngredientRepositoryProvider.get());
    }

    private CompleteCookingUseCase completeCookingUseCase() {
      return new CompleteCookingUseCase(singletonCImpl.bindMealPlanRepositoryProvider.get(), singletonCImpl.bindInventoryRepositoryProvider.get());
    }

    private AbortCookingUseCase abortCookingUseCase() {
      return new AbortCookingUseCase(singletonCImpl.bindMealPlanRepositoryProvider.get(), singletonCImpl.bindInventoryRepositoryProvider.get());
    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandle savedStateHandleParam,
        final ViewModelLifecycle viewModelLifecycleParam) {
      this.allergensViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 0);
      this.bundledRecipeDetailViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 1);
      this.cuisineMealsViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 2);
      this.groceriesViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 3);
      this.ingredientsViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 4);
      this.mainViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 5);
      this.manualRecipeViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 6);
      this.mealsViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 7);
      this.navViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 8);
      this.onboardingViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 9);
      this.pantryViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 10);
      this.planViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 11);
      this.recipeDetailViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 12);
      this.scrapeRecipeViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 13);
      this.settingsViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 14);
      this.suggestionViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 15);
      this.voiceInputViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 16);
    }

    @Override
    public Map<String, Provider<ViewModel>> getHiltViewModelMap() {
      return MapBuilder.<String, Provider<ViewModel>>newMapBuilder(17).put("com.familymealplanner.ui.screens.AllergensViewModel", ((Provider) allergensViewModelProvider)).put("com.familymealplanner.ui.screens.BundledRecipeDetailViewModel", ((Provider) bundledRecipeDetailViewModelProvider)).put("com.familymealplanner.ui.screens.CuisineMealsViewModel", ((Provider) cuisineMealsViewModelProvider)).put("com.familymealplanner.ui.screens.GroceriesViewModel", ((Provider) groceriesViewModelProvider)).put("com.familymealplanner.ui.screens.IngredientsViewModel", ((Provider) ingredientsViewModelProvider)).put("com.familymealplanner.MainViewModel", ((Provider) mainViewModelProvider)).put("com.familymealplanner.ui.screens.ManualRecipeViewModel", ((Provider) manualRecipeViewModelProvider)).put("com.familymealplanner.ui.screens.MealsViewModel", ((Provider) mealsViewModelProvider)).put("com.familymealplanner.ui.navigation.NavViewModel", ((Provider) navViewModelProvider)).put("com.familymealplanner.ui.onboarding.OnboardingViewModel", ((Provider) onboardingViewModelProvider)).put("com.familymealplanner.ui.screens.PantryViewModel", ((Provider) pantryViewModelProvider)).put("com.familymealplanner.ui.screens.PlanViewModel", ((Provider) planViewModelProvider)).put("com.familymealplanner.ui.screens.RecipeDetailViewModel", ((Provider) recipeDetailViewModelProvider)).put("com.familymealplanner.ui.screens.ScrapeRecipeViewModel", ((Provider) scrapeRecipeViewModelProvider)).put("com.familymealplanner.ui.screens.SettingsViewModel", ((Provider) settingsViewModelProvider)).put("com.familymealplanner.ui.screens.SuggestionViewModel", ((Provider) suggestionViewModelProvider)).put("com.familymealplanner.ui.screens.VoiceInputViewModel", ((Provider) voiceInputViewModelProvider)).build();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final ViewModelCImpl viewModelCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          ViewModelCImpl viewModelCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.viewModelCImpl = viewModelCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.familymealplanner.ui.screens.AllergensViewModel 
          return (T) new AllergensViewModel(singletonCImpl.bindAllergenRepositoryProvider.get(), viewModelCImpl.createAllergenUseCase(), viewModelCImpl.deleteAllergenUseCase());

          case 1: // com.familymealplanner.ui.screens.BundledRecipeDetailViewModel 
          return (T) new BundledRecipeDetailViewModel(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), singletonCImpl.localeManagerProvider.get(), singletonCImpl.bundledRecipeLoaderProvider.get(), singletonCImpl.translationSystemProvider.get(), viewModelCImpl.checkRecipeIngredientsUseCase(), singletonCImpl.bindGroceryRepositoryProvider.get(), singletonCImpl.onboardingPreferencesProvider.get(), viewModelCImpl.createScrapedMealUseCase(), viewModelCImpl.createMealPlanUseCase(), viewModelCImpl.startCookingUseCase(), singletonCImpl.bindMealRepositoryProvider.get(), singletonCImpl.bindMealPlanRepositoryProvider.get(), singletonCImpl.bindInventoryRepositoryProvider.get(), singletonCImpl.bindIngredientRepositoryProvider.get(), singletonCImpl.ingredientMatcherProvider.get());

          case 2: // com.familymealplanner.ui.screens.CuisineMealsViewModel 
          return (T) new CuisineMealsViewModel(singletonCImpl.bundledRecipeLoaderProvider.get(), singletonCImpl.translationSystemProvider.get(), singletonCImpl.bindIngredientRepositoryProvider.get(), singletonCImpl.onboardingPreferencesProvider.get(), singletonCImpl.ingredientMatcherProvider.get(), singletonCImpl.preloadCuisineAllergensUseCaseProvider.get(), singletonCImpl.favoriteRecipesPreferencesProvider.get());

          case 3: // com.familymealplanner.ui.screens.GroceriesViewModel 
          return (T) new GroceriesViewModel(singletonCImpl.bindGroceryRepositoryProvider.get(), viewModelCImpl.addGroceryItemToPantryUseCase(), singletonCImpl.onboardingPreferencesProvider.get(), singletonCImpl.bindIngredientRepositoryProvider.get(), singletonCImpl.bindInventoryRepositoryProvider.get(), singletonCImpl.translationSystemProvider.get(), singletonCImpl.ingredientMatcherProvider.get());

          case 4: // com.familymealplanner.ui.screens.IngredientsViewModel 
          return (T) new IngredientsViewModel(singletonCImpl.bindIngredientRepositoryProvider.get(), viewModelCImpl.createIngredientUseCase(), viewModelCImpl.updateIngredientUseCase(), viewModelCImpl.deleteIngredientUseCase(), viewModelCImpl.addIngredientSubstituteUseCase(), viewModelCImpl.fixIngredientCategoriesUseCase());

          case 5: // com.familymealplanner.MainViewModel 
          return (T) new MainViewModel(singletonCImpl.onboardingPreferencesProvider.get());

          case 6: // com.familymealplanner.ui.screens.ManualRecipeViewModel 
          return (T) new ManualRecipeViewModel(singletonCImpl.bindMealRepositoryProvider.get(), singletonCImpl.bindIngredientRepositoryProvider.get(), singletonCImpl.imageStorageProvider.get(), singletonCImpl.onboardingPreferencesProvider.get(), singletonCImpl.ingredientMatcherProvider.get());

          case 7: // com.familymealplanner.ui.screens.MealsViewModel 
          return (T) new MealsViewModel(singletonCImpl.bindMealRepositoryProvider.get(), singletonCImpl.translationSystemProvider.get(), viewModelCImpl.createMealUseCase(), viewModelCImpl.updateMealUseCase(), viewModelCImpl.deleteMealUseCase());

          case 8: // com.familymealplanner.ui.navigation.NavViewModel 
          return (T) new NavViewModel(singletonCImpl.onboardingPreferencesProvider.get());

          case 9: // com.familymealplanner.ui.onboarding.OnboardingViewModel 
          return (T) new OnboardingViewModel(singletonCImpl.onboardingPreferencesProvider.get(), singletonCImpl.localeManagerProvider.get());

          case 10: // com.familymealplanner.ui.screens.PantryViewModel 
          return (T) new PantryViewModel(singletonCImpl.bindInventoryRepositoryProvider.get(), singletonCImpl.translationSystemProvider.get(), viewModelCImpl.restockIngredientUseCase(), viewModelCImpl.adjustInventoryUseCase(), singletonCImpl.bindIngredientRepositoryProvider.get(), singletonCImpl.onboardingPreferencesProvider.get());

          case 11: // com.familymealplanner.ui.screens.PlanViewModel 
          return (T) new PlanViewModel(singletonCImpl.bindMealPlanRepositoryProvider.get(), singletonCImpl.bindMealRepositoryProvider.get(), singletonCImpl.bindInventoryRepositoryProvider.get(), singletonCImpl.bindGroceryRepositoryProvider.get(), viewModelCImpl.createMealPlanUseCase(), viewModelCImpl.startCookingUseCase(), viewModelCImpl.completeCookingUseCase(), viewModelCImpl.abortCookingUseCase(), singletonCImpl.onboardingPreferencesProvider.get(), singletonCImpl.substituteInitializerProvider.get(), singletonCImpl.translationSystemProvider.get());

          case 12: // com.familymealplanner.ui.screens.RecipeDetailViewModel 
          return (T) new RecipeDetailViewModel(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), singletonCImpl.localeManagerProvider.get(), singletonCImpl.bindMealRepositoryProvider.get(), viewModelCImpl.deleteMealUseCase(), singletonCImpl.imageStorageProvider.get(), viewModelCImpl.checkRecipeIngredientsUseCase(), singletonCImpl.bindGroceryRepositoryProvider.get(), viewModelCImpl.createMealPlanUseCase(), singletonCImpl.onboardingPreferencesProvider.get(), singletonCImpl.bindMealPlanRepositoryProvider.get(), singletonCImpl.bindInventoryRepositoryProvider.get(), singletonCImpl.translationSystemProvider.get());

          case 13: // com.familymealplanner.ui.screens.ScrapeRecipeViewModel 
          return (T) new ScrapeRecipeViewModel(singletonCImpl.openAiServiceProvider.get(), viewModelCImpl.createScrapedMealUseCase(), singletonCImpl.onboardingPreferencesProvider.get());

          case 14: // com.familymealplanner.ui.screens.SettingsViewModel 
          return (T) new SettingsViewModel(singletonCImpl.onboardingPreferencesProvider.get(), singletonCImpl.localeManagerProvider.get());

          case 15: // com.familymealplanner.ui.screens.SuggestionViewModel 
          return (T) new SuggestionViewModel(singletonCImpl.bindMealRepositoryProvider.get(), singletonCImpl.bundledRecipeLoaderProvider.get(), singletonCImpl.bindInventoryRepositoryProvider.get(), singletonCImpl.bindIngredientRepositoryProvider.get(), singletonCImpl.onboardingPreferencesProvider.get(), singletonCImpl.substituteInitializerProvider.get());

          case 16: // com.familymealplanner.ui.screens.VoiceInputViewModel 
          return (T) new VoiceInputViewModel(singletonCImpl.provideVoiceRecognitionManagerProvider.get(), singletonCImpl.ingredientMatcherProvider.get(), singletonCImpl.onboardingPreferencesProvider.get());

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ActivityRetainedCImpl extends MealPlannerApp_HiltComponents.ActivityRetainedC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl = this;

    private Provider<ActivityRetainedLifecycle> provideActivityRetainedLifecycleProvider;

    private ActivityRetainedCImpl(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;

      initialize();

    }

    @SuppressWarnings("unchecked")
    private void initialize() {
      this.provideActivityRetainedLifecycleProvider = DoubleCheck.provider(new SwitchingProvider<ActivityRetainedLifecycle>(singletonCImpl, activityRetainedCImpl, 0));
    }

    @Override
    public ActivityComponentBuilder activityComponentBuilder() {
      return new ActivityCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public ActivityRetainedLifecycle getActivityRetainedLifecycle() {
      return provideActivityRetainedLifecycleProvider.get();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // dagger.hilt.android.ActivityRetainedLifecycle 
          return (T) ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory.provideActivityRetainedLifecycle();

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ServiceCImpl extends MealPlannerApp_HiltComponents.ServiceC {
    private final SingletonCImpl singletonCImpl;

    private final ServiceCImpl serviceCImpl = this;

    private ServiceCImpl(SingletonCImpl singletonCImpl, Service serviceParam) {
      this.singletonCImpl = singletonCImpl;


    }
  }

  private static final class SingletonCImpl extends MealPlannerApp_HiltComponents.SingletonC {
    private final ApplicationContextModule applicationContextModule;

    private final SingletonCImpl singletonCImpl = this;

    private Provider<BundledRecipeLoader> bundledRecipeLoaderProvider;

    private Provider<ImageLoader> provideImageLoaderProvider;

    private Provider<ImagePreloader> imagePreloaderProvider;

    private Provider<AppDatabase> provideAppDatabaseProvider;

    private Provider<SubstituteInitializer> substituteInitializerProvider;

    private Provider<IngredientTranslator> ingredientTranslatorProvider;

    private Provider<RecipeTranslator> recipeTranslatorProvider;

    private Provider<CategoryTranslator> categoryTranslatorProvider;

    private Provider<TranslationSystem> translationSystemProvider;

    private Provider<LocaleManager> localeManagerProvider;

    private Provider<IngredientMatcher> ingredientMatcherProvider;

    private Provider<PreloadCuisineAllergensUseCase> preloadCuisineAllergensUseCaseProvider;

    private Provider<AllergenRepositoryImpl> allergenRepositoryImplProvider;

    private Provider<AllergenRepository> bindAllergenRepositoryProvider;

    private Provider<IngredientRepositoryImpl> ingredientRepositoryImplProvider;

    private Provider<IngredientRepository> bindIngredientRepositoryProvider;

    private Provider<InventoryRepositoryImpl> inventoryRepositoryImplProvider;

    private Provider<InventoryRepository> bindInventoryRepositoryProvider;

    private Provider<GroceryRepositoryImpl> groceryRepositoryImplProvider;

    private Provider<GroceryRepository> bindGroceryRepositoryProvider;

    private Provider<OnboardingPreferences> onboardingPreferencesProvider;

    private Provider<MealRepositoryImpl> mealRepositoryImplProvider;

    private Provider<MealRepository> bindMealRepositoryProvider;

    private Provider<ImageStorage> imageStorageProvider;

    private Provider<MealPlanRepositoryImpl> mealPlanRepositoryImplProvider;

    private Provider<MealPlanRepository> bindMealPlanRepositoryProvider;

    private Provider<FavoriteRecipesPreferences> favoriteRecipesPreferencesProvider;

    private Provider<Json> provideJsonProvider;

    private Provider<HttpClient> provideHttpClientProvider;

    private Provider<OpenAiService> openAiServiceProvider;

    private Provider<VoiceRecognitionManager> provideVoiceRecognitionManagerProvider;

    private SingletonCImpl(ApplicationContextModule applicationContextModuleParam) {
      this.applicationContextModule = applicationContextModuleParam;
      initialize(applicationContextModuleParam);

    }

    private IngredientDao ingredientDao() {
      return DatabaseModule_ProvideIngredientDaoFactory.provideIngredientDao(provideAppDatabaseProvider.get());
    }

    private IngredientSubstituteDao ingredientSubstituteDao() {
      return DatabaseModule_ProvideIngredientSubstituteDaoFactory.provideIngredientSubstituteDao(provideAppDatabaseProvider.get());
    }

    private AllergenDao allergenDao() {
      return DatabaseModule_ProvideAllergenDaoFactory.provideAllergenDao(provideAppDatabaseProvider.get());
    }

    private InventoryTransactionDao inventoryTransactionDao() {
      return DatabaseModule_ProvideInventoryTransactionDaoFactory.provideInventoryTransactionDao(provideAppDatabaseProvider.get());
    }

    private MealPlanDao mealPlanDao() {
      return DatabaseModule_ProvideMealPlanDaoFactory.provideMealPlanDao(provideAppDatabaseProvider.get());
    }

    private IngredientAllergenDao ingredientAllergenDao() {
      return DatabaseModule_ProvideIngredientAllergenDaoFactory.provideIngredientAllergenDao(provideAppDatabaseProvider.get());
    }

    private MealIngredientDao mealIngredientDao() {
      return DatabaseModule_ProvideMealIngredientDaoFactory.provideMealIngredientDao(provideAppDatabaseProvider.get());
    }

    private MealDao mealDao() {
      return DatabaseModule_ProvideMealDaoFactory.provideMealDao(provideAppDatabaseProvider.get());
    }

    private GroceryItemDao groceryItemDao() {
      return DatabaseModule_ProvideGroceryItemDaoFactory.provideGroceryItemDao(provideAppDatabaseProvider.get());
    }

    @SuppressWarnings("unchecked")
    private void initialize(final ApplicationContextModule applicationContextModuleParam) {
      this.bundledRecipeLoaderProvider = DoubleCheck.provider(new SwitchingProvider<BundledRecipeLoader>(singletonCImpl, 1));
      this.provideImageLoaderProvider = DoubleCheck.provider(new SwitchingProvider<ImageLoader>(singletonCImpl, 2));
      this.imagePreloaderProvider = DoubleCheck.provider(new SwitchingProvider<ImagePreloader>(singletonCImpl, 0));
      this.provideAppDatabaseProvider = DoubleCheck.provider(new SwitchingProvider<AppDatabase>(singletonCImpl, 4));
      this.substituteInitializerProvider = DoubleCheck.provider(new SwitchingProvider<SubstituteInitializer>(singletonCImpl, 3));
      this.ingredientTranslatorProvider = DoubleCheck.provider(new SwitchingProvider<IngredientTranslator>(singletonCImpl, 6));
      this.recipeTranslatorProvider = DoubleCheck.provider(new SwitchingProvider<RecipeTranslator>(singletonCImpl, 7));
      this.categoryTranslatorProvider = DoubleCheck.provider(new SwitchingProvider<CategoryTranslator>(singletonCImpl, 8));
      this.translationSystemProvider = DoubleCheck.provider(new SwitchingProvider<TranslationSystem>(singletonCImpl, 5));
      this.localeManagerProvider = DoubleCheck.provider(new SwitchingProvider<LocaleManager>(singletonCImpl, 9));
      this.ingredientMatcherProvider = DoubleCheck.provider(new SwitchingProvider<IngredientMatcher>(singletonCImpl, 11));
      this.preloadCuisineAllergensUseCaseProvider = DoubleCheck.provider(new SwitchingProvider<PreloadCuisineAllergensUseCase>(singletonCImpl, 10));
      this.allergenRepositoryImplProvider = new SwitchingProvider<>(singletonCImpl, 12);
      this.bindAllergenRepositoryProvider = DoubleCheck.provider((Provider) allergenRepositoryImplProvider);
      this.ingredientRepositoryImplProvider = new SwitchingProvider<>(singletonCImpl, 14);
      this.bindIngredientRepositoryProvider = DoubleCheck.provider((Provider) ingredientRepositoryImplProvider);
      this.inventoryRepositoryImplProvider = new SwitchingProvider<>(singletonCImpl, 13);
      this.bindInventoryRepositoryProvider = DoubleCheck.provider((Provider) inventoryRepositoryImplProvider);
      this.groceryRepositoryImplProvider = new SwitchingProvider<>(singletonCImpl, 15);
      this.bindGroceryRepositoryProvider = DoubleCheck.provider((Provider) groceryRepositoryImplProvider);
      this.onboardingPreferencesProvider = DoubleCheck.provider(new SwitchingProvider<OnboardingPreferences>(singletonCImpl, 16));
      this.mealRepositoryImplProvider = new SwitchingProvider<>(singletonCImpl, 17);
      this.bindMealRepositoryProvider = DoubleCheck.provider((Provider) mealRepositoryImplProvider);
      this.imageStorageProvider = DoubleCheck.provider(new SwitchingProvider<ImageStorage>(singletonCImpl, 18));
      this.mealPlanRepositoryImplProvider = new SwitchingProvider<>(singletonCImpl, 19);
      this.bindMealPlanRepositoryProvider = DoubleCheck.provider((Provider) mealPlanRepositoryImplProvider);
      this.favoriteRecipesPreferencesProvider = DoubleCheck.provider(new SwitchingProvider<FavoriteRecipesPreferences>(singletonCImpl, 20));
      this.provideJsonProvider = DoubleCheck.provider(new SwitchingProvider<Json>(singletonCImpl, 23));
      this.provideHttpClientProvider = DoubleCheck.provider(new SwitchingProvider<HttpClient>(singletonCImpl, 22));
      this.openAiServiceProvider = DoubleCheck.provider(new SwitchingProvider<OpenAiService>(singletonCImpl, 21));
      this.provideVoiceRecognitionManagerProvider = DoubleCheck.provider(new SwitchingProvider<VoiceRecognitionManager>(singletonCImpl, 24));
    }

    @Override
    public void injectMealPlannerApp(MealPlannerApp mealPlannerApp) {
      injectMealPlannerApp2(mealPlannerApp);
    }

    @Override
    public Set<Boolean> getDisableFragmentGetContextFix() {
      return Collections.<Boolean>emptySet();
    }

    @Override
    public ActivityRetainedComponentBuilder retainedComponentBuilder() {
      return new ActivityRetainedCBuilder(singletonCImpl);
    }

    @Override
    public ServiceComponentBuilder serviceComponentBuilder() {
      return new ServiceCBuilder(singletonCImpl);
    }

    private MealPlannerApp injectMealPlannerApp2(MealPlannerApp instance) {
      MealPlannerApp_MembersInjector.injectImagePreloader(instance, imagePreloaderProvider.get());
      MealPlannerApp_MembersInjector.injectSubstituteInitializer(instance, substituteInitializerProvider.get());
      MealPlannerApp_MembersInjector.injectTranslationSystem(instance, translationSystemProvider.get());
      MealPlannerApp_MembersInjector.injectLocaleManager(instance, localeManagerProvider.get());
      return instance;
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.familymealplanner.data.local.ImagePreloader 
          return (T) new ImagePreloader(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), singletonCImpl.bundledRecipeLoaderProvider.get(), singletonCImpl.provideImageLoaderProvider.get());

          case 1: // com.familymealplanner.data.local.BundledRecipeLoader 
          return (T) new BundledRecipeLoader(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 2: // coil.ImageLoader 
          return (T) ImageModule_ProvideImageLoaderFactory.provideImageLoader(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 3: // com.familymealplanner.data.local.SubstituteInitializer 
          return (T) new SubstituteInitializer(singletonCImpl.ingredientDao(), singletonCImpl.ingredientSubstituteDao());

          case 4: // com.familymealplanner.data.local.AppDatabase 
          return (T) DatabaseModule_ProvideAppDatabaseFactory.provideAppDatabase(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 5: // com.familymealplanner.data.local.TranslationSystem 
          return (T) new TranslationSystem(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), singletonCImpl.ingredientTranslatorProvider.get(), singletonCImpl.recipeTranslatorProvider.get(), singletonCImpl.categoryTranslatorProvider.get());

          case 6: // com.familymealplanner.data.local.IngredientTranslator 
          return (T) new IngredientTranslator(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 7: // com.familymealplanner.data.local.RecipeTranslator 
          return (T) new RecipeTranslator(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 8: // com.familymealplanner.data.local.CategoryTranslator 
          return (T) new CategoryTranslator(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 9: // com.familymealplanner.data.preferences.LocaleManager 
          return (T) new LocaleManager(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 10: // com.familymealplanner.domain.usecase.PreloadCuisineAllergensUseCase 
          return (T) new PreloadCuisineAllergensUseCase(singletonCImpl.bundledRecipeLoaderProvider.get(), singletonCImpl.translationSystemProvider.get(), singletonCImpl.ingredientMatcherProvider.get());

          case 11: // com.familymealplanner.domain.util.IngredientMatcher 
          return (T) new IngredientMatcher(singletonCImpl.translationSystemProvider.get());

          case 12: // com.familymealplanner.data.repository.AllergenRepositoryImpl 
          return (T) new AllergenRepositoryImpl(singletonCImpl.allergenDao());

          case 13: // com.familymealplanner.data.repository.InventoryRepositoryImpl 
          return (T) new InventoryRepositoryImpl(singletonCImpl.inventoryTransactionDao(), singletonCImpl.ingredientDao(), singletonCImpl.mealPlanDao(), singletonCImpl.bindIngredientRepositoryProvider.get(), singletonCImpl.mealDao());

          case 14: // com.familymealplanner.data.repository.IngredientRepositoryImpl 
          return (T) new IngredientRepositoryImpl(singletonCImpl.ingredientDao(), singletonCImpl.allergenDao(), singletonCImpl.ingredientAllergenDao(), singletonCImpl.ingredientSubstituteDao(), singletonCImpl.mealIngredientDao(), singletonCImpl.localeManagerProvider.get());

          case 15: // com.familymealplanner.data.repository.GroceryRepositoryImpl 
          return (T) new GroceryRepositoryImpl(singletonCImpl.groceryItemDao());

          case 16: // com.familymealplanner.data.preferences.OnboardingPreferences 
          return (T) new OnboardingPreferences(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 17: // com.familymealplanner.data.repository.MealRepositoryImpl 
          return (T) new MealRepositoryImpl(singletonCImpl.mealDao(), singletonCImpl.mealIngredientDao(), singletonCImpl.ingredientDao(), singletonCImpl.ingredientAllergenDao(), singletonCImpl.allergenDao(), singletonCImpl.ingredientSubstituteDao(), singletonCImpl.localeManagerProvider.get(), singletonCImpl.translationSystemProvider.get(), singletonCImpl.ingredientMatcherProvider.get());

          case 18: // com.familymealplanner.data.local.ImageStorage 
          return (T) new ImageStorage(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 19: // com.familymealplanner.data.repository.MealPlanRepositoryImpl 
          return (T) new MealPlanRepositoryImpl(singletonCImpl.mealPlanDao(), singletonCImpl.bindMealRepositoryProvider.get());

          case 20: // com.familymealplanner.data.preferences.FavoriteRecipesPreferences 
          return (T) new FavoriteRecipesPreferences(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 21: // com.familymealplanner.data.remote.OpenAiService 
          return (T) new OpenAiService(singletonCImpl.provideHttpClientProvider.get(), singletonCImpl.onboardingPreferencesProvider.get(), singletonCImpl.localeManagerProvider.get(), singletonCImpl.provideJsonProvider.get());

          case 22: // io.ktor.client.HttpClient 
          return (T) NetworkModule_ProvideHttpClientFactory.provideHttpClient(singletonCImpl.provideJsonProvider.get());

          case 23: // kotlinx.serialization.json.Json 
          return (T) NetworkModule_ProvideJsonFactory.provideJson();

          case 24: // com.familymealplanner.domain.util.VoiceRecognitionManager 
          return (T) AppModule_ProvideVoiceRecognitionManagerFactory.provideVoiceRecognitionManager(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), singletonCImpl.localeManagerProvider.get());

          default: throw new AssertionError(id);
        }
      }
    }
  }
}
