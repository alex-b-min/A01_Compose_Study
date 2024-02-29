package com.example.a01_compose_study.presentation.screen.main

import com.example.a01_compose_study.domain.model.ScreenType
import com.example.a01_compose_study.domain.model.SealedDomainType
import com.example.a01_compose_study.domain.util.ScreenSizeType
import com.example.a01_compose_study.presentation.screen.call.CallEvent

sealed class MainEvent() {
    object CloseDomainWindowEvent : MainEvent()

    /**
     * NoneDomainWindowEvent 가 screenSizeType을 갖는 이유는 현재 띄워져 있는 Window를 유지 하되 데이터가 없는 빈 화면을 None 화면으로 생각하였기 때문
     */
    data class NoneDomainWindowEvent(
        val screenSizeType: ScreenSizeType = ScreenSizeType.Zero,
    ) : MainEvent()

    data class OpenDomainWindowEvent(
        val domainType: SealedDomainType,
        val screenType: ScreenType,
        val data: Any?,
        val isError: Boolean,
        val screenSizeType: ScreenSizeType,
    ) : MainEvent()

    data class ChangeDomainWindowSizeEvent(
        val screenSizeType: ScreenSizeType,
    ) : MainEvent()

    data class ChangeScrollIndexEvent(
        val selectedScrollIndex: Int,
    ) : MainEvent()
}
