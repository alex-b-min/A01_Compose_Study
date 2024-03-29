package com.example.a01_compose_study.presentation.screen.help

import com.example.a01_compose_study.domain.model.HelpItemData
import com.example.a01_compose_study.domain.util.ScreenSizeType

sealed class HelpEvent {
    data class HelpListItemOnClick(
        val selectedHelpItem: HelpItemData
    ) : HelpEvent()

    data class OnHelpListBack(
        val isError: Boolean,
        val screenSizeType: ScreenSizeType
    ) : HelpEvent()

    object OnHelpDetailBack : HelpEvent()

    data class ChangeHelpWindowSizeEvent(
        val screenSizeType: ScreenSizeType
    ) : HelpEvent()

    object OnDismiss : HelpEvent()
}