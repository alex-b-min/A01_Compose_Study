package com.example.a01_compose_study.presentation.screen.main

import com.example.a01_compose_study.domain.util.ScreenSizeType

sealed class VRUiState {
    object NoneWindow: VRUiState()

    data class VRWindow(
        val visible: Boolean,
        val isError: Boolean = false,
        val text: String = "",
        val screenSizeType: ScreenSizeType = ScreenSizeType.Small
    ): VRUiState()
}