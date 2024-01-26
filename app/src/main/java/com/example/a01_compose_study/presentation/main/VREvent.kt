package com.example.a01_compose_study.presentation.main

import com.example.a01_compose_study.domain.util.ScreenSizeType

sealed class VREvent {

    object CloseVRWindowEvent : VREvent()

    data class OpenVRWindowEvent(
        val isError: Boolean,
        val text: String,
        val screenSizeType: ScreenSizeType
    ) : VREvent()
}