package com.example.a01_compose_study.presentation.screen.call

sealed class CallBusinessEvent {

    data class Calling(val phoneNumber: String) : CallBusinessEvent()
}