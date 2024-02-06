package com.example.a01_compose_study.data.custom

import com.example.a01_compose_study.data.DialogueMode
import com.example.a01_compose_study.data.HTextToSpeechState
import com.example.a01_compose_study.data.HVRError
import com.example.a01_compose_study.data.HVRState
import com.example.a01_compose_study.data.VRResult
import com.example.a01_compose_study.data.analyze.ParseDomainType
import com.example.a01_compose_study.data.analyze.ParserFactory
import com.example.a01_compose_study.domain.util.CustomLogger
import com.example.a01_compose_study.domain.util.VRResultListener
import com.example.a01_compose_study.presentation.data.UiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Singleton

@Singleton
class MWContext(
    val dialogueMode: DialogueMode,
) {
    private val helpManager: HelpManager = HelpManager()
    var vrState = HVRState.IDLE
    var ttsState = HTextToSpeechState.IDLE
    var promptId = mutableListOf<String>()

    var isSubContext = false

    init {
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

    /**
     * 파싱된 데이터를 하나로 묶기 위한 StateFlow 타입의 sealedParsedData 전역변수
     */
    private val _sealedParsedData = UiState._sealedParsedData
    val sealedParsedData: SharedFlow<SealedParsedData> = UiState._sealedParsedData

    private val job = CoroutineScope(Dispatchers.Default)

    fun onTTSState(state: HTextToSpeechState) {
        CustomLogger.i("setTTSState ${state.name}")
        ttsState = state

    }

    fun onVRState(state: HVRState) {
        CustomLogger.i("setVRState ${state.name}")
        vrState = state

    }

//    fun onVRError(error: HVRError) {
//        resultListener.onVRError(error)
//    }

    fun onVRResult(vrResult: VRResult) {
        val bundle = ParserFactory().dataParsing(vrResult, dialogueMode = DialogueMode.HELP)
        bundle?.type = ParseDomainType.HELP

        when (bundle?.type) {
            ParseDomainType.HELP -> {
                /**
                 * 기존에는 적절한 각 DomainManager의 onReceiveBundle()에 해당 bundle의 값을 넣어주는 방식
                 * ==> 바뀐 방식은 아래와 같음
                 * 각 DoaminManager에서 bundle의 값을 넣으면 파싱하여 적절한 데이터을 리턴해주는 함수를 구현한다.
                 * 참고로 Help의 경우,
                 * domainType/screenType/screenSizeType/data 의 데이터를 가져야하므로 ProcHelpData라는 데이터 클래스를 한 개 생성함
                 */
                val procHelpData = helpManager.parsedData(bundle)
                job.launch {
                    _sealedParsedData.emit(SealedParsedData.HelpData(procHelpData))
                }
            }

            else -> {
                /**
                 * 다른 도메인 타입들에 대한 값들이 들어간다.
                 */
            }
        }
    }
}
