package com.example.a01_compose_study.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a01_compose_study.domain.ScreenType
import com.example.a01_compose_study.domain.SealedDomainType
import com.example.a01_compose_study.domain.usecase.HelpUsecase
import com.example.a01_compose_study.domain.util.ScreenSizeType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val helpUsecase: HelpUsecase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<MainUiState>(MainUiState.NoneWindow())
    val uiState: StateFlow<MainUiState> = _uiState

    private val _vrUiState = MutableStateFlow<VRUiState>(VRUiState.NoneWindow)
    val vrUiState: StateFlow<VRUiState> = _vrUiState

    private val _domainWindowVisible = MutableStateFlow<Boolean>(false)
    val domainWindowVisible: StateFlow<Boolean> = _domainWindowVisible

    fun onVREvent(event: VREvent) {
        when (event) {
            is VREvent.CloseVRWindowEvent -> {
                _vrUiState.update { visible ->
                    VRUiState.NoneWindow
                }
                closeDomainWindow()
            }

            is VREvent.OpenVRWindowEvent -> {
                _uiState.update { uiState ->
                    MainUiState.NoneWindow()
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
                    delay(2500)
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
                        val helpList = helpUsecase()
                        if (helpList.isNotEmpty()) {
                            viewModelScope.launch {
                                _vrUiState.update { visible ->
                                    VRUiState.NoneWindow
                                }
                                closeVRWindow()

                                delay(500)
                                openDomainWindow()
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
    }

    fun onDomainEvent(event: MainEvent) {
        when (event) {
            is MainEvent.CloseDomainWindowEvent -> {
                closeDomainWindow()
            }

            is MainEvent.NoneDomainWindowEvent -> {
                _uiState.update { uiState ->
                    MainUiState.NoneWindow(event.screenSizeType)
                }
            }

            is MainEvent.OpenDomainWindowEvent -> {
                _uiState.update { uiState ->
                    val mainUiState = when (event.domainType) {
                        SealedDomainType.None -> {
                            MainUiState.NoneWindow()
                        }

                        SealedDomainType.Help -> {
                            MainUiState.HelpWindow(
                                domainType = event.domainType,
                                screenType = event.screenType,
                                data = event.data,
                                visible = true,
                                text = "HelpWindow",
                                screenSizeType = ScreenSizeType.Large
                            )
                        }

                        SealedDomainType.Announce -> {
                            MainUiState.AnnounceWindow(

                            )
                        }

                        SealedDomainType.MainMenu -> {
                            MainUiState.MainMenuWindow(

                            )
                        }

                        SealedDomainType.Call -> {
                            MainUiState.CallWindow(

                            )
                        }

                        SealedDomainType.Navigation -> {
                            MainUiState.NavigationWindow(

                            )
                        }

                        SealedDomainType.Radio -> {
                            MainUiState.RadioWindow(

                            )
                        }

                        else -> {
                            MainUiState.WeatherWindow(

                            )
                        }
                    }
                    mainUiState
                }
            }
        }
    }

    fun closeVRWindow() {
        // 현재의 error 상태에 따른 glow 애니메이션창을 내려야하기 때문에 isError에 uiState.isError로 설정
        if (_vrUiState.value is VRUiState.VRWindow) {
            _vrUiState.update { vrUiState ->
                (vrUiState as? VRUiState.VRWindow)?.copy(
                    visible = false,
                    isError = vrUiState.isError
                ) ?: vrUiState
            }
        }
        _domainWindowVisible.value = false
        _uiState.update { uiState ->
            MainUiState.NoneWindow()
        }
    }

    fun closeDomainWindow() {
        _domainWindowVisible.value = false
    }

    fun openDomainWindow() {
        _domainWindowVisible.value = true
    }
}
