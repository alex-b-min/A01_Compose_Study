package com.example.a01_compose_study.data.custom

import com.example.a01_compose_study.data.HVRError
import com.example.a01_compose_study.data.custom.call.ProcCallData
import com.example.a01_compose_study.data.custom.sendMsg.ProcSendMsgData

sealed class SealedParsedData {

    object NoneData : SealedParsedData()
    data class ErrorData(
        val error: HVRError,
    ) : SealedParsedData()

    data class HelpData(
        val procHelpData: ProcHelpData,
    ) : SealedParsedData()

    data class CallData(
        val procCallData: ProcCallData,
    ) : SealedParsedData()

    data class SendMsgData(
        val procSendMsgData: ProcSendMsgData
    ) : SealedParsedData()
}