package com.example.a01_compose_study.data.vr

import android.util.Log
import com.example.a01_compose_study.data.HVRGuidanceType
import com.example.a01_compose_study.domain.util.CustomLogger
import java.util.ArrayDeque
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VRMWController @Inject constructor(

) {

    val TAG: String? = this.javaClass.simpleName
    external fun getMessage(): String
    external fun speak(
        promptId: Array<String>,
        promptArgs: Array<String>,
        promptString: String,
        filePath: String,
        requestId: String
    )

    external fun playEarcon(requestId: String)
    external fun playFile(filePath: String, requestId: String)
    external fun playStop()

    //    external fun initSystem(dirPath: String, language: Int, userIdx: String)
    external fun initSystem(dirPath: String, userIdx: String)
    external fun loadVR(language: Int)
    external fun loadTTS(language: Int)

    external fun setEventListener(listener: Any)
    external fun recogStart(
        dlgType: String,
        promptId: Array<String>,
        promptArgs: Array<String>,
        promptString: String,
        guidanceType: Int,
        testPcmPath: String,
        requestId: String,
        deviceId: String
    )

    external fun recogStop()
    external fun setLanguage(dirPath: String, language: Int)
    external fun isVRLanguageSupported(language: Int)
    external fun getVRSupportedLanguage()
    external fun isTTSLanguageSupported(language: Int)
    external fun getTTSSupportedLanguage()

    val m_testPcmList = ArrayDeque<String>()

    init {
        CustomLogger.i("$TAG Constructor Hash[${this.hashCode()}]")
    }

    fun callRecogStart(
        dlgType: String,
        promptId: List<String> = listOf(),
        deviceId: String = "",
        promptArgs: List<String> = listOf(),
        promptString: String = "",
        guidanceType: HVRGuidanceType = HVRGuidanceType.PROMPT,
        testPcmPath: String = "",
        requestId: String = ""
    ) {
        var pcmPath = ""
        if (!m_testPcmList.isEmpty()) {
            pcmPath = m_testPcmList.removeFirst()
        } else {
            pcmPath = testPcmPath
        }
        CustomLogger.d(
            "callRecogStart dlgType: ${dlgType}, promptId: ${promptId.joinToString()}, deviceId: $deviceId, " + "promptArgs: ${promptArgs.joinToString()}, promptString: $promptString, guidanceType: ${guidanceType.name}, testPcmPath: $pcmPath"
        )
        recogStart(
            dlgType,
            promptId.toTypedArray(),
            promptArgs.toTypedArray(),
            promptString,
            guidanceType.ordinal,
            pcmPath,
            requestId,
            deviceId
        )
    }

    fun callSpeak(
        promptId: List<String> = listOf(),
        promptArgs: List<String> = listOf(),
        promptString: String = "",
        filePath: String = "",
        requestId: String = ""
    ) {
        CustomLogger.i("speak promptString: $promptString")
        speak(promptId.toTypedArray(), promptArgs.toTypedArray(), promptString, filePath, requestId)
    }

    companion object {
        init {
            Log.d("@@ 네이티브 라이브러리 로드 시도", "vrmwservice")
//            System.loadLibrary("vrmwservice") // 네이티브 라이브러리 로드
            CustomLogger.i("Constructor companion object Hash[${this.hashCode()}]")
        }

        private var instance: VRMWController? = null

        fun getInstance(): VRMWController {
            if (instance == null) {
                instance = VRMWController()
            }
            return instance!!
        }
    }

}
