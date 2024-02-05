package com.example.a01_compose_study.data.vr

import android.media.AudioFormat
import android.media.AudioManager
import com.example.a01_compose_study.data.repository.JobManager
import com.example.a01_compose_study.domain.util.CustomLogger
import kotlinx.coroutines.Job
import java.nio.ByteBuffer
import javax.inject.Inject
import javax.inject.Singleton

//public final static int USAGE_VR_TTS = 32;
@Singleton
class MediaOut @Inject constructor(
    val audioManager: AudioManager,
//    @AudioTrackVR private val audioTrack: AudioTrack,
//    @AudioTrackNavi private val audioTrackNavi: AudioTrack,
) : JobManager() {

    val TAG: String? = this.javaClass.simpleName


    @Volatile
    var stop = false

    @Volatile
    var isAudioTrackBusy = false

    //    val pcmTrackBuffer = AudioTrackBuffer()
//    var bufferDeque = ConcurrentLinkedDeque<AudioData>()
    var job: Job? = null
    var isRecording = false
    var startTime: Long = 0

//    var resampler = Resampler(16000, 22050)
//    var downSampler = Resampler(22050, 16000)

    var ttsToInputListener: TTSInterface? = null

    private var m_sampleRate = 22050
    private var m_channelCount = AudioFormat.CHANNEL_OUT_MONO
    private var m_streamFormat = AudioFormat.ENCODING_PCM_16BIT
//    private var m_audioType: MWAudioType

    init {
        CustomLogger.i("$TAG Constructor Hash[${this.hashCode()}]")
//        m_audioType = MediaUtil.nativeAudioType(getAudioType())
    }

    external fun setMediaOutListener(listener: Any): Int

//    @JvmOverloads
//    override fun init(): Boolean {
//        return true
//    }
//
//    override fun deinit(): Boolean {
//        stop = true
//        resampler.release()
//        audioTrack.release()
//        audioTrackNavi.release()
//        return true
//    }
//
//    override fun startPlay(channelCnt: Int, streamFormat: Int, sampleRateParam: Int): Boolean {
////        m_audioType = MediaUtil.nativeAudioType(getAudioType())
////        m_sampleRate = sampleRateParam
////        m_streamFormat = MediaUtil.nativeStreamFormatToAudioTrack(streamFormat)
////        m_channelCount = MediaUtil.nativeChannelToAudioTrackChannel(channelCnt)
//        CustomLogger.i("StartPlay m_audioType[$m_audioType], m_channelCount[$m_channelCount], m_streamFormat[$m_streamFormat], m_sampleRate[$m_sampleRate]")
//
//        isAudioTrackBusy = true
//        if (isRecording) {
//            audioTrack.playbackRate = 22050
//            if (audioTrack.playbackRate != m_sampleRate) {
//                resampler.initResample(m_sampleRate, audioTrack.playbackRate)
//            }
//        } else {
//            audioTrack.playbackRate = m_sampleRate
//        }
//
//        if (audioTrack.playState != AudioTrack.PLAYSTATE_PLAYING) {
//            audioTrack.play()
//        }
//
//
//        return true
//    }

//    override fun stopPlay(): Boolean {
//        CustomLogger.i("stopPlay")
//        ttsToInputListener?.stopPlay()
//        isAudioTrackBusy = false
//        audioTrackStop()
//        audioTrackNavi.stop()
//        audioTrackNavi.flush()
//        return true
//    }

//    fun audioTrackStop() {
//        CustomLogger.e("audioTrackStop playJob:${playJob?.isActive}")
//        synchronized(pcmTrackBuffer) {
//            if (playJob?.isActive == false) {
//                audioTrack.stop()
//                audioTrack.flush()
//            }
//        }
//    }

    fun setTTSInput(listener: TTSInterface?) {
        ttsToInputListener = listener
    }

//    override fun write(buffer: ByteArray, bufferSize: Int): Boolean {
//        CustomLogger.i(
//            "write bufferSize $bufferSize, audioType $m_audioType, audioMs ${
////                MediaUtil.calculateAudioLengthInMs(
////                    bufferSize, m_sampleRate, m_channelCount, m_streamFormat
////                )
//            }"
//        )
//        ttsToInputListener?.let {
//            addSyncJob {
//                val resampleData = downSampler.resampleData(buffer)
//                resampleData?.let { data ->
//                    it.write(data, data.size)
//                }
//            }
//        }
//        write(buffer, bufferSize, false)
//
//        return true
//    }

    fun write(buffer: ByteArray, bufferSize: Int, isPCM: Boolean): Boolean {
        if (isRecording) {
            if (m_sampleRate != 22050) {
//                val resampleData = resampler.resampleData(buffer)
//                resampleData?.let {
//                    bufferWrite(resampleData, resampleData.size, isPCM)
//                }

            } else {
                bufferWrite(buffer, bufferSize, isPCM)
            }
        } else {
            bufferWrite(buffer, bufferSize, isPCM)
        }
        return true
    }

    fun bufferWrite(buffer: ByteArray, bufferSize: Int, isPCM: Boolean = false) {
//        synchronized(pcmTrackBuffer) {
//            if (isPCM) {
//                resampler.resampleData(buffer)?.let {
//                    pcmTrackBuffer.addData(it)
//                }
//            } else {
//                val start = System.currentTimeMillis()
//                val pcmSize = pcmTrackBuffer.readableSize()
//                if (pcmSize > 0) {

//                    if (pcmSize > buffer.size) {
//                        val pcmData = checkPcmEOF(pcmTrackBuffer.drainBuffer(buffer.size))
//                        val mixData = PcmUtil.mixPCMDataByte(buffer, pcmData)
//                        trackWrite(mixData)
//                    } else {
//                        val pcmData = checkPcmEOF(pcmTrackBuffer.drainBuffer(pcmSize))
//                        val tempBuffer = ByteBuffer.allocate(buffer.size)
//                        tempBuffer.put(pcmData)
//                        val mixData = PcmUtil.mixPCMDataByte(buffer, tempBuffer.array())
//                        trackWrite(mixData)
//                    }

//                } else {
//                    trackWrite(buffer)
//                }
//                CustomLogger.i("Audio Track Write : ${System.currentTimeMillis() - start} ")
//            }
//        }

//        if (isRecording) {
//            bufferDeque.addLast(AudioData(buffer, (System.nanoTime() / 1000) - startTime))
//        }
    }

//    fun trackWrite(buffer: ByteArray) {
//        if (audioTrack.playState != AudioTrack.PLAYSTATE_PLAYING) {
//            audioTrack.play()
//        }
//        audioTrack.write(buffer, 0, buffer.size)
//    }
//
//    fun setRecord(boolean: Boolean) {
//        startTime = (System.nanoTime() / 1000)
//        isRecording = boolean
//        bufferDeque.clear()
//    }

//    fun playPCM(pcmFile: File) {
//        job?.cancel()
//        job = CoroutineScope(Dispatchers.IO).launch {
//            try {
////                startPCM()
//                val inputStream: InputStream = FileInputStream(pcmFile)
//                val buffer = ByteArray(1024)
//                var read = 0
//                inputStream.use {
//                    while (inputStream.read(buffer).also { read = it } != -1) {
//                        if (isRecording) {
////                            val resampleData = resampler.resampleData(buffer)
////                            resampleData?.let {
////                                write(resampleData, resampleData.size, true)
//                            }
//                        } else {
//                            write(buffer, read, true)
//                        }
//                        yield()
//                    }
//                }
//                synchronized(pcmTrackBuffer) {
//                    pcmTrackBuffer.addData(byteArrayOf(0xff.toByte(), 0xff.toByte()))
//                }
//            } catch (e: Exception) {
//                CustomLogger.i("Error playing PCM ${e.message}")
//            }
//        }.also {
//            it.invokeOnCompletion { }
//        }
//    }

//    fun release() {
//        try {
//            audioTrack.stop()
//            audioTrack.release()
//            audioTrackNavi.stop()
//            audioTrackNavi.release()
//        } catch (e: Exception) {
//            e.message?.let { CustomLogger.e(it) }
//        }
//
//        try {
////            playJob?.cancel()
////            playJob = null
//        } catch (e: Exception) {
//            e.message?.let { CustomLogger.e(it) }
//        }
//
//        resampler.release()
//        downSampler.release()
//    }

    var playJob: Job? = null
//    fun startPCM() {
//        pcmTrackBuffer.clear()
//        playJob?.cancel()
//        playJob = CoroutineScope(Dispatchers.IO).launch {
//
//            while (isActive) {
//                if (!isAudioTrackBusy) {
//                    synchronized(pcmTrackBuffer) {
//                        val pcmSize = pcmTrackBuffer.readableSize()
//                        if (pcmSize > 0) {
//                            val pcmData =
//                                if (pcmSize > 2048) {
//                                    checkPcmEOF(pcmTrackBuffer.drainBuffer(2048))
//                                } else {
//                                    checkPcmEOF(pcmTrackBuffer.drainBuffer(pcmSize))
//                                }
//                            val start = System.currentTimeMillis()
//                            trackWrite(pcmData)
//                            CustomLogger.i("Pcm Track Write buffer:${pcmData.size} during ${System.currentTimeMillis() - start} ")
//                        }
//                    }
//                    yield()
//                }
//            }
//            CustomLogger.e(" playJob while Finish ")
//        }
//        playJob?.invokeOnCompletion {
//            CustomLogger.e(" playJob finished : ${it?.message}")
//            audioTrackStop()
//        }
//
//    }

    fun checkPcmEOF(data: ByteArray): ByteArray {
        val data1 = data[data.size - 2]
        val data2 = data[data.size - 1]
        //val shortValue: Short = ((data1.toInt() shl 8) or (data2.toInt() and 0xFF)).toShort()
        if (data1 == 0xff.toByte() && data2 == 0xff.toByte()) {
            val newData = ByteArray(data.size - 2)
            ByteBuffer.wrap(data).get(newData)
            CustomLogger.e("Found PCM EOF ---")
            playJob?.cancel()
            return newData
        } else {
            return data
        }
    }

    external fun getAudioType(): Int

}
