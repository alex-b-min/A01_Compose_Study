package com.example.a01_compose_study.presentation.screen.call

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a01_compose_study.data.custom.call.BtCall
import com.example.a01_compose_study.presentation.data.UiState
import com.example.a01_compose_study.presentation.screen.main.DomainUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CallViewModel @Inject constructor(private val btCall: BtCall) : ViewModel() {

    private val _domainUiState = UiState._domainUiState
    val domainUiState: StateFlow<DomainUiState> = UiState.domainUiState

    fun onCallEvent(event: CallEvent) {
        when(event) {
            is  CallEvent.ChangeScrollIndexEvent -> {
                _domainUiState.update { domainUiState ->
                    domainUiState.copyWithNewScrollIndex(event.selectedScrollIndex)!!
                }
            }

            is CallEvent.Calling -> {
                Log.d("@@ Calling 일때의 domainUiState", "${domainUiState.value}")
                if (domainUiState.value is DomainUiState.CallWindow) {
                    val selectedIndex = (domainUiState.value as DomainUiState.CallWindow).scrollIndex
                    val contactList = (domainUiState.value as DomainUiState.CallWindow).data
                    if (selectedIndex != null && selectedIndex - 1 in contactList.indices) {
                        viewModelScope.launch {
                            // 2. 유효한 경우 해당 인덱스의 Contact 객체를 가져옵니다.
                            val selectedContact = contactList[selectedIndex - 1]

                            // 3. 가져온 Contact 객체에서 phoneNumber 값을 사용합니다.
                            val phoneNumber = selectedContact.number

                            // onCalling 함수에 phoneNumber를 전달합니다.
                            btCall.outgoingCall(phoneNumber)
                        }
                    }
                }
            }
        }
    }
}