package com.example.a01_compose_study.presentation.screen.call

import androidx.lifecycle.ViewModel
import com.example.a01_compose_study.data.custom.call.BtCall
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CallViewModel @Inject constructor(private val btCall: BtCall) : ViewModel() {

    fun onCallEvent(event: CallEvent) {
        when(event) {
            is  CallEvent.OnCallBack -> {
            }

            is CallEvent.ContactListItemOnClick -> {

            }
        }
    }

    fun onCallBusinessEvent(event: CallBusinessEvent) {
        when(event) {
            is CallBusinessEvent.Calling -> {
                btCall.outgoingCall("010-1234-5491")
            }
        }
    }
}