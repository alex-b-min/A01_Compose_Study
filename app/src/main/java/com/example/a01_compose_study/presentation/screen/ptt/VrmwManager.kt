package com.example.a01_compose_study.presentation.screen.ptt

import android.content.Context
import android.os.LocaleList
import com.example.a01_compose_study.data.HLanguageType
import com.example.a01_compose_study.data.HTextToSpeechError
import com.example.a01_compose_study.data.HTextToSpeechEvent
import com.example.a01_compose_study.data.HTextToSpeechState
import com.example.a01_compose_study.data.HVRConfigEvent
import com.example.a01_compose_study.data.HVRError
import com.example.a01_compose_study.data.HVRG2PMode
import com.example.a01_compose_study.data.HVRGuidanceType
import com.example.a01_compose_study.data.HVRPromptStatus
import com.example.a01_compose_study.data.HVRSpeechDetection
import com.example.a01_compose_study.data.HVRState
import com.example.a01_compose_study.data.VRResult
import com.example.a01_compose_study.data.custom.MWContext
import com.example.a01_compose_study.data.vr.IMwController
import com.example.a01_compose_study.data.vr.IVRMWListener
import com.example.a01_compose_study.data.vr.MediaIn
import com.example.a01_compose_study.data.vr.MediaOut
import com.example.a01_compose_study.data.vr.MwStateMachine
import com.example.a01_compose_study.data.vr.VRMWController
import com.example.a01_compose_study.data.vr.VRMWEvent
import com.example.a01_compose_study.data.vr.VRMWEventInfo
import com.example.a01_compose_study.domain.state.BluetoothState
import com.example.a01_compose_study.domain.state.MWState
import com.example.a01_compose_study.domain.util.CustomLogger
import com.example.a01_compose_study.domain.util.G2PController
import com.example.a01_compose_study.domain.util.VRResultListener
import com.example.a01_compose_study.presentation.data.ServiceState
import com.example.a01_compose_study.presentation.util.StringManager
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.coroutineContext

@Singleton
class VrmwManager @Inject constructor(
    @ApplicationContext val context: Context,
    val controller: VRMWController,
    val g2pController: G2PController,
    val mediaIn: MediaIn,
    val mediaOut: MediaOut,
) : IVRMWListener, IMwController() {

    val TAG: String? = this.javaClass.simpleName
    val gson = Gson()
    var vrResult: VRResult? = null
    var currContext: MWContext? = null
    var resultListener: VRResultListener? = null
    var skipStore = false
    private val languageCheckMap = mutableMapOf<String, CompletableDeferred<Any>>()

    var m_stateMachine: MwStateMachine = MwStateMachine()

    init {
        CustomLogger.i("VrmwManager Constructor Hash[${this.hashCode()}]")
        m_stateMachine.init(this)
    }

    fun init() {
        val localeList: LocaleList = context.resources.configuration.locales
        val primaryLocale: Locale? = localeList.get(0)
        CustomLogger.i("ACTION_LOCALE_CHANGED $primaryLocale")
//        val languageType = if (BuildConfig.INSTALL_TYPE != "TABLET") {
//            primaryLocale?.let { Util.parseLanguageType(it.toString()) } ?: HLanguageType.UK_ENGLISH
//        } else {
//            // tablet일때에는 한국어가 대부분이므로 eng로 바꾸게함.
//            HLanguageType.UK_ENGLISH
//        }
        val dirPath = ""//applicationInfo.nativeLibraryDir
//

        controller.setEventListener(this)
        controller.initSystem(
            dirPath,
            ServiceState.settingState.userIdx.value.toString()
        )
        CustomLogger.i("call setG2PListener")
        g2pController.setG2PListener(g2pController)

        CustomLogger.i("call setMediaInListener")
        mediaIn.setMediaInListener(mediaIn)
        CustomLogger.i("call setMediaOutListener")
        mediaOut.setMediaOutListener(mediaOut)
    }

    fun setTTSState(state: HTextToSpeechState, force: Boolean = false) {
        try {
            CustomLogger.i("setTTSState getMwState().ttsState.value ${getMwState().ttsState.value}")
            if (!force) {
                if (getMwState().ttsState.value == HTextToSpeechState.TERMINATED) {
                    return
                }
            }
            CustomLogger.i("setTTSState ${state.name} to ${currContext?.dialogueMode}")
            getMwState().ttsState.value = state
            currContext?.onTTSState(state)
        } catch (e: Exception) {
            e.message?.let { CustomLogger.e(it) }
        }
    }

    fun setVRState(state: HVRState, force: Boolean = false) {
        CustomLogger.i("setVRState ${state.name} to ${currContext?.dialogueMode}")

        if (!force) {
            if (getMwState().vrState.value == HVRState.TERMINATED) {
                return
            }
        }

        getMwState().vrState.value = state
        if (state == HVRState.PREPARING) {
            getMwState().userSpeaking.value = false
        }
        currContext?.onVRState(state)
    }

    fun setVRError(error: HVRError) {
        getMwState().vrError.value = (error)
//        currContext?.onVRError(error)
    }

    fun setVRResult(vrResult: VRResult) {
        this.vrResult = vrResult

        if (currContext == null) {
            cancel()
            resultListener?.onCancel()
        }
        if (mediaIn.isStoreRecord) {
            if (skipStore) {
                skipStore = false
                mediaIn.finishStore("", cancel = true)
            } else {
                vrResult.result?.get(0)?.phrase?.let {
                    mediaIn.finishStore("${currContext?.dialogueMode?.name}_${it}", false)

                } ?: { mediaIn.finishStore("", cancel = true) }
            }
            StringManager.getPcmList()
        }
        currContext?.onVRResult(vrResult)
    }

//    fun setTTSError(error: HTextToSpeechError) {
//        CustomLogger.i("setTTSError ${error.name} to ${currContext?.dialogueMode}")
//        currContext?.onTTSError(error)
//    }

//    fun setVRConfigEvent(event: HVRConfigEvent) {
//        CustomLogger.i("setVRConfigEvent ${event.name} ")
//        getMwState().vrConfigEvent.value = (event)
//
//        if (event == HVRConfigEvent.INITIALIZE_DONE) {
//            getMwState().isVRManagerInitializeDone = true
//            m_stateMachine.addEvent(VRMWEventInfo(VRMWEvent.VRMW_INITIALIZED))
//            if (getMwState().isTTSManagerInitializeDone) {
//                // TODO init이 되었다는 데이터 관리를 다르게 해야할듯.
//                CustomLogger.i("ModuleStatus Ready!")
//                viewModel.systemState.moduleStatus.value = VRMWState.IDLE
//            }
//            CustomLogger.i("currLang: ${viewModel.languageType.value.value}, isVRManagerInitializeDone:${getMwState().isVRManagerInitializeDone}, isTTSManagerInitializeDone:${getMwState().isTTSManagerInitializeDone}, moduleStatus:${viewModel.systemState.moduleStatus.value}")
//        }
//    }

//    fun setTTSConfigEvent(event: HTextToSpeechEvent, languageType: Int) {
//        CustomLogger.i("setTTSConfigEvent ${event.name} ")
//        getMwState().ttsConfigEvent.value = (event)
//        val ttsConfigLanguageType = HLanguageType.values().find { it.id == languageType }
//        // 언어 변경 이후 INITIALIZE_DONE 오면
//        if (HTextToSpeechEvent.INITIALIZE_DONE == event) {
//            // 현재 언어와 올려준 언어 코드 비교 -> 동일한 언어이면 IDLE로 변경
//            CustomLogger.i("setTTSConfigEvent viewModel.languageType.value : [${viewModel.languageType.value}], languageType : [$ttsConfigLanguageType]")
//            val eventInfo = VRMWEventInfo(VRMWEvent.TTSM_INITIALIZED)
//            eventInfo.language = viewModel.languageType.value
//            m_stateMachine.addEvent(eventInfo)
//            if (viewModel.languageType.value == ttsConfigLanguageType) {
//                setTTSState(HTextToSpeechState.IDLE, true)
//                getMwState().isTTSManagerInitializeDone = true
//                if (getMwState().isVRManagerInitializeDone) {
//                    // TODO init이 되었다는 데이터 관리를 다르게 해야할듯.
//                    CustomLogger.i("ModuleStatus Ready!")
//                    viewModel.systemState.moduleStatus.value = VRMWState.IDLE
//                }
//                CustomLogger.i("currLang: ${viewModel.languageType.value.value}, isVRManagerInitializeDone:${getMwState().isVRManagerInitializeDone}, isTTSManagerInitializeDone:${getMwState().isTTSManagerInitializeDone}, moduleStatus:${viewModel.systemState.moduleStatus.value}")
//                CustomLogger.i("send TTS_INITIALIZED")
//                context.sendBroadcast(
//                    Intent(ReceiveAction.SELVAS_ACTION_VR_READY.action),
//                    "com.selvas.permission.VR_SERVICE"
//                )
//            }
//        }
//    }


    fun cancel() {
        currContext = null
        setTTSState(HTextToSpeechState.IDLE)
        setVRState(HVRState.IDLE)
    }

//    fun checkStopVR(mwContext: MWContext): Boolean {
//        pttViewModel.currScreen.value?.let {
//            if (it.mwContext?.contextId == mwContext.contextId) {
//                return true
//            } else {
//                CustomLogger.e("stop ignored ${mwContext.contextId}:${it.mwContext?.contextId}")
//            }
//        } ?: CustomLogger.e("Resume ignored : currScreen null")
//        return false
//    }
//
//
//    fun checkResumeVR(mwContext: MWContext, beep: Boolean) {
//        pttViewModel.currScreen.value?.let {
//            if (it.mwContext?.contextId == mwContext.contextId) {
//                resumeVR(mwContext, beep)
//            } else {
//                CustomLogger.e("Resume ignored ${mwContext.contextId}:${it.mwContext?.contextId}")
//            }
//        } ?: CustomLogger.e("Resume ignored : currScreen null")
//    }

    fun resumeVR(context: MWContext, beep: Boolean = true) {
        context.let {
            setContext(it)
            StringManager.printSttString("")
            controller.callRecogStart(
                it.dialogueMode.value,
                guidanceType = if (beep) HVRGuidanceType.BEEP_START else HVRGuidanceType.NO_BEEP
            )
        }
    }

    fun resumeVR() {
        currContext?.let {
            StringManager.printSttString("")
            controller.callRecogStart(
                it.dialogueMode.value, guidanceType = HVRGuidanceType.BEEP_START
            )
        }
    }

    fun startVR(
        mwContext: MWContext, promptString: String = "", promptArgs: List<String> = listOf()
    ) {

        stop()
        CustomLogger.i("startVR")
        setContext(mwContext)
        currContext?.let { context ->
            CustomLogger.i("currScenario set ${context.dialogueMode}")
            CustomLogger.i("currScenario run ${context.dialogueMode.value}")
            CustomLogger.i("mwContext.promptId ${mwContext.promptId}")
            var promptId = mutableListOf<String>()
            if (mwContext.promptId.size > 0 && mwContext.promptId.first().isNotEmpty()) {
                CustomLogger.i("mwContext.promptId isNotEmpty")
                promptId = mwContext.promptId.toMutableList()
            } else {
                promptId.add("PID_CMN_COMM_01_01")
            }
            StringManager.printSttString("")
            controller.callRecogStart(
                context.dialogueMode.value,
                promptId,
                getDeviceId(),
                promptArgs,
                promptString = promptString
            )
        }
    }

    suspend fun startTTS(promptString: String, requestId: String) {
//        CustomLogger.e(TAG, "startTTS:$requestId [$promptString]")
        setTTSState(HTextToSpeechState.MAX)
        controller.callSpeak(promptString = promptString, requestId = requestId)
        checkAndWaitIdle(30000)
    }

    fun playFile(filePath: String, requestId: String) {
        controller.playFile(filePath, requestId)
    }

    fun playEarcon(requestId: String) {
        controller.playEarcon(requestId)
    }


    fun getDeviceId(): String {
        val deviceId = if (getBluetoothState().hfpDevice.value.connect) {
            if (!getBluetoothState().hfpDevice.value.recognizing) {
                getBluetoothState().hfpDevice.value.device
            } else {
                getBluetoothState().prevHfpDevice.value
            }
        } else {
            ""
        }
        CustomLogger.i("getDeviceId $deviceId")

        return deviceId
    }

//    fun requestVR(
//        dialogue: DialogueMode,
//        promptId: List<String> = listOf(),
//        promptArgs: List<String> = listOf()
//    ) {
//        CustomLogger.i("onRequestVR dialogue : ${dialogue.value}, promptId : ${promptId}, promptArgs:${promptArgs}")
//        pttViewModel.printSttString("")
//        getUiState().showError.value = false
//        controller.callRecogStart(
//            dialogue.value, promptId = promptId, promptArgs = promptArgs, deviceId = getDeviceId()
//        )
//
//    }


//    fun requestTTS(
//        promptId: List<String> = listOf(),
//        promptArgs: List<String> = listOf(),
//        promptString: String = "",
//        printAnnounce: Boolean = false,
//        waitFinish: Boolean = true,
//        showError: Boolean = false,
//        requestId: String = "",
//        runnable: java.lang.Runnable? = null,
//    ) {
//        CustomLogger.i("onRequestTTS ${promptString} ${promptId}")
//        CustomLogger.i("from====")
//        Thread.currentThread().stackTrace.forEach {
//            CustomLogger.i("====${it.methodName}")
//        }
//
//        pttViewModel.addJob(keyString = "requestTTS", interruptable = true) {
//            checkAndWaitIdle()
//            setTTSState(HTextToSpeechState.MAX)
//            if (showError) {
//                getUiState().showError.value = true
//            }
//            CustomLogger.i("waitFinish - $waitFinish, printAnnounce - $printAnnounce, showError - $showError")
//            controller.callSpeak(promptId, promptArgs, promptString, requestId = requestId)
//            if (printAnnounce) {
//                CustomLogger.i("printAnnounce call")
//                printAnnounceString(promptString, true, runnable)
//            } else {
//                if (waitFinish) {
//                    checkAndWaitIdle(0)
//                } else {
//                    checkAndWaitIdle()
//                }
//                if (showError) {
//                    getUiState().showError.value = false
//                }
//                CustomLogger.i("requestTTS run before")
//                runnable?.run()
//                CustomLogger.i("requestTTS run after")
//            }
//
//        }
//        //runnable?.run()
//    }

    fun setContext(mwContext: MWContext) {
        currContext = mwContext
    }

    fun vrStop() {
        controller.recogStop()
    }

    fun playStop() {
        controller.playStop()
    }

    fun stop() {
        CustomLogger.i("STOP tts:${getMwState().ttsState.value.name} vr:${getMwState().vrState.value.name} vrError:${getMwState().vrError.value.name}")
        vrStop()
        playStop()
        cancel()
    }

    fun validateBundle(hashCode: Int): Boolean {
        return currContext?.hashCode() == hashCode
    }

//    fun printAnnounceString(string: String) {
//        printAnnounceString(string, false, null)
//    }

//    fun printAnnounceString(string: String, sync: Boolean, runnable: Runnable? = null) {
//        CustomLogger.i("string - $string, sync - $sync")
//        pttViewModel.addJob(keyString = "printAnnounceString", interruptable = true) {
//
////            viewModel.announceString.postValue(string)
////            runnable?.run()
//
//            // 한글자씩 출력 사양에 없으면 제거
//            val temp = StringBuilder()
//            if (sync) {
//                while (getMwState().ttsState.value != HTextToSpeechState.PLAYING) {
//                    delay(80)
//                }
//            }
//            string.forEach {
//                temp.append(it)
//                pttViewModel.announceString.postValue(temp.toString())
//                delay(80)
//            }
//
//            if (sync) {
//                while (getMwState().ttsState.value != HTextToSpeechState.IDLE) {
//                    delay(80)
//                }
//            }
//            CustomLogger.i("printAnnounceString run before")
//            runnable?.run()
//            CustomLogger.i("printAnnounceString run after")
//        }
//    }

    fun playBeep() {
        controller.playEarcon("")
    }

    fun setLanguage(language: HLanguageType) {
        CustomLogger.i("${language.name}")
        val dirPath = ""//context.applicationInfo.nativeLibraryDir
        controller.setLanguage(dirPath, language.id)
    }

    suspend fun isVRLanguageSupported(language: Int): IntArray {
        CustomLogger.i("")
        languageCheckMap["VRLanguageSupported"] = CompletableDeferred<Any>()
        m_stateMachine.addEvent(VRMWEventInfo(VRMWEvent.VRMW_INITIALIZE))
        controller.isVRLanguageSupported(language)
//        CustomLogger.i("wait response")
//        val result = languageCheckMap["VRLanguageSupported"]?.await()
//        languageCheckMap.remove("VRLanguageSupported")
        var item = IntArray(2)
//        if (result is IntArray) {
//            item = result
//        }
//        CustomLogger.i("available: ${item[0]}, online: ${item[1]}")
        return item
    }

    suspend fun getVRSupportedLanguage(): JSONObject {
        CustomLogger.i("")
        languageCheckMap["VRSupportedLanguage"] = CompletableDeferred()
        controller.getVRSupportedLanguage()
        CustomLogger.i("wait response")
        val result = languageCheckMap["VRSupportedLanguage"]?.await()
        languageCheckMap.remove("VRSupportedLanguage")

        var jsonObject = JSONObject()
        result?.let {
            jsonObject = JSONObject(result as String)
            val embedded = jsonObject.getJSONArray("embedded")
            val server = jsonObject.getJSONArray("server")
            CustomLogger.i("embeddedSize: ${embedded.length()}, serverSize: ${server.length()}")
            CustomLogger.i("embedded: $embedded, server: $server")
        }
        return jsonObject
    }

    suspend fun isTTSLanguageSupported(language: Int): Int {
        CustomLogger.i("")
//        languageCheckMap["TTSLanguageSupported"] = CompletableDeferred<Any>()
        m_stateMachine.addEvent(VRMWEventInfo(VRMWEvent.TTSM_INITIALIZE))
        controller.isTTSLanguageSupported(language)
//        CustomLogger.i("wait response")
//        val result = languageCheckMap["TTSLanguageSupported"]?.await()
//        languageCheckMap.remove("TTSLanguageSupported")
        var available = -1
//        if (result is Int) {
//            available = result
//        }
//        CustomLogger.i("isTTSLanguageSupported result: $available")
        return available
    }

    suspend fun getTTSSupportedLanguage(): JSONObject? {
        CustomLogger.i("")
        languageCheckMap["TTSSupportedLanguage"] = CompletableDeferred<Any>()
        controller.getTTSSupportedLanguage()
        CustomLogger.i("wait response")
        val result = languageCheckMap["TTSSupportedLanguage"]?.await()
        languageCheckMap.remove("TTSSupportedLanguage")

        return if (result is String) {
            val jsonObject = JSONObject(result)
            val embedded = jsonObject.getJSONArray("embedded")
            CustomLogger.i("embeddedSize: ${embedded.length()}")
            CustomLogger.i("embedded: $embedded")
            jsonObject
        } else {
            return null
        }
    }

    fun isTTSIdle(): Boolean {
        val ttsState = getMwState().ttsState.value
        return ttsState == HTextToSpeechState.IDLE || ttsState == HTextToSpeechState.TERMINATED
    }

    fun isVRIdle(): Boolean {
        val vrState = getMwState().vrState.value
        return vrState == HVRState.IDLE || vrState == HVRState.TERMINATED
    }

    fun isIdle(onlyTTS: Boolean = false): Boolean {
        var ret = false
        if (onlyTTS) {
            val ttsState = getMwState().ttsState.value
            if (ttsState == HTextToSpeechState.IDLE || ttsState == HTextToSpeechState.TERMINATED) {
                ret = true
            }
        } else {
            val vrState = getMwState().vrState.value
            if (vrState == HVRState.IDLE || vrState == HVRState.TERMINATED) {
                val ttsState = getMwState().ttsState.value
                if (ttsState == HTextToSpeechState.IDLE || ttsState == HTextToSpeechState.TERMINATED) {
                    ret = true
                }
            }
        }
        return ret
    }

    suspend fun checkAndWaitIdle(timeOutMS: Long = 5000) {
        var count = 0
        val startTime = System.currentTimeMillis()
        CustomLogger.i("start vrState: ${getMwState().vrState.value}, ttsState: ${getMwState().ttsState.value}, timeOutMs : $timeOutMS")
        val job = coroutineContext[Job]
        while (!isIdle() && job?.isActive == true) {
            delay(100)
            count++
            if (count > 10) {
                CustomLogger.i(
                    " still waiting.. " +
                            "vrState:${getMwState().vrState.value.name} " +
                            "ttsState:${getMwState().ttsState.value.name}\n" +
                            "currCtx:${currContext?.dialogueMode} " +
                            "currTime:${System.currentTimeMillis() - startTime} " +
                            "timeout:${timeOutMS}"
                )
                count = 0
                if (timeOutMS > 0) {
                    if (System.currentTimeMillis() - startTime > timeOutMS) {
                        break
                    }
                } else {
                    if (System.currentTimeMillis() - startTime > 15000) {
                        break
                    }
                }
            }
        }
        CustomLogger.i("end vrState: ${getMwState().vrState.value}, ttsState: ${getMwState().ttsState.value}")
    }

    override fun onTTSStateChanged(state: Int, requestId: String) {
        CustomLogger.i("Main onTTSStateChanged $state [$requestId]")
        val ttsState = HTextToSpeechState.values()[state]
        getMwState().ttsState.value = ttsState
        setTTSState(ttsState, true)
    }

    override fun onVREventReceived(event: Int, requestId: String) {
        CustomLogger.i("onVREventReceived [$event] [$requestId]")
        val vrEvent = HVRConfigEvent.values()[event]

        if (HVRConfigEvent.INITIALIZE_DONE == vrEvent) {
            // TTS 요청 가능 전달
//            CoroutineScope(Dispatchers.IO).launch {
//                try{
//                    CustomLogger.i("send TTS_INITIALIZED")
//                    val requestor: IntentRequestor =
//                        EntryPoints.get(context, RequestorFactoryEntry::class.java)
//                            .makeIntentRequestor()
//                    val intent: Intent = Intent("android.intent.action.VR_INITIALIZED")
//                    requestor.request(RequestMode.VOID, RequestType.NONE, intent, 1000)
//                }catch (e : Exception){
//                    e.message?.let { CustomLogger.e(it) }
//                }
//
//            }
        }
    }

    override fun onVRConfigEventReceived(event: Int) {
        CustomLogger.i("onVRConfigEventReceived event : [$event]")
        if (event in HTextToSpeechEvent.values().indices) {
            val vrConfigEvent = HVRConfigEvent.values()[event].apply {
                if (this == HVRConfigEvent.INITIALIZE_DONE) {
                    // /mnt/product/vrdata/vr_var1/data/apps/g2p/10/ ** path가 이렇게되는데,
                    // prefix는 native_jni에 있는 global string으로 설정예정.
                    g2pController.setG2PCachePath(HVRG2PMode.PHONE_BOOK.ordinal, "/phonebook/")
                    g2pController.setG2PCachePath(HVRG2PMode.DAB.ordinal, "/dab/")
                }
            }
            CustomLogger.i("recieved event ${vrConfigEvent.name}")
//            setVRConfigEvent(vrConfigEvent)
        } else {
            CustomLogger.i("unknown event")
        }

    }

    override fun onTTSConfigEventReceived(event: Int, languageType: Int) {
        CustomLogger.i("onTTSConfigEventReceived event : [$event] languageType : [$languageType]")

        if (event in HTextToSpeechEvent.values().indices) {
            val ttsConfigEvent = HTextToSpeechEvent.values()[event]
            CustomLogger.i("recieved event ${ttsConfigEvent.name}")
//            setTTSConfigEvent(ttsConfigEvent, languageType)
        } else {
            CustomLogger.i("unknown event")
        }

    }

    override fun onTTSEventReceived(event: Int, requestId: String) {
        CustomLogger.i("onTTSEventReceived [$event] [$requestId]")
        if (event in HTextToSpeechEvent.values().indices) {
            val ttsEvent = HTextToSpeechEvent.values()[event]
            getMwState().ttsEvent.value = ttsEvent
        } else {
            CustomLogger.i("unknown event")
        }
    }

    override fun onSpeechDetected(event: Int) {
        CustomLogger.i("onSpeechDetected [$event]")
        if (event == HVRSpeechDetection.BOS.ordinal) {
            if (!getMwState().speechDetected.value) {
                getMwState().speechDetected.value = true
            }
        } else {
            if (getMwState().speechDetected.value) {
                getMwState().speechDetected.value = false
            }
        }
    }

    override fun onVRResult(result: String) {
        CustomLogger.i("Main onVRResult $result")

        val logMaxLength = 1000
        CustomLogger.i("==========================================================================")

        if (result.length > logMaxLength) {
            var startIndex = 0
            var endIndex = logMaxLength
            while (startIndex < result.length) {
                endIndex = kotlin.math.min(endIndex, result.length)
                CustomLogger.i(result.substring(startIndex, endIndex))
                startIndex += logMaxLength
                endIndex += logMaxLength
            }
        }
        var tmpResult = result
        CoroutineScope(Dispatchers.Default).launch {
            vrResult?.let { setVRResult(it) }
        }
    }

    override fun onVRStateChanged(state: Int, requestId: String) {
        val vrState = HVRState.values()[state]
        CustomLogger.i("Main onVRStateChanged $state ${vrState.name}")

        setVRState(vrState, true)
    }

    override fun onVRErrorEventRecieved(error: Int, requestId: String) {
        CustomLogger.i("Main onVRErrorEventRecieved $error [$requestId]")
        val vrError = HVRError.values().firstOrNull { it.value == error }
        if (vrError != null) {
            CustomLogger.i("recieved error ${vrError.name}")
            setVRError(vrError)
        } else {
            CustomLogger.i("unknown error")
            setVRError(HVRError.UNKNOWN)
        }
    }

    override fun onTTSErrorEventRecieved(error: Int, requestId: String) {
        CustomLogger.i("Main onTTSErrorEventRecieved $error")
        val ttsError = HTextToSpeechError.values().firstOrNull { it.value == error }
        getMwState().ttsErrorEvent.value = ttsError

    }

    override fun onVRPartialResultUpdated(result: String) {
//        CustomLogger.i("Main onVRPartialResultUpdated $result")
//        CoroutineScope(Dispatchers.Default).launch {
//
//
//            val partialResult = GsonWrapper.fromJson(result, PartialResult::class.java)
//            partialResult.dictation?.let {
//                if (Tags.PARTIAL.isEqual(it.type.toString()) || Tags.FINAL.isEqual(it.type.toString())) {
//                    pttViewModel.printSttString(it.text.toString())
//                }
//            }
//        }
    }


    override fun onVoiceAmplitudeLevelChanged(level: Int, negativeSign: Boolean) {
//        CustomLogger.i("Main onVoiceAmplitudeLevelChanged $level, $negativeSign")
//
//        val threshold = pttViewModel.getVrConfig().voiceAmplitudeThreshold
//        val signedLevel = if (negativeSign) level * -1 else level
//        var isSpeaking = false
//
//        if (negativeSign) {
//            if (signedLevel >= threshold) {
//                isSpeaking = true
//            }
//        } else {
//            if (signedLevel >= threshold * -1) {
//                isSpeaking = true
//            }
//        }
//
//        if (isSpeaking) {
//            if (!getMwState().userSpeaking.value) {
//                getMwState().userSpeaking.value = true
//            }
//
//            if (pttViewModel.isDevelopMode.value) {
//                if (!mediaIn.isStoreRecord) {
//                    mediaIn.startStore()
//                }
//            }
//
//        } else {
//            if (getMwState().userSpeaking.value) {
//                getMwState().userSpeaking.value = false
//            }
//        }
    }

    override fun onVRLanguageSupportedReceived(
        available: Int, languageType: Int, online: Int, keywordSpotAvailable: Int
    ) {
//        CustomLogger.i("Main onVRLanguageSupportedReceived available: $available, languageType: $languageType, online: $online, keywordSpotAvailable: $keywordSpotAvailable")
////        languageCheckMap["VRLanguageSupported"]?.complete(intArrayOf(available, online))
//
//        pttViewModel.getVrConfig().isSupportServer = online == 1
//        pttViewModel.getVrConfig().isSupportASR = available == 1
//
//        val event = VRMWEventInfo(VRMWEvent.LANGUAGE_SUPPORTED)
//        event.language = pttViewModel.languageType.value
//        event.languageAvailable = available
//        event.online = online
//        m_stateMachine.addEvent(event)
    }

    override fun onVRSupportedLanguageReceived(supportedLangList: String) {
        CustomLogger.i("Main onVRSupportedLanguageReceived $supportedLangList")
        languageCheckMap["VRSupportedLanguage"]?.complete(supportedLangList)
    }

    override fun onTTSLanguageSupportedReceived(available: Int, languageType: Int) {
//        CustomLogger.i("Main onTTSLanguageSupportedReceived available: $available, languageType: $languageType")
//        pttViewModel.getVrConfig().isSupportTTS = available == 1
//        val event = VRMWEventInfo(VRMWEvent.LANGUAGE_SUPPORTED)
//        event.language = pttViewModel.languageType.value
//        event.languageAvailable = available
//        m_stateMachine.addEvent(event)
    }

    override fun onTTSSupportedLanguageReceived(supportedLangList: String) {
        CustomLogger.i("Main onTTSSupportedLanguageReceived $supportedLangList")
        languageCheckMap["TTSSupportedLanguage"]?.complete(supportedLangList)
    }

    override fun onPromptStatusUpdated(status: Int) {
        val promptStatus = HVRPromptStatus.values().firstOrNull { it.ordinal == status }
        CustomLogger.i("Main onPromptStatusUpdated status: $promptStatus")
    }

    override fun onCrashDetected() {
        CustomLogger.e("onCrashDetected:")
        stop()
    }

    fun getBluetoothState(): BluetoothState {
        return ServiceState.bluetoothState
    }

    fun getMwState(): MWState {
        return ServiceState.mwState
    }

//    fun getUiState(): UIState {
//        return pttViewModel.uiState
//    }

    fun addEvent(eventInfo: VRMWEventInfo) {
        m_stateMachine.addEvent(eventInfo)
    }

    override fun changeLanguageTTSM(eventInfo: VRMWEventInfo): Boolean {
        // TTSManager language
        CustomLogger.i("$eventInfo")
        controller.isTTSLanguageSupported(eventInfo.language.id)
        return true
    }

    override fun changeLanguageVRMA(eventInfo: VRMWEventInfo): Boolean {
        // VRManager language
        CustomLogger.i("$eventInfo")
        controller.isVRLanguageSupported(eventInfo.language.id)
        return true
    }

    override fun loadVR(eventInfo: VRMWEventInfo): Boolean {
        // todo 향후에 다이렉트로 호출하는형태가아닌, 상태에 따라서 요청할수있게 할 예정
        CustomLogger.i("$eventInfo")
        controller.loadVR(eventInfo.language.id)
        return true
    }

    override fun loadTTS(eventInfo: VRMWEventInfo): Boolean {
        // todo 향후에 다이렉트로 호출하는형태가아닌, 상태에 따라서 요청할수있게 할 예정
        CustomLogger.i("$eventInfo")
        controller.loadTTS(eventInfo.language.id)
        return true
    }

    override fun VRMWInitialized(eventInfo: VRMWEventInfo): Boolean {
        CustomLogger.i("$eventInfo")
//        setLanguage(eventInfo.language)
        return true
    }

    override fun startVR(eventInfo: VRMWEventInfo): Boolean {
        // todo 향후에 다이렉트로 호출하는형태가아닌, 상태에 따라서 요청할수있게 할 예정
        CustomLogger.i("$eventInfo")
        return true
    }

    override fun stopVR(eventInfo: VRMWEventInfo): Boolean {
        // todo 향후에 다이렉트로 호출하는형태가아닌, 상태에 따라서 요청할수있게 할 예정
        CustomLogger.i("$eventInfo")
        return true
    }

    override fun speak(eventInfo: VRMWEventInfo): Boolean {
        // todo 향후에 다이렉트로 호출하는형태가아닌, 상태에 따라서 요청할수있게 할 예정
        CustomLogger.i("$eventInfo")
        return true
    }

    override fun stopTTS(eventInfo: VRMWEventInfo): Boolean {
        // todo 향후에 다이렉트로 호출하는형태가아닌, 상태에 따라서 요청할수있게 할 예정
        CustomLogger.i("$eventInfo")
        return true
    }

}