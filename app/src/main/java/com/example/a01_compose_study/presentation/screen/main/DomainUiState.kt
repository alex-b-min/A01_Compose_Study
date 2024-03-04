package com.example.a01_compose_study.presentation.screen.main

import com.example.a01_compose_study.data.Contact
import com.example.a01_compose_study.data.custom.sendMsg.MsgData
import com.example.a01_compose_study.data.custom.sendMsg.ScreenData
import com.example.a01_compose_study.domain.model.HelpItemData
import com.example.a01_compose_study.domain.model.ScreenType
import com.example.a01_compose_study.domain.model.SealedDomainType
import com.example.a01_compose_study.domain.util.ScreenSizeType

sealed class DomainUiState(
    open val screenSizeType: ScreenSizeType = ScreenSizeType.Zero,
    open val screenType: ScreenType = ScreenType.None,
    open val scrollIndex: Int? = null
) {
    data class NoneWindow(
        override val screenSizeType: ScreenSizeType = ScreenSizeType.Zero,
        override val scrollIndex: Int? = null
    ) : DomainUiState(screenSizeType)

    data class PttWindow(
        val domainType: SealedDomainType,
        override val screenType: ScreenType,
        val isError: Boolean = false,
        val errorText: String = "",
        val guideText: String = "",
        override val screenSizeType: ScreenSizeType = ScreenSizeType.Middle,
        override val scrollIndex: Int? = null
    ) : DomainUiState(screenSizeType, screenType, scrollIndex)

    data class HelpWindow(
        val domainType: SealedDomainType,
        override val screenType: ScreenType,
        val data: List<HelpItemData>,
        val detailData: HelpItemData = HelpItemData(command = ""),
        val isError: Boolean = false,
        val text: String = "",
        override val screenSizeType: ScreenSizeType = ScreenSizeType.Small,
        override val scrollIndex: Int? = null
    ) : DomainUiState(screenSizeType, screenType, scrollIndex)

    data class AnnounceWindow(
        val text: String,
        override val screenSizeType: ScreenSizeType = ScreenSizeType.Small,
        override val scrollIndex: Int? = null
    ) : DomainUiState(screenSizeType, scrollIndex = scrollIndex)

    data class CallWindow(
        val domainType: SealedDomainType,
        val data: List<Contact>,
        val detailData: Contact = Contact(),
        val isError: Boolean = false,
        override val scrollIndex: Int? = null,
        override val screenType: ScreenType,
        override val screenSizeType: ScreenSizeType,
    ) : DomainUiState(screenSizeType, screenType, scrollIndex)

    data class DomainMenuWindow(
        override val screenSizeType: ScreenSizeType = ScreenSizeType.Zero,
        override val scrollIndex: Int? = null
    ) : DomainUiState(screenSizeType)

    data class NavigationWindow(
        override val screenSizeType: ScreenSizeType = ScreenSizeType.Zero,
        override val scrollIndex: Int? = null
    ) : DomainUiState(screenSizeType)

    data class RadioWindow(
        override val screenSizeType: ScreenSizeType = ScreenSizeType.Zero,
        override val scrollIndex: Int? = null
    ) : DomainUiState(screenSizeType)

    data class WeatherWindow(
        override val screenSizeType: ScreenSizeType = ScreenSizeType.Zero,
        override val scrollIndex: Int? = null
    ) : DomainUiState(screenSizeType)

    data class SendMessageWindow(
        val domainType: SealedDomainType,
        override val screenType: ScreenType,
        val msgData: MsgData? = null,
        val selectListItem: Contact? = null,
        val isError: Boolean = false,
        val screenData: ScreenData,
        override val screenSizeType: ScreenSizeType = ScreenSizeType.Zero,
        override val scrollIndex: Int? = null
    ) : DomainUiState(screenSizeType, screenType, scrollIndex)

    fun copyWithNewSizeType(sizeType: ScreenSizeType): DomainUiState {
        return when (this) {
            is NoneWindow -> copy(screenSizeType = sizeType)
            is PttWindow -> copy(screenSizeType = sizeType)
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

    fun copyWithNewScrollIndex(newScrollIndex: Int?): DomainUiState {
        return when (this) {
            is NoneWindow -> copy(scrollIndex = newScrollIndex)
            is PttWindow -> copy(scrollIndex = newScrollIndex)
            is HelpWindow -> copy(scrollIndex = newScrollIndex)
            is AnnounceWindow -> copy(scrollIndex = newScrollIndex)
            is DomainMenuWindow -> copy(scrollIndex = newScrollIndex)
            is CallWindow -> copy(scrollIndex = newScrollIndex)
            is NavigationWindow -> copy(scrollIndex = newScrollIndex)
            is RadioWindow -> copy(scrollIndex = newScrollIndex)
            is WeatherWindow -> copy(scrollIndex = newScrollIndex)
            is SendMessageWindow -> copy(scrollIndex = newScrollIndex)
        }
    }
}
