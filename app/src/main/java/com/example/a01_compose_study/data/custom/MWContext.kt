package com.example.a01_compose_study.data.custom

import android.util.Log
import com.example.a01_compose_study.data.DialogueMode
import com.example.a01_compose_study.data.HTextToSpeechState
import com.example.a01_compose_study.data.HVRError
import com.example.a01_compose_study.data.HVRState
import com.example.a01_compose_study.data.VRResult
import com.example.a01_compose_study.data.analyze.ParseDomainType
import com.example.a01_compose_study.data.analyze.ParserFactory
import com.example.a01_compose_study.domain.util.CustomLogger
import com.example.a01_compose_study.presentation.data.UiState
import com.example.a01_compose_study.presentation.screen.SelectVRResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class MWContext(
    val dialogueMode: DialogueMode,
    private val resultListener: VRResultListener,
) {
    var vrState = HVRState.IDLE
    var ttsState = HTextToSpeechState.IDLE
    var promptId = mutableListOf<String>()

    var isSubContext = false
    var contextId = this.hashCode()

    /**
     * 파싱된 데이터를 하나로 묶기 위한 StateFlow 타입의 sealedParsedData 전역변수
     */
    private val _sealedParsedData = UiState._sealedParsedData
    val sealedParsedData: SharedFlow<SealedParsedData> = UiState._sealedParsedData

    private val job = CoroutineScope(Dispatchers.Default)

    init {
        isSubContext = when (dialogueMode) {
            DialogueMode.YESNO,
            DialogueMode.CALLNAME,
            DialogueMode.LIST,
            -> {
                true
            }

            else -> {
                false
            }
        }
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
        /**
         * Error가 발생하는 상황에 대해,
         * - 발화를 했을 때 인식하지 못할 때의 오류일 때(PTT 화면을 빨갛게 띄웠다가 다시 파란색으로 띄워야함
         * - Domain Window 화면을 성공적으로 띄우고 나서의 오류일 때도 있나..?
         */
        job.launch {
            _sealedParsedData.emit(SealedParsedData.ErrorData(error))
        }
    }

    /**
     * bundle의 type : 어떤 매니저에게 전달할지를 결정하는 변수
     * bundle의 dialogue : 해당 매니저에서 구체적인 파싱 작업을 위해 dialogue 값을 활용하는 변수
     */
    fun onVRResult(vrResult: VRResult, selectVRResult: SelectVRResult) {
        Log.d("@@ MWContext onVRResult", "${vrResult}")
//        val bundle = ParserFactory().dataParsing(vrResult, dialogueMode = DialogueMode.HELP)
//        bundle?.type = ParseDomainType.HELP

        val bundle = when (selectVRResult) {
            SelectVRResult.HelpResult -> {
                ParserFactory().dataParsing(vrResult, dialogueMode = dialogueMode)
                    .also {
                        it?.type = ParseDomainType.HELP
                        it?.dialogueMode = DialogueMode.HELP
                    }
            }

            SelectVRResult.PttResult -> {
                null
            }

            SelectVRResult.CallListResult -> {
                ParserFactory().dataParsing(vrResult, dialogueMode = dialogueMode)
                    .also {
                        it?.type = ParseDomainType.CALL
                        it?.dialogueMode = DialogueMode.CALL
                    }
            }

            SelectVRResult.CallIndexListResult -> {
                ParserFactory().dataParsing(vrResult, dialogueMode = dialogueMode)
                    .also {
                        it?.type = ParseDomainType.CALL
                        it?.dialogueMode = DialogueMode.CALL
                    }
            }

            SelectVRResult.CallRecognizedContact -> {
                ParserFactory().dataParsing(vrResult, dialogueMode = dialogueMode)
                    .also {
                        it?.type = ParseDomainType.CALL
                        it?.dialogueMode = DialogueMode.CALL
                    }
            }

            SelectVRResult.ScrollIndexResult -> {
                ParserFactory().dataParsing(vrResult, dialogueMode = dialogueMode)
                    .also {
                        it?.type = ParseDomainType.CALL
                        it?.dialogueMode = DialogueMode.LIST
                    }
            }

            SelectVRResult.CallOtherNameResult -> {
                ParserFactory().dataParsing(vrResult, dialogueMode = dialogueMode)
                    .also {
                        it?.type = ParseDomainType.CALL
                        it?.dialogueMode = DialogueMode.CALLNAME
                    }
            }
        }

        bundle?.let {
            it.contextId = this.contextId

            if (it.type == ParseDomainType.UNSUPPORTED_DOMAIN) {
                resultListener.onVRError(HVRError.ERROR_HMI)
            } else {
                resultListener.onReceiveBundle(bundle = it, selectVRResult = selectVRResult)
            }
        } ?: run {
            resultListener.onBundleParsingErr()
        }

//        when (bundle?.type) {
//            ParseDomainType.HELP -> {
//                /**
//                 * 기존에는 적절한 각 DomainManager의 onReceiveBundle()에 해당 bundle의 값을 넣어주는 방식
//                 * ==> 바뀐 방식은 아래와 같음
//                 * 각 DoaminManager에서 bundle의 값을 넣으면 파싱하여 적절한 데이터을 리턴해주는 함수를 구현한다.
//                 * 참고로 Help의 경우,
//                 * domainType/screenType/screenSizeType/data 의 데이터를 가져야하므로 ProcHelpData라는 데이터 클래스를 한 개 생성함
//                 */
//                val procHelpData = dataProducer?.helpManager?.parsedData(bundle)
//                job.launch {
//                    procHelpData?.let { SealedParsedData.HelpData(it) }
//                        ?.let { _sealedParsedData.emit(it) }
//                }
//            }
//
//            ParseDomainType.CALL -> {
//                val procCallData = dataProducer?.callManager?.parsedData(bundle)
//                Log.d("@@ procCallData", "${procCallData}")
//                job.launch {
//                    procCallData?.let { SealedParsedData.CallData(it) }
//                        ?.let { _sealedParsedData.emit(it) }
//                }
//            }
//
//            else -> {
//                /**
//                 * 다른 도메인 타입들에 대한 값들이 들어간다.
//                 */
//            }
//        }
    }
}
