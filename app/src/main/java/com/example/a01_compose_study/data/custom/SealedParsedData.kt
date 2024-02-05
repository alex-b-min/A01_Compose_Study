package com.example.a01_compose_study.data.custom

import com.example.a01_compose_study.data.pasing.CallModel

sealed class SealedParsedData {

    object NoneData : SealedParsedData()

    data class HelpData(
        val procHelpData: ProcHelpData,
    ) : SealedParsedData()

    data class CallData(val data: CallModel) : SealedParsedData()
}