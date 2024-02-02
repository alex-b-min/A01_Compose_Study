package com.example.a01_compose_study.data.vr

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioRecord
import android.media.MediaRecorder
import android.media.audiofx.AcousticEchoCanceler
import androidx.core.app.ActivityCompat
import com.example.a01_compose_study.domain.util.CustomLogger
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.util.concurrent.ConcurrentLinkedDeque
import javax.inject.Inject
import javax.inject.Singleton


//public final static int USAGE_VR_TTS = 32;
@Singleton
class MediaIn @Inject constructor(
    @ApplicationContext private val context: Context,
    private val audioManager: AudioManager,
) : MediaInInterface {

    var isStoreRecord = false

    var audioRecord: AudioRecord? = null
    val TAG: String? = this.javaClass.simpleName

    @Volatile
    var stop = false

    private var aec: AcousticEchoCanceler? = null

//    var resampler: Resampler? = null
//    val ttsTrackBuffer = AudioTrackBuffer()

    private var m_sampleRate = 16000
    private var m_channelCount = AudioFormat.CHANNEL_OUT_MONO
    private var m_streamFormat = AudioFormat.ENCODING_PCM_16BIT
//    private var m_audioType: MWAudioType
//
//    init {
//        CustomLogger.i("$TAG Constructor Hash[${this.hashCode()}]")
//        m_audioType = MediaUtil.nativeAudioType(getAudioType())
//    }

    external fun setMediaInListener(listener: Any): Int

    @JvmOverloads
    override fun init(): Boolean {

        return true
    }

    override fun deinit(): Boolean {
        CustomLogger.e("audioRecord deinit")
        stop = true
        aec?.release()
        audioRecord?.release()
        return true
    }

//    override fun recordOpen(channelCnt: Int, streamFormat: Int, sampleRate: Int): Boolean {
//        //audioRecord?.activeRecordingConfiguration
//        m_audioType = MediaUtil.nativeAudioType(getAudioType())
//        m_sampleRate = sampleRate
//        m_streamFormat = MediaUtil.nativeStreamFormatToAudioTrack(streamFormat)
//        m_channelCount = MediaUtil.nativeChannelToAudioTrackChannel(channelCnt)
//        CustomLogger.i("recordOpen start m_audioType[$m_audioType], m_channelCount[$m_channelCount], m_streamFormat[$m_streamFormat], m_sampleRate[$m_sampleRate]")
//
//        CustomLogger.i("recordOpen isMicrophoneMute : ${audioManager.isMicrophoneMute}, getMode : ${audioManager.mode}, context : ${context.hashCode()}")
//        if (audioRecord == null) {
//            audioRecord = provideAudioRecord(context)
//        }
//        CustomLogger.i("Record Data AudioSource : ${audioRecord?.audioSource}, SampleRate : ${audioRecord?.sampleRate}, ChannelCount : ${audioRecord?.channelCount}, State : ${audioRecord?.state}, Recording State: ${audioRecord?.recordingState}")
//
//        CustomLogger.i("audioRecord recordOpen session:${audioRecord?.audioSessionId}")
//        if (audioRecord?.state != AudioRecord.STATE_INITIALIZED) {
//            // AudioRecord가 아직 초기화되지 않았습니다.
//            CustomLogger.i("audioRecord not initialized")
////            initAudioRecord()
//        }
//
//        audioRecord?.let {
//            if (AcousticEchoCanceler.isAvailable()) {
//                aec = AcousticEchoCanceler.create(it.audioSessionId)
//                CustomLogger.i("audioRecord aec Supported")
//                if (aec != null) {
//                    aec?.enabled = true
//                }
//            }
//        }
//        audioRecord?.startRecording()
//        return true
//    }

    fun releaseRecord() {
        try {
            audioRecord?.stop()
            audioRecord?.release()
            audioRecord = null
        } catch (e: Exception) {
            e.message?.let { CustomLogger.e(it) }
        }

    }

//    override fun recordClose(): Boolean {
//        CustomLogger.i("audioRecord recordClose")
//        pcmBuffer = null
//        if (audioRecord?.state != AudioRecord.RECORDSTATE_RECORDING) {
//            audioRecord?.stop()
//        }
//        return true
//    }

    private fun byteToString(arr: ByteArray) {
        val hexStringBuilder = StringBuilder()
        for (item in arr) {
            val hex = String.format("%02X", item) // 바이트를 16진수 문자열로 변환
            hexStringBuilder.append(hex)
        }

        val hexString = hexStringBuilder.toString()
        CustomLogger.i("hexString: $hexString")
    }

//    override fun read(buffer: ByteArray, bufferSize: Int): ByteArray {
//        // 녹음 시작
//
//        if (audioRecord == null) {
//            return buffer.copyOfRange(0, bufferSize)
//        } else {
//
//            val readSize = audioRecord!!.read(buffer, 0, bufferSize)
//            //CustomLogger.i("audioRecord read buffer:${bufferSize} readSize: $readSize sampleRate:${audioRecord?.sampleRate} chn:${audioRecord?.channelCount}")
//            var returnBuffer = buffer.copyOfRange(0, readSize)
//            CustomLogger.i(
//                "readBuffer readFrame: $readSize, bufferSize: $bufferSize, Time :${
//                    MediaUtil.calculateAudioLengthInMs(
//                        readSize,
//                        m_sampleRate,
//                        m_channelCount,
//                        m_streamFormat
//                    )
//                }ms"
//            )
//            val elapseTime = (System.nanoTime() / 1000) - startTime
//            pcmBuffer?.let {
//                var bufferRead = bufferSize
//                var isLast = false
//                if (bufferRead > it.remaining()) {
//                    bufferRead = it.remaining()
//                    isLast = true
//                }
//                val read = ByteArray(bufferRead)
//                CustomLogger.i("audioRecord bufferRead :${bufferRead} remain:${it.remaining()} read:${read.size} isLast:${isLast}")
//                try {
//                    it.get(read)
//                    if (isLast) {
//                        pcmBuffer = null
//                        val fill = ByteArray(bufferSize)
//                        val fillBuffer = ByteBuffer.wrap(fill)
//                        fillBuffer.put(read)
//                        fillBuffer.rewind()
//                        // 원래 음성데이터 믹스
//                        fillBuffer.get(fill)
//                        returnBuffer = fill
//                    } else {
//                        returnBuffer = read
//                    }
//
//                } catch (e: Exception) {
//                    e.message?.let { CustomLogger.e(it) }
//                }
//            }
//            if (isStoreRecord) {
//                arrayDeque.add(returnBuffer.copyOf())
//            }
//            if (isRecording) {
//                val resampleData = resampler?.resampleData(buffer)
//                resampleData?.let {
//                    bufferDeque.addLast(AudioData(resampleData, elapseTime))
//                    CustomLogger.d("AudioRecorder", "Elapsed time ( ms): ${elapseTime / 1000}")
//                }
//            }
//
//            synchronized(ttsTrackBuffer) {
//                ttsTrackBuffer.getFirst()?.let {
//                    CustomLogger.e("ttsTrackBuffer dataSize:${it.size} reqBufferSize:${bufferSize}")
//                    if (it.size > bufferSize) {
//                        val returnByte = it.copyOfRange(0, bufferSize)
//                        ttsTrackBuffer.addFirst(it.copyOfRange(bufferSize, it.size))
//                        return returnByte
//                    } else {
//                        val nextData = ttsTrackBuffer.getFirst()
//                        if (nextData != null) {
//                            val tempBuffer = ByteBuffer.allocate(it.size + nextData.size)
//                            tempBuffer.put(it)
//                            tempBuffer.put(nextData)
//                            ttsTrackBuffer.addFirst(tempBuffer.array())
//                        } else {
//                            ttsTrackBuffer.addFirst(it)
//                        }
//                    }
//                }
//            }
//            return returnBuffer
//        }
//
//    }

//    fun clearTTSBuffer() {
//        synchronized(ttsTrackBuffer) {
//            ttsTrackBuffer.clear()
//        }
//    }
//
//    fun addTTSBuffer(byteArray: ByteArray) {
//        synchronized(ttsTrackBuffer) {
//            ttsTrackBuffer.addLast(byteArray)
//        }
//    }

    var startTime: Long = 0
    var frameTime: Long = 0
//    fun setRecord(boolean: Boolean) {
//        startTime = (System.nanoTime() / 1000)
//        resampler?.release()
//        if (boolean) {
//            resampler = Resampler(16000, 22050)
//        } else {
//            resampler = null
//        }
//        CustomLogger.d("AudioRecorder startTimeStamp :${startTime / 1000000}")
//        frameTime = 0
//        isRecording = boolean
//        bufferDeque.clear()
//    }
//
//
//    fun startStore() {
//        arrayDeque.clear()
//        isStoreRecord = true
//    }

    fun finishStore(phrase: String, cancel: Boolean) {
//        isStoreRecord = false
        if (!cancel) {
//            val context = BaseApplication.appContext
//            val filename =
//                "${context.filesDir}/developPcm/${Util.getCurrentTimeAsString()}_${
//                    phrase.replace(
//                        " ",
//                        "_"
//                    )
//                }"

//            val file = File(filename)
//            try {
//
//                if (!file.parentFile.exists()) {
//                    file.parentFile.mkdirs()
//                }
//                if (!file.exists()) {
//
//                    file.createNewFile()
//                    CustomLogger.d("file created : ${file.absolutePath}")
//                    FileOutputStream(file).use { fileOutputStream ->
//                        while (arrayDeque.firstOrNull() != null) {
//                            fileOutputStream.write(arrayDeque.removeFirst())
//                        }
//                    }
//                }
//            } catch (e: IOException) {
//                CustomLogger.e("file create fail: ${filename}")
//                e.message?.let { CustomLogger.e(it) }
//            }
//        } else {
//            arrayDeque.clear()
//        }
    }

//    fun clearPCM() {
//        pcmBuffer?.let {
//            it.clear()
//            pcmBuffer = null
//        }
//    }

//    fun setPCM(pcmFile: File) {
//
//        CustomLogger.e("setPCM:${pcmFile.name}")
//        pcmBuffer?.let {
//            it.clear()
//            pcmBuffer = null
//        }
//        RandomAccessFile(pcmFile, "r").use { file ->
//            val buffer = ByteArray(file.length().toInt())
//            val bytesRead = file.read(buffer)
//            if (bytesRead == -1) {
//                CustomLogger.e("end of file or read byte failed")
//            }
//            pcmBuffer = ByteBuffer.wrap(buffer)
//        }
//        return
//    }

//    fun setPCMStream(pcmStream: InputStream) {
//        CustomLogger.e("setPCMStream")
//        pcmBuffer?.let {
//            it.clear()
//            pcmBuffer = null
//        }
//
//        val buffer = ByteArray(1024)
//        var byteBuffer = ByteBuffer.allocate(1024 * 10)
//        var bytesRead: Int
//
//        while (pcmStream.read(buffer).also { bytesRead = it } != -1) {
//            if (byteBuffer.remaining() < bytesRead) {
//                val newByteBuffer = ByteBuffer.allocate(byteBuffer.capacity() * 2)
//                byteBuffer.flip()
//                newByteBuffer.put(byteBuffer)
//                byteBuffer = newByteBuffer
//            }
//            byteBuffer.put(buffer, 0, bytesRead)
//        }
//        byteBuffer.flip()
////
////        pcmBuffer = byteBuffer
//    }
////
//    var pcmBuffer: ByteBuffer? = null
//    var isStoreRecord = false
//    var isRecording = false
//    private val arrayDeque = ConcurrentLinkedDeque<ByteArray>()
//    val bufferDeque = ConcurrentLinkedDeque<AudioData>()
//
//    fun reInit() {
//        audioRecord?.stop()
//        audioRecord?.release()
//        audioRecord = provideAudioRecord(context)
//    }

//    fun provideAudioRecord(@ApplicationContext context: Context): AudioRecord {
//        CustomLogger.i("initAudioRecord start")
//        val config = loadConfig(context)
//        val sampleRate = config.audio_record.sampleRate
//        val channelConfig = getChannelInConfig(config.audio_record.channelConfig)
//        val audioFormat = getAudioFormat(config.audio_record.audioFormat)
//        val minBufSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)
//
//        CustomLogger.i("initAudioRecord sampleRate:${sampleRate}")
//        CustomLogger.i("initAudioRecord channelConfig:${channelConfig}")
//        CustomLogger.i("initAudioRecord audioFormat:${audioFormat}")
//        CustomLogger.i("initAudioRecord minBufSize:${minBufSize}")
//
//        if (ActivityCompat.checkSelfPermission(
//                context, Manifest.permission.RECORD_AUDIO
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            CustomLogger.i("AudioRecord PERMISSION_DENIED")
//        }
////        val record = AudioRecord(
////            MediaRecorder.AudioSource.MIC, sampleRate, channelConfig, audioFormat, minBufSize
////        )
//
////        CustomLogger.i("Record Data AudioSource : ${record.audioSource}, SampleRate : ${record.sampleRate}, ChannelCount : ${record.channelCount}, State : ${record.state}, Recording State: ${record.recordingState}")
////        return record
//    }


//    private fun loadConfig(context: Context): AudioConfig {
//        CustomLogger.i("loadConfig")
//        val json = context.assets.open("json/audio_data_config.json").bufferedReader()
//            .use { it.readText() }
//        return GsonWrapper.fromJson(json, AudioConfig::class.java)
//    }

//    private fun getChannelOutConfig(configValue: String): Int {
//        return when (configValue) {
//            "MONO" -> AudioFormat.CHANNEL_OUT_MONO
//            "STEREO" -> AudioFormat.CHANNEL_OUT_STEREO
//            else -> throw IllegalArgumentException("Invalid channel config value")
//        }
//    }
//
//    private fun getChannelInConfig(configValue: String): Int {
//        return when (configValue) {
//            "MONO" -> AudioFormat.CHANNEL_IN_MONO
//            "STEREO" -> AudioFormat.CHANNEL_IN_STEREO
//            else -> throw IllegalArgumentException("Invalid channel config value")
//        }
//    }
//
//
//    private fun getAudioFormat(configValue: String): Int {
//        return when (configValue) {
//            "16BIT" -> AudioFormat.ENCODING_PCM_16BIT
//            "8BIT" -> AudioFormat.ENCODING_PCM_8BIT
//            else -> throw IllegalArgumentException("Invalid audio format value")
//        }
//    }

    external fun getAudioType(): Int

}}
