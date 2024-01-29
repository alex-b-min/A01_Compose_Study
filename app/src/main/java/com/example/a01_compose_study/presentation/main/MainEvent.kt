package com.example.a01_compose_study.presentation.main

import com.example.a01_compose_study.domain.ScreenType
import com.example.a01_compose_study.domain.SealedDomainType
import com.example.a01_compose_study.domain.util.ScreenSizeType

sealed class MainEvent() {
    object CloseDomainWindowEvent : MainEvent()

    /**
     * NoneDomainWindowEvent 가 screenSizeType을 갖는 이유는 현재 띄워져 있는 Window를 유지 하되 데이터가 없는 빈 화면을 None 화면으로 생각하였기 때문
     */
    data class NoneDomainWindowEvent(
        val screenSizeType: ScreenSizeType
    ) : MainEvent()

    data class OpenDomainWindowEvent(
        val domainType: SealedDomainType,
        val screenType: ScreenType,
        val data: Any,
        val isError: Boolean,
        val screenSizeType: ScreenSizeType
    ) : MainEvent()

    data class ChangeDomainWindowSizeEvent(
        val screenSizeType: ScreenSizeType
    ) : MainEvent()
}
