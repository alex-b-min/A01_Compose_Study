package com.example.a01_compose_study.data.custom.call

import com.example.a01_compose_study.data.Contact
import com.example.a01_compose_study.domain.model.NoticeModel

sealed class ProcCallData {

    object RejectRequest : ProcCallData()
    data class ListTTSRequest(val promptId: String) : ProcCallData()
    data class NoticeTTSRequest(val noticeModel: NoticeModel) : ProcCallData()
    data class ProcCallNameScreen(val data: Contact) : ProcCallData() // CallName 화면
    data class RecognizedContactListScreen(val data: List<Contact>) : ProcCallData() // 인식된 전화번호부
    data class AllContactListScreen(val data: List<Contact>) : ProcCallData() // 전체 전화번호부
    data class ScrollIndex(val index: Int?) : ProcCallData()
    data class ProcYesNoOtherNumberResult(val callYesNoOtherNumberResult: CallYesNoOtherNumberResult) : ProcCallData()
}
