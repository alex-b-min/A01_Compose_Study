package com.example.a01_compose_study.data.custom.sendMsg

import com.example.a01_compose_study.data.Contact
import com.example.a01_compose_study.data.custom.MWContext
import com.example.a01_compose_study.domain.model.NoticeModel
import com.example.a01_compose_study.domain.model.ScreenType
import com.example.a01_compose_study.domain.model.SealedDomainType

data class ProcSendMsgData(
    val domainType: SealedDomainType = SealedDomainType.SendMessage,
    val screenType: ScreenType = ScreenType.None,
    val data: SendMsgDataType,
    val mwContext: MWContext? = null,
)

sealed class SendMsgDataType {
    data class SendMsgData(
        val contacts: List<Contact>? = null,
        val msgData: MsgData? = null,
        val screenData: ScreenData = ScreenData.PUSH
    ) : SendMsgDataType()

    data class SendScreenData(
        val screenData: ScreenData,
        val clearMsg: Boolean = false,
    ) : SendMsgDataType()

    //open domain event말고 다른 이벤트 고려
    data class SendListNum(
        val index: Int?
    ) : SendMsgDataType()

    data class ErrorMsgData(
        val notice: NoticeModel
    ) : SendMsgDataType()
}
enum class ScreenData {
    PUSH,
    POP,
    CHANGE,
    REJECT,
    BtPhoneAppRun,
}

data class MsgData(
    val name: String = "",
    val phoneNum: String = "",
    val msg: String = "",
)