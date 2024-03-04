package com.example.a01_compose_study.data.custom.sendMsg


import com.example.a01_compose_study.data.Contact
import com.example.a01_compose_study.data.DialogueMode
import com.example.a01_compose_study.data.DomainType

import com.example.a01_compose_study.data.HVRError
import com.example.a01_compose_study.data.Intentions
import com.example.a01_compose_study.data.analyze.ParseBundle
import com.example.a01_compose_study.data.custom.ContactsManager
import com.example.a01_compose_study.data.custom.SealedParsedData
import com.example.a01_compose_study.data.custom.VRResultListener
import com.example.a01_compose_study.data.custom.call.BtCall
import com.example.a01_compose_study.data.pasing.CommonModel
import com.example.a01_compose_study.data.pasing.SendMsgModel
import com.example.a01_compose_study.domain.model.ScreenType
import com.example.a01_compose_study.domain.model.SealedDomainType
import com.example.a01_compose_study.domain.util.CustomLogger
import com.example.a01_compose_study.presentation.data.UiState
import com.example.a01_compose_study.presentation.data.UiState._sealedParsedData
import com.example.a01_compose_study.presentation.screen.main.DomainUiState
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
                    return handlePopIntention()
                }
                // TODO : reject()
                return handlePopIntention()
            }

            else -> {
                // TODO : reject()
                return handlePopIntention()
            }
        }
    }


    private fun procSendMsgIntention(bundle: ParseBundle<out Any?>): ProcSendMsgData {
        val sendMsgModel = bundle.model as? SendMsgModel

        initSendMsgValue()

        val errorNotice = contactsManager.preConditionCheck(DomainType.SendMessage)

        if (errorNotice != null) {
            return ProcSendMsgData(
                domainType = SealedDomainType.Announce,
                screenType = ScreenType.Prepare,
                data = SendMsgDataType.ErrorMsgData(
                    notice = errorNotice
                )
            )
        }

        sendMsgModel?.let {
            checkMessageExistence(sendMsgModel)

            if (sendMsgModel.items.isEmpty()) {
                sendMsgContactList = contactsManager.makePhoneBookContactList()
                return ProcSendMsgData(
                    screenType = ScreenType.MessageAllList,
                    data = SendMsgDataType.SendMsgData(msgData = null)
                )
            } else {

                val nameCheckList =
                    contactsManager.makeContactList(sendMsgModel.getContactItems(), true)
                when (nameCheckList.size) {
                    0 -> {
                        sendMsgContactList = nameCheckList.toMutableList()
                        return ProcSendMsgData(
                            screenType = ScreenType.MessageAllList,
                            data = SendMsgDataType.SendMsgData(msgData = null)
                        )
                    }

                    1 -> {
                        return handleCategory(sendMsgModel)
                    }

                    else -> {
                        return ProcSendMsgData(
                            screenType = ScreenType.MessageSelectNameList,
                            data = SendMsgDataType.SendMsgData(
                                msgData = MsgData(contacts = sendMsgContactList),
                            )
                        )
                    }
                }
            }
        } ?: run {
            // TODO : reject()
            return handlePopIntention()
        }
    }


    private fun handleCategory(sendMsgModel: SendMsgModel): ProcSendMsgData {
        sendMsgContactList = contactsManager.makeContactList(sendMsgModel.getContactItems(), false)
        if (sendMsgContactList.size > 1) {
            //getCategoryListRunnable().run()
            isCategoryListScreen.value = true
            return ProcSendMsgData(
                screenType = ScreenType.MessageSelectCategoryList,
                data = SendMsgDataType.SendMsgData(
                    msgData = MsgData(contacts = sendMsgContactList),
                )
            )
        }
        // 1명의 전화번호부에 Category가 1개
        else {
            selectedPhonebookItem = sendMsgContactList[0]
            if (messageValue.value != "") {
                // getMessageChangeRunnable(sendMsgModel).run()
                return ProcSendMsgData(
                    screenType = ScreenType.SendMessage, data = SendMsgDataType.SendMsgData(
                        msgData = MsgData(
                            name = sendMsgModel.getContactItems()[0].name,
                            msg = sendMsgModel.messageValue
                        ),
                    )
                )
            } else {
                // getMessageNameRunnable().run()
                return ProcSendMsgData(
                    screenType = ScreenType.SayMessage, data = SendMsgDataType.SendMsgData(
                        msgData = MsgData(name = sendMsgModel.getContactItems()[0].name),
                    )
                )
            }
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
            return ProcSendMsgData(
                screenType = ScreenType.ScreenStack,
                data = SendMsgDataType.SendScreenData(
                    screenData = ScreenData.REJECT,
                )
            )
        }

        sendMsgModel?.let {
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

    private fun handlePopIntention(clearMsg: Boolean = false): ProcSendMsgData {
        if (clearMsg) {
            return ProcSendMsgData(
                screenType = ScreenType.ScreenStack,
                data = SendMsgDataType.SendScreenData(
                    screenData = ScreenData.POP,
                    clearMsg = true
                )
            )
        }
        return ProcSendMsgData(
            screenType = ScreenType.ScreenStack,
            data = SendMsgDataType.SendScreenData(
                screenData = ScreenData.POP
            )
        )
    }

    private fun handleRejectIntention(): ProcSendMsgData {
        return ProcSendMsgData(
            screenType = ScreenType.ScreenStack,
            data = SendMsgDataType.SendScreenData(
                screenData = ScreenData.REJECT
            )
        )
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

            } else {
                // name,msg 시나리오 : send - no - list -> 데이터 유지
                // # No 발화시 List화면 / PTT화면 (List선택시 Message유지)
                // name,msg 시나리오 : send - cm - say- msg - no - list -> 데이터 유지
                //Send Message <Name, Msg> 시나리오
                return handlePopIntention()
            }
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
            screenType = ScreenType.List,
            data = SendMsgDataType.SendListNum(
                index = data.index
            )
        )
    }

    // message 발화 화면(sayMessage)이 uiStateStack에 있는지 체크
    private fun checkUiStateStack(screenType: ScreenType): Boolean {
        var checkResult = false

        for (uiState in UiState._domainUiStateStack) {
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
}

//            // 인식된 name이 있는 경우
//            if (sendMsgModel.items.isNotEmpty()) {
//                // sendMsgModel.items의 name으로 연락처 몇 개가 검색 되는지 확인
//                val nameCheckList = contactsManager.makeContactList(sendMsgModel.getContactItems(), true)
//
//                // Name으로 찾은 이름이 여러 명
//                if (nameCheckList.size > 1) {
//                    sendMsgContactList = nameCheckList.toMutableList()
//                    // getListRunnable(bundle).run()
//                    return ProcSendMsgData(
//                        screenType = ScreenType.MessageSelectNameList,
//                        data = SendMsgDataType.SendMsgData(
//                            msgData = MsgData(contacts = sendMsgContactList),
//                        )
//                    )
//                }
//                // 검색된 결과가 없는 경우 - 전체 전화번호부 표시
//                else if (nameCheckList.size == 0) {
//                    // 기존 전화번호부에서 리스트 생성
//                    sendMsgContactList = contactsManager.makePhoneBookContactList()
//                    //getNoResultListRunnable().run()
//                    return ProcSendMsgData(
//                        screenType = ScreenType.MessageAllList,
//                        data = SendMsgDataType.SendMsgData(msgData = null)
//                    )
//                }
//                // name으로 검색된 연락처가 1개
//                else {
//                    // 1명의 전화번호부에 Category가 1개 이상 있는지 확인 하기 위해
//                    // 위 if 조건과 filter가 다르므로 sendMsgContactList가 바뀌어야 함.
//                    sendMsgContactList =
//                        contactsManager.makeContactList(
//                            sendMsgModel.getContactItems(),
//                            false
//                        )
//                    // 1명의 전화번호부에 Category가 여러 개
//                    if (sendMsgContactList.size > 1) {
//                        //getCategoryListRunnable().run()
//                        isCategoryListScreen.value = true
//                        return ProcSendMsgData(
//                            screenType = ScreenType.MessageSelectCategoryList,
//                            data = SendMsgDataType.SendMsgData(
//                                msgData = MsgData(contacts = sendMsgContactList),
//                            )
//                        )
//                    }
//                    // 1명의 전화번호부에 Category가 1개
//                    else {
//                        selectedPhonebookItem = sendMsgContactList[0]
//                        if (messageValue.value != "") {
//                            // getMessageChangeRunnable(sendMsgModel).run()
//                            return ProcSendMsgData(
//                                screenType = ScreenType.SendMessage,
//                                data = SendMsgDataType.SendMsgData(
//                                    msgData = MsgData(
//                                        name = sendMsgModel.getContactItems()[0].name,
//                                        msg = sendMsgModel.messageValue
//                                    ),
//                                )
//                            )
//                        } else {
//                            // getMessageNameRunnable().run()
//                            return ProcSendMsgData(
//                                screenType = ScreenType.SayMessage,
//                                data = SendMsgDataType.SendMsgData(
//                                    msgData = MsgData(name = sendMsgModel.getContactItems()[0].name),
//                                )
//                            )
//                        }
//                    }
//                }
//            }
//             //Send Message만 발화 시 Embedded Intention / Server Intention에 slot count 0 - 전체 전화번호부 표시
//
//            sendMsgContactList = contactsManager.makePhoneBookContactList()
//            // getContactListRunnable().run()
//            return ProcSendMsgData(
//                screenType = ScreenType.MessageAllList,
//                data = SendMsgDataType.SendMsgData(msgData = null)
//            )