package com.example.a01_compose_study.presentation.data

import android.util.Log
import com.example.a01_compose_study.data.Contact
import com.example.a01_compose_study.data.custom.SealedParsedData
import com.example.a01_compose_study.data.custom.sendMsg.MsgData
import com.example.a01_compose_study.data.custom.sendMsg.SendMsgDataType
import com.example.a01_compose_study.domain.model.HelpItemData
import com.example.a01_compose_study.domain.model.SealedDomainType
import com.example.a01_compose_study.domain.util.ScreenSizeType
import com.example.a01_compose_study.presentation.screen.main.DomainUiState
import com.example.a01_compose_study.presentation.screen.main.MainEvent
import com.example.a01_compose_study.presentation.screen.main.VREvent
import com.example.a01_compose_study.presentation.screen.main.route.VRUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

object UiState {

    val _sealedParsedData = MutableSharedFlow<SealedParsedData>()
    val sealedParsedData: SharedFlow<SealedParsedData> = _sealedParsedData

    val _domainUiState = MutableStateFlow<DomainUiState>(DomainUiState.NoneWindow())
    val domainUiState: StateFlow<DomainUiState> = _domainUiState

    val _domainWindowVisible = MutableStateFlow<Boolean>(false)
    val domainWindowVisible: StateFlow<Boolean> = _domainWindowVisible

    val _domainUiStateStack = mutableListOf<DomainUiState>()

    val _VRUiState = MutableStateFlow<VRUiState>(VRUiState.PttNone(active = false, isError = false))
    val vrUiState: StateFlow<VRUiState> = _VRUiState

    val coroutineScope = CoroutineScope(Dispatchers.IO)
    fun onDomainEvent(event: MainEvent) {
        when (event) {
            is MainEvent.CloseDomainWindowEvent -> {
                coroutineScope.launch {
                    closeDomainWindow()
                    delay(500)
                    onDomainEvent(MainEvent.NoneDomainWindowEvent())
                }

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
                UiState.pushUiState(domainUiState.value)
            }

            is MainEvent.ChangeScrollIndexEvent -> {
                /**
                 * 발화로 스크롤이 변경되는 환경이라면 CallViewModel의 Event에 있어야 하지만,
                 * 현재 버튼 클릭으로 index 값에 대한 스크롤이 변경되는 환경이라 MainEvent에 임의로 정의해서 사용하고 있는 상황
                 */
                Log.d("@@  MainEvent.ChangeScrollIndexEvent", "수행 / ${event.selectedScrollIndex}")
                _domainUiState.update { domainUiState ->
                    domainUiState.copyWithNewScrollIndex(event.selectedScrollIndex)!!
                }
            }

            is MainEvent.SendDataEvent -> {
                CoroutineScope(Dispatchers.Default).launch {
                    _sendUiData.emit(event.data)
                }
            }
        }
    }

    val _sendUiData = MutableSharedFlow<Any>()
    val sendUiData: SharedFlow<Any> = _sendUiData

    /**
     * 화면을 스택에 쌓음
     */
    fun pushUiState(uiState: DomainUiState) {
        _domainUiStateStack.add(uiState)
        _domainUiStateStack.forEachIndexed { index, domainUiState ->
            Log.d("@@ _domainUiStateStack", "index: $index / data: $domainUiState")
        }
    }

    /**
     * 화면을 스택에 쌓지 않고 변화만 시킴
     */
    fun changeUiState(uiState: DomainUiState) {
        _domainUiState.update { domainUiState ->
            uiState.copyWithNewSizeType(domainUiState.screenSizeType)
        }
    }

    fun popUiState() {
        if (_domainUiStateStack.size > 1) {
            _domainUiStateStack.removeAt(_domainUiStateStack.size - 1)
            _domainUiState.value = _domainUiStateStack.last()
        }
    }

    fun clearUiState() {
        _domainUiStateStack.clear()
    }

    fun onVREvent(event: VREvent) {
        Log.d("@@ vrUiState", "${vrUiState.value.active}")
        when(event) {
            is VREvent.ChangeVRUIEvent -> {
                _VRUiState.update { vrUiState ->
                    event.vrUiState
                }
            }
        }
    }

    fun getCurrDomainUiState(): StateFlow<DomainUiState> {
        return domainUiState
    }

    fun openDomainWindow() {
        _domainWindowVisible.value = true
    }

    fun closeDomainWindow() {
        _domainWindowVisible.value = false
        UiState.clearUiState()
    }
}
