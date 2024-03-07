package com.example.a01_compose_study.presentation.screen.call

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.a01_compose_study.data.custom.call.CallManager
import com.example.a01_compose_study.domain.model.ScreenType
import com.example.a01_compose_study.presentation.data.UiState
import com.example.a01_compose_study.presentation.screen.main.DomainUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class CallViewModel @Inject constructor(
    private val callManager: CallManager,
) : ViewModel() {

    private val _domainUiState = UiState._domainUiState
    private val _domainWindowVisible = UiState._domainWindowVisible

    fun onCallEvent(event: CallEvent) {
        when(event) {
            is  CallEvent.OnCallBack -> {
            }

            is CallEvent.ContactListItemOnClick -> {
                val isContactNameUnique = callManager.isContactNameUnique(event.selectedContactItem)
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

            is CallEvent.OnOtherNameButtonClick -> {
                val currentContact = event.currentContact
                val matchingContacts = callManager.findContactsByName(currentContact)

                Log.d("@@@@ OnOtherNameButtonClick", "${matchingContacts}")
                if (matchingContacts.size == 2) {
                    val differentContact = matchingContacts.find { it.number != currentContact.number }
                    Log.d("@@@@ differentContact", "${differentContact}")

                    /**
                     * 현재 화면 데이터를 사용하여 일부 속성만을 업데이트하는 하여 UI를 변경하는 상황인데 무한 재구성이 발생하는 이슈가 생김..
                     */
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
                            detailData = event.currentContact,
                        ) ?: domainUiState
                        UiState.pushUiStateMwContext(pairUiStateMwContext = Pair(first = updatedState, second = null))
                        updatedState
                    }
                }
            }
        }
    }

    private fun onCallBusinessEvent(event: CallBusinessEvent) {
        when(event) {
            is CallBusinessEvent.Calling -> {
                callManager.makeCall(event.phoneNumber)
            }
        }
    }
}