package com.example.a01_compose_study.data.custom.sendMsg

import com.example.a01_compose_study.data.Contact

sealed class SendMsgEvent {
    data class SendMsgAllListItemOnClick(
        val selectedSendMsgItem: Contact
    ) : SendMsgEvent()

    data class SendMsgNameListItemOnClick(
        val selectedSendMsgItem: Contact
    ) : SendMsgEvent()

    data class SendMsgCategoryListItemOnClick(
        val selectedSendMsgItem: Contact
    ) : SendMsgEvent()

    object OnBack : SendMsgEvent()

    object SayMessage : SendMsgEvent()
    object SayMessageNo : SendMsgEvent()
    object SendMessage : SendMsgEvent()
    object SendMessageYes : SendMsgEvent()
    object SendMessageNo : SendMsgEvent()

}