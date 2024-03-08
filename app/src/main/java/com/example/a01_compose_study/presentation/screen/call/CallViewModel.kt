package com.example.a01_compose_study.presentation.screen.call

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a01_compose_study.data.custom.SealedParsedData
import com.example.a01_compose_study.data.custom.call.CallManager
import com.example.a01_compose_study.data.custom.call.ProcCallData
import com.example.a01_compose_study.domain.model.ScreenType
import com.example.a01_compose_study.presentation.data.UiState
import com.example.a01_compose_study.presentation.data.UiState.sealedParsedData
import com.example.a01_compose_study.presentation.screen.main.DomainUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CallViewModel @Inject constructor(
    private val callManager: CallManager,
) : ViewModel() {

    private val _domainUiState = UiState._domainUiState
    val domainUiState: StateFlow<DomainUiState> = UiState._domainUiState

    private val _vrProcessingResultState = MutableStateFlow<VRProcessingResult>(VRProcessingResult.None)
    val vrProcessingResultState: StateFlow<VRProcessingResult> = _vrProcessingResultState

    init {
        viewModelScope.launch {
            sealedParsedData.collect { sealedParsedData ->
                Log.d("@@@@ CallModel 생성 후 콜렉", "${sealedParsedData}")
                if (sealedParsedData is SealedParsedData.CallData) {
                    when(sealedParsedData.procCallData) {

                        is ProcCallData.ProcYesResult -> {
                            _vrProcessingResultState.update { currResult ->
                                VRProcessingResult.Yes
                            }
                        }

                        is ProcCallData.ProcNoResult -> {
                            _vrProcessingResultState.update { currResult ->
                                VRProcessingResult.No
                            }
                        }

                        is ProcCallData.ProcOtherNumberResult -> {
                            _vrProcessingResultState.update { currResult ->
                                VRProcessingResult.OtherNumber
                            }
                        }

                        else -> {
                            Log.d("@@ else 호출", "${sealedParsedData.procCallData}")
                        }
                    }
                }
            }
        }
    }

    /**
     * 실제로 UI와 인터렉션이 발생했을때 사용하는 이벤트
     * ( 아래의 onCallBusinessEvent()를 사용하여 로직을 구성 )
     */
    fun onCallEvent(event: CallEvent) {
        when(event) {
            is  CallEvent.OnCallBack -> {
            }

            is CallEvent.ContactListItemOnClick -> {
                val isContactNameUnique = callManager.isContactIdUnique(event.selectedContactItem)
                Log.d("@@@@ isContactNameUnique 결과값", "${isContactNameUnique}")
                _domainUiState.update { domainUiState ->
                    val updatedState = (domainUiState as? DomainUiState.CallWindow)?.copy(
                        screenType = ScreenType.CallYesNo,
                        detailData = event.selectedContactItem,
                        isContactNameUnique = isContactNameUnique
                    ) ?: domainUiState
                    UiState.pushUiStateMwContext(pairUiStateMwContext = Pair(first = updatedState, second = null))
                    updatedState
                }
            }

            is CallEvent.OnYesButtonClick -> {
                onCallBusinessEvent(CallBusinessEvent.Calling(phoneNumber = event.phoneNumber))
                _vrProcessingResultState.update { currResult ->
                    VRProcessingResult.None
                }
            }

            is CallEvent.OnOtherNumberButtonClick -> {
                val currentContact = (domainUiState.value as DomainUiState.CallWindow).detailData
                val matchingContacts = callManager.findContactsByContactId(currentContact)

                /**
                 * 일치하는 cotact_id가 2개라면 다른 번호로 교체
                 */
                if (matchingContacts.size == 2) {
                    val differentContact = matchingContacts.find { it.number != currentContact.number }
                    _domainUiState.update { domainUiState ->
                        val updatedState = differentContact?.let {
                            (domainUiState as? DomainUiState.CallWindow)?.copy(
                                detailData = it,
                            )
                        } ?: domainUiState
                        UiState.pushUiStateMwContext(pairUiStateMwContext = Pair(first = updatedState, second = null))
                        updatedState
                    }
                } else {
                    /**
                     * 일치하는 cotact_id가 2개가 아니라면(3개 이상) 동일한 contact_id를 가진 연락처 목록을 띄워줌
                     */
                    _domainUiState.update { domainUiState ->
                        val updatedState = (domainUiState as? DomainUiState.CallWindow)?.copy(
                            data = matchingContacts,
                            screenType = ScreenType.CallIndexedList,
                            detailData = domainUiState.detailData,
                        ) ?: domainUiState
                        UiState.pushUiStateMwContext(pairUiStateMwContext = Pair(first = updatedState, second = null))
                        updatedState
                    }
                }
            }
        }
        /**
         * 음성인식 이벤트를 발생시키는 State 초기화
          */
        clearVRProcessingResultState()
    }

    /**
     * 구체적인 Call 비즈니스 로직을 발생시키는 이벤트
     */
    private fun onCallBusinessEvent(event: CallBusinessEvent) {
        when(event) {
            is CallBusinessEvent.Calling -> {
                callManager.makeCall(event.phoneNumber)
            }
        }
    }

    /**
     * 음성인식에 대한 이벤트를 발생시켰다면 해당 이벤트를 초기화를 해줘야함
     * ==> 왜냐하면 초기화를 시키지 않으면 계속 값을 들고 있기 때문에 해당 이벤트를 계속 발생시킴
     */
    private fun clearVRProcessingResultState() {
        _vrProcessingResultState.update { currResult ->
            VRProcessingResult.None
        }
    }
}