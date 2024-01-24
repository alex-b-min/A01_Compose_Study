package com.example.a01_compose_study.ui.help

import com.example.a01_compose_study.domain.model.ParseBundle

sealed class HelpEvent {
    object CancelHelp : HelpEvent()
    object SetHelpListView : HelpEvent()
    data class SelectIndex(val serviceIndex : Int) : HelpEvent()
    data class  ReceiveBundle(val bundle: ParseBundle<out Any>) : HelpEvent()
}