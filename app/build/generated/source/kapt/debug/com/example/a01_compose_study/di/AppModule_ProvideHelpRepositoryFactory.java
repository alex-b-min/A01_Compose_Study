package com.example.a01_compose_study.di;

import com.example.a01_compose_study.domain.repository.domain.HelpRepository;
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
public final class AppModule_ProvideHelpRepositoryFactory implements Factory<HelpRepository> {
  @Override
  public HelpRepository get() {
    return provideHelpRepository();
  }

  public static AppModule_ProvideHelpRepositoryFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static HelpRepository provideHelpRepository() {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideHelpRepository());
  }

  private static final class InstanceHolder {
    private static final AppModule_ProvideHelpRepositoryFactory INSTANCE = new AppModule_ProvideHelpRepositoryFactory();
  }
}
