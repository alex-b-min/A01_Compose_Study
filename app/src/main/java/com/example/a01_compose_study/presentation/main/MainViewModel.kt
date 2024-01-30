package com.example.a01_compose_study.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a01_compose_study.domain.ScreenType
import com.example.a01_compose_study.domain.SealedDomainType
import com.example.a01_compose_study.domain.model.HelpItemData
import com.example.a01_compose_study.domain.usecase.VRUseCase
import com.example.a01_compose_study.domain.util.ScreenSizeType
import com.example.a01_compose_study.presentation.data.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val VRUsecase: VRUseCase,
) : ViewModel() {

    private val _domainUiState = UiState._domainUiState
    val domainUiState: StateFlow<DomainUiState> = UiState.domainUiState

    private val _vrUiState = UiState._vrUiState
    val vrUiState: StateFlow<VRUiState> = UiState.vrUiState

    private val _domainWindowVisible = UiState._domainWindowVisible
    val domainWindowVisible: StateFlow<Boolean> = UiState.domainWindowVisible

    fun onVREvent(event: VREvent) {
        when (event) {
            is VREvent.CloseVRWindowEvent -> {
                _vrUiState.update { vrUiState ->
                    when (vrUiState) {
                        is VRUiState.VRWindow -> vrUiState.copy(visible = false)
                        else -> VRUiState.NoneWindow
                    }
                }
            }

            is VREvent.CloseAllVRWindowsEvent -> {
                _vrUiState.update { vrUiState ->
                    when (vrUiState) {
                        is VRUiState.VRWindow -> vrUiState.copy(visible = false)
                        else -> VRUiState.NoneWindow
                    }
                }
                closeDomainWindow()
            }

            is VREvent.ChangeVRWindowSizeEvent -> {
                /**
                 * 현재 데이터는 유지한 체 ScreenSizeType 프로퍼티만 변경하기
                 * ==> 즉, 현재 화면에서 직접적으로 화면 크기를 변경하게 할 수 있음
                 * 어차피 vrUiState의 타입이 VRWindow 일 때만 사이즈를 변경 가능할 수 있기에 타입을 지정하여 copy()를 사용함.
                 */
                _vrUiState.update { currentState ->
                    when (currentState) {
                        is VRUiState.VRWindow -> currentState.copy(screenSizeType = event.screenSizeType)
                        else -> currentState
                    }
                }
            }

            is VREvent.OpenVRWindowEvent -> {
                _domainUiState.update { uiState ->
                    DomainUiState.NoneWindow()
                }

                _vrUiState.update { vrUiState ->
                    VRUiState.VRWindow(
                        visible = true,
                        isError = event.isError,
                        text = event.text,
                        screenSizeType = event.screenSizeType
                    )
                }

                viewModelScope.launch {
                    /**
                     * TODO 추후 UseCase()의 결과값을 통해 자동으로 DomainType이 주입 되야함
                     * 현재는 helpUsecase()로부터 값을 받아오기 때문에 도메인 타입이 Help로 고정됨
                     */
                    delay(2000)
                    if (event.isError) {
                        // 에러일때 VR 윈도우 재호출
                        onVREvent(
                            event = VREvent.OpenVRWindowEvent(
                                isError = false,
                                text = "음성 인식 중 입니다...",
                                screenSizeType = ScreenSizeType.Middle
                            )
                        )
                    } else { // 에러가 아닐 때 다음 DomainEvent 발행
                        val helpList = VRUsecase()

                        if (helpList is List<*> && helpList.isNotEmpty()) {
                            onVREvent(VREvent.CloseVRWindowEvent)
                            delay(500)
                            onDomainEvent(
                                event = MainEvent.OpenDomainWindowEvent(
                                    domainType = SealedDomainType.Help,
                                    screenType = ScreenType.HelpList,
                                    data = helpList,
                                    isError = false,
                                    screenSizeType = ScreenSizeType.Large
                                )
                            )
                        }
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
                 * 현재 데이터는 유지한 체 ScreenSizeType 프로퍼티만 변경하기
                 * ==> 즉, 현재 화면에서 직접적으로 화면 크기를 변경하게 할 수 있음
                 * MainUiState에 미리 copyWithNewSizeType()라는 함수를 정의하여 기존 데이터는 유지한 체 screenSizeType 만을 변경하여 사용하도록 함.
                 */
                _domainUiState.update { uiState ->
                    uiState.copyWithNewSizeType(event.screenSizeType)
                }
            }

            is MainEvent.OpenDomainWindowEvent -> {
                _domainWindowVisible.value = true
                _domainUiState.update { uiState ->
                    val domainUiState = when (event.domainType) {
                        SealedDomainType.None -> {
                            DomainUiState.NoneWindow()
                        }

                        SealedDomainType.Help -> {
                            val helpData = event.data as? List<HelpItemData> ?: emptyList()
                            DomainUiState.HelpWindow(
                                domainType = event.domainType,
                                screenType = event.screenType,
                                data = helpData,
                                visible = true,
                                text = "HelpWindow",
                                screenSizeType = ScreenSizeType.Large
                            )
                        }

                        SealedDomainType.Announce -> {
                            DomainUiState.AnnounceWindow(

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
            }
        }
    }

    fun closeVRWindow() {
        // 현재의 error 상태에 따른 glow 애니메이션창을 내려야하기 때문에 isError에 uiState.isError로 설정
        if (vrUiState.value is VRUiState.VRWindow) {
            _vrUiState.update { vrUiState ->
                (vrUiState as? VRUiState.VRWindow)?.copy(
                    visible = false,
                    isError = vrUiState.isError
                ) ?: vrUiState
            }
        }
        // VRWindow의 닫기 버튼을 클릭한다면 DomainWindow도 닫혀야 된다고 생각하고 아래의 코드 추가
        closeDomainWindow()
        _domainUiState.update { uiState ->
            DomainUiState.NoneWindow()
        }
    }

    fun closeDomainWindow() {
        _domainWindowVisible.value = false
    }

    fun openDomainWindow() {
        _domainWindowVisible.value = true
    }
}
