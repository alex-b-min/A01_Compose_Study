package com.example.a01_compose_study.di

import android.app.Application
import com.example.a01_compose_study.data.repositoryImpl.HelpRepositoryImpl
import com.example.a01_compose_study.domain.repository.HelpRepository
import com.example.a01_compose_study.domain.usecase.HelpUsecase
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
    fun provideHelpRepository(): HelpRepository {
        return HelpRepositoryImpl()
    }

    @Provides
    @Singleton
   fun provideHelpUseCase(repository: HelpRepository) : HelpUsecase {
       return HelpUsecase(
           repository
       )
   }
}
