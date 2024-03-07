package com.example.a01_compose_study.di

import android.content.Context
import com.example.a01_compose_study.data.custom.ContactsDummyProvider
import com.example.a01_compose_study.data.custom.ContactsManager
import com.example.a01_compose_study.data.custom.ContactsRepository
import com.example.a01_compose_study.data.custom.DataProducer
import com.example.a01_compose_study.data.custom.HelpManager
import com.example.a01_compose_study.data.custom.call.BtCall
import com.example.a01_compose_study.data.custom.call.CallManager
import com.example.a01_compose_study.data.custom.ptt.PttManager
import com.example.a01_compose_study.data.custom.sendMsg.SendMsgManager
import com.example.a01_compose_study.presentation.screen.ptt.VrmwManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
annotation class IODispatcher
@Qualifier
annotation class IOCoroutineScope

@Module
@InstallIn(SingletonComponent::class)
object ManagerModule {

    @Singleton
    @Provides
    @IODispatcher
    fun provideIoDispatcher(): CoroutineDispatcher {
        return Dispatchers.IO
    }

    @Singleton
    @Provides
    @IOCoroutineScope
    fun provideCoroutineScope(@IODispatcher coroutineDispatcher: CoroutineDispatcher): CoroutineScope {
        return CoroutineScope(coroutineDispatcher)
    }

    @Provides
    @Singleton
    fun provideHelpManager(@IOCoroutineScope coroutineScope: CoroutineScope,): HelpManager {
        return HelpManager(coroutineScope)
    }

    @Provides
    @Singleton
    fun provideCallManager(
        @ApplicationContext context: Context,
        contactsManager: ContactsManager,
        @IOCoroutineScope coroutineScope: CoroutineScope,

    ): CallManager {
        return CallManager(
            context = context,
            contactsManager = contactsManager,
            coroutineScope = coroutineScope,
            btCall = BtCall(context)
        )
    }

    @Provides
    @Singleton
    fun provideContactsManager(
        @ApplicationContext context: Context,
        vrmwManager: VrmwManager,
        @IOCoroutineScope coroutineScope: CoroutineScope,
        @IODispatcher ioDispatcher: CoroutineDispatcher
    ): ContactsManager {
        val contactsRepository = ContactsRepository(
            dummySource = ContactsDummyProvider(context),
            ioDispatcher = ioDispatcher
        )
        return ContactsManager(
            context = context,
            vrmwManager = vrmwManager,
            coroutineScope = coroutineScope,
            ioDispatcher = ioDispatcher,
            contactsRepository = contactsRepository
        )
    }

    @Provides
    @Singleton
    fun provideDataProducer(
        helpManager: HelpManager,
        callManager: CallManager
    ): DataProducer {
        return DataProducer(
            helpManager = helpManager,
            callManager = callManager
        )
    }

    @Provides
    @Singleton
    fun providePttManager(
        @ApplicationContext context: Context,
        vrmwManager: VrmwManager,
        dataProducer: DataProducer
    ) : PttManager {
        return PttManager(
            context = context,
            vrmwManager = vrmwManager,
            dataProducer = dataProducer
        )
    }

    @Provides
    @Singleton
    fun provideSendMsgManager(
        contactsManager: ContactsManager,
        @IOCoroutineScope ioCoroutineScope: CoroutineScope,
    ): SendMsgManager {
        return SendMsgManager(
            contactsManager = contactsManager,
            job = ioCoroutineScope,
        )
    }
}
