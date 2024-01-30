package com.example.a01_compose_study.di;

import com.example.a01_compose_study.domain.repository.HelpRepository;
import com.example.a01_compose_study.domain.usecase.HelpUseCase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class AppModule_ProvideHelpUseCaseFactory implements Factory<HelpUseCase> {
  private final Provider<HelpRepository> repositoryProvider;

  public AppModule_ProvideHelpUseCaseFactory(Provider<HelpRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public HelpUseCase get() {
    return provideHelpUseCase(repositoryProvider.get());
  }

  public static AppModule_ProvideHelpUseCaseFactory create(
      Provider<HelpRepository> repositoryProvider) {
    return new AppModule_ProvideHelpUseCaseFactory(repositoryProvider);
  }

  public static HelpUseCase provideHelpUseCase(HelpRepository repository) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideHelpUseCase(repository));
  }
}
