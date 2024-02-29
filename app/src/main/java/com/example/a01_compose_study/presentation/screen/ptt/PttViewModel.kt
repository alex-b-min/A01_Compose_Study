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

    val announceString = MutableStateFlow("")
    val vrConfig = MutableStateFlow(VrConfig())
    val guideString = MutableLiveData<String>()


    var onlineRandomCommands = mutableListOf("")
    var offlineRandomCommands = mutableListOf("")

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

            is PttEvent.SetAnnounceType -> {
                _domainUiState.update { domainUiState ->
                    (domainUiState as? DomainUiState.PttWindow)?.copy(
                        screenType = ScreenType.PttAnounce
                    ) ?: domainUiState
                }
            }

            is PttEvent.PreparePtt -> {
                val notice = pttManager.checkStarting()
                if (notice != null) {
                    _domainUiState.update { domainUiState ->
                        (domainUiState as? DomainUiState.PttWindow)?.copy(
                            screenType = ScreenType.PttAnounce,
                            errorText = notice.noticeString
                        ) ?: domainUiState
                    }
                }
                pttManager.pttPrepare()
                UiState.clearUiState()
            }

            is PttEvent.StartVR -> {
                pttManager.pttEvent()
            }
        }
    }
}

