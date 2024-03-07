package com.example.a01_compose_study.presentation.screen.call

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.a01_compose_study.data.custom.call.BtCall
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
    private val btCall: BtCall,
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
        }
    }

    fun onCallBusinessEvent(event: CallBusinessEvent) {
        when(event) {
            is CallBusinessEvent.Calling -> {
                btCall.outgoingCall("010-1234-5491")
            }
        }
    }
}