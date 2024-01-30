package com.example.a01_compose_study.di;

import com.example.a01_compose_study.domain.repository.vr.VRRepository;
import com.example.a01_compose_study.domain.usecase.VRUsecase;
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
public final class AppModule_ProvideHelpUseCaseFactory implements Factory<VRUsecase> {
  private final Provider<VRRepository> repositoryProvider;

  public AppModule_ProvideHelpUseCaseFactory(Provider<VRRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public VRUsecase get() {
    return provideHelpUseCase(repositoryProvider.get());
  }

  public static AppModule_ProvideHelpUseCaseFactory create(
      Provider<VRRepository> repositoryProvider) {
    return new AppModule_ProvideHelpUseCaseFactory(repositoryProvider);
  }

  public static VRUsecase provideHelpUseCase(VRRepository repository) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideVRUseCase(repository));
  }
}
