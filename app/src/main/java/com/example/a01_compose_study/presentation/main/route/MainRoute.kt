package com.example.a01_compose_study.presentation.main.route

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.a01_compose_study.domain.util.ScreenSizeType
import com.example.a01_compose_study.presentation.components.button.PttButton
import com.example.a01_compose_study.presentation.main.MainEvent
import com.example.a01_compose_study.presentation.main.MainUiState
import com.example.a01_compose_study.presentation.main.MainViewModel
import com.example.a01_compose_study.presentation.main.VREvent
import com.example.a01_compose_study.presentation.main.VRUiState
import com.example.a01_compose_study.presentation.window.vr_window.VRWindow
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

    /**
     * Compose에서 해당 뷰를 조작하는 변수(visible)는 remember 타입으로 해야하지만,
     * 이렇게 하게 되면 해당 뷰를 조작하는 변수를 다른곳에서 조작을 하지 못하는 이슈가 있음.
     * 그래서 정답은 아니지만, viewModel에서 visible 변수를 만들어 관리함
     */
//    var visible by remember { mutableStateOf(false) }
    val domainWindowVisibleState by viewModel.domainWindowVisible.collectAsStateWithLifecycle()

    val targetFillMaxHeight by remember { mutableStateOf(Animatable(0.4f)) }

    LaunchedEffect(uiState) {
        Log.d("@@ uiState 변경", "${uiState.screenSizeType}")
        val newTargetValue = when (uiState.screenSizeType) {
            is ScreenSizeType.Small -> 0.15f
            is ScreenSizeType.Middle ->0.268f
            is ScreenSizeType.Large -> 0.433f
        }
        targetFillMaxHeight.animateTo(newTargetValue)
    }

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

        AnimatedVisibility(
            visible = domainWindowVisibleState,
            modifier = Modifier.fillMaxWidth(),
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(1000)
            ),
            exit = slideOutVertically(
                targetOffsetY = { it },
                animationSpec = tween(1000)
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomStart
            ) {
                Column(
                    modifier = Modifier
                        .offset(x = 10.dp, y = (10).dp)
                        .fillMaxHeight(targetFillMaxHeight.value)
                        .fillMaxWidth(0.233f)
                        .background(
                            color = Color.DarkGray,
                            shape = RoundedCornerShape(15.dp)
                        ),
                ) {
                    when (uiState) {
                        is MainUiState.NoneWindow -> {
                        }

                        is MainUiState.HelpWindow -> {
                            ComposeHelpScreen(mainUiState = uiState as MainUiState.HelpWindow,
                                contentColor = Color.White,
                                onDismiss = {
                                    /**
                                     * 닫기 버튼
                                     */
                                    viewModel.closeDomainWindow()
                                },
                                onHelpListBackButton = {
                                    /*TODO(뒤로가기 구현)*/
                                },
                                onScreenSizeChange = { screenSizeType ->
                                    /**
                                     * 혹시나 띄어져 있는 화면(현재)에서 직접적으로 화면 사이즈를 변경하고 싶을때
                                     */
                                    scope.launch {
                                        viewModel.changeDomainScreenSizeType(sizeType = screenSizeType)
                                    }
                                }
                            )
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
                }
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
                contentText = "Domain Close",
                onClick = {
                    scope.launch {
                        viewModel.onDomainEvent(MainEvent.CloseDomainWindowEvent)
                    }
                }
            )
            PttButton(
                modifier = Modifier.fillMaxSize(0.13f),
                contentText = "None Screen",
                onClick = {
                    scope.launch {
                        viewModel.onDomainEvent(MainEvent.NoneDomainWindowEvent(uiState.screenSizeType))
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