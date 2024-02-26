package com.example.a01_compose_study.data.custom.call

import android.content.Context
import com.example.a01_compose_study.R
import com.example.a01_compose_study.data.Contact
import com.example.a01_compose_study.data.DialogueMode
import com.example.a01_compose_study.data.DomainType
import com.example.a01_compose_study.data.Intentions
import com.example.a01_compose_study.data.analyze.ParseBundle
import com.example.a01_compose_study.data.custom.ContactsManager
import com.example.a01_compose_study.data.pasing.CallModel
import com.example.a01_compose_study.data.pasing.CommonModel
import com.example.a01_compose_study.domain.model.NoticeModel
import com.example.a01_compose_study.presentation.data.UiState.getCurrDomainUiState
import com.example.a01_compose_study.presentation.screen.main.DomainUiState
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CallManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val contactsManager: ContactsManager,
) {

    private var recogCategory = 0

    fun parsedData(bundle: ParseBundle<out Any>): ProcCallData {
        /**
         * ParseBundle<out Any>? 타입의 bundle을 HelpData로 파싱하는 로직이 담겨야함
         */

        return when (bundle.dialogueMode) {
            DialogueMode.CALL -> {
                val callModel = bundle.model as? CallModel

                callModel?.let {
                    val appleCarPlayNoticeModel = createAppleCarPlayNoticeModel(context)
                    val bluetoothProcessingNoticeModel = createBluetoothProcessingNoticeModel(context)
                    val bluetoothNotConnectedNotPairingNoticeModel = createBluetoothNotConnectedNotPairingNoticeModel(context)
                    val bluetoothNotConnectedYesPairingNoticeModel = createBluetoothNotConnectedYesPairingNoticeModel(context)
                    procCallIntention(it, appleCarPlayNoticeModel, fetchContacts())
                } ?: run {
                    return ProcCallData.RejectRequest
                }
            }
            DialogueMode.LIST -> {
                val commonModel = bundle.model as? CommonModel

                commonModel?.let {
                    procListIntention(it)
                } ?: run {
                    return ProcCallData.RejectRequest
                }
            }
            DialogueMode.CALLNAME -> {
                val commonModel = bundle.model as? CommonModel

                commonModel?.let {
                    procYesNoIntention(it)
                } ?: run {
                    return ProcCallData.RejectRequest
                }
            }
            else -> {
                return ProcCallData.RejectRequest
            }
        }
    }

    /** DialogueMode가 CALL 인 경우에 실행!![ 실제 경우 : 누구에게 전화 걸지를 말하고 해당 음성에 대한 인식 결과에 대한 로직을 작성 ]
     * 전화 가능한 상태인 지 확인하고 오류 출력 안내를 할 수 없는 환경이므로 noticeModel을 강제로 주입하여 사용
     * 인식된 이름과 매칭되는 여러 전화번호부 상황을 만들기 위해 기존의 makeContactList()를 이용하는것이 아닌 tempList를 강제로 주입 강제로 주입하여 사용
     */
    private fun procCallIntention(
        callModel: CallModel,
        noticeModel: NoticeModel?,
        tempList: List<Contact>,
    ): ProcCallData {
        /**
         * 1. 전화가 가능한 상태인지 조건 확인
         * 2 전화 가능한 상태라면 인식된 이름에 따라 각각의 전화번호부를 생성한다.
         * 2-1. [인식 이름 1개]: YesNo 화면이 띄워진 CallName 화면 띄우기(해당 인식된 이름 데이터 반환)
         * 2-2. [인식 이름 여러 개]: 인식된 이름의 전화번호부 화면 띄우기(해당 인식된 이름 List 데이터 반환)
         * 2-3. [인식된 이름 없음]: 전체 전화번호부 목록 화면 띄우기(전체 전화번호부 List 데이터 반환)
         */

        callModel.run {
            val notice = contactsManager.preConditionCheck(DomainType.Call)

            if (noticeModel != null) {
                // 오류 출력 안내(tts 요청도 해주어야함)
                return ProcCallData.NoticeTTSRequest(noticeModel = noticeModel)
            } else {

                /**
                 * 연락처 리스트를 생성할 때 startVR()를 해줘야 하는데 매개변수의 값으로 MWContex(DialogueMode)의 객체를 넣는다.
                 * - 인식된 연락처 생성 - DialogueMode.LIST 주입
                 * - 전체 연락처 생성 - DialogueMode.CALL 주입
                 */
                // 인식된 이름이 있는 경우
                if (callModel.items.size > 0) {
                    /**
                     * 실제로 전화번호부가 만들어진는 과정 생략
                     */
//                    // 카테고리 검색 여부 판별을 위해 recogCategory에 type 값 할당
//                    val items = callModel.getContactItems()
//
//                    recogCategory = items[0].type
//
//                    // 인식된 이름으로 연락처 몇 개가 검색 되는지 확인
//                    val tempList = contactsManager.makeContactList(
//                        items,
//                        true,
//                        category = recogCategory
//                    )

                    // 인식된 이름으로 매칭되는 연락처가 여러개인 경우
                    if (tempList.size > 1) {
                        ProcCallData.ContactListScreen(data = tempList) // 인덱스가 존재하는 전화번호부 목록 반환[DomainType.Call / ScreenType.List]
                        TODO("startVR(MWContext(DailogueMode.LIST) 실행")
                        // 인식된 이름으로 매칭되는 연락처가 없는 경우
                    } else if (tempList.isEmpty()) {
                        ProcCallData.FullContactListScreen(data = tempList) // 인덱스가 존재하지 않는 전체 전화번호부 목록 반환[DomainType.Call / ScreenType.List]
                        TODO("startVR(MWContext(DailogueMode.CALL) 실행")
                        // 인식된 이름으로 매칭되는 연락처 1개인 경우
                    } else {
                        ProcCallData.ProcCallNameScreen(data = tempList[0]) // 하나의 전화번호 반환[DomainType.Call / ScreenType.YesNo]
                        TODO("startVR(DialogueMode.CALLNAME) 실행")
                    }
                } else { // Call 만 발화한 상황
                    ProcCallData.FullContactListScreen(data = tempList) // 인덱스가 존재하지 않는 전체 전화번호부 목록 반환[DomainType.Call, ScreenType.List]
                    TODO("startVR(DailogueMode.CALL) 실행")
                }
            }
        }
    }

    /**
     * DialogueMode가 LIST 인 경우에 실행!![ 실제 경우 : 인식된 이름으로 매칭되는 인덱스가 존재하는 전화번호부 목록 화면에서 VR 실행을 실행한 결과 ]
     */
    private fun procListIntention(data: CommonModel): ProcCallData {
        /**
         * 가정 : DialogueMode가 LIST가 되었다고 가정을 하는 상황이라면, 인식된 이름으로 매칭되는 인덱스가 존재하는 전화번호부 목록을 보여주고 있는 상태일 것이다.
         * 1. 서버로부터 받은 index의 크기가 현재 스크린을 구성하고 있는 모델의 인덱스보다 큰 경우(이동 불가 경우)
         * ==> 몇 번째 연락처를 선택할까요? TTS 요청
         * 2. 서버로부터 받은 index의 크기가 현재 스크린을 구성하고 있는 모델의 인덱스보다 작은 경우(이동 가능 경우)
         * ==> 서버로부터의 내려온 index의 위치로 이동
         */
        val index = data.index
        index?.let { serverIndex -> // server의 index가 null 아닐때
            getCurrDomainUiState().value.let { currDomainUiState -> // 현재 화면을 구성하는 데이터가 null이 아닐때
                val currCallModel = currDomainUiState as? DomainUiState.CallWindow
                currCallModel?.let { callModel -> // 현재 화면을 구성하고 있는 CallModel의 데이터가 null이 아닐때
                    /**
                     * 서버의 index 보다 해당 callModel의 index 값을 비교
                     * - 크다면 TTS 요청
                     * - 작다면 해당 ScrollIndex를 반환(추후 ScrollIndex의 값은 스크롤을 움직이는 값으로 사용됨)
                     */
                    return if (callModel.data.size < serverIndex) {
                        ProcCallData.ListTTSRequest(promptId = "PID_CMN_COMM_02_31")
                    } else {
                        ProcCallData.ScrollIndex(index = 3)
                    }
                }
            }
        }
        return ProcCallData.ScrollIndex(index = null)
    }

    /**
     * DialogueMode가 CallName 인 경우에 실행!![ 실제 경우 : YesNo 화면에서 발화를 한 결과에 대한 처리 ]
     */
    private fun procYesNoIntention(data: CommonModel): ProcCallData {
        val intention = data.intention.replace(" ", "")

        return when (intention) {
            Intentions.Yes.value -> {
                ProcCallData.YesNoOtherNumberResultProc(callYesNoOtherNumberResult = CallYesNoOtherNumberResult.Yes)
            }
            Intentions.No.value -> {
                ProcCallData.YesNoOtherNumberResultProc(callYesNoOtherNumberResult = CallYesNoOtherNumberResult.No)
            }
            Intentions.OtherNumber.value -> {
                // otherNumber 3개 이상일 경우, LIST 화면 전환
                if (fetchContacts().size > 2) {
                    ProcCallData.YesNoOtherNumberResultProc(
                        callYesNoOtherNumberResult = CallYesNoOtherNumberResult.OtherNumberList(
                            fetchContacts()
                        )
                    )
                } else if (fetchContacts().size > 1) { // otherNumber가 2개 밖에 없을 시, 현재 번호 말고 다른 번호로 바로 표시
                    ProcCallData.YesNoOtherNumberResultProc(
                        callYesNoOtherNumberResult = CallYesNoOtherNumberResult.OtherNumber(
                            fetchContact()
                        )
                    )
                } else { // otherNumber가 1개인 경우, 현재의 번호밖에 없는 상태니까 reject() 표시
                    ProcCallData.YesNoOtherNumberResultProc(callYesNoOtherNumberResult = CallYesNoOtherNumberResult.Reject)
                }
            }
            else -> {
                ProcCallData.RejectRequest
            }
        }
    }
}

fun fetchContacts(): List<Contact> {
    val result: MutableList<Contact> = mutableListOf()
    result.add(Contact(id = "1", name = "문재민", number = "010-1111-2222"))
    result.add(Contact(id = "2", name = "문제민", number = "010-2222-3333"))
    result.add(Contact(id = "3", name = "혜원 9838", number = "010-3333-4444"))
    result.add(Contact(id = "4", name = "Alex", number = "010-4444-5555"))
    result.add(Contact(id = "5", name = "Alexander Sandor", number = "010-4444-5555"))
    result.add(Contact(id = "6", name = "포티투닷 이순신", number = "010-4444-5555"))
    result.add(Contact(id = "7", name = "포티투닷 홍길동 책임연구원", number = "031-131"))
    result.add(Contact(id = "8", name = "엄마", number = "1509"))
    result.add(Contact(id = "9", name = "김혜원 어머님", number = "010-1111-5555"))
    result.add(Contact(id = "10", name = "홍길 동사무소", number = "010-4444-5555"))
    result.add(Contact(id = "11", name = "홍길동 (HMC 유럽)", number = "010-4444-5555"))
    result.add(Contact(id = "12", name = "강신부", number = "010-4444-5555"))
    result.add(Contact(id = "13", name = "우리♥︎", number = "010-4444-5555"))
    result.add(Contact(id = "14", name = "ㅇ ㅏ ㅇ ㅣ ㄷ ㅣ", number = "010-4444-5555"))
    result.add(Contact(id = "15", name = "1096119838", number = "010-4444-5555"))
    result.add(Contact(id = "16", name = "119 장난전화", number = "1509"))
    result.add(Contact(id = "17", name = "이일구", number = "02-131"))

    return result.toList()
}

fun fetchContact(): Contact {
    return Contact(id = "4", name = "Alex", number = "010-4444-5555")
}

fun createAppleCarPlayNoticeModel(context: Context): NoticeModel {
    return NoticeModel().apply {
        noticeString = context.getString(R.string.TID_SCH_PHON_02_01)
        noticePromptId = "PID_SCH_PHON_02_01"
    }
}

fun createBluetoothProcessingNoticeModel(context: Context): NoticeModel {
    return NoticeModel().apply {
        noticeString = context.getString(R.string.TID_SCH_PHON_03_01)
        noticePromptId = "PID_SCH_PHON_03_01"
        args["changeScreen"] = 0
        screenExist = true
    }
}

fun createBluetoothNotConnectedNotPairingNoticeModel(context: Context): NoticeModel {
    return NoticeModel().apply {
        noticeString = context.getString(R.string.TID_SCH_PHON_03_03)
        noticePromptId = "PID_SCH_PHON_03_03"
        args["changeScreen"] = 0
        screenExist = true
    }
}

fun createBluetoothNotConnectedYesPairingNoticeModel(context: Context): NoticeModel {
    return NoticeModel().apply {
        noticeString = context.getString(R.string.TID_SCH_PHON_03_02)
        noticePromptId = "PID_SCH_PHON_03_02"
        args["changeScreen"] = 0
        screenExist = true
    }
}