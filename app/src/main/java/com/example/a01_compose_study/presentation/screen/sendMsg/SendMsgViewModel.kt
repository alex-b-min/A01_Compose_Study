package com.example.a01_compose_study.presentation.screen.sendMsg

import androidx.lifecycle.ViewModel
import com.example.a01_compose_study.data.custom.sendMsg.SendMsgEvent
import com.example.a01_compose_study.data.custom.sendMsg.SendMsgManager
import com.example.a01_compose_study.domain.model.ScreenType
import com.example.a01_compose_study.presentation.data.UiState
import com.example.a01_compose_study.presentation.screen.main.DomainUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SendMsgViewModel @Inject constructor(
    val sendMsgManager: SendMsgManager,
) : ViewModel() {
    private val _domainUiState = UiState._domainUiState
    private val sealedParsedData = UiState.sealedParsedData

    fun onSendMsgEvent(event: SendMsgEvent) {
        when (event) {
            is SendMsgEvent.SendMsgListItemOnClick -> {
                val screenData = when (val currentState = _domainUiState.value) {
                    is DomainUiState.SendMessageWindow -> currentState.screenData
                    else -> error("Current state is not SendMessageWindow")
                }

                sendMsgManager.handleScreenData(screenData, _domainUiState.value)

                _domainUiState.update { domainUiState ->
                    val updatedState = (domainUiState as? DomainUiState.SendMessageWindow)?.copy(
                        screenType = ScreenType.SayMessage,
                    ) ?: domainUiState

                    updatedState
                }
            }

            is SendMsgEvent.OnBack -> {
                UiState.popUiState()
            }

            is SendMsgEvent.SayMessage -> {
                // startVR & sendMessage 화면 전환
            }

            is SendMsgEvent.SayMessageNo -> {
                // POP
            }

            is SendMsgEvent.SendMessage -> {
            }

            is SendMsgEvent.SendMessageYes -> {
                // BtPhoneAppRun
            }

            is SendMsgEvent.SendMessageNo -> {
                // POP
            }
        }
    }
}