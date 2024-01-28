package com.example.a01_compose_study.presentation.main

import com.example.a01_compose_study.domain.ScreenType
import com.example.a01_compose_study.domain.SealedDomainType
import com.example.a01_compose_study.domain.util.ScreenSizeType

sealed class MainUiState(
    open val screenSizeType: ScreenSizeType = ScreenSizeType.Small
) {
    data class NoneWindow(
        override val screenSizeType: ScreenSizeType = ScreenSizeType.Small
    ) : MainUiState(screenSizeType)

    data class HelpWindow(
        val domainType: SealedDomainType,
        val screenType: ScreenType,
        val data: Any,
        val visible: Boolean,
        val isError: Boolean = false,
        val text: String = "",
        override val screenSizeType: ScreenSizeType = ScreenSizeType.Small
    ) : MainUiState(screenSizeType)

    data class AnnounceWindow(
        override val screenSizeType: ScreenSizeType = ScreenSizeType.Small
    ) : MainUiState(screenSizeType)

    data class MainMenuWindow(
        override val screenSizeType: ScreenSizeType = ScreenSizeType.Small
    ) : MainUiState(screenSizeType)

    data class CallWindow(
        override val screenSizeType: ScreenSizeType = ScreenSizeType.Small
    ) : MainUiState(screenSizeType)

    data class NavigationWindow(
        override val screenSizeType: ScreenSizeType = ScreenSizeType.Small
    ) : MainUiState(screenSizeType)

    data class RadioWindow(
        override val screenSizeType: ScreenSizeType = ScreenSizeType.Small
    ) : MainUiState(screenSizeType)

    data class WeatherWindow(
        override val screenSizeType: ScreenSizeType = ScreenSizeType.Small
    ) : MainUiState(screenSizeType)

    data class SendMessageWindow(
        override val screenSizeType: ScreenSizeType = ScreenSizeType.Small
    ) : MainUiState(screenSizeType)
}
