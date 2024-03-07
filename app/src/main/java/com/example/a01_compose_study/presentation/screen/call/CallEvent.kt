package com.example.a01_compose_study.presentation.screen.call

import com.example.a01_compose_study.data.Contact

sealed class CallEvent {

    object OnCallBack : CallEvent()

    data class ContactListItemOnClick(
        val selectedContactItem: Contact
    ) : CallEvent()

    data class OnYesButtonClick(
        val phoneNumber: String
    ) : CallEvent()

    data class OnOtherNameButtonClick(
        val currentContact: Contact
    ) : CallEvent()
}