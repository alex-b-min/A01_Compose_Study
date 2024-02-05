//package com.example.a01_compose_study.data.vr
//
//import android.content.Context
//import com.example.a01_compose_study.data.HTextToSpeechState
//import com.example.a01_compose_study.data.HVRState
//import com.example.a01_compose_study.data.MWContext
//import com.example.a01_compose_study.data.VRResult
//import com.example.a01_compose_study.domain.state.MWState
//import com.example.a01_compose_study.domain.util.CustomLogger
//import com.example.a01_compose_study.domain.util.VRResultListener
//import com.example.a01_compose_study.presentation.ServiceViewModel
//import dagger.hilt.android.qualifiers.ApplicationContext
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import javax.inject.Inject
//import javax.inject.Singleton
//
//@Singleton
//class VrmwManager @Inject constructor(
//    @ApplicationContext val context: Context,
//    val viewModel: ServiceViewModel,
//    val mediaIn: MediaIn,
//) : IVRMWListener, IMwController() {
//
//    val TAG: String? = this.javaClass.simpleName
//    var vrResult: VRResult? = null
//    var currContext: MWContext? = null
//    var resultListener: VRResultListener? = null
//
//    fun getMwState(): MWState {
//        return viewModel.mwState
//    }
//
//    override fun onVRResult(result: String) {
//        CustomLogger.i("Main onVRResult $result")
//
//        val logMaxLength = 1000
//        CustomLogger.i("==========================================================================")
//
//        if (result.length > logMaxLength) {
//            var startIndex = 0
//            var endIndex = logMaxLength
//            while (startIndex < result.length) {
//                endIndex = kotlin.math.min(endIndex, result.length)
//                CustomLogger.i(result.substring(startIndex, endIndex))
//                startIndex += logMaxLength
//                endIndex += logMaxLength
//            }
//        }
//        var tmpResult = result
//        CoroutineScope(Dispatchers.Default).launch {
//            val vrResult = GsonWrapper.fromJson(tmpResult, VRResult::class.java)
//            CustomLogger.i("recognized size:${vrResult.result?.size}")
//            setVRResult(vrResult)
//        }
//    }
//
//    fun setVRResult(vrResult: VRResult) {
//        this.vrResult = vrResult
//        CustomLogger.i("setVRResult to ${currContext?.TAG} store:${mediaIn.isStoreRecord}")
//        if (currContext == null) {
//            cancel()
//            resultListener?.onCancel()
//        }
//        if (mediaIn.isStoreRecord) {
//            if (viewModel.skipStore) {
//                viewModel.skipStore = false
//                mediaIn.finishStore("", cancel = true)
//            } else {
//                vrResult.result?.get(0)?.phrase?.let {
//                    mediaIn.finishStore("${currContext?.dialogueMode?.name}_${it}", false)
//
//                } ?: { mediaIn.finishStore("", cancel = true) }
//            }
//            //viewModel.getPcmList()
//        }
//        currContext?.onVRResult(vrResult)
//    }
//
//    fun cancel() {
//        currContext = null
//        setTTSState(HTextToSpeechState.IDLE)
//        setVRState(HVRState.IDLE)
//    }
//
//    fun setTTSState(state: HTextToSpeechState, force: Boolean = false) {
//        try {
//            CustomLogger.i("setTTSState getMwState().ttsState.value ${getMwState().ttsState.value}")
//            if (!force) {
//                if (getMwState().ttsState.value == HTextToSpeechState.TERMINATED) {
//                    return
//                }
//            }
//            CustomLogger.i("setTTSState ${state.name} to ${currContext?.dialogueMode}")
//            getMwState().ttsState.value = state
//            currContext?.onTTSState(state)
//        } catch (e: Exception) {
//            e.message?.let { CustomLogger.e(it) }
//        }
//    }
//
//    fun setVRState(state: HVRState, force: Boolean = false) {
//        CustomLogger.i("setVRState ${state.name} to ${currContext?.dialogueMode}")
//
//        if (!force) {
//            if (getMwState().vrState.value == HVRState.TERMINATED) {
//                return
//            }
//        }
//
//        getMwState().vrState.value = state
//        if (state == HVRState.PREPARING) {
//            getMwState().userSpeaking.value = false
//        }
//        currContext?.onVRState(state)
//    }
//
//}