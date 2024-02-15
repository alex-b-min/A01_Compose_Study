package com.example.a01_compose_study.presentation.data

import com.example.a01_compose_study.data.custom.SealedParsedData
import com.example.a01_compose_study.presentation.screen.main.DomainUiState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

object UiState {

    val _sealedParsedData = MutableSharedFlow<SealedParsedData>()
    val sealedParsedData: SharedFlow<SealedParsedData> = _sealedParsedData

    val _domainUiState = MutableStateFlow<DomainUiState>(DomainUiState.NoneWindow())
    val domainUiState: StateFlow<DomainUiState> = _domainUiState

    val _domainWindowVisible = MutableStateFlow<Boolean>(false)
    val domainWindowVisible: StateFlow<Boolean> = _domainWindowVisible

    val _domainUiStateStack = mutableListOf<DomainUiState>()

    fun pushUiState(uiState: DomainUiState) {
        _domainUiStateStack.add(uiState)
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
}
