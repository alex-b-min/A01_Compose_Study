package com.example.a01_compose_study.presentation.screen.main

import com.example.a01_compose_study.presentation.screen.main.route.VRUiState

//sealed class VREvent {
//
//    object CloseVRWindowEvent : VREvent()
//
//    object CloseAllVRWindowsEvent : VREvent()
//
//    data class OpenVRWindowEvent(
//        val isError: Boolean,
//        val text: String,
//        val screenSizeType: ScreenSizeType
//    ) : VREvent()
//
//    data class ChangeVRWindowSizeEvent(
//        val screenSizeType: ScreenSizeType
//    ) : VREvent()
//}

sealed class VREvent {
    data class ChangeVRUIEvent(
        val vrUiState: VRUiState
    ) : VREvent()
}