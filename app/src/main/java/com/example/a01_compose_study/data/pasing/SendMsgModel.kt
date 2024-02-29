
package com.example.a01_compose_study.data.pasing

import com.example.a01_compose_study.data.Contact


data class SendMsgModel(var intent: String) : BaseModel(intent) {

    var enableIndex = true
    var prompt: String = ""
    var messageValue: String = ""

    fun getContactItems(): MutableList<Contact> {
        return items as MutableList<Contact>
    }
}
