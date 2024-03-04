package com.example.a01_compose_study.presentation.screen.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a01_compose_study.data.Contact
import com.example.a01_compose_study.data.HVRError
import com.example.a01_compose_study.data.custom.SealedParsedData
import com.example.a01_compose_study.data.custom.call.ProcCallData
import com.example.a01_compose_study.data.custom.ptt.PttManager
import com.example.a01_compose_study.data.custom.sendMsg.MsgData
import com.example.a01_compose_study.data.custom.sendMsg.SendMsgDataType
import com.example.a01_compose_study.domain.model.HelpItemData
import com.example.a01_compose_study.domain.model.ScreenType
import com.example.a01_compose_study.domain.model.SealedDomainType
import com.example.a01_compose_study.domain.util.ScreenSizeType
import com.example.a01_compose_study.presentation.data.UiState
import com.example.a01_compose_study.presentation.screen.SelectVRResult
import com.example.a01_compose_study.presentation.screen.main.route.VRUiState
import com.example.a01_compose_study.presentation.screen.ptt.PttEvent
import com.example.a01_compose_study.presentation.screen.ptt.VrmwManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val vrmwManager: VrmwManager, // MainViewModel에서 필요한 이유는, 음성인식 결과를 직접적으로 생성하기 위해
    val pttManager: PttManager,
) : ViewModel() {

    private val sealedParsedData: SharedFlow<SealedParsedData> = UiState._sealedParsedData

    private val _domainUiState = UiState._domainUiState
    val domainUiState: StateFlow<DomainUiState> = UiState.domainUiState

    private val _domainWindowVisible = UiState._domainWindowVisible
    val domainWindowVisible: StateFlow<Boolean> = UiState.domainWindowVisible

    private val _vrUiState = UiState._VRUiState
    val vrUiState: StateFlow<VRUiState> = UiState.vrUiState

    private val _sendUiData = UiState._sendUiData
    val sendUiData: SharedFlow<Any> = _sendUiData

    val announceString = pttManager.announceString

    init {
        collectParsedData()
    }

    private fun collectParsedData() {
        viewModelScope.launch {
            sealedParsedData.onEach { sealedParsedData ->
//                TODO("사전처리 작업이 있을 때 로직 추가")
            }.collect { sealedParsedData ->
                when (sealedParsedData) {
                    is SealedParsedData.ErrorData -> {
                        when (sealedParsedData.error) {
                            HVRError.ERROR_DIALOGUE_ASR_RECOGNITION_TIMEOUT -> {
//                                procTimeOut(it)
                            }

                            HVRError.ERROR_DIALOGUE_ASR_SERVER_RESPONSE,
                            HVRError.ERROR_DIALOGUE_ASR_SERVER_UNAVAILABLE,
                            HVRError.ERROR_DIALOGUE_ASR_SERVER_CONNECTION,
                            HVRError.ERROR_DIALOGUE_ASR_NETWORK_NO_SIGNAL,
                            HVRError.ERROR_DIALOGUE_ASR_SERVER_NO_RESPONSE,
                            -> {
//                               procServerErr(error, it)
                            }

                            HVRError.ERROR_DIALOGUE_ARBITRATOR_REJECTION -> {
//                                reject()
                            }

                            HVRError.ERROR_HMI -> {
                                _domainUiState.update { uiState ->
                                    when (uiState) {
                                        is DomainUiState.PttWindow -> uiState.copy(
                                            isError = true,
                                            errorText = sealedParsedData.error.name
                                        )

                                        else -> uiState
                                    }
                                }
                            }

                            else -> {

                            }
                        }
                    }

                    is SealedParsedData.HelpData -> {
                        onDomainEvent(
                            event = MainEvent.OpenDomainWindowEvent(
                                domainType = SealedDomainType.Announce,
                                screenType = ScreenType.Prepare,
                                data = sealedParsedData.procHelpData.domainType.text,
                                isError = false,
                                screenSizeType = ScreenSizeType.Small
                            )
                        )
                        delay(1000)
                        onDomainEvent(
                            event = MainEvent.OpenDomainWindowEvent(
                                domainType = sealedParsedData.procHelpData.domainType,
                                screenType = sealedParsedData.procHelpData.screenType,
                                data = sealedParsedData.procHelpData.data,
                                isError = false,
                                screenSizeType = sealedParsedData.procHelpData.screenSizeType,
                            )
                        )
                    }

                    is SealedParsedData.CallData -> {
                        when (sealedParsedData.procCallData) {
                            is ProcCallData.RejectRequest -> {
                                Log.d("@@ ProcCallData", "RejectRequest")
                            }

                            is ProcCallData.ListTTSRequest -> {
                                Log.d("@@ ProcCallData", "ListTTSRequest - Prompt ID: ${sealedParsedData.procCallData.promptId}")
                            }

                            is ProcCallData.NoticeTTSRequest -> {
                                Log.d("@@ ProcCallData", "NoticeTTSRequest - Notice Model: ${sealedParsedData.procCallData.noticeModel}")
                            }

                            is ProcCallData.ProcCallNameScreen -> {
                                Log.d("@@ ProcCallData", "ProcCallNameScreen - Contact: ${sealedParsedData.procCallData.data}")
                            }

                            is ProcCallData.RecognizedContactListScreen -> {
                                Log.d("@@ ProcCallData", "ContactListScreen - Contact List: ${sealedParsedData.procCallData.data}")
                                onDomainEvent(
                                    event = MainEvent.OpenDomainWindowEvent(
                                        domainType = sealedParsedData.procCallData.sealedDomainType,
                                        screenType = sealedParsedData.procCallData.screenType,
                                        data = sealedParsedData.procCallData.data,
                                        isError = false,
                                        screenSizeType = sealedParsedData.procCallData.screenSizeType,
                                    )
                                )
                            }

                            is ProcCallData.AllContactListScreen -> {
                                Log.d("@@ ProcCallData", "FullContactListScreen - Full Contact List: ${sealedParsedData.procCallData.data}")
                                onDomainEvent(
                                    event = MainEvent.OpenDomainWindowEvent(
                                        domainType = sealedParsedData.procCallData.sealedDomainType,
                                        screenType = sealedParsedData.procCallData.screenType,
                                        data = sealedParsedData.procCallData.data,
                                        isError = false,
                                        screenSizeType = sealedParsedData.procCallData.screenSizeType,
                                    )
                                )
                            }

                            is ProcCallData.ScrollIndex -> {
                                val newIndex = sealedParsedData.procCallData.index
                                val currentScrollIndex = domainUiState.value.scrollIndex

                                if (newIndex != null && currentScrollIndex != null) {
                                    if (newIndex < currentScrollIndex) {
                                        onDomainEvent(MainEvent.ChangeScrollIndexEvent(newIndex))
                                    } else {
                                        pttManager.vrmwManager.requestTTs(
                                            promptId = listOf("PID_CMN_COMM_02_31"),
                                            runnable = { vrmwManager.resumeVR() }
                                        )
                                    }
                                    Log.d("@@ ProcCallData", "ScrollIndex - Index: ${sealedParsedData.procCallData.index}")
                                }
                            }

                            is ProcCallData.ProcYesNoOtherNumberResult -> {
                                Log.d("@@ ProcCallData", "YesNoOtherNumberResultProc - Result: ${sealedParsedData.procCallData.callYesNoOtherNumberResult}")
                            }
                        }
                    }

                    is SealedParsedData.SendMsgData -> {
//                        onDomainEvent(
//                            event = MainEvent.OpenDomainWindowEvent(
//                                domainType = sealedParsedData.procSendMsgData.domainType,
//                                screenType = sealedParsedData.procSendMsgData.screenType,
//                                data = sealedParsedData.procSendMsgData.data,
//                                isError = false,
//                                screenSizeType = ScreenSizeType.Large
//                            )
//                        )
                        when (sealedParsedData.procSendMsgData.data) {
                            is SendMsgDataType.SendMsgData -> {
                                onDomainEvent(
                                    event = MainEvent.OpenDomainWindowEvent(
                                        domainType = sealedParsedData.procSendMsgData.domainType,
                                        screenType = sealedParsedData.procSendMsgData.screenType,
                                        data = sealedParsedData.procSendMsgData.data,
                                        isError = false,
                                        screenSizeType = ScreenSizeType.Large
                                    )
                                )
                            }

                            is SendMsgDataType.SendScreenData -> {
                                onDomainEvent(
                                    event = MainEvent.SendDataEvent(
                                        data = sealedParsedData.procSendMsgData.data.screenData
                                    )
                                )
                            }

                            is SendMsgDataType.SendListNum -> {
                                onDomainEvent(
                                    event = MainEvent.SendDataEvent(
                                        data = sealedParsedData.procSendMsgData.data.index ?: -1
                                    )
                                )
                            }

                            is SendMsgDataType.ErrorMsgData -> {
                                onDomainEvent(
                                    event = MainEvent.SendDataEvent(
                                        data = sealedParsedData.procSendMsgData.data.notice
                                    )
                                )
                            }
                        }
                    }

                    else -> {
                    }
                }
            }
        }
    }

    fun onDomainEvent(event: MainEvent) {
        when (event) {
            is MainEvent.CloseDomainWindowEvent -> {
                UiState.coroutineScope.launch {
                    UiState.closeDomainWindow()
                    delay(500)
                    onDomainEvent(MainEvent.NoneDomainWindowEvent())
                }

            }

            is MainEvent.NoneDomainWindowEvent -> {
                UiState._domainUiState.update { uiState ->
                    DomainUiState.NoneWindow(event.screenSizeType)
                }
            }

            is MainEvent.ChangeDomainWindowSizeEvent -> {
                /**
                 * [버튼을 클릭하여 직접적으로 버튼 사이즈 조절할 때 사용]
                 * 현재 데이터는 유지한 체 ScreenSizeType 프로퍼티만 변경하기
                 * ==> 즉, 현재 화면에서 직접적으로 화면 크기를 변경하게 할 수 있음
                 * MainUiState에 미리 copyWithNewSizeType()라는 함수를 정의하여 기존 데이터는 유지한 체 screenSizeType 만을 변경하여 사용하도록 함.
                 */
                UiState._domainUiState.update { uiState ->
                    uiState.copyWithNewSizeType(event.screenSizeType)
                }
            }

            is MainEvent.OpenDomainWindowEvent -> {
                UiState.openDomainWindow()
                UiState._domainUiState.update { uiState ->
                    val domainUiState = when (event.domainType) {
                        SealedDomainType.None -> {
                            DomainUiState.NoneWindow()
                        }

                        SealedDomainType.Ptt -> {
                            DomainUiState.PttWindow(
                                domainType = event.domainType,
                                screenType = event.screenType,
                                isError = event.isError,
                                screenSizeType = event.screenSizeType
                            )
                        }

                        SealedDomainType.Help -> {
                            val helpData = event.data as? List<HelpItemData> ?: emptyList()
                            DomainUiState.HelpWindow(
                                domainType = event.domainType,
                                screenType = event.screenType,
                                data = helpData,
                                text = event.domainType.text,
                                screenSizeType = ScreenSizeType.Large
                            )
                        }

                        SealedDomainType.Announce -> {
                            DomainUiState.AnnounceWindow(
                                text = event.data as String
                            )
                        }

                        SealedDomainType.Call -> {
                            val contactList = event.data as? List<Contact> ?: emptyList()
                            Log.d("@@ contactList", "${contactList}")
                            Log.d("@@ event", "${event}")
                            DomainUiState.CallWindow(
                                domainType = event.domainType,
                                screenType = event.screenType,
                                screenSizeType = event.screenSizeType,
                                data = contactList,
                            )
                        }

                        SealedDomainType.SendMessage -> {
                            val eventData = event.data as SendMsgDataType.SendMsgData
                            DomainUiState.SendMessageWindow(
                                domainType = event.domainType,
                                screenType = event.screenType,
                                isError = event.isError,
                                msgData = eventData.msgData as MsgData,
                                screenData = eventData.screenData,
                            )
                        }

                        SealedDomainType.MainMenu -> {
                            DomainUiState.DomainMenuWindow(

                            )
                        }

                        SealedDomainType.Navigation -> {
                            DomainUiState.NavigationWindow(

                            )
                        }

                        SealedDomainType.Radio -> {
                            DomainUiState.RadioWindow(

                            )
                        }

                        else -> {
                            DomainUiState.WeatherWindow(

                            )
                        }
                    }
                    domainUiState
                }
                UiState.pushUiState(UiState.domainUiState.value)
            }

            is MainEvent.ChangeScrollIndexEvent -> {
                Log.d("@@  MainEvent.ChangeScrollIndexEvent", "수행 / ${event.selectedScrollIndex}")
                UiState._domainUiState.update { domainUiState ->
                    domainUiState.copyWithNewScrollIndex(event.selectedScrollIndex)
                }
            }

            is MainEvent.SendDataEvent -> {
                CoroutineScope(Dispatchers.Default).launch {
                    UiState._sendUiData.emit(event.data)
                }
            }
        }
    }

    /**
     * Ptt 이벤트(VR시작, Listen/Speak/Loading 상태를 받았을 때 실행되는 로직 구현)
     */
    fun onPttEvent(event: PttEvent) {
        when (event) {
            is PttEvent.SetListenType -> {
                _domainUiState.update { domainUiState ->
                    (domainUiState as? DomainUiState.PttWindow)?.copy(
                        isError = false,
                        screenType = ScreenType.PttListen
                    ) ?: domainUiState
                }
            }

            is PttEvent.SetSpeakType -> {
                _domainUiState.update { domainUiState ->
                    (domainUiState as? DomainUiState.PttWindow)?.copy(
                        isError = false,
                        screenType = ScreenType.PttSpeak
                    ) ?: domainUiState
                }
            }

            is PttEvent.SetLoadingType -> {
                _domainUiState.update { domainUiState ->
                    (domainUiState as? DomainUiState.PttWindow)?.copy(
                        isError = false,
                        screenType = ScreenType.PttLoading
                    ) ?: domainUiState
                }
            }

            is PttEvent.PreparePtt -> {
                val guideString = pttManager.pttPrepare()

                _domainUiState.update { domainUiState ->
                    (domainUiState as? DomainUiState.PttWindow)?.copy(
                        isError = false,
                        screenType = ScreenType.Prepare,
                        guideText = guideString
                    ) ?: domainUiState
                }
            }

            is PttEvent.StartVR -> {
                val notice = pttManager.checkStarting()
                if (notice != null) {
                    /**
                     * Notice 띄우기
                     */
                    viewModelScope.launch {

                    }

                    onDomainEvent(
                        event = MainEvent.OpenDomainWindowEvent(
                            domainType = SealedDomainType.Ptt,
                            screenType = ScreenType.Prepare,
                            data = notice.noticeString,
                            isError = false,
                            screenSizeType = ScreenSizeType.Small
                        )
                    )
                    _domainUiState.update { domainUiState ->
                        (domainUiState as? DomainUiState.PttWindow)?.copy(
                            isError = false,
                            screenType = ScreenType.Prepare,
                            errorText = notice.noticeString
                        ) ?: domainUiState
                    }
                } else {
                    if (event.selectVRResult == SelectVRResult.PttResult) {
                        /**
                         * Ptt 버튼 클릭
                         */
                        onDomainEvent(
                            event = MainEvent.OpenDomainWindowEvent(
                                domainType = SealedDomainType.Ptt,
                                screenType = ScreenType.Prepare,
                                data = null,
                                isError = false,
                                screenSizeType = ScreenSizeType.Small
                            )
                        )
                    } else {
                        /**
                         * Ptt 버튼 클릭 -> VRResult 생성 이어지는 동작 할 때
                         */
                        viewModelScope.launch {
                            /**
                             * onDomainEvent()을 통해 맨 처음의 화면만을 바꿔주는 역할
                             */
                            onDomainEvent(
                                event = MainEvent.OpenDomainWindowEvent(
                                    domainType = SealedDomainType.Ptt,
                                    screenType = ScreenType.Prepare,
                                    data = "",
                                    isError = false,
                                    screenSizeType = ScreenSizeType.Small
                                )
                            )
                            /**
                             * onPttEvent()을 통해 각 상황에 맞는 로직을 실행하여 각 화면 상태를 나타내주는 역할
                             */
                            onPttEvent(PttEvent.SetLoadingType)
                            delay(1500)
                            onPttEvent(PttEvent.PreparePtt)
                            delay(1500)
                            onPttEvent(PttEvent.SetSpeakType)
                            delay(1500)
                            onPttEvent(PttEvent.SetLoadingType)
                            delay(1500)

                            pttManager.pttEvent(selectVRResult = event.selectVRResult)
                        }
                    }
                }
            }
        }
    }
}