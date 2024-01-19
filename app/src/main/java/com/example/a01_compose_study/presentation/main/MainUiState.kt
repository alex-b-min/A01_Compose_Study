package com.example.a01_compose_study.presentation.main

import com.example.a01_compose_study.domain.util.ScreenSizeType

sealed class MainUiState {

    object DoneScreen: MainUiState()

    data class OneScreen(
        val isVisible: Boolean,
        val text: String
    ) : MainUiState()

    data class TwoScreen(
        val isVisible: Boolean,
        val text: String,
        val screenSizeType: ScreenSizeType = ScreenSizeType.Small
    ) : MainUiState()
}