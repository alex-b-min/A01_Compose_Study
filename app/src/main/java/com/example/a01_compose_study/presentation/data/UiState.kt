package com.example.a01_compose_study.presentation.data

import com.example.a01_compose_study.data.custom.SealedParsedData
import com.example.a01_compose_study.presentation.screen.main.DomainUiState
import com.example.a01_compose_study.presentation.screen.main.VRUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object UiState {

    val _sealedParsedData = MutableStateFlow<SealedParsedData>(SealedParsedData.NoneData)
    val sealedParsedData: StateFlow<SealedParsedData> = _sealedParsedData

    val _domainUiState = MutableStateFlow<DomainUiState>(DomainUiState.NoneWindow())
    val domainUiState: StateFlow<DomainUiState> = _domainUiState

    val _vrUiState = MutableStateFlow<VRUiState>(VRUiState.NoneWindow)
    val vrUiState: StateFlow<VRUiState> = _vrUiState

    val _domainWindowVisible = MutableStateFlow<Boolean>(false)
    val domainWindowVisible: StateFlow<Boolean> = _domainWindowVisible
}