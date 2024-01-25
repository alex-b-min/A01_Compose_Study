package com.example.a01_compose_study.presentation.main

import com.example.a01_compose_study.domain.util.ScreenSizeType

sealed class MainEvent {
    object CloseWindowEvent : MainEvent()

    data class OpenHelpWindowEvent(
        val text: String,
        val screenSizeType: ScreenSizeType
    ) : MainEvent()
}