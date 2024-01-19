package com.example.a01_compose_study.presentation.main

import com.example.a01_compose_study.domain.util.ScreenSizeType

sealed class MainEvent {
    object Close : MainEvent()
    data class OneScreenOpen(
        val text: String
    ) : MainEvent()

    data class TwoScreenOpen(
        val text: String,
        val screenSizeType: ScreenSizeType
    ) : MainEvent()
}