package com.example.a01_compose_study.presentation.screen.main.route

import com.example.a01_compose_study.presentation.screen.main.DomainUiState

/**
 * `ScreenToDisplay`는 각 도메인 화면을 구성하기 위한 sealed class입니다.
 * 각각의 서브 클래스는 특정 도메인 화면을 나타내며, `domainUiState`의 값이 변경될 때 해당 화면을 업데이트합니다.
 * 이 클래스를 사용하여 불필요한 화면 변경을 방지하고, `domainUiState`의 변화에 따라 화면을 구성합니다.
 */
sealed class ScreenToDisplay {
    data class PttScreen(val uiState: DomainUiState.PttWindow, val announceString: String) : ScreenToDisplay()
    data class HelpScreen(val uiState: DomainUiState.HelpWindow, val vrUiState: VRUiState) : ScreenToDisplay()
    data class AnnounceScreen(val text: String) : ScreenToDisplay()
    data class CallScreen(val uiState: DomainUiState.CallWindow, val vrUiState: VRUiState) : ScreenToDisplay()
    data class DomainMenuScreen(val uiState: DomainUiState.DomainMenuWindow) : ScreenToDisplay()
    data class NavigationScreen(val uiState: DomainUiState.NavigationWindow) : ScreenToDisplay()
    data class RadioScreen(val uiState: DomainUiState.RadioWindow) : ScreenToDisplay()
    data class WeatherScreen(val uiState: DomainUiState.WeatherWindow) : ScreenToDisplay()
    data class SendMessageScreen(val uiState: DomainUiState.SendMessageWindow) : ScreenToDisplay()
}