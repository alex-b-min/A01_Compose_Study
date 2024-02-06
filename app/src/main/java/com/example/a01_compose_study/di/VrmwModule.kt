package com.example.a01_compose_study.di

import android.content.Context
import com.example.a01_compose_study.data.vr.MediaIn
import com.example.a01_compose_study.data.vr.MediaOut
import com.example.a01_compose_study.data.vr.VRMWController
import com.example.a01_compose_study.domain.util.G2PController
import com.example.a01_compose_study.presentation.screen.ptt.VrmwManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class VrmwModule {

    @Singleton
    @Provides
    fun provideVrmwManager(
        @ApplicationContext context: Context,
        controller: VRMWController,
        g2pController: G2PController,
        mediaIn: MediaIn,
        mediaOut: MediaOut,
    ): VrmwManager {
        return VrmwManager(context, controller, g2pController, mediaIn, mediaOut)
    }
}