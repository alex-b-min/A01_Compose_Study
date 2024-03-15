package com.example.a01_compose_study.presentation.data

import android.util.Log
import com.example.a01_compose_study.data.custom.MWContext
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

    val _domainUiStateStack = mutableListOf<Pair<DomainUiState, MWContext?>>()

    val _VRUiState = MutableStateFlow<VRUiState>(VRUiState.PttNone(active = false, isError = false))
    val vrUiState: StateFlow<VRUiState> = _VRUiState

    val _sendMsgUiData = MutableSharedFlow<Pair<ScreenType, Any>>()
    val sendMsgUiData: SharedFlow<Pair<ScreenType, Any>> = _sendMsgUiData

    val _mwContext = MutableStateFlow<MWContext?>(null)
    val mwContext: StateFlow<MWContext?> = _mwContext

    val _isVrInput = MutableStateFlow(false)
    val isVrInput: StateFlow<Boolean> = _isVrInput

    val isVrActive = MutableStateFlow(true)

    fun pushUiState(domainUiPair: Pair<DomainUiState, MWContext?>) {
//        val domainUiPair:Pair<DomainUiState,MWContext?> = Pair(domainUiState,mwContext)
        _domainUiStateStack.add(domainUiPair)
    }

    fun changeUiState(domainUiPair: Pair<DomainUiState, MWContext?>) {
        val uiState = domainUiPair.first
        val mwContext = domainUiPair.second
        _domainUiState.update { domainUiState ->
            uiState.copyWithNewSizeType(domainUiState.screenSizeType)
        }
        _mwContext.update { mwContext }
    }

    fun popUiState() {
        Log.d("sendMsg", "popUiState, _domainUiStateStack.size:${_domainUiStateStack.size}")
        if (_domainUiStateStack.size > 1) {

            _domainUiStateStack.removeLast()
            _domainUiState.value = _domainUiStateStack.last().first
            _mwContext.value = _domainUiStateStack.last().second

            Log.d("sendMsg", "popUiState, _domainUiStateStack.size:${_domainUiStateStack.size}")
        } else {
            Log.d("sendMsg", "popUiState : else")
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

    fun handleScreenData(screenData: ScreenData, domainUiPair: Pair<DomainUiState, MWContext?>? = null) {
        Log.d("sendMsg", "handleScreenData : ${screenData}")
        when (screenData) {
            ScreenData.PUSH -> domainUiPair?.let { pushUiState(it) }
            ScreenData.POP -> popUiState()
            ScreenData.CHANGE -> domainUiPair?.let { changeUiState(it) }
            ScreenData.REJECT -> TODO()
            ScreenData.BtPhoneAppRun -> Log.d("sendMsg","requestBtPhoneAppRun")
        }
    }
}
