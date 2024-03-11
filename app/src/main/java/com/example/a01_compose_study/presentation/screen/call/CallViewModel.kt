package com.example.a01_compose_study.presentation.screen.call

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a01_compose_study.data.Contact
import com.example.a01_compose_study.data.custom.SealedParsedData
import com.example.a01_compose_study.data.custom.call.CallManager
import com.example.a01_compose_study.data.custom.call.ProcCallData
import com.example.a01_compose_study.data.custom.ptt.PttManager
import com.example.a01_compose_study.domain.model.ScreenType
import com.example.a01_compose_study.presentation.data.UiState
import com.example.a01_compose_study.presentation.data.UiState.sealedParsedData
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

    private val _vrProcessingResultState = MutableStateFlow<VRProcessingResult>(VRProcessingResult.None)
    val vrProcessingResultState: StateFlow<VRProcessingResult> = _vrProcessingResultState

    init {
        viewModelScope.launch {
            sealedParsedData.collect { sealedParsedData ->
                Log.d("@@@@ CallModel 생성 후 콜렉", "${sealedParsedData}")
                if (sealedParsedData is SealedParsedData.CallData) {
                    when (sealedParsedData.procCallData) {
                        is ProcCallData.ScrollIndex -> {
                            Log.d("@@ ScrollIndex", "${sealedParsedData.procCallData}")
                            val voiceRecognitionIndex = sealedParsedData.procCallData.index // 음성인식으로 받아온 index
                            domainUiState.value.let { currDomainUiState -> // 현재 UI를 구성하는 DomainUiState
                                val currCallModel = currDomainUiState as? DomainUiState.CallWindow
                                currCallModel?.let { callModel ->
                                    val currentCallWindowContactListLastIndex = callModel.data?.lastIndex ?: 0 // 현재 Index가 존재하는 ContactList의 마지막 인덱스 번호
                                    if (currentCallWindowContactListLastIndex < voiceRecognitionIndex) { // 스크롤 음성인식 결과가 현재 UI에 존재하는 스크롤 인덱스 번호보다 크다면, TTS 요청
                                        pttManager.vrmwManager.requestTTs(
                                            promptId = listOf("PID_CMN_COMM_02_31"),
                                            runnable = { pttManager.vrmwManager.resumeVR() }
                                        )
                                    } else { // 스크롤 음성인식 결과가 현재 UI에 존재하는 스크롤 인덱스 번호보다 작다면 현재 UI의 ScrollIndex 프로퍼티에 음성인식 인덱스를 할당
                                        UiState._domainUiState.update { domainUiState ->
                                            val updatedState = domainUiState.copyWithNewScrollIndex(newScrollIndex = sealedParsedData.procCallData.index - 1, isClicked = true) // 클릭 후 상태 변경
                                            /**
                                             * domainUiState의 scrollIndex의 값이 null인 경우에만 스택에 추가
                                             * null이 아닌 경우에는 기존에 쌓여있던
                                             */
                                            if (domainUiState.scrollIndex == null) {
                                                UiState.pushUiStateMwContext(Pair(first = updatedState, second = sealedParsedData.procCallData.mwContext))
                                            }
                                            updatedState
                                        }
                                    }
                                }
                            }
                        }

                        is ProcCallData.ProcYesResult -> {
                            _vrProcessingResultState.update { currResult ->
                                UiState.replaceTopUiStateMwContext(newUiStateMwContext = Pair(first = domainUiState.value, second = sealedParsedData.procCallData.mwContext))
                                VRProcessingResult.Yes
                            }
                        }

                        is ProcCallData.ProcNoResult -> {
                            _vrProcessingResultState.update { currResult ->
                                UiState.replaceTopUiStateMwContext(newUiStateMwContext = Pair(first = domainUiState.value, second = sealedParsedData.procCallData.mwContext))
                                VRProcessingResult.No
                            }
                        }

                        is ProcCallData.ProcOtherNumberResult -> {
                            _vrProcessingResultState.update { currResult ->
                                UiState.replaceTopUiStateMwContext(newUiStateMwContext = Pair(first = domainUiState.value, second = sealedParsedData.procCallData.mwContext))
                                VRProcessingResult.OtherNumber
                            }
                        }

                        else -> {
                            Log.d("@@ else 호출", "${sealedParsedData.procCallData}")
                        }
                    }
                }
            }
        }
    }

    /**
     * 실제로 UI와 인터렉션이 발생했을때 사용하는 이벤트
     * ( 아래의 onCallBusinessEvent()를 사용하여 로직을 구성 )
     */
    fun onCallEvent(event: CallEvent) {
        when (event) {
            is CallEvent.OnCallBack -> {
            }

            is CallEvent.ContactListItemOnClick -> {
                val isContactIdUnique = callManager.isContactIdUnique(event.selectedContactItem)
                Log.d("@@@@ isContactNameUnique 결과값", "${isContactIdUnique}")
                Log.d("@@@@ ContactListItemOnClick ", "${event.itemIndex}")
                _domainUiState.update { domainUiState ->
                    val updatedState = (domainUiState as? DomainUiState.CallWindow)?.copy(
                        screenType = ScreenType.CallYesNo,
                        detailData = event.selectedContactItem,
                        isContactIdUnique = isContactIdUnique
                    ) ?: domainUiState

                    val isClickResetState = domainUiState.copyWithNewScrollIndex(newScrollIndex = event.itemIndex, isClicked = false)// 클릭 전 상태(뒤로가기를 하여 이전 화면으로 돌아갈때는 클릭 전 상태로 돌아가야 한다.)
                    /**
                     * 다음 화면으로 전환하기 전에 현재 마지막으로 쌓인 스택에는 scrollIndex 값이 할당되어 있지 않습니다.
                     * 따라서 이벤트로 받아온 인덱스 데이터를 사용하여 마지막 스택에 있는 데이터의 scrollIndex 값을 업데이트 해줍니다.
                     */
                    UiState.replaceTopUiStateMwContext(newUiStateMwContext = Pair(first = isClickResetState, second = null))
                    /**
                     * 다음 화면으로 전환하기
                     */
                    UiState.pushUiStateMwContext(
                        pairUiStateMwContext = Pair(
                            first = updatedState,
                            second = null
                        )
                    )
                    updatedState
                }
            }

            is CallEvent.OnYesButtonClick -> {
                onCallBusinessEvent(CallBusinessEvent.Calling(phoneNumber = event.phoneNumber))
            }

            is CallEvent.OnOtherNumberButtonClick -> {
                val currentContact = (domainUiState.value as DomainUiState.CallWindow).detailData
                val matchingContacts = callManager.findContactsByContactId(currentContact)

                /**
                 * 일치하는 cotact_id가 2개라면 다른 번호로 교체
                 */
                if (matchingContacts.size == 2) {
                    val differentContact =
                        matchingContacts.find { it.number != currentContact.number }
                    _domainUiState.update { domainUiState ->
                        val updatedState = differentContact?.let {
                            (domainUiState as? DomainUiState.CallWindow)?.copy(
                                detailData = it,
                            )
                        } ?: domainUiState
                        UiState.pushUiStateMwContext(
                            pairUiStateMwContext = Pair(
                                first = updatedState,
                                second = null
                            )
                        )
                        updatedState
                    }
                } else {
                    /**
                     * 일치하는 cotact_id가 2개가 아니라면(3개 이상) 동일한 contact_id를 가진 연락처 목록을 띄워줌
                     */
                    _domainUiState.update { domainUiState ->
                        val updatedState = (domainUiState as? DomainUiState.CallWindow)?.copy(
                            data = matchingContacts,
                            screenType = ScreenType.CallIndexedList,
                            detailData = domainUiState.detailData,
                        ) ?: domainUiState
                        UiState.pushUiStateMwContext(
                            pairUiStateMwContext = Pair(
                                first = updatedState,
                                second = null
                            )
                        )
                        updatedState
                    }
                }
            }
        }
        /**
         * 음성인식 이벤트를 발생시키는 State 초기화
         */
        clearVRProcessingResultState()
    }

    /**
     * 구체적인 Call 비즈니스 로직을 발생시키는 이벤트
     */
    private fun onCallBusinessEvent(event: CallBusinessEvent) {
        when (event) {
            is CallBusinessEvent.Calling -> {
                callManager.makeCall(event.phoneNumber)
            }
        }
    }

    /**
     * 음성인식에 대한 이벤트를 발생시켰다면 해당 이벤트를 초기화를 해줘야함
     * ==> 왜냐하면 초기화를 시키지 않으면 계속 값을 들고 있기 때문에 해당 이벤트를 계속 발생시킴
     */
    private fun clearVRProcessingResultState() {
        _vrProcessingResultState.update { currResult ->
            VRProcessingResult.None
        }
    }
}