package com.familymealplanner.di;

import com.familymealplanner.domain.util.PermissionChecker;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
public final class AppModule_ProvidePermissionCheckerFactory implements Factory<PermissionChecker> {
  @Override
  public PermissionChecker get() {
    return providePermissionChecker();
  }

  public static AppModule_ProvidePermissionCheckerFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static PermissionChecker providePermissionChecker() {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.providePermissionChecker());
  }

  private static final class InstanceHolder {
    private static final AppModule_ProvidePermissionCheckerFactory INSTANCE = new AppModule_ProvidePermissionCheckerFactory();
  }
}
