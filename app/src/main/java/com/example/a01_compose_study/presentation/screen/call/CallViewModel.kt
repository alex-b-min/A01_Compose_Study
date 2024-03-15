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
    private val _callYesNoOtherNumberEventState = MutableStateFlow<CallYesNoOtherNumberEvent>(CallYesNoOtherNumberEvent.None)
    val callYesNoOtherNumberEventState: StateFlow<CallYesNoOtherNumberEvent> = _callYesNoOtherNumberEventState

    // CallList 화면에서 발생하는 Click 이벤트를 관리하는 MutableStateFlow
    private val _callListEventState = MutableStateFlow<CallListEvent>(CallListEvent.None)
    val callListEventState: StateFlow<CallListEvent> = _callListEventState

    init {
        viewModelScope.launch {
            sealedParsedData.collect { sealedParsedData -> // Call 데이터 수집
                if (sealedParsedData is SealedParsedData.CallData) {
                    handleCallData(sealedParsedData.procCallData) // Call 데이터 처리
                }
            }
        }
    }

    /**
     * CallManager로부터 데이터를 받아 각 상황에 맞게 처리하는 함수
     */
    private fun handleCallData(procCallData: ProcCallData) {
        when (procCallData) {
            /**
             * ScrollIndex를 받아 처리함
             */
            is ProcCallData.ScrollIndex -> handleScrollIndex(procCallData)
            /**
             * Yes 결과를 받아 실제 Yes 버튼을 클릭 했을 시의 로직을 처리함
             */
            is ProcCallData.ProcYesResult -> updateCallYesNoOtherNumberEventState(procCallData, CallYesNoOtherNumberEvent.Yes)
            /**
             * No 결과를 받아 실제 Yes 버튼을 클릭 했을 시의 로직을 처리함
             */
            is ProcCallData.ProcNoResult -> updateCallYesNoOtherNumberEventState(procCallData, CallYesNoOtherNumberEvent.No)
            /**
             * OtherNumber 결과를 받아 실제 Yes 버튼을 클릭 했을 시의 로직을 처리함
             */
            is ProcCallData.ProcOtherNumberResult -> updateCallYesNoOtherNumberEventState(procCallData, CallYesNoOtherNumberEvent.OtherNumber)
            else -> {}
        }
    }

    /**
     * 사용자 버튼 클릭 시의 이벤트에 대한 처리를 담당하는 함수
     */
    fun onCallEvent(event: CallEvent) {
        when (event) {
            is CallEvent.CloseButtonClick -> UiState.closeDomainWindow()
            is CallEvent.BackButtonClick ->  { UiState.popUiState() }
            is CallEvent.ContactListItemOnClick -> handleContactListItemClick(event)
            is CallEvent.OnYesButtonClick -> onCallBusinessEvent(CallBusinessEvent.Calling(event.phoneNumber))
            is CallEvent.OnOtherNumberButtonClick -> handleOtherNumberButtonClick()
        }
        /**
         * 아래는 이벤트가 발생했다면 해당 이벤트를 계속 유지하지 않고 초기화 주는 코드
         */
        clearCallYesNoOtherNumberEventState() // CallYesNo Event 결과 초기화
        clearCallListEventState() // CallList Event 결과 초기화
    }

    /**
     * 스크롤 인덱스 처리 함수
     */
    private fun handleScrollIndex(procCallData: ProcCallData.ScrollIndex) {
        val voiceRecognitionIndex = procCallData.index
        domainUiState.value.let { currDomainUiState ->
            val currCallModel = currDomainUiState as? DomainUiState.CallWindow
            currCallModel?.let { callModel ->
                val currentCallWindowContactListLastIndex = callModel.data?.lastIndex ?: 0

                /**
                 * 음성 인식 결과인 index가 현재 스크린을 구성하는 List의 인덱스보다 큰 경우 (이동 불가능한 경우)
                 * ==> "몇 번째 연락처를 선택할까요?" TTS 요청
                 */
                if (currentCallWindowContactListLastIndex < voiceRecognitionIndex) {
                    requestTTs(promptId = "PID_CMN_COMM_02_31") {
                        pttManager.vrmwManager.resumeVR()
                    }
                    /**
                     * 음성 인식 결과인 index가 현재 스크린을 구성하는 List의 인덱스보다 작은 경우 (이동 가능한 경우)
                     * ==>  음성 인식 결과인 index의 위치로 스크롤 이동
                     */
                } else {
                    updateUiStateWithScrollIndex(procCallData) // 스크롤 인덱스 업데이트
                }
            }
        }
    }

    /**
     * Yes/No Other Number (클릭이 되었는지) 이벤트 상태를 업데이트 하는 함수
     */
    private fun updateCallYesNoOtherNumberEventState(procCallData: ProcCallData, callYesNoOtherNumberEvent: CallYesNoOtherNumberEvent) {
        _callYesNoOtherNumberEventState.update {
            replaceTopUiStateMwContext(domainUiState.value, procCallData.mwContext) //현재 스택의 마지막에 쌓여 있는 UI를 유지한 체 MWContext의 값만 교체
            callYesNoOtherNumberEvent// 인자로 들어온 이벤트 값으로 업데이트
        }
    }

    /**
     * 연락처 List 화면에서의 아이템 클릭 시 실행 될 상세 로직 함수
     * ==> 클릭 후 OtherNumber에 따라 YesNo / YesNoOtherNumber 화면으로 이동함
     */
    private fun handleContactListItemClick(event: CallEvent.ContactListItemOnClick) {
        val isContactIdUnique = callManager.isContactIdUnique(event.selectedContactItem) //클릭으로 선택된 연락처가 고유한 값인지(OtherNumber가 존재하지 않는지) Boolean 값
        /**
         * 선택된 연락처가 고유한 값인지 여부를 현재 UI를 구성하고 있는 DomainUiState.CallWindow에 업데이트
         */
        _domainUiState.update { currentUiState ->
            val updatedState = (currentUiState as? DomainUiState.CallWindow)?.copy(
                screenType = ScreenType.CallYesNo,
                detailData = event.selectedContactItem,
                isContactIdUnique = isContactIdUnique
            ) ?: currentUiState
            /**
             * 스택을 관리와 함께 UI 업데이트
             */
            updateUiStateForContactListItemClick(itemIndex = event.itemIndex, currentUiState = currentUiState, updatedState = updatedState)
            updatedState
        }
    }

    /**
     * OtherNumber 버튼 클릭 시 살행 될 상세 로직 함수
     */
    private fun handleOtherNumberButtonClick() {
        val currentContact = (domainUiState.value as? DomainUiState.CallWindow)?.detailData // 현재 선택된 Contact 정보
        val matchingContacts = currentContact?.let { callManager.findContactsByContactId(it) } // 현재 선택된 연락처의 contactId와 일치하는 연락처 목록을 전체 연락처에서 비교하여 반환

        if (matchingContacts?.size == 2) { // 일치하는 contact_id가 2개라면 다른 번호로 교체
            val differentContact = matchingContacts.find { it.number != currentContact.number }
            differentContact?.let { contact ->
                updateUiStateForDifferentContact(contact)
            }
        } else { // 일치하는 cotact_id가 2개가 아니라면(3개 이상) 동일한 contact_id를 가진 연락처 목록(Contact List)을 띄워줌
            matchingContacts?.let { contacts ->
                updateUiStateForMatchingContacts(contacts)
            }
        }
    }

    /**
     * 음성 인식 결과인 Scroll Index를 받아와 현재 UIState의 scrollIndex 프로퍼티를 업데이트
     */
    private fun updateUiStateWithScrollIndex(procCallData: ProcCallData.ScrollIndex) {
        viewModelScope.launch {
            UiState._domainUiState.update { domainUiState ->
                val updatedState = domainUiState.copyWithNewScrollIndex(newScrollIndex = procCallData.index - 1, enableScroll = true) // 클릭 후 상태 변경
                /**
                 * domainUiState의 scrollIndex의 값이 null인 경우에만 스택에 추가
                 * null이 아닌 경우에는 기존에 쌓여있던 데이터를 쌓는게 아니라 데이터 교체를 해야하기 때문이다.
                 * [참고: 데이터 교체는 클릭 이벤트에 대한 로직을 구현하는 CallEvent.ContactListItemOnClick 에서 한다.]
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

            /**
             * 스크롤을 업데이트 시키고 클릭 이벤트를 발생
             */
            _callListEventState.update {
                CallListEvent.Click
            }
        }
    }

    /**
     * 연락처 목록에서 항목을 클릭했을 때 UI 상태를 업데이트합니다.
     *
     * @param itemIndex 클릭한 항목의 인덱스
     * @param currentUiState 현재 UI 상태
     * @param updatedState 업데이트된 UI 상태
     */
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

    /**
     * OtherNumber가 1개인 상황에서, 해당 OtherNumber로 UI를 교체
     */
    private fun updateUiStateForDifferentContact(differentContact: Contact) {
        _domainUiState.update { domainUiState ->
            val updatedState = (domainUiState as? DomainUiState.CallWindow)?.copy(
                detailData = differentContact
            ) ?: domainUiState
            // 스택에 추가하지 않고 스택의 마지막 index에 존재하는 데이터와 교체
            replaceTopUiStateMwContext(newUiStateMwContext = Pair(first = updatedState, second = null))
            updatedState
        }
    }

    /**
     * contact_id가 동일한 인덱스가 존재하는 연락처 목록으로 UI 업데이트
     */
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

    /**
     * 전화 비즈니스 이벤트(실제 전화) 처리 함수
     */
    private fun onCallBusinessEvent(event: CallBusinessEvent) {
        when (event) {
            is CallBusinessEvent.Calling -> callManager.makeCall(event.phoneNumber)
        }
    }

    /**
     * YesNoOtherNumber 에서 발생될 이벤트를 관리하는 변수를 None 상태로 초기화 하는 함수
     *  ==> 이벤트 발생 후 만약 초기화를 하지 않는다면 어떠한 값으로 계속해서 값을 유지하기에 계속해서 이벤트가 발생될 것이다.
     *  ==> 그렇기에 이벤트를 발생시켰다면 초기화를 해줘야함
     */
    private fun clearCallYesNoOtherNumberEventState() {
        _callYesNoOtherNumberEventState.update { CallYesNoOtherNumberEvent.None }
    }

    /**
     * CallList에서 발생될 이벤트를 관리하는 변수를 None 상태로 초기화 하는 함수
     *  ==> 이벤트 발생 후 만약 초기화를 하지 않는다면 Click 이벤트로 계속해서 값을 유지하기에 계속해서 이벤트가 발생될 것이다.
     *  ==> 그렇기에 이벤트를 발생시켰다면 초기화를 해줘야함
     */
    private fun clearCallListEventState() {
        _callListEventState.update { CallListEvent.None }
    }

    /**
     * TTS 요청 함수
     */
    private fun requestTTs(promptId: String, onCompleted: () -> Unit) {
        pttManager.vrmwManager.requestTTs(
            promptId = listOf(promptId),
            runnable = onCompleted
        )
    }
}