package com.example.a01_compose_study.presentation.screen.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a01_compose_study.data.HVRError
import com.example.a01_compose_study.data.custom.SealedParsedData
import com.example.a01_compose_study.domain.model.HelpItemData
import com.example.a01_compose_study.domain.model.ScreenType
import com.example.a01_compose_study.domain.model.SealedDomainType
import com.example.a01_compose_study.domain.util.ScreenSizeType
import com.example.a01_compose_study.presentation.data.UiState
import com.example.a01_compose_study.presentation.screen.main.route.VRUiState
import com.example.a01_compose_study.presentation.screen.ptt.VrmwManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val vrmwManager: VrmwManager, // MainViewModel에서 필요한 이유는, 음성인식 결과를 직접적으로 생성하기 위해
) : ViewModel() {

    private val sealedParsedData: SharedFlow<SealedParsedData> = UiState._sealedParsedData

    private val _domainUiState = UiState._domainUiState
    val domainUiState: StateFlow<DomainUiState> = UiState.domainUiState

    private val _domainWindowVisible = UiState._domainWindowVisible
    val domainWindowVisible: StateFlow<Boolean> = UiState.domainWindowVisible

    private val _vrUiState = UiState._VRUiState
    val vrUiState: StateFlow<VRUiState> = UiState.vrUiState

    init {
        collectParsedData()
    }

    private fun collectParsedData() {
        viewModelScope.launch {
            sealedParsedData.collect { sealedParsedData ->
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
                                screenType = ScreenType.PttAnounce,
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

                        SealedDomainType.MainMenu -> {
                            DomainUiState.DomainMenuWindow(

                            )
                        }

                        SealedDomainType.Call -> {
                            DomainUiState.CallWindow(

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
        }
    }

//    fun onVREvent(event: VREvent) {
//        when(event) {
//            is VREvent.ChangeVRUIEvent -> {
//                Log.d("@@ vrUiState 값은?1111", "${event.vrUiState.active} / ${event.vrUiState.isError}")
//                _vrUiState.update { vrUiState ->
//                    event.vrUiState
//                }
//            }
//        }
//    }

    fun closeDomainWindow() {
        _domainWindowVisible.value = false
        UiState.clearUiState()
    }

    fun openDomainWindow() {
        _domainWindowVisible.value = true
    }


}