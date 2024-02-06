package com.example.a01_compose_study.di

import android.app.ActivityManager
import android.app.AlarmManager
import android.app.NotificationManager
import android.bluetooth.BluetoothManager
import android.content.Context
import android.location.LocationManager
import android.media.AudioManager
import android.net.ConnectivityManager
import android.telephony.TelephonyManager
import com.example.a01_compose_study.data.custom.MWContext
import com.example.a01_compose_study.domain.util.CustomLogger
import dagger.Component
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule2 {

    @Provides
    fun provideTelephonyManager(@ApplicationContext context: Context): TelephonyManager =
        context.applicationContext.getSystemService(Context.TELEPHONY_SERVICE)
                as TelephonyManager

//    @Provides
//    fun provideServiceViewModel(application: Application, controller: VRMWController): ServiceViewModel {
//        return ServiceViewModel(application, controller)
//    }

    @Provides
    fun provideLocationManager(@ApplicationContext context: Context): LocationManager =
        context.applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    @Provides
    fun provideAudioManager(@ApplicationContext context: Context): AudioManager {
        val manager =
            context.applicationContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        CustomLogger.i("ProvideAudioManager isMicrophoneMute : ${manager.isMicrophoneMute}, getMode : ${manager.mode}, context : ${context.hashCode()}")
        return manager
    }


    @Provides
    fun provideConnectivityManager(@ApplicationContext context: Context): ConnectivityManager =
        context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE)
                as ConnectivityManager

    @Provides
    fun provideActivityManager(@ApplicationContext context: Context): ActivityManager =
        context.applicationContext.getSystemService(Context.ACTIVITY_SERVICE)
                as ActivityManager

    @Provides
    fun provideNotificationManager(@ApplicationContext context: Context): NotificationManager =
        context.applicationContext.getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager

    @Provides
    fun provideAlarmManager(@ApplicationContext context: Context): AlarmManager =
        context.applicationContext.getSystemService(Context.ALARM_SERVICE)
                as AlarmManager

    @Provides
    fun provideBluetoothManager(@ApplicationContext context: Context): BluetoothManager =
        context.applicationContext.getSystemService(Context.BLUETOOTH_SERVICE)
                as BluetoothManager

    @ApplicationScope
    @Singleton
    @Provides
    fun providesApplicationScope(
        @DefaultDispatcher defaultDispatcher: CoroutineDispatcher,
    ): CoroutineScope = CoroutineScope(SupervisorJob() + defaultDispatcher)
}

@Component(modules = [AppModule::class])
interface AppComponent {
    fun inject(mwContext: MWContext)
}
