package com.example.a01_compose_study.data.custom.sendMsg

import com.example.a01_compose_study.data.Contact

sealed class SendMsgEvent {
    data class MsgAllListItemOnClick(
        val selectedSendMsgItem: Contact
    ) : SendMsgEvent()

    data class SelectNameListItemOnClick(
        val selectedSendMsgItem: Contact
    ) : SendMsgEvent()
    data class SelectCategoryListItemOnClick(
        val selectedSendMsgItem: Contact
    ) : SendMsgEvent()

    object OnBack : SendMsgEvent()
//    object SayMessage : SendMsgEvent()
    object SayMessageNo : SendMsgEvent()
    object SendMessageYes : SendMsgEvent()
//    object SendMessageNo : SendMsgEvent()
}