package com.example.a01_compose_study.di

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioRecord
import android.media.AudioTrack
import android.media.MediaRecorder
import android.os.Build
import androidx.core.app.ActivityCompat
import com.example.a01_compose_study.data.vr.GsonWrapper
import com.example.a01_compose_study.domain.util.CustomLogger
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

data class AudioConfig(
    val audio_track: RecordConfig, val audio_record: TrackConfig
)

data class RecordConfig(
    val sampleRate: Int, val channelConfig: String, val audioFormat: String
)

data class TrackConfig(
    val sampleRate: Int, val channelConfig: String, val audioFormat: String
)


@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AudioTrackVR

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AudioTrackNavi

@Module
@InstallIn(SingletonComponent::class)
class MediaModule {


    @AudioTrackVR
    @Provides
    @Singleton
    fun provideAudioTrack(@ApplicationContext context: Context): AudioTrack {
        CustomLogger.i("initAudioTrack start")
        lateinit var audioTrack: AudioTrack
//        val audioTrack: AudioTrack
        val config = loadConfig(context)

        val sampleRate = config.audio_track.sampleRate
        val channelConfig = getChannelOutConfig(config.audio_track.channelConfig)
        val audioFormat = getAudioFormat(config.audio_track.audioFormat)
        val bufferSize = AudioTrack.getMinBufferSize(sampleRate, channelConfig, audioFormat)

        CustomLogger.i("provideAudioTrack find abi ${Build.SUPPORTED_ABIS.joinToString()}")

        when {
            "x86_64" in Build.SUPPORTED_ABIS.toList() -> {
                CustomLogger.i("provideAudioTrack x86_64")
                audioTrack = AudioTrack(
                    AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        //.setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build(),
                    AudioFormat.Builder().setSampleRate(sampleRate).setChannelMask(channelConfig)
                        .setEncoding(audioFormat).build(),
                    bufferSize,
                    AudioTrack.MODE_STREAM,
                    AudioManager.AUDIO_SESSION_ID_GENERATE
                )
            }

            "arm64-v8a" in Build.SUPPORTED_ABIS.toList() -> {
                CustomLogger.i("provideAudioTrack arm64-v8a")
                audioTrack = AudioTrack(
                    //AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ASSISTANCE_NAVIGATION_GUIDANCE)
                    AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        //.setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build(),
                    AudioFormat.Builder().setSampleRate(sampleRate).setChannelMask(channelConfig)
                        .setEncoding(audioFormat).build(),
                    bufferSize,
                    AudioTrack.MODE_STREAM,
                    AudioManager.AUDIO_SESSION_ID_GENERATE
                )
            }
        }

        return audioTrack
    }

    @AudioTrackNavi
    @Provides
    @Singleton
    fun provideAudioTrackForNavi(@ApplicationContext context: Context): AudioTrack {
        CustomLogger.i("initAudioTrack start")
        lateinit var audioTrack: AudioTrack
        val config = loadConfig(context)

        val sampleRate = config.audio_track.sampleRate
        val channelConfig = getChannelOutConfig(config.audio_track.channelConfig)
        val audioFormat = getAudioFormat(config.audio_track.audioFormat)
        val bufferSize = AudioTrack.getMinBufferSize(sampleRate, channelConfig, audioFormat)

        CustomLogger.i("provideAudioTrack find abi ${Build.SUPPORTED_ABIS.joinToString()}")

        when {
            "x86_64" in Build.SUPPORTED_ABIS.toList() -> {
                CustomLogger.i("provideAudioTrack x86_64")
                audioTrack = AudioTrack(
                    AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        //.setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build(),
                    AudioFormat.Builder().setSampleRate(sampleRate)
                        .setChannelMask(channelConfig)
                        .setEncoding(audioFormat).build(),
                    bufferSize,
                    AudioTrack.MODE_STREAM,
                    AudioManager.AUDIO_SESSION_ID_GENERATE
                )
            }

            "arm64-v8a" in Build.SUPPORTED_ABIS.toList() -> {
                CustomLogger.i("provideAudioTrack arm64-v8a")
                audioTrack = AudioTrack(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ASSISTANCE_NAVIGATION_GUIDANCE)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .build(),
                    AudioFormat.Builder().setSampleRate(sampleRate)
                        .setChannelMask(channelConfig)
                        .setEncoding(audioFormat).build(),
                    bufferSize,
                    AudioTrack.MODE_STREAM,
                    AudioManager.AUDIO_SESSION_ID_GENERATE
                )
            }
        }

        return audioTrack
    }

    @Provides
    @Singleton
    fun provideAudioRecord(@ApplicationContext context: Context): AudioRecord {
        CustomLogger.i("initAudioRecord start")
        val config = loadConfig(context)
        val sampleRate = config.audio_record.sampleRate
        val channelConfig = getChannelInConfig(config.audio_record.channelConfig)
        val audioFormat = getAudioFormat(config.audio_record.audioFormat)
        val minBufSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)
        if (ActivityCompat.checkSelfPermission(
                context, Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            CustomLogger.i("AudioRecord PERMISSION_DENIED")
//            return null
        }

        return AudioRecord(
            MediaRecorder.AudioSource.MIC, sampleRate, channelConfig, audioFormat, minBufSize
        )
    }

    private fun loadConfig(context: Context): AudioConfig {
        CustomLogger.i("loadConfig")
        val json = context.assets.open("json/audio_data_config.json").bufferedReader()
            .use { it.readText() }
        CustomLogger.i("loadConfig json: ${json}")
        var audioConfig: AudioConfig? = null
        try {
            audioConfig = GsonWrapper.fromJson(json, AudioConfig::class.java)
        } catch (e: Exception) {
            e.message?.let { CustomLogger.e(it) }
        }
        CustomLogger.i("loadConfig audioConfig:${audioConfig}")

        return audioConfig!!
    }

    private fun getChannelOutConfig(configValue: String): Int {
        return when (configValue) {
            "MONO" -> AudioFormat.CHANNEL_OUT_MONO
            "STEREO" -> AudioFormat.CHANNEL_OUT_STEREO
            else -> throw IllegalArgumentException("Invalid channel config value")
        }
    }

    private fun getChannelInConfig(configValue: String): Int {
        return when (configValue) {
            "MONO" -> AudioFormat.CHANNEL_IN_MONO
            "STEREO" -> AudioFormat.CHANNEL_IN_STEREO
            else -> throw IllegalArgumentException("Invalid channel config value")
        }
    }

    private fun getAudioFormat(configValue: String): Int {
        return when (configValue) {
            "16BIT" -> AudioFormat.ENCODING_PCM_16BIT
            "8BIT" -> AudioFormat.ENCODING_PCM_8BIT
            else -> throw IllegalArgumentException("Invalid audio format value")
        }
    }


}