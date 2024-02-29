package com.example.a01_compose_study.presentation.screen.ptt

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a01_compose_study.data.VrConfig
import com.example.a01_compose_study.data.custom.ptt.PttManager
import com.example.a01_compose_study.domain.model.ScreenType
import com.example.a01_compose_study.domain.model.SealedDomainType
import com.example.a01_compose_study.domain.util.ScreenSizeType
import com.example.a01_compose_study.presentation.data.UiState
import com.example.a01_compose_study.presentation.data.UiState.onDomainEvent
import com.example.a01_compose_study.presentation.screen.main.DomainUiState
import com.example.a01_compose_study.presentation.screen.main.MainEvent
import com.example.a01_compose_study.presentation.screen.SelectVRResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PttViewModel @Inject constructor(
    val pttManager: PttManager,
) : ViewModel() {

    private val _domainUiState = UiState._domainUiState
    val announceString = pttManager.announceString
    val vrConfig = MutableStateFlow(VrConfig())
    val guideString = MutableLiveData<String>()


    var onlineRandomCommands = mutableListOf("")
    var offlineRandomCommands = mutableListOf("")

    /**
     * DomainUiState-PttWindow 타입을 유지한 채 Ptt의 상태에 따라 screenType만을 변경하여 UI 구성
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
                        screenType = ScreenType.PttPrepare,
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
                    onDomainEvent(
                        event = MainEvent.OpenDomainWindowEvent(
                            domainType = SealedDomainType.Ptt,
                            screenType = ScreenType.PttPrepare,
                            data = notice.noticeString,
                            isError = false,
                            screenSizeType = ScreenSizeType.Small
                        )
                    )
                } else {
                    if (event.selectVRResult == SelectVRResult.PttResult) {
                        /**
                         * Ptt 버튼 클릭
                         */
                        onDomainEvent(
                            event = MainEvent.OpenDomainWindowEvent(
                                domainType = SealedDomainType.Ptt,
                                screenType = ScreenType.PttPrepare,
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
                                    screenType = ScreenType.PttPrepare,
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

