package com.example.a01_compose_study.presentation.screen.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a01_compose_study.data.Contact
import com.example.a01_compose_study.data.HVRError
import com.example.a01_compose_study.data.custom.MWContext
import com.example.a01_compose_study.data.custom.SealedParsedData
import com.example.a01_compose_study.data.custom.call.ProcCallData
import com.example.a01_compose_study.data.custom.sendMsg.ScreenData
import com.example.a01_compose_study.data.custom.sendMsg.SendMsgDataType
import com.example.a01_compose_study.domain.model.HelpItemData
import com.example.a01_compose_study.domain.model.ScreenType
import com.example.a01_compose_study.domain.model.SealedDomainType
import com.example.a01_compose_study.domain.util.ScreenSizeType
import com.example.a01_compose_study.presentation.data.UiState
import com.example.a01_compose_study.presentation.data.UiState._sendMsgUiData
import com.example.a01_compose_study.presentation.screen.main.route.VRUiState
import com.example.a01_compose_study.presentation.screen.ptt.VrmwManager
import com.example.a01_compose_study.presentation.screen.sendMsg.SendMsgViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val application: Application,
    val vrmwManager: VrmwManager, // MainViewModel에서 필요한 이유는, 음성인식 결과를 직접적으로 생성하기 위해
) : ViewModel() {

    private val sealedParsedData: SharedFlow<SealedParsedData> = UiState._sealedParsedData

    private val _domainUiState = UiState._domainUiState
    val domainUiState: StateFlow<DomainUiState> = UiState.domainUiState

    private val _mwContext = UiState._mwContext
    val mwContext: StateFlow<MWContext?> = UiState.mwContext

    private val _domainWindowVisible = UiState._domainWindowVisible
    val domainWindowVisible: StateFlow<Boolean> = UiState.domainWindowVisible

    private val _vrUiState = UiState._VRUiState
    val vrUiState: StateFlow<VRUiState> = UiState.vrUiState

    private val _sendMsgData = UiState._sendMsgUiData
    val sendMsgData: SharedFlow<Any> = _sendMsgData

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
                                mwContext = sealedParsedData.procHelpData.mwContext,
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
                                mwContext = sealedParsedData.procHelpData.mwContext,
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
                                Log.d(
                                    "@@ ProcCallData",
                                    "ListTTSRequest - Prompt ID: ${sealedParsedData.procCallData.promptId}"
                                )
                            }

                            is ProcCallData.NoticeTTSRequest -> {
                                Log.d(
                                    "@@ ProcCallData",
                                    "NoticeTTSRequest - Notice Model: ${sealedParsedData.procCallData.noticeModel}"
                                )
                            }

                            is ProcCallData.ProcCallNameScreen -> {
                                Log.d(
                                    "@@ ProcCallData",
                                    "ProcCallNameScreen - Contact: ${sealedParsedData.procCallData.data}"
                                )
                            }

                            is ProcCallData.RecognizedContactListScreen -> {
                                Log.d(
                                    "@@ ProcCallData",
                                    "ContactListScreen - Contact List: ${sealedParsedData.procCallData.data}"
                                )
                                onDomainEvent(
                                    event = MainEvent.OpenDomainWindowEvent(
                                        domainType = sealedParsedData.procCallData.sealedDomainType,
                                        screenType = sealedParsedData.procCallData.screenType,
                                        mwContext = sealedParsedData.procCallData.mwContext,
                                        data = sealedParsedData.procCallData.data,
                                        isError = false,
                                        screenSizeType = sealedParsedData.procCallData.screenSizeType,
                                    )
                                )
                            }

                            is ProcCallData.AllContactListScreen -> {
                                Log.d(
                                    "@@ ProcCallData",
                                    "FullContactListScreen - Full Contact List: ${sealedParsedData.procCallData.data}"
                                )
                            }

                            is ProcCallData.ScrollIndex -> {
                                if (sealedParsedData.procCallData.index != null) {
                                    onDomainEvent(MainEvent.ChangeScrollIndexEvent(sealedParsedData.procCallData.index))
                                }
                                Log.d(
                                    "@@ ProcCallData",
                                    "ScrollIndex - Index: ${sealedParsedData.procCallData.index}"
                                )
                            }

                            is ProcCallData.ProcYesNoOtherNumberResult -> {
                                Log.d(
                                    "@@ ProcCallData",
                                    "YesNoOtherNumberResultProc - Result: ${sealedParsedData.procCallData.callYesNoOtherNumberResult}"
                                )
                            }
                        }
                    }

                    is SealedParsedData.SendMsgData -> {
                        when (sealedParsedData.procSendMsgData.data) {
                            is SendMsgDataType.SendMsgData -> {
                                Log.d(
                                    "sendMsg",
                                    "ProcSendMsgData.SendMsgData - data: ${sealedParsedData.procSendMsgData.data}"
                                )
                                Log.d("sendMsg", "MVM SealedParsedData.msgData: {${sealedParsedData.procSendMsgData.data.msgData?.msg}}")
                                UiState.onVREvent(
                                    VREvent.ChangeVRUIEvent(
                                        VRUiState.PttLoading(
                                            active = true,
                                            isError = false
                                        )
                                    )
                                )
                                onDomainEvent(
                                    event = MainEvent.OpenDomainWindowEvent(
                                        domainType = sealedParsedData.procSendMsgData.domainType,
                                        screenType = sealedParsedData.procSendMsgData.screenType,
                                        mwContext = sealedParsedData.procSendMsgData.mwContext,
                                        data = sealedParsedData.procSendMsgData.data,
                                        isError = false,
                                        screenData = sealedParsedData.procSendMsgData.data.screenData,
                                        screenSizeType = ScreenSizeType.Large
                                    )
                                )
                            }

                            is SendMsgDataType.SendScreenData -> {
                                Log.d(
                                    "sendMsg",
                                    "ProcSendMsgData.SendMsgData - data: ${sealedParsedData.procSendMsgData.data}"
                                )
                                onDomainEvent(
                                    event = MainEvent.SendDataEvent(
                                        domainType = sealedParsedData.procSendMsgData.domainType,
                                        screenType = sealedParsedData.procSendMsgData.screenType,
                                        data = sealedParsedData.procSendMsgData.data.screenData
                                    )
                                )
                            }

                            is SendMsgDataType.SendListNum -> {
                                Log.e(
                                    "sendMsg",
                                    "ProcSendMsgData.SendMsgData - data: ${sealedParsedData.procSendMsgData.data}"
                                )
                                onDomainEvent(
                                    event = MainEvent.SendDataEvent(
                                        domainType = SealedDomainType.SendMessage,
                                        screenType = sealedParsedData.procSendMsgData.screenType,
                                        data = sealedParsedData.procSendMsgData.data.index ?: -1
                                    )
                                )
                            }

                            is SendMsgDataType.ErrorMsgData -> {
                                Log.d(
                                    "sendMsg",
                                    "ProcSendMsgData.SendMsgData - data: ${sealedParsedData.procSendMsgData.data}"
                                )
                                onDomainEvent(
                                    event = MainEvent.SendDataEvent(
                                        domainType = SealedDomainType.SendMessage,
                                        screenType = sealedParsedData.procSendMsgData.screenType,
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
                closeDomainWindow()
            }

            is MainEvent.NoneDomainWindowEvent -> {
                _domainUiState.update { uiState ->
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
                _domainUiState.update { uiState ->
                    uiState.copyWithNewSizeType(event.screenSizeType)
                }
            }

            is MainEvent.OpenDomainWindowEvent -> {
                openDomainWindow()
                _domainUiState.update { uiState ->
                    val domainUiState = when (event.domainType) {
                        SealedDomainType.None -> {
                            DomainUiState.NoneWindow()
                        }

                        SealedDomainType.Ptt -> {
                            DomainUiState.PttWindow(
                                domainType = event.domainType,
                                screenType = event.screenType,
                                isError = event.isError,
                                errorText = event.data as String,
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
                            Log.d("sendMsg", "MVM contactList: {${eventData.contacts}}")
                            Log.d("sendMsg", "MVM eventData.msgData: {${eventData.msgData?.msg}}")
                            UiState.isVrActive.value = true
                            Log.d("isVrActive", "MVM.OpenDomainWindowEvent")
                            DomainUiState.SendMessageWindow(
                                domainType = event.domainType,
                                screenType = event.screenType,
                                isError = event.isError,
                                msgData = eventData.msgData,
                                contactList = eventData.contacts,
                                screenData = eventData.screenData,
                                screenSizeType = ScreenSizeType.Large
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
                if (event.screenData != ScreenData.CHANGE)
                    UiState.pushUiState(Pair(domainUiState.value, mwContext.value))
            }

            is MainEvent.ChangeScrollIndexEvent -> {
                Log.d("@@  MainEvent.ChangeScrollIndexEvent", "수행 / ${event.selectedScrollIndex}")
                _domainUiState.update { domainUiState ->
                    domainUiState.copyWithNewScrollIndex(event.selectedScrollIndex)!!
                }
            }

            is MainEvent.SendDataEvent -> {
                when (event.domainType) {
                    is SealedDomainType.SendMessage -> {
                        viewModelScope.launch {
                            _sendMsgUiData.emit(event.screenType to event.data)
                            Log.d("sendMsg", "수행 /${event.screenType} ,${event.data}")
                            Log.d("sendMsg", "_sendMsgData: ${_sendMsgData}")
                        }
                    }

                    else -> {

                    }
                }
            }
        }
    }

    fun closeDomainWindow() {
        _domainWindowVisible.value = false
        UiState.clearUiState()
    }

    fun openDomainWindow() {
        _domainWindowVisible.value = true
    }
}