package com.example.a01_compose_study.presentation.screen.ptt

import androidx.lifecycle.ViewModel
import com.example.a01_compose_study.data.custom.ptt.PttManager
import com.example.a01_compose_study.domain.model.ScreenType
import com.example.a01_compose_study.presentation.data.UiState
import com.example.a01_compose_study.presentation.screen.main.DomainUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class PttViewModel @Inject constructor(
    val pttManager: PttManager
) : ViewModel() {

    private val _domainUiState = UiState._domainUiState

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
                UiState.clearUiState()
            }

            is PttEvent.StartVR -> {
                pttManager.pttEvent()
            }
        }
    }
}

