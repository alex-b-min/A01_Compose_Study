package com.example.a01_compose_study.di

import com.example.a01_compose_study.data.repositoryImpl.VRRepositoryImpl
import com.example.a01_compose_study.domain.repository.VRRepository
import com.example.a01_compose_study.domain.usecase.VRUsecase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideHelpRepository(): VRRepository {
        return VRRepositoryImpl()
    }

    @Provides
    @Singleton
   fun provideHelpUseCase(repository: VRRepository) : VRUsecase {
       return VRUsecase(
           repository
       )
   }
}
