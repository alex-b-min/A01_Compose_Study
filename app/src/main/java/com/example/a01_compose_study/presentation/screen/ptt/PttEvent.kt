package com.example.a01_compose_study.presentation.screen.ptt

sealed class PttEvent {
    object SetLoadingType : PttEvent()
    object SetSpeakType : PttEvent()
    object SetListenType : PttEvent()
}