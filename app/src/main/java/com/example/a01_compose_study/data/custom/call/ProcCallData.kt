package com.example.a01_compose_study.data.custom.call

import com.example.a01_compose_study.data.Contact
import com.example.a01_compose_study.data.custom.MWContext
import com.example.a01_compose_study.domain.model.NoticeModel
import com.example.a01_compose_study.domain.model.ScreenType
import com.example.a01_compose_study.domain.model.SealedDomainType
import com.example.a01_compose_study.domain.util.ScreenSizeType

sealed class ProcCallData(
    val sealedDomainType: SealedDomainType = SealedDomainType.Call,
    val screenSizeType: ScreenSizeType = ScreenSizeType.Large,
    val screenType: ScreenType = ScreenType.None,
    open val mwContext: MWContext? = null
) {
    object RejectRequest : ProcCallData()

    data class ListTTSRequest(
        val promptId: String,
        override val mwContext: MWContext
    ) : ProcCallData(mwContext = mwContext)

    data class NoticeTTSRequest(
        val noticeModel: NoticeModel,
        override val mwContext: MWContext
    ) : ProcCallData(mwContext = mwContext)

    data class ProcCallNameScreen(
        val data: Contact,
        override val mwContext: MWContext
    ) : ProcCallData(screenType = ScreenType.CallYesNo, mwContext = mwContext)

    data class RecognizedContactListScreen(
        val data: List<Contact>,
        override val mwContext: MWContext
    ) : ProcCallData(screenType = ScreenType.CallIndexedList, mwContext = mwContext)

    data class AllContactListScreen(
        val data: List<Contact>,
        override val mwContext: MWContext
    ) : ProcCallData(screenType = ScreenType.CallList, mwContext = mwContext)

    data class ScrollIndex(
        val index: Int?,
        override val mwContext: MWContext
    ) : ProcCallData(screenType = ScreenType.CallIndexedList, mwContext = mwContext)

    data class ProcYesNoOtherNumberResult(
        val callYesNoOtherNumberResult: CallYesNoOtherNumberResult,
        override val mwContext: MWContext
    ) : ProcCallData(screenType = ScreenType.CallYesNo, mwContext = mwContext)

    data class ProcOtherNumberResult(
        override val mwContext: MWContext
    ) : ProcCallData(screenType = ScreenType.CallYesNo, mwContext = mwContext)

    data class ProcYesResult(
        override val mwContext: MWContext
    ) : ProcCallData(screenType = ScreenType.CallYesNo, mwContext = mwContext)
}
