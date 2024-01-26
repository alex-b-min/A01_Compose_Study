package com.example.a01_compose_study.presentation.main

import com.example.a01_compose_study.domain.ScreenType
import com.example.a01_compose_study.domain.SealedDomainType
import com.example.a01_compose_study.domain.model.HelpItemData
import com.example.a01_compose_study.domain.util.ScreenSizeType

sealed class MainUiState {

    // TODO(각 시나리오 별 윈도우 정의, 추후 시나리오 별 필요한 데이터를 정의해야 함)
//    data class VRWindow(
//        val visible: Boolean,
//        val isError: Boolean = false,
//        val text: String = "",
//        val screenSizeType: ScreenSizeType = ScreenSizeType.Small
//    ): MainUiState()

    object NoneWindow: MainUiState()

    data class HelpWindow(
        val domainType: SealedDomainType,
        val screenType: ScreenType,
        val data: Any,
        val visible: Boolean,
        val isError: Boolean = false,
        val text: String = "",
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
