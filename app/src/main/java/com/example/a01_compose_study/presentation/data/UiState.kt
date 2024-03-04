package com.example.a01_compose_study.presentation.data

import android.util.Log
import com.example.a01_compose_study.data.custom.SealedParsedData
import com.example.a01_compose_study.data.custom.sendMsg.ScreenData
import com.example.a01_compose_study.domain.model.ScreenType
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

    val _sendMsgUiData = MutableSharedFlow<Pair<ScreenType, Any>>()
    val sendMsgUiData: SharedFlow<Pair<ScreenType, Any>> = _sendMsgUiData

    /**
     * 화면을 스택에 쌓음
     */
    fun pushUiState(uiState: DomainUiState) {
        _domainUiStateStack.add(uiState)
        _domainUiStateStack.forEachIndexed { index, domainUiState ->
            Log.d("@@ _domainUiStateStack", "index: $index / data: $domainUiState")
        }
    }

    /**
     * 화면을 스택에 쌓지 않고 변화만 시킴
     */
    fun changeUiState(uiState: DomainUiState) {
        _domainUiState.update { domainUiState ->
            uiState.copyWithNewSizeType(domainUiState.screenSizeType)
        }
    }

    fun popUiState() {
        if (_domainUiStateStack.size > 1) {
            _domainUiStateStack.removeAt(_domainUiStateStack.size - 1)
            _domainUiState.value = _domainUiStateStack.last()
        } else {
//            _domainUiState.update {
//                DomainUiState.PttWindow(
//                    domainType = SealedDomainType.Ptt,
//                    screenType = ScreenType.PttListen,
//                    guideText = "",
//                    isError = false,
//                    screenSizeType = ScreenSizeType.Small
//                )
//            }
//        }
        }
    }

    fun clearUiState() {
        _domainUiStateStack.clear()
    }

    fun onVREvent(event: VREvent) {
        Log.d("@@ vrUiState", "${vrUiState.value.active}")
        when (event) {
            is VREvent.ChangeVRUIEvent -> {
                _VRUiState.update { vrUiState ->
                    event.vrUiState
                }
            }

            else -> {}
        }
    }

    fun getCurrDomainUiState(): StateFlow<DomainUiState> {
        return domainUiState
    }

    fun handleScreenData(screenData: ScreenData, uiState: DomainUiState) {
        when (screenData) {
            ScreenData.PUSH -> pushUiState(uiState)
            ScreenData.POP -> popUiState()
            ScreenData.CHANGE -> popUiState()
            ScreenData.REJECT -> TODO()
            ScreenData.BtPhoneAppRun -> TODO()
        }
    }
}
