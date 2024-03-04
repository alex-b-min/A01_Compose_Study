package com.example.a01_compose_study.presentation.data

import android.util.Log
import com.example.a01_compose_study.data.custom.SealedParsedData
import com.example.a01_compose_study.domain.model.SealedDomainType
import com.example.a01_compose_study.presentation.screen.main.DomainUiState
import com.example.a01_compose_study.presentation.screen.main.VREvent
import com.example.a01_compose_study.presentation.screen.main.route.VRUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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

    val coroutineScope = CoroutineScope(Dispatchers.IO)

    val _sendUiData = MutableSharedFlow<Any>()
    val sendUiData: SharedFlow<Any> = _sendUiData

    /**
     * domainUiState의 domainType이 Ptt,None 타입이 아닐때만 스택에 추가함
     */
    fun pushUiState(uiState: DomainUiState) {
        if (domainUiState.value.domainType != SealedDomainType.Ptt && domainUiState.value.domainType != SealedDomainType.None) {
            _domainUiStateStack.add(uiState)
            _domainUiStateStack.forEachIndexed { index, domainUiState ->
                Log.d("@@ _domainUiStateStack", "index: $index / data: $domainUiState")
            }
        }
    }

    /**
     * 화면을 스택에 쌓지 않고 스크린 사이즈만 변화 시킴
     */
    fun changeUiState(uiState: DomainUiState) {
        _domainUiState.update { domainUiState ->
            uiState.copyWithNewSizeType(domainUiState.screenSizeType)
        }
    }

    /**
     * 스택 사이즈가 1일때 백버튼을 누른다면 Window를 닫아버리고,
     * 스택 사이즈가 0보다 클 때(1 이상)는 가장 마지막에 쌓여있는 스택을 삭제한다.
     */
    fun popUiState() {
        if (_domainUiStateStack.size == 1) {
            closeDomainWindow()
        } else if (_domainUiStateStack.size > 0) {
            _domainUiStateStack.removeAt(_domainUiStateStack.size - 1)
            _domainUiState.value = _domainUiStateStack.last()
        }
    }

    fun onVREvent(event: VREvent) {
        Log.d("@@ vrUiState", "${vrUiState.value.active}")
        when (event) {
            is VREvent.ChangeVRUIEvent -> {
                _VRUiState.update { vrUiState ->
                    event.vrUiState
                }
            }
        }
    }

    fun getCurrDomainUiState(): StateFlow<DomainUiState> {
        return domainUiState
    }

    fun openDomainWindow() {
        _domainWindowVisible.value = true
    }

    fun closeDomainWindow() {
        _domainWindowVisible.value = false
        clearUiState()
    }

    fun clearUiState() {
        _domainUiStateStack.clear()
    }
}
