package com.example.a01_compose_study.presentation.screen.call

import com.example.a01_compose_study.data.Contact

sealed class CallEvent {

    object CloseButtonClick : CallEvent()

    object BackButtonClick : CallEvent()

    data class ContactListItemOnClick(
        val selectedContactItem: Contact,
        val itemIndex: Int
    ) : CallEvent()

    data class OnYesButtonClick(
        val phoneNumber: String
    ) : CallEvent()

    object OnOtherNumberButtonClick : CallEvent()
}