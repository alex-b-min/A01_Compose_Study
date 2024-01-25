package com.example.a01_compose_study.data

//phoneType: ContactsContract.CommonDataKinds.Phone.TYPE_HOME etc..
//https://developer.android.com/reference/android/provider/ContactsContract.CommonDataKinds.Phone#TYPE_MOBILE
data class Contact(
    var id: String = "",
    var contact_id: String = "",
    var first_name: String = "",
    var middle_name: String = "",
    var last_name: String = "",
    var display_name: String = "",
    var image: String? = null,
    var name: String = "",
    var nameRmSpecial: String = "",
    var number: String = "",
    var type: Int = 0,
    var slotType: String = "",
    var memory_type: Int = 0,
) {


    fun trimFirstName(): String {
        return first_name.trim()
    }

    fun trimLastName(): String {
        return last_name.trim()
    }

    fun trimName(): String {
        return name.trim()
    }

    override fun toString(): String {
        return "Contact(id='$id', contact_id='$contact_id', first_name='$first_name', " +
                "middle_name='$middle_name', last_name='$last_name', display_name='$display_name', " +
                "image='$image', name='$name', nameRmSpecial='$nameRmSpecial', number='$number', type=$type, slotType=$slotType, memory_type=$memory_type)"
    }

//    fun copy(slotType: String) : Contact{
//        this.slotType = slotType
//        return this.copy()
//    }
}


data class ACLDevice(
    val device: String = "",
    val connect: Boolean = false,
)

data class HfpDevice(
    val device: String = "",
    val connect: Boolean = false,
    var recognizing: Boolean = false
)
