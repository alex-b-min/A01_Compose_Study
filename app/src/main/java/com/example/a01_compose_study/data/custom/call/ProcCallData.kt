package com.example.a01_compose_study.data.custom.call

import com.example.a01_compose_study.data.Contact
import com.example.a01_compose_study.domain.model.NoticeModel
import com.example.a01_compose_study.domain.model.ScreenType
import com.example.a01_compose_study.domain.model.SealedDomainType
import com.example.a01_compose_study.domain.util.ScreenSizeType

sealed class ProcCallData(
    val sealedDomainType: SealedDomainType = SealedDomainType.Call,
    val screenSizeType: ScreenSizeType = ScreenSizeType.Large,
    val screenType: ScreenType = ScreenType.None,
) {

    object RejectRequest : ProcCallData(
        sealedDomainType = SealedDomainType.Announce,
        screenType = ScreenType.Prepare
    )

    data class ListTTSRequest(val promptId: String) : ProcCallData(screenType = ScreenType.CallIndexedList)
    data class NoticeTTSRequest(val noticeModel: NoticeModel) : ProcCallData(screenType = ScreenType.CallIndexedList)
    data class ProcCallNameScreen(val data: Contact) : ProcCallData(screenType = ScreenType.CallYesNo) // CallName 화면
    data class RecognizedContactListScreen(val data: List<Contact>) : ProcCallData(screenType = ScreenType.CallIndexedList) // 인식된 전화번호부
    data class AllContactListScreen(val data: List<Contact>) : ProcCallData(screenType = ScreenType.CallIndexedList) // 전체 전화번호부
    data class ScrollIndex(val index: Int?) : ProcCallData(screenType = ScreenType.CallIndexedList)
    data class ProcYesNoOtherNumberResult(val callYesNoOtherNumberResult: CallYesNoOtherNumberResult) :
        ProcCallData(screenType = ScreenType.CallIndexedList)
}
