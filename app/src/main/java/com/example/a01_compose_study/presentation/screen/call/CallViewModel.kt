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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
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

    private val _vrProcessingResultState = MutableSharedFlow<VRProcessingResult>()
    val vrProcessingResultState: SharedFlow<VRProcessingResult> = _vrProcessingResultState

    init {
        viewModelScope.launch {
            sealedParsedData.collect { sealedParsedData ->
                Log.d("@@@@ CallModel 생성 후 콜렉", "${sealedParsedData}")
                if (sealedParsedData is SealedParsedData.CallData) {
                    when(sealedParsedData.procCallData) {
                        is ProcCallData.ProcCallNameScreen -> {
                            Log.d("@@ ProcCallNameScreen 호출", "${sealedParsedData.procCallData}")
                        }

                        is ProcCallData.ProcOtherNumberResult -> {
                            _vrProcessingResultState.emit(VRProcessingResult.OtherNumber)
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
            }

            is CallEvent.OnOtherNumberButtonClick -> {
                val currentContact = (domainUiState.value as DomainUiState.CallWindow).detailData
                val matchingContacts = callManager.findContactsByContactId(currentContact)

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
}