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

    fun onVRResult(vrResult: VRResult, selectVRResult: SelectVRResult) {
        Log.d("@@ MWContext onVRResult", "${vrResult}")

        val bundle = when (selectVRResult) {
            SelectVRResult.HelpResult -> {
                ParserFactory().dataParsing(vrResult, dialogueMode = dialogueMode)
                    .also {
                        it?.type = ParseDomainType.HELP
                        it?.dialogueMode = DialogueMode.CALL
                    }
            }

            SelectVRResult.PttResult -> {
                null
            }

            SelectVRResult.CallIndexListResult -> {
                ParserFactory().dataParsing(vrResult, dialogueMode = dialogueMode)
                    .also {
                        it?.type = ParseDomainType.CALL
                        it?.dialogueMode = DialogueMode.CALL
                    }
            }

            SelectVRResult.SendMsgResult -> {
                ParserFactory().dataParsing(vrResult, dialogueMode = dialogueMode)
                    .also {
                        it?.type = ParseDomainType.SEND_MESSAGE
                        it?.dialogueMode = DialogueMode.SEND_MESSAGE
                        it?.selectVRResult = SelectVRResult.SendMsgResult
                        Log.d("sub","${it?.selectVRResult}")
                    }
            }

            SelectVRResult.SendMsgNameResult -> {
                ParserFactory().dataParsing(vrResult, dialogueMode = dialogueMode)
                    .also {
                        it?.type = ParseDomainType.SEND_MESSAGE
                        it?.dialogueMode = DialogueMode.SEND_MESSAGE
                        it?.selectVRResult = SelectVRResult.SendMsgNameResult
                        Log.d("sub","${it?.selectVRResult}")
                    }
            }

            SelectVRResult.SendMsgNameMsgResult -> {
                ParserFactory().dataParsing(vrResult, dialogueMode = dialogueMode)
                    .also {
                        it?.type = ParseDomainType.SEND_MESSAGE
                        it?.dialogueMode = DialogueMode.SEND_MESSAGE
                        it?.selectVRResult = SelectVRResult.SendMsgNameMsgResult
                        Log.d("sub","${it?.selectVRResult}")
                    }
            }

            SelectVRResult.NoResult(isSayMessage = true) -> {
                ParserFactory().dataParsing(vrResult, dialogueMode = dialogueMode)
                    .also {
                        it?.type = ParseDomainType.SEND_MESSAGE
                        it?.dialogueMode = DialogueMode.SEND_MESSAGE_NAME
                        it?.selectVRResult = SelectVRResult.NoResult(isSayMessage = true)
                    }
            }

            SelectVRResult.NoResult(isSayMessage = false) -> {
                ParserFactory().dataParsing(vrResult, dialogueMode = dialogueMode)
                    .also {
                        it?.type = ParseDomainType.SEND_MESSAGE
                        it?.dialogueMode = DialogueMode.SEND_MESSAGE_NAME_CHANGE
                        it?.selectVRResult = SelectVRResult.NoResult(isSayMessage = false)
                    }
            }

            SelectVRResult.YesResult -> {
                ParserFactory().dataParsing(vrResult, dialogueMode = dialogueMode)
                    .also {
                        it?.type = ParseDomainType.SEND_MESSAGE
                        it?.dialogueMode = DialogueMode.SEND_MESSAGE_NAME_CHANGE
                        it?.selectVRResult = SelectVRResult.YesResult
                    }
            }
            SelectVRResult.MessageReult -> {
                ParserFactory().dataParsing(vrResult, dialogueMode = dialogueMode)
                    .also {
                        it?.type = ParseDomainType.SEND_MESSAGE
                        it?.dialogueMode = DialogueMode.SEND_MESSAGE_NAME
                        it?.selectVRResult = SelectVRResult.MessageReult
                    }
            }
            SelectVRResult.ChangeMessage -> {
                ParserFactory().dataParsing(vrResult, dialogueMode = dialogueMode)
                    .also {
                        it?.type = ParseDomainType.SEND_MESSAGE
                        it?.dialogueMode = DialogueMode.SEND_MESSAGE_NAME_CHANGE
                        it?.selectVRResult = SelectVRResult.ChangeMessage
                    }
            }

            SelectVRResult.ScrollIndexResult -> {
                Log.e("sendMsg","MWContext안 ScrollIndexResult")
                ParserFactory().dataParsing(vrResult, dialogueMode = dialogueMode)
                    .also {
                        it?.type = ParseDomainType.SEND_MESSAGE
                        it?.dialogueMode = DialogueMode.LIST
                        it?.selectVRResult = SelectVRResult.ScrollIndexResult
                    }
            }

            else -> {
                ParserFactory().dataParsing(vrResult, dialogueMode = dialogueMode)
                    .also {
                        it?.type = ParseDomainType.SEND_MESSAGE
                        it?.dialogueMode = DialogueMode.NONE
                    }
            }
        }

        bundle?.let {
            it.contextId = this.contextId

            if (it.type == ParseDomainType.UNSUPPORTED_DOMAIN) {
                resultListener.onVRError(HVRError.ERROR_HMI)
            } else {
                resultListener.onReceiveBundle(it)
                Log.d("sub","${it?.selectVRResult}")
            }
        } ?: run {
            resultListener.onBundleParsingErr()
        }
    }
}
