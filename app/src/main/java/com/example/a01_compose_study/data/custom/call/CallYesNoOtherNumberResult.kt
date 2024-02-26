package com.example.a01_compose_study.data.custom.call

import com.example.a01_compose_study.data.Contact

sealed class CallYesNoOtherNumberResult {

    object Yes: CallYesNoOtherNumberResult()
    object No: CallYesNoOtherNumberResult()
    object Reject: CallYesNoOtherNumberResult()
    data class OtherNumberList(val data: List<Contact>): CallYesNoOtherNumberResult()
    data class OtherNumber(val data: Contact): CallYesNoOtherNumberResult()
}