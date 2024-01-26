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
import com.example.a01_compose_study.presentation.window.vr_window.VRWindow
import com.example.a01_compose_study.presentation.main.MainEvent
import com.example.a01_compose_study.presentation.main.MainUiState
import com.example.a01_compose_study.presentation.main.MainViewModel
import com.example.a01_compose_study.presentation.main.VREvent
import com.example.a01_compose_study.presentation.main.VRUiState
import com.example.a01_compose_study.ui.help.ComposeHelpScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MainRoute(
    viewModel: MainViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val vrUiState by viewModel.vrUiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        when (vrUiState) {
            is VRUiState.NoneWindow -> {

            }

            is VRUiState.VRWindow -> {
                VRWindow(
                    vrUiState = vrUiState as VRUiState.VRWindow,
                    contentColor = Color.Green,
                    onDismiss = {
                        viewModel.onDomainEvent(MainEvent.CloseDomainWindowEvent)
                        viewModel.onVREvent(VREvent.CloseVRWindowEvent)
                    }
                )
            }
        }


        when (uiState) {
            is MainUiState.NoneWindow -> {
            }

            is MainUiState.HelpWindow -> {
                //ToDo(Help 윈도우 띄우기)
//                com.example.a01_compose_study.presentation.window.vr_window.HelpDummyScreen(
//                    mainUiState = uiState as MainUiState.HelpWindow,
//                    contentColor = Color.Red,
//                    onDismiss = {
//                        viewModel.onDomainEvent(MainEvent.CloseDomainWindowEvent)
//                    },
//                    onBackButton = {
//                        viewModel.onDomainEvent(MainEvent.CloseDomainWindowEvent)
//                        viewModel.onVREvent(
//                            event = VREvent.OpenVRWindowEvent(
//                                isError = false,
//                                text = "음성 인식 중 입니다...",
//                                screenSizeType = ScreenSizeType.Middle
//                            )
//                        )
//                    }
//                )
                ComposeHelpScreen(mainUiState = uiState as MainUiState.HelpWindow,
                    contentColor = Color.Red,
                    onDismiss = {
                        /*TODO*/
                    },
                    onBackButton = {
                        /*TODO*/
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

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.End
        ) {
            PttButton(
                modifier = Modifier.fillMaxSize(0.13f),
                contentText = "VR Open",
                onClick = {
                    viewModel.onVREvent(
                        event = VREvent.OpenVRWindowEvent(
                            isError = false,
                            text = "음성 인식 중 입니다...",
                            screenSizeType = ScreenSizeType.Middle
                        )
                    )
                }
            )
            PttButton(
                modifier = Modifier.fillMaxSize(0.13f),
                contentText = "VR Close",
                onClick = {
                    scope.launch {
                        viewModel.closeVRWindow()
                        delay(500)
                        viewModel.onVREvent(VREvent.CloseVRWindowEvent)
                    }
                }
            )
            PttButton(
                modifier = Modifier.fillMaxSize(0.13f),
                contentText = "Help Close",
                onClick = {
                    scope.launch {
                        viewModel.closeHelpWindow()
                        delay(500)
                        viewModel.onDomainEvent(MainEvent.CloseDomainWindowEvent)
                    }
                }
            )
            PttButton(
                modifier = Modifier.fillMaxSize(0.13f),
                contentText = "Error",
                onClick = {
                    viewModel.onVREvent(
                        event = VREvent.OpenVRWindowEvent(
                            isError = true,
                            text = "음성 인식 오류...",
                            screenSizeType = ScreenSizeType.Middle
                        )
                    )
                }
            )
        }
    }
}
