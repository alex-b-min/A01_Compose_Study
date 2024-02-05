package com.example.a01_compose_study.presentation.screen.ptt

import com.example.a01_compose_study.presentation.screen.help.HelpEvent

sealed class PttEvent {
    object SetLoadingType : PttEvent()
    object SetSpeakType : PttEvent()
    object SetListenType : PttEvent()
    object SetAnnounceType : PttEvent()
}