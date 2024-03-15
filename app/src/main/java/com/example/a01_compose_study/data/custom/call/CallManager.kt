package com.example.a01_compose_study.data.custom.call

import android.content.Context
import android.provider.ContactsContract
import com.example.a01_compose_study.R
import com.example.a01_compose_study.data.Contact
import com.example.a01_compose_study.data.DialogueMode
import com.example.a01_compose_study.data.HVRError
import com.example.a01_compose_study.data.Intentions
import com.example.a01_compose_study.data.analyze.ParseBundle
import com.example.a01_compose_study.data.custom.ContactsManager
import com.example.a01_compose_study.data.custom.MWContext
import com.example.a01_compose_study.data.custom.SealedParsedData
import com.example.a01_compose_study.data.custom.VRResultListener
import com.example.a01_compose_study.data.pasing.CallModel
import com.example.a01_compose_study.data.pasing.CommonModel
import com.example.a01_compose_study.domain.model.NoticeModel
import com.example.a01_compose_study.presentation.data.UiState
import com.example.a01_compose_study.presentation.screen.SelectVRResult
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CallManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val contactsManager: ContactsManager,
    private val coroutineScope: CoroutineScope,
    private val btCall: BtCall,
) : VRResultListener {

    private val _sealedParsedData = UiState._sealedParsedData

    fun parsedData(bundle: ParseBundle<out Any>, selectVRResult: SelectVRResult): ProcCallData {
        /**
         * 다양한  Call 음성인식에 대한  결과를 하나의 타입으로 전달하여 그에 따른 적합한 CallWindow를 띄위기 위해
         * sealed class를 사용하여 다양한 하위 타입을 나누고 결국 하나의 ProcCallData 데이터로 처리합니다.
         */
        return when (bundle.dialogueMode) {
            DialogueMode.CALL -> {
                val callModel = CallModel("Sample Intent").also { //bundle의 model 값을 이용하는게 아닌 임의로 직접 CallModel을 생성해서 테스트함
                    it.items = mutableListOf<Any>("Data 1", "Data 2", "Data 3") // 원래는 인식된 항목에 따라 일치하는 연락처를 생성해야 하지만, 현재는 일치하는 데이터에 대해서는 더미 데이터를 사용하여 생성하고 있습니다. 추후 변경할 예정입니다.
                }

                callModel.let {
                    procCallIntention(
                        callModel = it,
                        selectVRResult = selectVRResult
                    )
                } ?: run { // 위에서 callModel이 직접 할당되어서 null 가능성이 없다고 나오는 것임 음성 인식 결과는 null이 될 수 있음.
                    return ProcCallData.RejectRequest
                }
            }

            DialogueMode.LIST -> {
                val commonModel = bundle.model as? CommonModel
                commonModel?.index = 5 //임의로 발화 결과인 라인 넘버의 값을 할당함

                commonModel?.let {
                    procListIntention(
                        data = it,
                        selectVRResult = selectVRResult
                    )
                } ?: run {
                    return ProcCallData.RejectRequest
                }
            }

            DialogueMode.CALLNAME -> {
                val commonModel =
                    CommonModel("Yes") //bundle의 model 값을 이용하는게 아닌 임의로 직접 CommonModel을 생성해서 테스트함
                commonModel.let {
                    procYesNoIntention(
                        data = it,
                        selectVRResult = selectVRResult
                    )
                } ?: run { // 위에서 commonModel이 직접 할당되어서 null 가능성이 없다고 나오는 것임 음성 인식 결과는 null이 될 수 있음.
                    return ProcCallData.RejectRequest
                }
            }

            else -> {
                return ProcCallData.RejectRequest
            }
        }
    }

    /** DialogueMode가 CALL 인 경우에 실행!![PttWindow 에서 ]
     * 전화 가능한 상태인 지 확인하고 오류 출력 안내를 할 수 없는 환경이므로 noticeModel을 강제로 주입하여 사용
     * 인식된 이름과 매칭되는 여러 전화번호부 상황을 만들기 위해 기존의 makeContactList()를 이용하는것이 아닌 tempList를 강제로 주입 강제로 주입하여 사용
     */
    private fun procCallIntention(
        callModel: CallModel,
        selectVRResult: SelectVRResult,
    ): ProcCallData {
        /** 구현해야 할 로직
         * 1. 전화가 가능한 상태인지 조건 확인
         * 2 전화 가능한 상태라면 인식된 이름에 따라 각각의 전화번호부를 생성한다.
         * 2-1. [인식 이름 1개]: YesNo 화면이 띄워진 CallName 화면 띄우기(해당 인식된 이름 데이터 반환)
         * 2-2. [인식 이름 여러 개]: 인식된 이름의 전화번호부 화면 띄우기(해당 인식된 이름 List 데이터 반환)
         * 2-3. [인식된 이름 없음]: 전체 전화번호부 목록 화면 띄우기(전체 전화번호부 List 데이터 반환)
         */
        callModel.run {
            /**
             * 원래는 위와 같이 contactsManager의 preConditionCheck()을 통해 noticeModel을 생성해야 하지만,
             * 현재는 기기 연동이 안되는 상황이기에 임의로 더미 데이터 noticeModel을 생성하여 진행하려는 목적임
             */
//            val noticeModel: NoticeModel? = createNoticeModel(NoticeModelType.APPLE_CAR_PLAY, context)
//            val noticeModel: NoticeModel? = createNoticeModel(NoticeModelType.BLUETOOTH_PROCESSING, context)
//            val noticeModel: NoticeModel? = createNoticeModel(NoticeModelType.BLUETOOTH_NOT_CONNECTED_NOT_PAIRING, context)
//            val noticeModel: NoticeModel? = createNoticeModel(NoticeModelType.BLUETOOTH_NOT_CONNECTED_YES_PAIRING, context)
            val noticeModel: NoticeModel? = createNoticeModel(NoticeModelType.DEFAULT, context) //Notice Model이 null일 때

            if (noticeModel != null) {
                return ProcCallData.NoticeTTSRequest(
                    noticeModel = noticeModel,
                    mwContext = MWContext(DialogueMode.CALL, this@CallManager)
                )
                TODO("오류 출력 안내 vrmwManager.requestTTS() 실행")
            } else {
                /**
                 * 연락처 리스트를 생성할 때 startVR()를 해줘야 하는데 매개변수의 값으로 MWContex(DialogueMode)의 객체를 넣는다.
                 * - 인식된 이름과 매칭되는 연락처가 2개 이상일 때 - DialogueMode.LIST 주입
                 * - 인식된 이름과 매칭되는 연락처가 1개 일 때 - DialougeMode.CALLNAME 주입
                 * - 인식된 이름과 매칭되는 연락처가 없거나 Call 발화 했을때 - DialogueMode.CALL 주입
                 */


                /**
                 * 인식된 이름이 있는 경우
                 */
                if (callModel.items.size > 0) {
                    /**
                     * [실제로 전화번호부가 만들어진는 과정 생략]
                     * 기존에는 contactsManager의 makeContactList()를 통해서 인식된 이름을 통한 전화번호부 목록을 생성을 했지만,
                     * 인식된 이름을 통해 생성된 전화번호부 목록을 더미 데이터로 할당 하도록 함
                     */
                    val recognizedList = when (selectVRResult) {
                        is SelectVRResult.CallListResult -> emptyList() // 인식된 이름이 없는 더미 데이터
                        is SelectVRResult.CallIndexListResult -> fetchRecognizedContacts(8) // 인식된 이름이 8개인 더미 데이터
                        is SelectVRResult.CallRecognizedContact -> fetchRecognizedContacts(1) // 인식된 이름이 1개인 더미 데이터
                        else -> emptyList()
                    }

                    /**
                     * 인식된 이름으로 매칭되는 연락처가 여러개인 경우
                     */
                    if (recognizedList.size > 1) {
                        return ProcCallData.RecognizedContactListScreen(
                            data = removeDuplicateContactIds(
                                recognizedList
                            ),
                            mwContext = MWContext(DialogueMode.LIST, this@CallManager)
                        ) // 인덱스가 존재하는 전화번호부 목록 반환[DomainType.Call / ScreenType.List]
                        TODO("startVR(MWContext(DailogueMode.LIST) 실행")

                        /**
                         * 인식된 이름으로 매칭되는 연락처가 없는 경우
                         */
                    } else if (recognizedList.isEmpty()) {
                        return ProcCallData.AllContactListScreen(
                            data = removeDuplicateContactIds(fetchAllContacts()),
                            mwContext = MWContext(DialogueMode.CALL, this@CallManager)
                        ) // 인덱스가 존재하지 않는 전체 전화번호부 목록 반환[DomainType.Call / ScreenType.List]
                        TODO("startVR(MWContext(DailogueMode.CALL) 실행")

                        /**
                         * 인식된 이름으로 매칭되는 연락처 1개인 경우
                         */
                    } else {
                        return ProcCallData.ProcCallNameScreen(
                            data = recognizedList.firstOrNull() ?: Contact(),
                            mwContext = MWContext(DialogueMode.CALLNAME, this@CallManager)
                        ) // 하나의 전화번호 반환[DomainType.Call / ScreenType.YesNo]
                        TODO("startVR(DialogueMode.CALLNAME) 실행")
                    }
                    /**
                     * Call 만 발화한 상황
                     */
                } else {
                    return ProcCallData.AllContactListScreen(
                        data = fetchAllContacts(),
                        mwContext = MWContext(DialogueMode.CALL, this@CallManager)
                    ) // 인덱스가 존재하지 않는 전체 전화번호부 목록 반환[DomainType.Call, ScreenType.List]
                    TODO("startVR(DailogueMode.CALL) 실행")
                }
            }
        }
    }

    /**
     * DialogueMode가 LIST 인 경우에 실행[실제 경우 : 인식된 이름으로 매칭되는 인덱스가 존재하는 전화번호부 목록 화면에서 VR 실행을 한 결과 ]
     */
    private fun procListIntention(data: CommonModel, selectVRResult: SelectVRResult): ProcCallData {
        /**
         * 기존에는 아래의 로직을 CallManager에 구현했지만, 변경된 CallManager에서는 음성 인식 결과의
         * index가 null이 아닌 경우 해당 index를 CallViewModel로 보내고,
         * null인 경우에는 TTS 요청 메시지를 CallViewModel에게 보내어 CallViewModel이 데이터를 받아 처리함
         *
         * 1. 서버로부터 받은 index가 현재 스크린을 구성하는 모델의 인덱스보다 큰 경우 (이동 불가능한 경우)
         *    ==> "몇 번째 연락처를 선택할까요?" TTS 요청
         *
         * 2. 서버로부터 받은 index가 현재 스크린을 구성하는 모델의 인덱스보다 작은 경우 (이동 가능한 경우)
         *    ==> 서버로부터 받은 index의 위치로 이동
         */
        val index = data.index // 원래는 CommonModel(음성 인식 결과 데이터)로부터 index 가져와야함
        val customIndex = (selectVRResult as? SelectVRResult.ScrollIndexResult)?.inputIndex // 음성 인식 결과인 CommonModel의 index 값의 더미 데이터

        customIndex.let {
            ProcCallData.ScrollIndex(
                index = customIndex!!, // not null assertion(!!)를 사용한 이유는 customIndex를 let 확장함수를 통해 null이 아닐때만 실행되기 때문
                mwContext = MWContext(DialogueMode.LIST, this@CallManager)
            )
        }
        // 음성 인식 결과 데이터의 null 여부에 따라 보내는 데이터가 달라짐
        return if (customIndex != null) {
            ProcCallData.ScrollIndex(
                index = customIndex,
                mwContext = MWContext(DialogueMode.LIST, this@CallManager)
            )
        } else {
            ProcCallData.RejectRequest
        }
    }


    /**
     * DialogueMode가 CallName 인 경우에 실행!![ 실제 경우 : YesNo 화면에서 발화를 한 결과에 대한 처리 ]
     */
    private fun procYesNoIntention(
        data: CommonModel,
        selectVRResult: SelectVRResult,
    ): ProcCallData {
        val intention = data.intention.replace(" ", "") // 원래는 CommonModel(음성 인식 결과 데이터)로부터 intention을 가져와야함

        /**
         * 음성 인식 결과를 바탕으로 임의로 생성한 Intention
         */
        val dummyIntention = when (selectVRResult) {
            SelectVRResult.CallOtherNameResult -> "OtherNumber"
            SelectVRResult.CallYesResult -> "Yes"
            SelectVRResult.CallNoResult -> "No"
            else -> ""
        }

        return when (dummyIntention) {
            Intentions.Yes.value -> {
                ProcCallData.ProcYesResult(
                    mwContext = MWContext(
                        DialogueMode.CALLNAME,
                        this@CallManager
                    )
                )
            }

            Intentions.No.value -> {
                ProcCallData.ProcNoResult(
                    mwContext = MWContext(
                        DialogueMode.CALLNAME,
                        this@CallManager
                    )
                )
            }

            Intentions.OtherNumber.value -> {
                ProcCallData.ProcOtherNumberResult(
                    mwContext = MWContext(
                        DialogueMode.CALLNAME,
                        this@CallManager
                    )
                )
            }

            else -> {
                ProcCallData.RejectRequest
            }
        }
    }

    /**
     * 음성 인식 결과 데이터인 bundle을 받는 곳
     */
    override fun onReceiveBundle(bundle: ParseBundle<out Any>, selectVRResult: SelectVRResult) {
        coroutineScope.launch {
            val procCallData = parsedData(bundle, selectVRResult)
            _sealedParsedData.emit(SealedParsedData.CallData(procCallData))
        }
    }

    fun makeCall(phoneNumber: String) {
        btCall.outgoingCall(phoneNumber)
    }

    override fun onBundleParsingErr() {
        TODO("Not yet implemented")
    }

    override fun onCancel() {
        TODO("Not yet implemented")
    }

    override fun onVRError(error: HVRError) {
        TODO("Not yet implemented")
    }

    /**
     * Set자료구조를 사용하여 전화번호부 목록에서 중복되는 contact_id를 가진 데이터 중에 처음 나오는 데이터만 남기는 함수
     */
    fun removeDuplicateContactIds(contacts: List<Contact>): List<Contact> {
        val uniqueIds = HashSet<String>()
        val filteredList = mutableListOf<Contact>()

        for (contact in contacts) {
            if (!uniqueIds.contains(contact.contact_id)) {
                uniqueIds.add(contact.contact_id)
                filteredList.add(contact)
            }
        }

        return filteredList
    }

    /**
     * 중복되는 contatct_id 값이 2개 이상 존재하는지를 판별하는 함수
     * ==> 만약 중복되는 contatct_id가 2개 이상이라면 OtherNumber 존재해야함
     */
    fun isContactIdUnique(contact: Contact): Boolean {
        val allContacts = fetchAllContacts()
        val count = allContacts.count { it.contact_id == contact.contact_id }
        return count <= 1
    }

    /**
     * 인자로 받은 Contact의 contacit_id와 일치하는 연락처 리스트를 반환하는 함수
     */
    fun findContactsByContactId(contact: Contact): List<Contact> {
        val allContacts = fetchAllContacts()
        return allContacts.filter { it.contact_id == contact.contact_id }
    }
}

/**
 * 전체 전화번호부 목록 더미데이터
 */
fun fetchAllContacts(): List<Contact> {
    val result: MutableList<Contact> = mutableListOf()

    val types = listOf(
        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,
        ContactsContract.CommonDataKinds.Phone.TYPE_HOME,
        ContactsContract.CommonDataKinds.Phone.TYPE_WORK,
        ContactsContract.CommonDataKinds.Phone.TYPE_OTHER
    )

    result.add(
        Contact(
            id = "1",
            contact_id = "1",
            name = "문재민",
            number = "010-1111-2222",
            type = types.indexOf(1)
        )
    )
    result.add(
        Contact(
            id = "2",
            contact_id = "1",
            name = "문재민",
            number = "010-1312-1443",
            type = types.indexOf(2)
        )
    )

    result.add(
        Contact(
            id = "3",
            contact_id = "2",
            name = "이일구",
            number = "010-8765-4321",
            type = types.indexOf(1)
        )
    )
    result.add(
        Contact(
            id = "4",
            contact_id = "2",
            name = "이일구",
            number = "010-1234-5678",
            type = types.indexOf(3)
        )
    )

    result.add(
        Contact(
            id = "5",
            contact_id = "3",
            name = "엄마",
            number = "010-5555-5555",
            type = types.indexOf(1)
        )
    )
    result.add(
        Contact(
            id = "6",
            contact_id = "3",
            name = "엄마",
            number = "010-6666-6666",
            type = types.indexOf(2)
        )
    )
    result.add(
        Contact(
            id = "7",
            contact_id = "3",
            name = "엄마",
            number = "010-7777-7777",
            type = types.indexOf(3)
        )
    )

    result.add(
        Contact(
            id = "8",
            contact_id = "4",
            name = "삐쓰까또레부르쥬미첼라햄페스츄리치즈나쵸스트링스파게티",
            number = "010-2222-3333",
            type = types.random()
        )
    )
    result.add(
        Contact(
            id = "9",
            contact_id = "5",
            name = "하늘별님구름햇님보다사랑스러우리",
            number = "010-3333-4444",
            type = types.random()
        )
    )
    result.add(
        Contact(
            id = "10",
            contact_id = "6",
            name = "Alex",
            number = "010-4444-5555",
            type = types.random()
        )
    )
    result.add(
        Contact(
            id = "11",
            contact_id = "7",
            name = "포티투닷 이순신",
            number = "010-4444-5555",
            type = types.random()
        )
    )
    result.add(
        Contact(
            id = "12",
            contact_id = "8",
            name = "포티투닷 홍길동 책임연구원 하하하하하",
            number = "031-131",
            type = types.random()
        )
    )
    result.add(
        Contact(
            id = "13",
            contact_id = "9",
            name = "김혜원 어머님",
            number = "010-1111-5555",
            type = types.random()
        )
    )
    result.add(
        Contact(
            id = "14",
            contact_id = "10",
            name = "홍길 동사무소",
            number = "010-4444-5555",
            type = types.random()
        )
    )
    result.add(
        Contact(
            id = "15",
            contact_id = "11",
            name = "홍길동 (HMC 유럽)",
            number = "010-4444-5555",
            type = types.random()
        )
    )
    result.add(
        Contact(
            id = "16",
            contact_id = "12",
            name = "강신부",
            number = "010-4444-5555",
            type = types.random()
        )
    )
    result.add(
        Contact(
            id = "17",
            contact_id = "13",
            name = "우리♥︎",
            number = "010-4444-5555",
            type = types.random()
        )
    )
    result.add(
        Contact(
            id = "18",
            contact_id = "14",
            name = "ㅇ ㅏ ㅇ ㅣ ㄷ ㅣ",
            number = "010-4444-5555",
            type = types.random()
        )
    )
    result.add(
        Contact(
            id = "19",
            contact_id = "15",
            name = "119 장난전화",
            number = "1509",
            type = types.random()
        )
    )
    result.add(
        Contact(
            id = "20",
            contact_id = "16",
            name = "Alexander Sandor Signiel",
            number = "010-4444-5555",
            type = types.random()
        )
    )

    return result.toList()
}


/**
 * 인덱스를 가진 인식된 전체번호부 목록 더미 데이터
 */
fun fetchRecognizedContacts(count: Int): List<Contact> {
    val allContacts = fetchAllContacts().shuffled()
    return allContacts.take(count)
}

/**
 * 아래는 각 상황(AppleCarPlay, BluetoothProcessing, BluetoothNotConnectedNotPairing, BluetoothNotConnectedYesPairing)에 따른 NoticeModel을 만드는 함수
 */
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

enum class NoticeModelType {
    APPLE_CAR_PLAY,
    BLUETOOTH_PROCESSING,
    BLUETOOTH_NOT_CONNECTED_NOT_PAIRING,
    BLUETOOTH_NOT_CONNECTED_YES_PAIRING,
    DEFAULT
}

fun createNoticeModel(type: NoticeModelType, context: Context): NoticeModel? {
    return when (type) {
        NoticeModelType.APPLE_CAR_PLAY -> createAppleCarPlayNoticeModel(context)
        NoticeModelType.BLUETOOTH_PROCESSING -> createBluetoothProcessingNoticeModel(context)
        NoticeModelType.BLUETOOTH_NOT_CONNECTED_NOT_PAIRING -> createBluetoothNotConnectedNotPairingNoticeModel(
            context
        )

        NoticeModelType.BLUETOOTH_NOT_CONNECTED_YES_PAIRING -> createBluetoothNotConnectedYesPairingNoticeModel(
            context
        )

        NoticeModelType.DEFAULT -> null
    }
}