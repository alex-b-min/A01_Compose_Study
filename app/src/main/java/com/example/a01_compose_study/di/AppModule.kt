package com.example.a01_compose_study.di

import android.content.Context
import com.example.a01_compose_study.data.custom.ContactsDummyProvider
import com.example.a01_compose_study.data.custom.ContactsManager
import com.example.a01_compose_study.data.custom.ContactsRepository
import com.example.a01_compose_study.data.custom.DataProducer
import com.example.a01_compose_study.data.custom.HelpManager
import com.example.a01_compose_study.data.custom.call.CallManager
import com.example.a01_compose_study.data.repository.help.HelpRepositoryImpl
import com.example.a01_compose_study.domain.repository.domain.HelpRepository
import com.example.a01_compose_study.domain.usecase.HelpUseCase
import com.example.a01_compose_study.presentation.screen.ptt.VrmwManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
//    @Provides
//    @Singleton
//    fun provideVRRepository(): VRRepository {
//        return MWContext()
//    }
//
//    @Provides
//    @Singleton
//    fun provideVRUseCase(repository: VRRepository): VRUseCase {
//        return VRUseCase(
//            repository
//        )
//    }

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

    @Provides
    @Singleton
    fun provideDataProducer(@ApplicationContext context: Context, vrmwManager: VrmwManager): DataProducer {
        return DataProducer(
            helpManager = HelpManager(),
            callManager = CallManager(
                context = context,
                contactsManager = ContactsManager(
                    context = context,
                    vrmwManager = vrmwManager,
                    coroutineScope = CoroutineScope(Dispatchers.IO), // 이 부분을 적절히 초기화해야 합니다.
                    ioDispatcher = Dispatchers.IO,
                    contactsRepository = ContactsRepository(
                        dummySource = ContactsDummyProvider(context),
                        ioDispatcher = Dispatchers.IO
                    )
                )
            )
        )
    }
}
