package com.example.a01_compose_study.di

import com.example.a01_compose_study.data.repository.help.HelpRepositoryImpl
import com.example.a01_compose_study.data.repository.vr.VRRepositoryImpl
import com.example.a01_compose_study.domain.repository.domain.HelpRepository
import com.example.a01_compose_study.domain.repository.vr.VRRepository
import com.example.a01_compose_study.domain.usecase.HelpUseCase
import com.example.a01_compose_study.domain.usecase.VRUseCase
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
    fun provideVRRepository(): VRRepository {
        return VRRepositoryImpl()
    }

    @Provides
    @Singleton
    fun provideVRUseCase(repository: VRRepository): VRUseCase {
        return VRUseCase(
            repository
        )
    }

    @Provides
    @Singleton
    fun provideHelpRepository(): HelpRepository {
        return HelpRepositoryImpl()
    }

    @Provides
    @Singleton
    fun provideHelpUseCase(repository: HelpRepository): HelpUseCase {
        return HelpUseCase(
            repository
        )
    }
}
