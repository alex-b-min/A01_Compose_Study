package com.example.a01_compose_study.presentation.screen.call

import com.example.a01_compose_study.domain.model.HelpItemData
import com.example.a01_compose_study.domain.util.ScreenSizeType

sealed class CallEvent {
    data class ChangeScrollIndexEvent(
        val selectedScrollIndex: Int
    ) : CallEvent()
}