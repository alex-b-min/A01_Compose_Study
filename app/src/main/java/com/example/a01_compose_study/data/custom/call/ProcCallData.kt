package com.example.a01_compose_study.data.custom.call

import com.example.a01_compose_study.data.Contact
import com.example.a01_compose_study.domain.model.NoticeModel

sealed class ProcCallData {

    object RejectRequest : ProcCallData()
    data class ListTTSRequest(val promptId: String) : ProcCallData()
    data class NoticeTTSRequest(val noticeModel: NoticeModel) : ProcCallData()
    data class ProcCallNameScreen(val data: Contact) : ProcCallData()
    data class ContactListScreen(val data: List<Contact>) : ProcCallData()
    data class FullContactListScreen(val data: List<Contact>) : ProcCallData()
    data class ScrollIndex(val index: Int?) : ProcCallData()
    data class YesNoOtherNumberResultProc(val callYesNoOtherNumberResult: CallYesNoOtherNumberResult) : ProcCallData()
}
