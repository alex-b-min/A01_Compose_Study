package com.example.a01_compose_study.presentation.main

import com.example.a01_compose_study.domain.ScreenType
import com.example.a01_compose_study.domain.SealedDomainType
import com.example.a01_compose_study.domain.util.ScreenSizeType

sealed class MainEvent() {
    object CloseDomainWindowEvent : MainEvent()

    data class OpenDomainWindowEvent(
        val domainType: SealedDomainType,
        val screenType: ScreenType,
        val data: Any,
        val isError: Boolean,
        val screenSizeType: ScreenSizeType
    ) : MainEvent()

//    data class OpenHelpWindowEvent(
//        val isError: Boolean,
//        val text: String,
//        val screenSizeType: ScreenSizeType
//    ) : MainEvent()
}