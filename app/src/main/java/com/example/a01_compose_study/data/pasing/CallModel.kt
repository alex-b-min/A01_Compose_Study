package com.example.a01_compose_study.data.pasing

import com.example.a01_compose_study.data.Contact

data class CallModel(var intent: String) : BaseModel(intent) {
    var enableIndex = true
    var lastVisiblePosition = -1
    var selectedItem = Contact()
    fun getContactItems(): MutableList<Contact> {
        return items as MutableList<Contact>
    }
}

//data class PhonebookItem(
//    var id : String? = null,
//    var contact_id : String = "",
//    var name: String? = null,
//    var phoneNumber: String? = null,
//    var type: Int = -1
//)
