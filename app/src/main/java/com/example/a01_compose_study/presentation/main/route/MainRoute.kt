package com.example.a01_compose_study.presentation.main.route

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.a01_compose_study.domain.util.ScreenSizeType
import com.example.a01_compose_study.presentation.main.MainEvent
import com.example.a01_compose_study.presentation.main.MainUiState
import com.example.a01_compose_study.presentation.main.MainViewModel
import com.example.a01_compose_study.presentation.components.bottom_bar.BottomMenuBar
import com.example.a01_compose_study.presentation.components.window.CustomSizeAlertDialog

@Composable
fun MainRoute(
    viewModel: MainViewModel = hiltViewModel(),
) {
    var visible by remember { mutableStateOf(false) }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        when (uiState) {
            is MainUiState.NoneWindow -> {
            }

            is MainUiState.HelpWindow -> {
                CustomSizeAlertDialog(
                    uiState = uiState as MainUiState.HelpWindow,
                    contentColor = Color(0xFFCDDC39),
                    onDismiss = {
                        viewModel.onEvent(MainEvent.CloseWindowEvent)
                    })
            }

            is MainUiState.AnnounceWindow -> {

            }

            is MainUiState.MainMenuWindow -> {

            }

            is MainUiState.CallWindow -> {

            }

            is MainUiState.NavigationWindow -> {

            }

            is MainUiState.RadioWindow -> {

            }

            is MainUiState.WeatherWindow -> {

            }

            is MainUiState.SendMessageWindow -> {

            }
        }

        BottomMenuBar(
            isVisible = true,
            helpWindowOnClick = {
                viewModel.onEvent(
                    event = MainEvent.OpenHelpWindowEvent(
                        text = "HelpWindow",
                        screenSizeType = ScreenSizeType.Large
                    )
                )
            }
        )
    }
}
