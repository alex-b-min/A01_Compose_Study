package com.example.a01_compose_study.presentation.screen.call

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a01_compose_study.data.custom.SealedParsedData
import com.example.a01_compose_study.data.custom.call.BtCall
import com.example.a01_compose_study.presentation.data.UiState
import com.example.a01_compose_study.presentation.screen.main.DomainUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CallViewModel @Inject constructor(private val btCall: BtCall) : ViewModel() {

    private val _domainUiState = UiState._domainUiState
    val domainUiState: StateFlow<DomainUiState> = UiState.domainUiState

    private val sealedParsedData: SharedFlow<SealedParsedData> = UiState._sealedParsedData

    init {
        viewModelScope.launch {
            sealedParsedData.collect { sealedParsedData ->
                if (sealedParsedData is SealedParsedData.CallData) {

                }
            }
        }
    }

    fun onCallEvent(event: CallEvent) {
        when(event) {
            is  CallEvent.ChangeScrollIndexEvent -> {
                _domainUiState.update { domainUiState ->
                    domainUiState.copyWithNewScrollIndex(event.selectedScrollIndex)!!
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