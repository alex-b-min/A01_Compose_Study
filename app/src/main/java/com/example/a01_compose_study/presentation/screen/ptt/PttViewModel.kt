package com.example.a01_compose_study.presentation.screen.ptt

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.a01_compose_study.data.VrConfig
import com.example.a01_compose_study.data.custom.ptt.PttManager
import com.example.a01_compose_study.domain.model.ScreenType
import com.example.a01_compose_study.presentation.data.UiState
import com.example.a01_compose_study.presentation.screen.main.DomainUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class PttViewModel @Inject constructor(
    val pttManager: PttManager
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
                    _domainUiState.update { domainUiState ->
                        (domainUiState as? DomainUiState.PttWindow)?.copy(
                            isError = false,
                            screenType = ScreenType.PttPrepare,
                            errorText = notice.noticeString
                        ) ?: domainUiState
                    }
                } else {
                    pttManager.pttEvent()
                }
            }
        }
    }
}

