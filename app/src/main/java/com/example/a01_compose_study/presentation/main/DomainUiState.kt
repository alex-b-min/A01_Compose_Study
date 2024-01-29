package com.example.a01_compose_study.presentation.main

import com.example.a01_compose_study.domain.ScreenType
import com.example.a01_compose_study.domain.SealedDomainType
import com.example.a01_compose_study.domain.util.ScreenSizeType

sealed class DomainUiState(
    open val screenSizeType: ScreenSizeType = ScreenSizeType.Small
) {
    data class NoneWindow(
        override val screenSizeType: ScreenSizeType = ScreenSizeType.Small
    ) : DomainUiState(screenSizeType)

    data class HelpWindow(
        val domainType: SealedDomainType,
        val screenType: ScreenType,
        val data: Any,
        val visible: Boolean,
        val isError: Boolean = false,
        val text: String = "",
        override val screenSizeType: ScreenSizeType = ScreenSizeType.Small
    ) : DomainUiState(screenSizeType)

    data class AnnounceWindow(
        override val screenSizeType: ScreenSizeType = ScreenSizeType.Small
    ) : DomainUiState(screenSizeType)

    data class DomainMenuWindow(
        override val screenSizeType: ScreenSizeType = ScreenSizeType.Small
    ) : DomainUiState(screenSizeType)

    data class CallWindow(
        override val screenSizeType: ScreenSizeType = ScreenSizeType.Small
    ) : DomainUiState(screenSizeType)

    data class NavigationWindow(
        override val screenSizeType: ScreenSizeType = ScreenSizeType.Small
    ) : DomainUiState(screenSizeType)

    data class RadioWindow(
        override val screenSizeType: ScreenSizeType = ScreenSizeType.Small
    ) : DomainUiState(screenSizeType)

    data class WeatherWindow(
        override val screenSizeType: ScreenSizeType = ScreenSizeType.Small
    ) : DomainUiState(screenSizeType)

    data class SendMessageWindow(
        override val screenSizeType: ScreenSizeType = ScreenSizeType.Small
    ) : DomainUiState(screenSizeType)

    fun copyWithNewSizeType(sizeType: ScreenSizeType): DomainUiState {
        return when (this) {
            is NoneWindow -> copy(screenSizeType = sizeType)
            is HelpWindow -> copy(screenSizeType = sizeType)
            is AnnounceWindow -> copy(screenSizeType = sizeType)
            is DomainMenuWindow -> copy(screenSizeType = sizeType)
            is CallWindow -> copy(screenSizeType = sizeType)
            is NavigationWindow -> copy(screenSizeType = sizeType)
            is RadioWindow -> copy(screenSizeType = sizeType)
            is WeatherWindow -> copy(screenSizeType = sizeType)
            is SendMessageWindow -> copy(screenSizeType = sizeType)
        }
    }
}
