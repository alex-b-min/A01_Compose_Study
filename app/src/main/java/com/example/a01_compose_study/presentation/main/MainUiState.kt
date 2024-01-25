package com.example.a01_compose_study.presentation.main

import com.example.a01_compose_study.domain.util.ScreenSizeType

sealed class MainUiState {

    // TODO(각 시나리오 별 윈도우 정의, 추후 시나리오 별 필요한 데이터를 정의해야 함)
    object NoneWindow: MainUiState()

    data class HelpWindow(
        val text: String,
        val screenSizeType: ScreenSizeType = ScreenSizeType.Small
    ) : MainUiState()

    object AnnounceWindow: MainUiState()

    object MainMenuWindow: MainUiState()

    object CallWindow: MainUiState()

    object NavigationWindow: MainUiState()

    object RadioWindow: MainUiState()

    object WeatherWindow: MainUiState()

    object SendMessageWindow: MainUiState()
}
