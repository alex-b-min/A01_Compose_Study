package com.example.a01_compose_study.data.custom.sendMsg

import androidx.lifecycle.ViewModel
import com.example.a01_compose_study.data.custom.SealedParsedData
import com.example.a01_compose_study.presentation.data.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SendMsgViewModel @Inject constructor() : ViewModel(){
    private val _domainUiState = UiState._domainUiState
    private val sealedParsedData = UiState.sealedParsedData

    fun onSendMsgEvent(event: SendMsgEvent) {
        when(event) {
            is SendMsgEvent.SendMsgAllListItemOnClick -> {
//                _domainUiState.update { domainUiState ->
//                    val updatedState = (domainUiState as? DomainUiState.)
//                }

            }
            is SendMsgEvent.SendMsgNameListItemOnClick ->{

            }
            is SendMsgEvent.SendMsgCategoryListItemOnClick->{

            }
            is SendMsgEvent.OnBack->{
                UiState.popUiState()
            }
            is SendMsgEvent.SayMessage ->{

            }
            is SendMsgEvent.SayMessageNo ->{

            }
            is SendMsgEvent.SendMessage ->{

            }
            is SendMsgEvent.SendMessageYes ->{

            }
            is SendMsgEvent.SendMessageNo ->{

            }
        }

    }
}