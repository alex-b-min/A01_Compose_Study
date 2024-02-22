package com.example.a01_compose_study.presentation.data

import android.util.Log
import com.example.a01_compose_study.data.custom.SealedParsedData
import com.example.a01_compose_study.presentation.screen.main.DomainUiState
import com.example.a01_compose_study.presentation.screen.main.VREvent
import com.example.a01_compose_study.presentation.screen.main.route.VRUiState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

object UiState {

    val _sealedParsedData = MutableSharedFlow<SealedParsedData>()
    val sealedParsedData: SharedFlow<SealedParsedData> = _sealedParsedData

    val _domainUiState = MutableStateFlow<DomainUiState>(DomainUiState.NoneWindow())
    val domainUiState: StateFlow<DomainUiState> = _domainUiState

    val _domainWindowVisible = MutableStateFlow<Boolean>(false)
    val domainWindowVisible: StateFlow<Boolean> = _domainWindowVisible

    val _domainUiStateStack = mutableListOf<DomainUiState>()

    val _VRUiState = MutableStateFlow<VRUiState>(VRUiState.PttNone(active = false, isError = false))
    val vrUiState: StateFlow<VRUiState> = _VRUiState


    fun pushUiState(uiState: DomainUiState) {
        _domainUiStateStack.add(uiState)
        _domainUiStateStack.forEachIndexed { index, domainUiState ->
            Log.d("@@ _domainUiStateStack", "index: $index / data: $domainUiState")
        }
    }

    fun popUiState() {
        if (_domainUiStateStack.size > 1) {
            _domainUiStateStack.removeAt(_domainUiStateStack.size - 1)
            _domainUiState.value = _domainUiStateStack.last()
        }
    }

    fun clearUiState() {
        _domainUiStateStack.clear()
    }

    fun onVREvent(event: VREvent) {
        when(event) {
            is VREvent.ChangeVRUIEvent -> {
                Log.d("@@ vrUiState 값은?1111", "${event.vrUiState.active} / ${event.vrUiState.isError}")
                _VRUiState.update { vrUiState ->
                    event.vrUiState
                }
            }
        }
    }
}
