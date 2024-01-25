package com.example.a01_compose_study.presentation.main.route

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.a01_compose_study.domain.util.ScreenSizeType
import com.example.a01_compose_study.presentation.components.button.PttButton
import com.example.a01_compose_study.presentation.components.window.CustomSizeAlertDialog
import com.example.a01_compose_study.presentation.main.MainEvent
import com.example.a01_compose_study.presentation.main.MainUiState
import com.example.a01_compose_study.presentation.main.MainViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MainRoute(
    viewModel: MainViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        when (uiState) {
            is MainUiState.NoneWindow -> {
            }

            is MainUiState.HelpWindow -> {
                CustomSizeAlertDialog(
                    uiState = uiState as MainUiState.HelpWindow,
                    contentColor = Color.Black,
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

//        BottomMenuBar(
//            isVisible = true,
//            helpWindowOnClick = {
//                viewModel.onEvent(
//                    event = MainEvent.OpenHelpWindowEvent(
//                        text = "HelpWindow",
//                        screenSizeType = ScreenSizeType.Large
//                    )
//                )
//            }
//        )
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.End
        ) {
            PttButton(
                modifier = Modifier.fillMaxSize(0.1f),
                contentText = "Open",
                onClick = {
                    viewModel.onEvent(
                        event = MainEvent.OpenHelpWindowEvent(
                            text = "HelpWindow",
                            screenSizeType = ScreenSizeType.Large
                        )
                    )
                }
            )
            PttButton(
                modifier = Modifier.fillMaxSize(0.1f),
                contentText = "Close",
                onClick = {
                    scope.launch {
                        viewModel.closeHelpWindow()
                        delay(500)
                        viewModel.onEvent(MainEvent.CloseWindowEvent)
                    }
                }
            )
            PttButton(
                modifier = Modifier.fillMaxSize(0.1f),
                contentText = "Error",
                onClick = {
                }
            )
        }
    }
}
