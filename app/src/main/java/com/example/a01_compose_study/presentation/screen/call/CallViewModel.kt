package com.example.a01_compose_study.presentation.screen.call

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a01_compose_study.data.Contact
import com.example.a01_compose_study.data.custom.SealedParsedData
import com.example.a01_compose_study.data.custom.call.CallManager
import com.example.a01_compose_study.data.custom.call.ProcCallData
import com.example.a01_compose_study.data.custom.ptt.PttManager
import com.example.a01_compose_study.domain.model.ScreenType
import com.example.a01_compose_study.presentation.data.UiState
import com.example.a01_compose_study.presentation.data.UiState.pushUiStateMwContext
import com.example.a01_compose_study.presentation.data.UiState.replaceTopUiStateMwContext
import com.example.a01_compose_study.presentation.data.UiState.sealedParsedData
import com.example.a01_compose_study.presentation.screen.call.screen.CallListEvent
import com.example.a01_compose_study.presentation.screen.main.DomainUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CallViewModel @Inject constructor(
    private val callManager: CallManager,
    val pttManager: PttManager,
) : ViewModel() {

    private val _domainUiState = UiState._domainUiState
    val domainUiState: StateFlow<DomainUiState> = UiState._domainUiState

    // CallYesNo 화면에서 발생하는 Yes, No, OtherNumber 클릭 이벤트를 관리하는 MutableStateFlow
    private val _callYesNoEventState = MutableStateFlow<CallYesNoEvent>(CallYesNoEvent.None)
    val callYesNoEventState: StateFlow<CallYesNoEvent> = _callYesNoEventState

    // CallList 화면에서 발생하는 Click 이벤트를 관리하는 MutableStateFlow
    private val _callListEventState = MutableStateFlow<CallListEvent>(CallListEvent.None)
    val callListEventState: StateFlow<CallListEvent> = _callListEventState

    init {
        viewModelScope.launch {
            sealedParsedData.collect { sealedParsedData -> // Call 데이터 수집
                if (sealedParsedData is SealedParsedData.CallData) {
                    handleCallData(sealedParsedData.procCallData) //Call 데이터 처리
                }
            }
        }
    }

    // 사용자 이벤트에 대한 처리를 담당하는 함수
    fun onCallEvent(event: CallEvent) {
        when (event) {
            is CallEvent.CloseButtonClick -> UiState.closeDomainWindow()
            is CallEvent.BackButtonClick ->  {
                clearCallListEventState()
                UiState.popUiState()
            }
            is CallEvent.ContactListItemOnClick -> handleContactListItemClick(event)
            is CallEvent.OnYesButtonClick -> onCallBusinessEvent(CallBusinessEvent.Calling(event.phoneNumber))
            is CallEvent.OnOtherNumberButtonClick -> handleOtherNumberButtonClick()
        }
        clearCallYesNoEventState() // VR 처리 결과 초기화
    }

    // Call 데이터를 처리하는 함수
    private fun handleCallData(procCallData: ProcCallData) {
        when (procCallData) {
            is ProcCallData.ScrollIndex -> handleScrollIndex(procCallData)
            is ProcCallData.ProcYesResult -> handleProcResult(procCallData, CallYesNoEvent.Yes)
            is ProcCallData.ProcNoResult -> handleProcResult(procCallData, CallYesNoEvent.No)
            is ProcCallData.ProcOtherNumberResult -> handleProcResult(procCallData, CallYesNoEvent.OtherNumber)

            else -> {}
        }
    }

    // 스크롤 인덱스 처리 함수
    private fun handleScrollIndex(procCallData: ProcCallData.ScrollIndex) {
        val voiceRecognitionIndex = procCallData.index
        domainUiState.value.let { currDomainUiState ->
            val currCallModel = currDomainUiState as? DomainUiState.CallWindow
            currCallModel?.let { callModel ->
                val currentCallWindowContactListLastIndex = callModel.data?.lastIndex ?: 0
                // 음성 인식 인덱스가 현재 UI에 표시된 마지막 인덱스보다 크면 TTS 요청
                if (currentCallWindowContactListLastIndex < voiceRecognitionIndex) {
                    requestTTs(promptId = "PID_CMN_COMM_02_31") {
                        pttManager.vrmwManager.resumeVR()
                    }
                } else {
                    // 스크롤 인덱스 업데이트
                    updateUiStateWithScrollIndex(procCallData)
                }
            }
        }
    }

    // VR 처리 결과를 핸들링하는 함수, vrProcessingResult 값에 따라 클릭 이벤트(Yes, No, OtherNumber)가 발생됨.
    private fun handleProcResult(procCallData: ProcCallData, callYesNoEvent: CallYesNoEvent) {
        _callYesNoEventState.update {
            replaceTopUiStateMwContext(domainUiState.value, procCallData.mwContext)
            callYesNoEvent
        }
    }

    // 연락처 아이템 클릭 이벤트 처리 함수
    private fun handleContactListItemClick(event: CallEvent.ContactListItemOnClick) {
        val isContactIdUnique = callManager.isContactIdUnique(event.selectedContactItem)
        _domainUiState.update { currentUiState ->
            val updatedState = (currentUiState as? DomainUiState.CallWindow)?.copy(
                screenType = ScreenType.CallYesNo,
                detailData = event.selectedContactItem,
                isContactIdUnique = isContactIdUnique
            ) ?: currentUiState
            // 연락처 아이템 클릭 시 UI 상태 업데이트
            updateUiStateForContactListItemClick(itemIndex = event.itemIndex, currentUiState = currentUiState, updatedState = updatedState)
            updatedState
        }
    }

    // OtherNumber 버튼 클릭 처리 함수
    private fun handleOtherNumberButtonClick() {
        val currentContact = (domainUiState.value as? DomainUiState.CallWindow)?.detailData
        val matchingContacts = currentContact?.let { callManager.findContactsByContactId(it) }

        if (matchingContacts?.size == 2) { // 일치하는 cotact_id가 2개라면 다른 번호로 교체
            val differentContact = matchingContacts.find { it.number != currentContact.number }
            differentContact?.let { contact ->
                updateUiStateForDifferentContact(contact)
            }
        } else { // 일치하는 cotact_id가 2개가 아니라면(3개 이상) 동일한 contact_id를 가진 연락처 목록을 띄워줌
            matchingContacts?.let { contacts ->
                updateUiStateForMatchingContacts(contacts)
            }
        }
    }

    // 스크롤 인덱스를 새로운 상태로 업데이트하는 함수
    private fun updateUiStateWithScrollIndex(procCallData: ProcCallData.ScrollIndex) {
        viewModelScope.launch {
            UiState._domainUiState.update { domainUiState ->
                val updatedState = domainUiState.copyWithNewScrollIndex(newScrollIndex = procCallData.index - 1, enableScroll = true) // 클릭 후 상태 변경
                /**
                 * domainUiState의 scrollIndex의 값이 null인 경우에만 스택에 추가
                 * null이 아닌 경우에는 기존에 쌓여있던 데이터를 쌓는게 아니라 데이터 교체를 해야하기 때문이다.
                 * [참고: 데이터 교체는 CallEvent.ContactListItemOnClick 에서 한다.]
                 */
                if (domainUiState.scrollIndex == null) {
                    pushUiStateMwContext(
                        Pair(
                            first = updatedState,
                            second = procCallData.mwContext
                        )
                    )
                }
                updatedState
            }

            _callListEventState.update {
                CallListEvent.Click
            }
        }
    }

    // 연락처 아이템 클릭 이벤트에 대한 UI 상태 업데이트 함수
    private fun updateUiStateForContactListItemClick(itemIndex: Int, currentUiState: DomainUiState, updatedState: DomainUiState) {
        /**
         * LineNumber 음성 인식 결과를 collect할 때 바로 index를 업데이트할 수 있지만,
         * 직접 클릭할 때는 컴포저블 함수 내부에서 index 값을 외부로 전달을 해야만 index가 업데이트 가능하다.
         * ==> 따라서 음성 인식 발화를 통해 클릭 이벤트를 발생을 시키든, 실제로 클릭하든, 어떤 방법을 사용하든지 간에 클릭 시에는 index 데이터를 발행하도록 통일하였음
         */
        val isClickResetState = currentUiState.copyWithNewScrollIndex(newScrollIndex = itemIndex, enableScroll = false)
        /**
         * [마지막 스택에 저장되어 있는 데이터 교체하기]
         * 이유: 다음 화면으로 전환하기 전에 현재 마지막으로 쌓인 스택에는 scrollIndex 값이 할당되어 있지 않음
         * ==> 그렇기에 뒤로가기를 했을 시에는 전에 위치하던 ScrollIndex 위치에 존재하지 않고 Index가 0인 곳에 위치한다.
         * 따라서 이벤트로 받아온 인덱스 데이터를 사용하여 마지막 스택에 있는 데이터의 scrollIndex 값을 업데이트 해준다.
         * ==> 최종적으로 뒤로가기를 했을 시에도 전에 위치하던 ScrollIndex 위치에 존재하게 한다.
         * [중요한 것은 화면을 나타내기 위한 데이터인 DomainUiState만 교체를 하고 마지막 스택의 MWContext의 값은 그대로 유지 시킨다.]
         */
        replaceTopUiStateMwContext(isClickResetState, null)
        /**
         * [다음 화면으로 전환하기 및 스택에 쌓기]
         */
        pushUiStateMwContext(updatedState, null)
    }

    // 다른 연락처 정보로 UI 상태 업데이트 함수
    private fun updateUiStateForDifferentContact(differentContact: Contact) {
        _domainUiState.update { domainUiState ->
            val updatedState = (domainUiState as? DomainUiState.CallWindow)?.copy(
                detailData = differentContact
            ) ?: domainUiState
            // 스택에 추가하지 않고 마지막 index에 존재하는 데이터와 교체
            replaceTopUiStateMwContext(newUiStateMwContext = Pair(first = updatedState, second = null))
            updatedState
        }
    }

    // 동일한 연락처 목록으로 UI 상태 업데이트 함수
    private fun updateUiStateForMatchingContacts(matchingContacts: List<Contact>) {
        _domainUiState.update { domainUiState ->
            val updatedState = (domainUiState as? DomainUiState.CallWindow)?.copy(
                data = matchingContacts,
                screenType = ScreenType.CallIndexedList,
                detailData = domainUiState.detailData,
            ) ?: domainUiState
            // 다음 화면으로 전환하기 및 스택에 쌓기
            pushUiStateMwContext(
                pairUiStateMwContext = Pair(
                    first = updatedState,
                    second = null
                )
            )
            updatedState
        }
    }

    // 전화 비즈니스 이벤트 처리 함수
    private fun onCallBusinessEvent(event: CallBusinessEvent) {
        when (event) {
            is CallBusinessEvent.Calling -> callManager.makeCall(event.phoneNumber)
        }
    }

    // CallYesNo에서 발생될 이벤트를 관리하는 변수를 None 상태로 초기화 하는 함수
    // 만약 초기화를 하지 않는다면 어떠한 값으로 계속해서 값을 유지하기에 계속해서 이벤트가 발생될 것이다. 그렇기에 이벤트를 발생시켰다면 초기화를 해줘야함
    private fun clearCallYesNoEventState() {
        _callYesNoEventState.update { CallYesNoEvent.None }
    }

    // CallList에서 발생될 이벤트를 관리하는 변수를 None 상태로 초기화 하는 함수
    // 만약 초기화를 하지 않는다면 어떠한 값으로 계속해서 값을 유지하기에 계속해서 이벤트가 발생될 것이다. 그렇기에 이벤트를 발생시켰다면 초기화를 해줘야함
    private fun clearCallListEventState() {
        _callListEventState.update { CallListEvent.None }
    }

    // TTS 요청 함수
    private fun requestTTs(promptId: String, onCompleted: () -> Unit) {
        pttManager.vrmwManager.requestTTs(
            promptId = listOf(promptId),
            runnable = onCompleted
        )
    }
}