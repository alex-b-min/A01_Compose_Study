package com.example.a01_compose_study.data

import com.example.a01_compose_study.domain.model.ParseDomainType
import com.example.a01_compose_study.domain.util.VRResultListener
import com.example.a01_compose_study.domain.util.ParseBundle
import com.example.a01_compose_study.domain.util.CustomLogger

class MWContext(
    val dialogueMode: DialogueMode,
    private val resultListener: VRResultListener,
) {
    val TAG = this.javaClass.simpleName
    var vrState = HVRState.IDLE
    var ttsState = HTextToSpeechState.IDLE
    var vrResult: VRResult? = null
    var bundle: ParseBundle<out Any>? = null
    var procState = ProcState.NONE
    var domain = ParseDomainType.MAX
    var promptId = mutableListOf<String>()
    var promptArgs = mutableListOf<String>()
    var isSubContext = false
    var timeOutCnt = 0
    var rejectCnt = 0
    var contextId = this.hashCode()

    init {
        CustomLogger.i("$TAG Constructor Hash[${this.hashCode()}]")
        isSubContext = when (dialogueMode) {
            DialogueMode.YESNO,
            DialogueMode.CALLNAME,
            DialogueMode.LIST -> {
                true
            }

            else -> {
                false
            }
        }
    }

    fun resetCount() {
        resetReject()
        resetTimeout()
    }

    fun resetReject() {
        rejectCnt = 0
    }

    fun resetTimeout() {
        timeOutCnt = 0
    }

    fun onTTSState(state: HTextToSpeechState) {
        CustomLogger.i("setTTSState ${state.name}")
        ttsState = state

    }

    fun onVRState(state: HVRState) {
        CustomLogger.i("setVRState ${state.name}")
        vrState = state

    }

    fun onVRError(error: HVRError) {
        resultListener.onVRError(error)
    }

    fun onTTSError(error: HTextToSpeechError) {
        CustomLogger.i("setTTSError ${error.name}")
    }

    fun onVRResult(vrResult: VRResult) {
        TODO("발화 후 결과값에 대한 로직 처")
//        CustomLogger.i("onVRResult ${dialogueMode} VRResult")
//        this.vrResult = vrResult
//        this.procState = ProcState.READY
//        bundle = com.example.a01_compose_study.domain.ParserFactory()리
//            .dataParsing(vrResult, dialogueMode)
//        bundle?.let {
//            it.contextId = this.contextId
//            if (it.type == ParseDomainType.UNSUPPORTED_DOMAIN) {
//                resultListener.onVRError(HVRError.ERROR_HMI)
//            } else {
//                resultListener.onReceiveBundle(it)
//            }
//
//        } ?: run {
//            resultListener.onBundleParsingErr()
//        }
    }

    override fun toString(): String {
        return "HashCode[${hashCode()}], DialogueMode[$dialogueMode],  domain[$domain]"
    }
}
