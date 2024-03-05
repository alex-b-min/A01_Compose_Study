package com.example.a01_compose_study.presentation.data

import android.util.Log
import com.example.a01_compose_study.data.custom.MWContext
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

    val _mwContextState = MutableStateFlow<MWContext?>(null)
    val mwContextState: StateFlow<MWContext?> = _mwContextState

    val _domainWindowVisible = MutableStateFlow<Boolean>(false)
    val domainWindowVisible: StateFlow<Boolean> = _domainWindowVisible

    val _domainUiStateStack = mutableListOf<DomainUiState>()

    val domainUiStateMwContextStack = mutableListOf<Pair<DomainUiState, MWContext?>>()

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
                Log.d("@@@@ _domainUiStateStack", "index: $index / data: $domainUiState")
            }
        }
    }

    /**
     * domainUiState의 domainType이 Ptt,None 타입이 아닐때만 스택에 추가함
     * MWContext의 값이 nullable 한 타입으로 null이 들어올 수 있으며,
     * mwContextState를 최신 데이터로 업데이트 하는 시점은 domainUiState와 같이 스택에 쌓이는 타이밍에 업데이트 함
     * 추가적으로 화면을 이동하였으니 VR 화면을 띄워주도록함(이 로직은 적절한 시점에 호출해야하는데 어디인지는 추후에 정할것.)
     */
    fun pushUiStateMwContext(pairUiStateMwContext: Pair<DomainUiState, MWContext?>) {
        if (domainUiState.value.domainType != SealedDomainType.Ptt && domainUiState.value.domainType != SealedDomainType.None) {
            domainUiStateMwContextStack.add(pairUiStateMwContext)
            domainUiStateMwContextStack.forEachIndexed { index, domainUiStateMwContext ->
                _mwContextState.value = domainUiStateMwContext.second
                Log.d("@@// UiStateMwContext Push", "index: $index / mwContext: ${domainUiStateMwContext.second} / domainUiState: ${domainUiStateMwContext.first}")
            }
            onVREvent(
                VREvent.ChangeVRUIEvent(
                    VRUiState.PttLoading(
                        active = true,
                        isError = false
                    )
                )
            )
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
     * 뒤로가기와 같은 기능을 위한 popUiState()함수
     */
    fun popUiState() {
        val removeData = domainUiStateMwContextStack.removeLastOrNull()

        if (removeData != null) { // 지운 데이터가 존재 한다면
            // 지운 후 스택의 마지막 값이 존재하지 않는다면, closeDomainWIndow() 실행
            val (lastDomainUiState, lastMwContext) = domainUiStateMwContextStack.lastOrNull() ?: run {
                closeDomainWindow()
                return
            }
            // 지운 후 스택의 마지막 값이 존재한다면, 값 업데이트
            _domainUiState.value = lastDomainUiState
            _mwContextState.value = lastMwContext
        } else { // 지운 데이터가 존재하지 않는다면
            closeDomainWindow()
        }

        domainUiStateMwContextStack.forEachIndexed { index, domainUiStateMwContext ->
            val (domainUiState, mwContext) = domainUiStateMwContext
            Log.d("@@// UiStateMwContext POP", "index: $index / mwContext: $mwContext / domainUiState: $domainUiState")
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
        domainUiStateMwContextStack.clear()
    }
}
