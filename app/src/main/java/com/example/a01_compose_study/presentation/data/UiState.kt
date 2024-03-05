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

    val _domainUiStateStack = mutableListOf<Pair<DomainUiState,MWContext?>>()

    val _VRUiState = MutableStateFlow<VRUiState>(VRUiState.PttNone(active = false, isError = false))
    val vrUiState: StateFlow<VRUiState> = _VRUiState

    val _sendMsgUiData = MutableSharedFlow<Pair<ScreenType, Any>>()
    val sendMsgUiData: SharedFlow<Pair<ScreenType, Any>> = _sendMsgUiData

    val _mwContext = MutableStateFlow<MWContext?>(null)
    val mwContext: StateFlow<MWContext?> = _mwContext

    /**
     * 화면을 스택에 쌓음
     */
//    fun pushUiState(domainUiState: DomainUiState,mwContext: MWContext? = null) {
//        val domainUiPair:Pair<DomainUiState,MWContext?> = Pair(domainUiState,mwContext)
//        _domainUiStateStack.add(domainUiPair)
//        _domainUiStateStack.forEachIndexed { index, domainUiState ->
//            Log.d("@@ _domainUiStateStack", "index: $index / data: $domainUiState")
//        }
//    }
        fun pushUiState(domainUiPair:Pair<DomainUiState,MWContext?>) {
//        val domainUiPair:Pair<DomainUiState,MWContext?> = Pair(domainUiState,mwContext)
        _domainUiStateStack.add(domainUiPair)
    }

    /**
     * 화면을 스택에 쌓지 않고 변화만 시킴
     */
    fun changeUiState(domainUiPair:Pair<DomainUiState,MWContext?>) {
        val uiState = domainUiPair.first
        val mwContext = domainUiPair.second
        _domainUiState.update { domainUiState ->
            uiState.copyWithNewSizeType(domainUiState.screenSizeType)
        }
        _mwContext.update { mwContext }
    }

    fun popUiState() {
        if (_domainUiStateStack.size > 1) {
//            _domainUiStateStack.removeAt(_domainUiStateStack.size - 1)
//            _domainUiState.value = _domainUiStateStack.last()
            val poppedPair = _domainUiStateStack.removeLast()
            val domainUiState = poppedPair.first
            val mwContext = poppedPair.second

            _domainUiState.value = domainUiState
            _mwContext.value = mwContext

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

    fun handleScreenData(screenData: ScreenData, domainUiPair:Pair<DomainUiState,MWContext?>) {
        when (screenData) {
            ScreenData.PUSH -> pushUiState(domainUiPair)
            ScreenData.POP -> popUiState()
            ScreenData.CHANGE -> popUiState()
            ScreenData.REJECT -> TODO()
            ScreenData.BtPhoneAppRun -> TODO()
        }
    }
}
