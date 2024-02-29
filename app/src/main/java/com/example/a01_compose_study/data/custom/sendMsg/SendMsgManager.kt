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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SendMsgManager @Inject constructor(
    private val contactsManager: ContactsManager,
    private val job: CoroutineScope,
) : VRResultListener {

    var selectedPhonebookItem: Contact? = null
    private var sendMsgContactList = mutableListOf<Contact>()
    private var messageValue = MutableStateFlow("")
    private var isCategoryListScreen = MutableStateFlow(false)
    private var firstRecogMessage = MutableStateFlow(false)

    fun init() {

    }

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
                    return ProcSendMsgData(
                        data = SendMsgDataType.SendScreenData(
                            screenData = ScreenData.POP
                        )
                    )
                }
                // TODO : reject()
                return ProcSendMsgData(
                    data = SendMsgDataType.SendScreenData(
                        screenData = ScreenData.POP
                    )
                )
            }

            else -> {
                // TODO : reject()
                return ProcSendMsgData(
                    data = SendMsgDataType.SendScreenData(
                        screenData = ScreenData.POP
                    )
                )
            }
        }
    }


    private fun procSendMsgIntention(bundle: ParseBundle<out Any?>): ProcSendMsgData {
        /**
         * 실제 ParseBundle<out Any>?의 bundle을 통해 ProcSendMsgData 객체를 생성하는 로직이 담겨야함
         */
        val sendMsgModel = bundle.model as? SendMsgModel
        val errorNotice = contactsManager.preConditionCheck(DomainType.SendMessage)

        initSendMsgValue()

        if (errorNotice != null) {
            return ProcSendMsgData(
                domainType = SealedDomainType.Announce,
                screenType = ScreenType.PttAnounce,
                data = SendMsgDataType.ErrorMsgData(
                    notice = errorNotice
                )
            )
        } else {
            sendMsgModel?.let {

                // 시나리오에 Msg 유무 체크
                checkMessageExistence(sendMsgModel)

                // 인식된 name이 있는 경우
                if (sendMsgModel.items.size > 0) {

                    // sendMsgModel.items의 name으로 연락처 몇 개가 검색 되는지 확인
                    val nameCheckList =
                        contactsManager.makeContactList(sendMsgModel.getContactItems(), true)

                    // Name으로 찾은 이름이 여러 명
                    if (nameCheckList.size > 1) {
                        sendMsgContactList = nameCheckList.toMutableList()
                        // getListRunnable(bundle).run()
                        return ProcSendMsgData(
                            screenType = ScreenType.MessageSelectNameList,
                            data = SendMsgDataType.SendMsgData(
                                msgData = MsgData(contacts = sendMsgContactList),
                            )
                        )
                    }
                    // 검색된 결과가 없는 경우 - 전체 전화번호부 표시
                    else if (nameCheckList.size == 0) {
                        // 기존 전화번호부에서 리스트 생성
                        sendMsgContactList = contactsManager.makePhoneBookContactList()
                        //getNoResultListRunnable().run()
                        return ProcSendMsgData(
                            screenType = ScreenType.MessageAllList,
                            data = SendMsgDataType.SendMsgData(msgData = null)
                        )
                    }
                    // name으로 검색된 연락처가 1개
                    else {
                        // 1명의 전화번호부에 Category가 1개 이상 있는지 확인 하기 위해
                        // 위 if 조건과 filter가 다르므로 sendMsgContactList가 바뀌어야 함.
                        sendMsgContactList =
                            contactsManager.makeContactList(
                                sendMsgModel.getContactItems(),
                                false
                            )
                        // 1명의 전화번호부에 Category가 여러 개
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
                                    screenType = ScreenType.SendMessage,
                                    data = SendMsgDataType.SendMsgData(
                                        msgData = MsgData(
                                            name = sendMsgModel.getContactItems()[0].name,
                                            msg = sendMsgModel.messageValue
                                        ),
                                    )
                                )
                            } else {
                                // getMessageNameRunnable().run()
                                return ProcSendMsgData(
                                    screenType = ScreenType.SayMessage,
                                    data = SendMsgDataType.SendMsgData(
                                        msgData = MsgData(name = sendMsgModel.getContactItems()[0].name),
                                    )
                                )
                            }
                        }
                    }
                }
                // Send Message만 발화 시 Embedded Intention / Server Intention에 slot count 0 - 전체 전화번호부 표시
                else {
                    sendMsgContactList = contactsManager.makePhoneBookContactList()
                    // getContactListRunnable().run()
                    return ProcSendMsgData(
                        screenType = ScreenType.MessageAllList,
                        data = SendMsgDataType.SendMsgData(msgData = null)
                    )

                }
            } ?: run {
                // TODO : reject()
                return ProcSendMsgData(
                    data = SendMsgDataType.SendScreenData(
                        screenData = ScreenData.POP
                    )
                )
            }
        }
    }

    private fun procMessageNameIntention(bundle: ParseBundle<out Any?>): ProcSendMsgData {
        val commonModel = bundle.model as? CommonModel

        if (commonModel != null) {

            val intention = commonModel.intention.replace(" ", "")
            printSttString(commonModel.prompt)

            if (Intentions.No.isEqual(intention)) {
                // procMessageChange에서 온거라면 list 정보 가지고 있어야함
                // 그 외 popUiState()
                return ProcSendMsgData(
                    data = SendMsgDataType.SendScreenData(
                        screenData = ScreenData.POP
                    )
                )
            }
            // 로직상 무조건 no만 할 수 있어서 여기로 올 수 없음
            return ProcSendMsgData(
                data = SendMsgDataType.SendScreenData(
                    screenData = ScreenData.REJECT
                )
            )

        } else {
            val sendMsgModel = bundle.model as? SendMsgModel
            CustomLogger.i("procMessageNameIntention ${bundle.prompt}")
            sendMsgModel?.let {
                CustomLogger.i("procMessageNameIntention prompt : ${bundle.prompt} messageValue : ${sendMsgModel.messageValue}")
                val intention = sendMsgModel.intention.replace(" ", "")
                CustomLogger.i("procMessageNameIntention intention : $intention")

                printSttString(it.prompt)

                if (Intentions.No.isEqual(intention)) {
                    // popUiState()
                    return ProcSendMsgData(
                        data = SendMsgDataType.SendScreenData(
                            screenData = ScreenData.POP
                        )
                    )
                }
                // Message 발화 시 넘어 오는 intention
                else if (Intentions.MessageContent.isEqual(intention)) {
                    messageValue.value = sendMsgModel.messageValue
                    // Send Message <Name, Msg> 시나리오에서 Change Message 후 Message 발화하여 다시 MessageChange 화면
                    // "No" 발화 시 MessageName이 아닌 상황별 List나 PTT를 띄워야 함
                    if (firstRecogMessage.value) {
                        selectedPhonebookItem?.let {
                            CustomLogger.i("procMessageNameIntention selectedPhonebookItem?.let changeMessageChange")
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
                            CustomLogger.i("procMessageNameIntention selectedPhonebookItem?.let launchMessageChange")
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
                } else {
                    // TODO reject()
                    return ProcSendMsgData(
                        data = SendMsgDataType.SendScreenData(
                            screenData = ScreenData.POP
                        )
                    )
                }
            } ?: run {
                // TODO reject()
                return ProcSendMsgData(
                    data = SendMsgDataType.SendScreenData(
                        screenData = ScreenData.POP
                    )
                )
            }
        }
    }


    private fun procMessageChangeIntention(bundle: ParseBundle<out Any?>): ProcSendMsgData {

        val commonModel = bundle.model as? CommonModel

        if (commonModel != null) {
            val intention = commonModel.intention.replace(" ", "")
            printSttString(commonModel.prompt)

            if (Intentions.Yes.isEqual(intention)) {
                return ProcSendMsgData(
                    data = SendMsgDataType.SendScreenData(
                        screenData = ScreenData.BtPhoneAppRun
                    )
                )

            } else {
                return ProcSendMsgData(
                    data = SendMsgDataType.SendScreenData(
                        screenData = ScreenData.POP
                    )
                )
            }
        } else {

            val sendMsgModel = bundle.model as? SendMsgModel

            sendMsgModel?.let {
                val intention = sendMsgModel.intention.replace(" ", "")
                printSttString(it.prompt)

                if (Intentions.ChangeSMS.isEqual(intention)) {
                    //onChangeMessage()
                    // sayMessage 화면전환 / data -> Name
                    if (checkUiStateStack(ScreenType.SayMessage)) {
                        // popScreen & vrmwManager.stop
                        return ProcSendMsgData(
                            data = SendMsgDataType.SendScreenData(
                                screenData = ScreenData.POP
                            )
                        )
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
                    return ProcSendMsgData(
                        data = SendMsgDataType.SendScreenData(
                            screenData = ScreenData.POP
                        )
                    )
                }

            } ?: run {
                // TODO reject()
                return ProcSendMsgData(
                    data = SendMsgDataType.SendScreenData(
                        screenData = ScreenData.POP
                    )
                )
            }
        }
    }

    private fun procListIntention(data: CommonModel): ProcSendMsgData {
        // data만 넘겨주고 초점 맞추는 로직은 viewModel에서 구현
        return ProcSendMsgData(
            data = SendMsgDataType.SendListNum(
                index = data.index
            )
        )
    }

    // message 발화 화면(sayMessage)이 uiStateStack에 있는지 체크
    private fun checkUiStateStack(screenType: ScreenType): Boolean {
        var checkResult = false

        for (uiState in UiState._domainUiStateStack) {
            if (uiState.screenType == screenType)
                checkResult = true
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

    fun handleScreenData(screenData: ScreenData, uiState: DomainUiState){
        when(screenData){
            ScreenData.PUSH -> UiState.pushUiState(uiState)
            ScreenData.POP -> UiState.popUiState()
            ScreenData.CHANGE -> UiState.popUiState()
            ScreenData.REJECT -> TODO()
            ScreenData.BtPhoneAppRun -> TODO()
        }
    }
}