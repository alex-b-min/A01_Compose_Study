package com.example.a01_compose_study.presentation.screen.sendMsg

import androidx.lifecycle.ViewModel
import com.example.a01_compose_study.data.custom.sendMsg.ScreenData
import com.example.a01_compose_study.data.custom.sendMsg.SendMsgEvent
import com.example.a01_compose_study.data.custom.sendMsg.SendMsgManager
import com.example.a01_compose_study.domain.model.ScreenType
import com.example.a01_compose_study.presentation.data.UiState
import com.example.a01_compose_study.presentation.data.UiState.handleScreenData
import com.example.a01_compose_study.presentation.screen.main.DomainUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SendMsgViewModel @Inject constructor(
    val sendMsgManager: SendMsgManager,
) : ViewModel() {
    private val _domainUiState = UiState._domainUiState
    private val sealedParsedData = UiState.sealedParsedData

    init {
        observeSendMsgData()
    }

    fun onSendMsgEvent(event: SendMsgEvent) {
        when (event) {
            is SendMsgEvent.SendMsgListItemOnClick -> {
                val screenData = when (val currentState = _domainUiState.value) {
                    is DomainUiState.SendMessageWindow -> currentState.screenData
                    else -> error("Current state is not SendMessageWindow")
                }

                handleScreenData(screenData, _domainUiState.value)

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
                _domainUiState.update { domainUiState ->
                    val updatedState = (domainUiState as? DomainUiState.SendMessageWindow)?.copy(
                        screenType = ScreenType.SendMessage,
                    ) ?: domainUiState

                    updatedState
                }
            }

            is SendMsgEvent.SayMessageNo -> {
                UiState.popUiState()
            }

            is SendMsgEvent.SendMessageYes -> {
                // BtPhoneAppRun
                sendMsgManager.requestBtPhoneAppRun()
            }

            is SendMsgEvent.SendMessageNo -> {
                UiState.popUiState()
            }
            else -> {

            }
        }
    }

    fun observeSendMsgData(){
        CoroutineScope(Dispatchers.Main).launch {
            UiState._sendMsgUiData.collect { data ->
                handleSendMsgData(data)
            }
        }
    }

    fun handleSendMsgData(sendMsgData: Pair<ScreenType, Any>) {
        val screenType = sendMsgData.first
        val data = sendMsgData.second

        when (screenType){
            is ScreenType.List -> {
                // TODO procListIntention
            }
            is ScreenType.ScreenStack -> {
                val screenData = data as? ScreenData
                if (screenData != null) {
                    handleScreenData(screenData, _domainUiState.value)
                }
            }
            else ->{
                // Reject
            }
        }

    }
}