package com.example.a01_compose_study.ui.help

import com.example.a01_compose_study.domain.model.HelpItemData
import com.example.a01_compose_study.domain.util.ScreenSizeType
import com.example.a01_compose_study.presentation.main.VREvent

sealed class HelpEvent {
    object OnDismiss : HelpEvent()
//    object SetHelpListView : HelpEvent()
//    data class SelectIndex(val serviceIndex : Int) : HelpEvent()
//    data class  ReceiveBundle(val bundle: ParseBundle<out Any>) : HelpEvent()
    data class HelpListItemOnClick(val helpItemData: HelpItemData) : HelpEvent()
    data class OnHelpListBack(
        val isError: Boolean,
        val text: String,
        val screenSizeType: ScreenSizeType
    ) : HelpEvent()

    object OnHelpDetailBack: HelpEvent()

    data class ChangeHelpWindowSizeEvent(
        val screenSizeType: ScreenSizeType
    ) : HelpEvent()
}