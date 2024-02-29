package com.example.a01_compose_study.presentation.screen.ptt

import com.example.a01_compose_study.data.custom.MWContext
import com.example.a01_compose_study.presentation.screen.main.SelectVRResult

sealed class PttEvent {

    object SetLoadingType : PttEvent()
    object SetSpeakType : PttEvent()
    object SetListenType : PttEvent()
    object PreparePtt : PttEvent()
    data class StartVR(
//        val mwContext: MWContext,
        val selectVRResult: SelectVRResult,
        val promptString: String = "",
        val promptArgs: List<String> = listOf()
    ) : PttEvent()
}