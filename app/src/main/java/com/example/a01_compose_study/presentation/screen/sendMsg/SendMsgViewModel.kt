package com.example.a01_compose_study.presentation.screen.sendMsg

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.a01_compose_study.data.custom.SealedParsedData
import com.example.a01_compose_study.data.custom.sendMsg.ScreenData
import com.example.a01_compose_study.data.custom.sendMsg.SendMsgEvent
import com.example.a01_compose_study.data.custom.sendMsg.SendMsgManager
import com.example.a01_compose_study.data.pasing.SendMsgModel
import com.example.a01_compose_study.domain.model.ScreenType
import com.example.a01_compose_study.presentation.data.UiState
import com.example.a01_compose_study.presentation.data.UiState._isVrInput
import com.example.a01_compose_study.presentation.data.UiState._mwContext
import com.example.a01_compose_study.presentation.data.UiState.handleScreenData
import com.example.a01_compose_study.presentation.data.UiState.popUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SendMsgViewModel @Inject constructor(
    private val job: CoroutineScope,
    val sendMsgManager: SendMsgManager,
) : ViewModel() {
    private val _domainUiState = UiState._domainUiState
    private val sealedParsedData = UiState.sealedParsedData

    private var _lineIndex = MutableStateFlow(0)
    val lineIndex: StateFlow<Int> = _lineIndex

    init {
        Log.d("sendMsg", "SendMsgViewModel 생성됌")
        observeSendMsgData()
    }

    fun onSendMsgEvent(event: SendMsgEvent) {
        when (event) {
            // 카테고리 여러개인지 확인 로직
            is SendMsgEvent.MsgAllListItemOnClick -> {
                // TODO sendMsgModel 받아오는 로직
                Log.e("sendMsg", "onSendMsgEvent MsgAllListItemOnClick 안")
                val sendMsgModel = SendMsgModel("")
                sendMsgModel.getContactItems().add(event.selectedSendMsgItem)
                val bundle = sendMsgManager.handleCategory(
                    sendMsgModel = sendMsgModel,
                    contactId = event.selectedSendMsgItem.contact_id
                )
                job.launch {
                    UiState._sealedParsedData.emit(SealedParsedData.SendMsgData(bundle))
                }
            }

            is SendMsgEvent.SelectNameListItemOnClick -> {
                // TODO sendMsgModel 받아오는 로직
                //카테고리 여러개인지 매니저 로직 (위와 동일)
                Log.e("sendMsg", "아이템 clickEvent 실행}")
                val sendMsgModel = SendMsgModel("")
                sendMsgModel.getContactItems().add(event.selectedSendMsgItem)
                sendMsgModel.messageValue = "hello"
                val bundle = sendMsgManager.handleCategory(
                    sendMsgModel = sendMsgModel,
                    contactId = event.selectedSendMsgItem.contact_id
                )
                job.launch {
                    UiState._sealedParsedData.emit(SealedParsedData.SendMsgData(bundle))
                }
            }

            is SendMsgEvent.SelectCategoryListItemOnClick -> {
                // TODO sendMsgModel 받아오는 로직
                // 메세지 유무에 따라 화면 전환
                val sendMsgModel = SendMsgModel("")
                sendMsgModel.getContactItems().add(event.selectedSendMsgItem)
                sendMsgModel.messageValue = "hello"
                val bundle = sendMsgManager.handleMsgExistence(sendMsgModel)

                job.launch {
                    UiState._sealedParsedData.emit(SealedParsedData.SendMsgData(bundle))
                }
            }

            is SendMsgEvent.OnBack -> {
                popUiState()
            }


            is SendMsgEvent.SayMessageNo -> {
                // 버튼 터치 이벤트
                popUiState()
                // clearMsg 작업
            }

            is SendMsgEvent.SendMessageYes -> {
                // BtPhoneAppRun
                sendMsgManager.requestBtPhoneAppRun()
            }

        }
    }

    private fun observeSendMsgData() {
        Log.d("sendMsg", "observeSendMsgData 안")

        CoroutineScope(Dispatchers.Main).launch {
            UiState._sendMsgUiData.collect { data ->
                Log.d("sendMsg", "collect 함")
                handleSendMsgData(data)
            }
        }
    }


    private fun handleSendMsgData(sendMsgData: Pair<ScreenType, Any>) {
        val screenType = sendMsgData.first
        val data = sendMsgData.second
        Log.d("sendMsg", "handleSendMsgData:${screenType},${data}")

        when (screenType) {
            is ScreenType.List -> {
                _lineIndex.update { data as Int }
                _isVrInput.update { true }
                Log.e("sendMsg", "lineIndex:${_lineIndex}")
            }

            is ScreenType.ScreenStack -> {
                Log.d("sendMsg", "handleSendMsgData ScreenType.ScreenStack")
                val screenData = data as? ScreenData
                if (screenData != null) {
                    handleScreenData(
                        screenData,
                        Pair(UiState._domainUiState.value, _mwContext.value)
                    )
                }
            }

            else -> {
                Log.d("sendMsg", "handleSendMsgData Reject")
                // Reject
            }
        }
    }
}