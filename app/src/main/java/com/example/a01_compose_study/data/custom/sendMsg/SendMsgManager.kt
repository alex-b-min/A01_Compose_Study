package com.example.a01_compose_study.data.custom.sendMsg


import android.util.Log
import com.example.a01_compose_study.data.Contact
import com.example.a01_compose_study.data.DialogueMode
import com.example.a01_compose_study.data.DomainType
import com.example.a01_compose_study.data.HVRError
import com.example.a01_compose_study.data.Intentions
import com.example.a01_compose_study.data.analyze.ParseBundle
import com.example.a01_compose_study.data.custom.ContactsManager
import com.example.a01_compose_study.data.custom.MWContext
import com.example.a01_compose_study.data.custom.SealedParsedData
import com.example.a01_compose_study.data.custom.VRResultListener
import com.example.a01_compose_study.data.custom.call.BtCall
import com.example.a01_compose_study.data.pasing.CommonModel
import com.example.a01_compose_study.data.pasing.SendMsgModel
import com.example.a01_compose_study.domain.model.NoticeModel
import com.example.a01_compose_study.domain.model.ScreenType
import com.example.a01_compose_study.domain.model.SealedDomainType
import com.example.a01_compose_study.domain.util.CustomLogger
import com.example.a01_compose_study.presentation.data.UiState
import com.example.a01_compose_study.presentation.data.UiState._sealedParsedData
import com.example.a01_compose_study.presentation.util.StringManager.printSttString
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SendMsgManager @Inject constructor(
    private val contactsManager: ContactsManager,
    private val job: CoroutineScope,
    private val btCall: BtCall,
) : VRResultListener {

    private var selectedPhonebookItem: Contact? = null
    private var sendMsgContactList = mutableListOf<Contact>()
    private var messageValue = MutableStateFlow("")
    private var isCategoryListScreen = MutableStateFlow(false)
    private var firstRecogMessage = MutableStateFlow(false)
    private var changeMsgInitiated: Boolean = false
    override fun onReceiveBundle(bundle: ParseBundle<out Any>) {
        job.launch {
            _sealedParsedData.emit(SealedParsedData.SendMsgData(parsedData(bundle)))
        }
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


    fun parsedData(bundle: ParseBundle<out Any?>): ProcSendMsgData {
        /**
         * ParseBundle<out Any>? 타입의 bundle을 SendMsgData로 파싱하는 로직이 담겨야함
         */
        when (bundle.dialogueMode) {
            DialogueMode.SEND_MESSAGE -> {
                return procSendMsgIntention(bundle)
            }

            DialogueMode.SEND_MESSAGE_NAME -> {
                return procMessageNameIntention(bundle)
            }

            DialogueMode.SEND_MESSAGE_NAME_CHANGE -> {
                return procMessageChangeIntention(bundle)
            }

            DialogueMode.LIST -> {
                val commonModel = bundle.model as? CommonModel
                commonModel?.let {
                    procListIntention(it)
                } ?: run {
                    // TODO : reject()
                    return handleRejectIntention()
                }
                // TODO : reject()
                return handleRejectIntention()
            }

            else -> {
                // TODO : reject()
                return handleRejectIntention()
            }
        }
    }


    private fun procSendMsgIntention(bundle: ParseBundle<out Any?>): ProcSendMsgData {

        val bundle = SendMsgModel("")
        Log.d("sendMsg","procSendMsgIntention 안")
//        val sendMsgModel = bundle.model as? SendMsgModel
        val sendMsgModel = bundle as? SendMsgModel

        initSendMsgValue()

//        val errorNotice = contactsManager.preConditionCheck(DomainType.SendMessage)
        val errorNotice: NoticeModel? = null

        if (errorNotice != null) {
            return ProcSendMsgData(
                domainType = SealedDomainType.Announce,
                screenType = ScreenType.Prepare,
                data = SendMsgDataType.ErrorMsgData(
                    notice = errorNotice
                ),
                mwContext = MWContext(
                    DialogueMode.NONE, this@SendMsgManager
                )
            )
        }

        Log.d("sendMsg","procSendMsgIntention 직전")
        sendMsgModel?.let {
            checkMessageExistence(sendMsgModel)
            Log.d("sendMsg","sendMsgModel? 안")

            if (sendMsgModel.items.isEmpty()) {
                Log.d("sendMsg","procSendMsgIntention , items isEmpty")
//                sendMsgContactList = contactsManager.makePhoneBookContactList()
                sendMsgContactList = fetchAllContacts()
                return ProcSendMsgData(
                    screenType = ScreenType.MessageAllList,
                    mwContext = MWContext(
                        DialogueMode.SEND_MESSAGE, this@SendMsgManager
                    ),
                    data = SendMsgDataType.SendMsgData(contacts = sendMsgContactList ,msgData = null)
                )
            } else {

                Log.d("sendMsg","sendMsgModel? else 안")
                val nameCheckList =
                    contactsManager.makeContactList(sendMsgModel.getContactItems(), true)

                when (nameCheckList.size) {
                    0 -> {
                        sendMsgContactList = nameCheckList.toMutableList()
                        return ProcSendMsgData(
                            screenType = ScreenType.MessageAllList,
                            mwContext = MWContext(
                                DialogueMode.SEND_MESSAGE, this@SendMsgManager
                            ),
                            data = SendMsgDataType.SendMsgData(msgData = null)
                        )
                    }

                    1 -> {
                        return handleCategory(sendMsgModel)
                    }
                    // 검색된 이름 여러개
                    else -> {
                        return ProcSendMsgData(
                            screenType = ScreenType.MessageSelectNameList,
                            mwContext = MWContext(
                                DialogueMode.LIST, this@SendMsgManager
                            ),
                            data = SendMsgDataType.SendMsgData(
                                contacts = sendMsgContactList,
                            )
                        )
                    }
                }
            }
        } ?: run {
            // TODO : reject()
            Log.d("sendMsg","reject 안")
            return handlePopIntention()
        }
    }


    private fun procMessageNameIntention(bundle: ParseBundle<out Any?>): ProcSendMsgData {
        val commonModel = bundle.model as? CommonModel
        val sendMsgModel = bundle.model as? SendMsgModel

        commonModel?.let {
            val intention = it.intention.replace(" ", "")
            printSttString(it.prompt)

            if (Intentions.No.isEqual(intention)) {
                // list 선택 시 Message 없음
                return handlePopIntention(clearMsg = true)
            }
            // 로직상 무조건 no만 할 수 있어서 여기로 올 수 없음
            return handleRejectIntention()
        }

        sendMsgModel?.let { it ->
            val intention = it.intention.replace(" ", "")

            printSttString(it.prompt)

            if (Intentions.No.isEqual(intention)) {
                // 즉 cm이 있거나 send가 없으면 데이터 없음
                // 반대로 하면 cm이 없으면서 send가 있면 데이터 있음 -> checkScreenStack(send)
                // name 시나리오 : say - send - no - say - no  - list -> 데이터 유지
                // name 시나리오 : say - send - cm - say- no - list -> 데이터 없음
                // name 시나리오 : say - no -list -> 데이터 없음
                // name,msg 시나리오 : send - cm - say - no - list -> 데이터 없음
                if (!changeMsgInitiated && checkUiStateStack(ScreenType.SendMessage)) return handlePopIntention()
                return handlePopIntention(clearMsg = true)
            }

            // Message 발화 시 넘어 오는 intention
            if (Intentions.MessageContent.isEqual(intention)) {
                messageValue.value = it.messageValue
                // Send Message <Name, Msg> 시나리오에서 Change Message 후 Message 발화하여 다시 MessageChange 화면
                // "No" 발화 시 MessageName이 아닌 상황별 List나 PTT를 띄워야 함
                if (firstRecogMessage.value) {
                    selectedPhonebookItem?.let {
                        // changeMessageChange(it.name, sendMsgModel.messageValue)
                        // add 하지 않고 change / data -> Name, Message 넘겨줌
                        return ProcSendMsgData(
                            domainType = SealedDomainType.SendMessage,
                            screenType = ScreenType.SendMessage,
                            mwContext = MWContext(
                                dialogueMode = DialogueMode.SEND_MESSAGE_NAME_CHANGE,
                                resultListener = this@SendMsgManager
                            ),
                            data = SendMsgDataType.SendMsgData(
                                msgData = MsgData(name = it.name, msg = messageValue.value),
                                screenData = ScreenData.CHANGE
                            )
                        )
                    }
                } else {
                    // 1. Send Message 후 연락처 선택 후 Message 발화
                    // 2. Send Message <Name>
                    // 3. Change Message 발화 -> Say the Message 화면에서 발화
                    selectedPhonebookItem?.let {
                        // launchMessageChange(it.name, sendMsgModel.messageValue)
                        // addScreen / data -> Name, Message 넘겨줌
                        return ProcSendMsgData(
                            domainType = SealedDomainType.SendMessage,
                            screenType = ScreenType.SendMessage,
                            mwContext = MWContext(
                                dialogueMode = DialogueMode.SEND_MESSAGE_NAME_CHANGE,
                                resultListener = this@SendMsgManager
                            ),
                            data = SendMsgDataType.SendMsgData(
                                msgData = MsgData(name = it.name, msg = messageValue.value),
                            )
                        )
                    }
                }
            }
        }
        // TODO reject()
        return handlePopIntention()
    }


    private fun procMessageChangeIntention(bundle: ParseBundle<out Any?>): ProcSendMsgData {

        val commonModel = bundle.model as? CommonModel
        val sendMsgModel = bundle.model as? SendMsgModel

        commonModel?.let {
            val intention = commonModel.intention.replace(" ", "")
            printSttString(commonModel.prompt)

            if (Intentions.Yes.isEqual(intention)) {
                return ProcSendMsgData(
                    screenType = ScreenType.ScreenStack,
                    data = SendMsgDataType.SendScreenData(
                        screenData = ScreenData.BtPhoneAppRun
                    )
                )
            } else return handlePopIntention()
            // name,msg 시나리오 : send - no - list -> 데이터 유지
            // # No 발화시 List화면 / PTT화면 (List선택시 Message유지)
            // name,msg 시나리오 : send - cm - say- msg - no - list -> 데이터 유지
            //Send Message <Name, Msg> 시나리오
        }

        sendMsgModel?.let {
            val intention = sendMsgModel.intention.replace(" ", "")
            printSttString(it.prompt)

            if (Intentions.ChangeSMS.isEqual(intention)) {
                //onChangeMessage()
                // sayMessage 화면전환 / data -> Name
                changeMsgInitiated = true
                if (checkUiStateStack(ScreenType.SayMessage)) {
                    // popScreen & vrmwManager.stop
                    return handlePopIntention()
                }
                // Send Message <Name, Msg>
                else {
                    // changeMessageName()
                    return ProcSendMsgData(
                        domainType = SealedDomainType.SendMessage,
                        screenType = ScreenType.SayMessage,
                        mwContext = MWContext(
                            dialogueMode = DialogueMode.SEND_MESSAGE_NAME,
                            resultListener = this@SendMsgManager
                        ),
                        data = SendMsgDataType.SendMsgData(
                            msgData = MsgData(name = selectedPhonebookItem!!.name),
                            screenData = ScreenData.CHANGE
                        )
                    )
                }
            } else {
                // TODO reject()
                return handlePopIntention()
            }

        } ?: run {
            // TODO reject()
            return handlePopIntention()
        }
    }

    private fun procListIntention(data: CommonModel): ProcSendMsgData {
        // data만 넘겨주고 초점 맞추는 로직은 viewModel에서 구현
        return ProcSendMsgData(
            screenType = ScreenType.List, data = SendMsgDataType.SendListNum(
                index = data.index
            )
        )
    }


    private fun handleCategory(sendMsgModel: SendMsgModel): ProcSendMsgData {
        sendMsgContactList = contactsManager.makeContactList(sendMsgModel.getContactItems(), false)
        if (sendMsgContactList.size > 1) {
            //getCategoryListRunnable().run()
            isCategoryListScreen.value = true
            return ProcSendMsgData(
                screenType = ScreenType.MessageSelectCategoryList, mwContext = MWContext(
                    DialogueMode.LIST, this@SendMsgManager
                ), data = SendMsgDataType.SendMsgData(
                    contacts = sendMsgContactList,
                )
            )
        }
        // 1명의 전화번호부에 Category가 1개
        else {
            selectedPhonebookItem = sendMsgContactList[0]
            if (messageValue.value != "") {
                // getMessageNameRunnable().run()
                return ProcSendMsgData(
                    screenType = ScreenType.SayMessage, mwContext = MWContext(
                        DialogueMode.SEND_MESSAGE_NAME, this@SendMsgManager
                    ), data = SendMsgDataType.SendMsgData(
                        msgData = MsgData(name = sendMsgModel.getContactItems()[0].name),
                    )
                )
            } else {
                // getMessageChangeRunnable(sendMsgModel).run()
                return ProcSendMsgData(
                    screenType = ScreenType.SendMessage, mwContext = MWContext(
                        DialogueMode.SEND_MESSAGE_NAME_CHANGE, this@SendMsgManager
                    ), data = SendMsgDataType.SendMsgData(
                        msgData = MsgData(
                            name = sendMsgModel.getContactItems()[0].name,
                            msg = sendMsgModel.messageValue
                        ),
                    )
                )
            }
        }
    }


    private fun handlePopIntention(clearMsg: Boolean = false): ProcSendMsgData {
        if (clearMsg) {
            return ProcSendMsgData(
                screenType = ScreenType.ScreenStack, data = SendMsgDataType.SendScreenData(
                    screenData = ScreenData.POP, clearMsg = true
                )
            )
        }
        return ProcSendMsgData(
            screenType = ScreenType.ScreenStack, data = SendMsgDataType.SendScreenData(
                screenData = ScreenData.POP
            )
        )
    }

    private fun handleRejectIntention(): ProcSendMsgData {
        return ProcSendMsgData(
            screenType = ScreenType.ScreenStack, data = SendMsgDataType.SendScreenData(
                screenData = ScreenData.REJECT
            )
        )
    }

    // message 발화 화면(sayMessage)이 uiStateStack에 있는지 체크
    private fun checkUiStateStack(screenType: ScreenType): Boolean {
        var checkResult = false

        for (domainUiPair in UiState._domainUiStateStack) {
            val uiState = domainUiPair.first
            if (uiState.screenType == screenType) checkResult = true
        }
        return checkResult
    }

    private fun checkMessageExistence(sendMsgModel: SendMsgModel) {
        if (sendMsgModel.messageValue != "") {
            messageValue.value = sendMsgModel.messageValue
            // 시나리오가 <Name, Msg>인지 판단 용도
            firstRecogMessage.value = true
        }
    }

    private fun initSendMsgValue() {
        CustomLogger.i("initSendMsgValue")
        selectedPhonebookItem = null
        sendMsgContactList = mutableListOf()
        // PTT에서 시나리오를 처음 탔을 경우만 초기화 시키려고 사용
        // Send Message <Name, Msg> 발화 시 없는 이름인 경우 이름을 다시 발화하면서 procSendMsgIntention이 탐
        // initSendMsgValue를 타면서 messageValue 초기화도 시켜버림
        // screenDeque를 보고 시나리오가 처음 시작된 경우만 messageValue 초기화
        if (UiState._domainUiStateStack.isEmpty()) {
            messageValue.value = ""
            firstRecogMessage.value = false
        }
        isCategoryListScreen.value = false
    }

    fun requestBtPhoneAppRun(tabId: Int = 1) {
        CustomLogger.d("requestBtPhoneAppRun: tabId:$tabId")
        btCall.requestBtPhoneAppRun(tabId)
    }

    private fun sendMessage(name: String, phoneNumber: String, message: String) {
        CoroutineScope(Dispatchers.IO).launch {
            CustomLogger.d("sendMessage: name:$name, phoneNumber: $phoneNumber, message: $message")
            btCall.sendMessage(name, phoneNumber, message)
        }
    }

    fun fetchAllContacts(): ArrayList<Contact> {
        val result = arrayListOf<Contact>()
        result.add(Contact(id = "1", name = "문재민", number = "010-1111-2222"))
        result.add(Contact(id = "2", name = "삐쓰까또레부르쥬미첼라햄페스츄리치즈나쵸스트링스파게티", number = "010-2222-3333"))
        result.add(Contact(id = "3", name = "하늘별님구름햇님보다사랑스러우리", number = "010-3333-4444"))
        result.add(Contact(id = "4", name = "Alex", number = "010-4444-5555"))
        result.add(Contact(id = "5", name = "Alexander Sandor Signiel ", number = "010-4444-5555"))
        result.add(Contact(id = "6", name = "포티투닷 이순신", number = "010-4444-5555"))
        result.add(Contact(id = "7", name = "포티투닷 홍길동 책임연구원 하하하하하", number = "031-131"))
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

        return result
    }
}