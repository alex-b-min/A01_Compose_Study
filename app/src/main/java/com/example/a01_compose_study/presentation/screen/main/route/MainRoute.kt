package com.example.a01_compose_study.presentation.screen.main.route

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import com.example.a01_compose_study.domain.model.ScreenType
import com.example.a01_compose_study.domain.model.SealedDomainType
import com.example.a01_compose_study.domain.util.ScreenSizeType
import com.example.a01_compose_study.presentation.components.button.PttButton
import com.example.a01_compose_study.presentation.screen.help.screen.ComposeHelpScreen
import com.example.a01_compose_study.presentation.screen.main.DomainUiState
import com.example.a01_compose_study.presentation.screen.main.MainEvent
import com.example.a01_compose_study.presentation.screen.main.MainViewModel
import com.example.a01_compose_study.presentation.screen.ptt.ComposePttScreen
import com.example.a01_compose_study.presentation.screen.ptt.PttEvent
import com.example.a01_compose_study.presentation.screen.ptt.PttViewModel
import com.example.a01_compose_study.presentation.util.MultipleEventsCutter
import com.example.a01_compose_study.presentation.util.get
import kotlinx.coroutines.launch

@Composable
fun MainRoute(
    viewModel: MainViewModel = hiltViewModel(),
) {
    val domainUiState by viewModel.domainUiState.collectAsStateWithLifecycle()
//    val vrUiState by viewModel.vrUiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val multipleEventsCutter = remember { MultipleEventsCutter.get() }
    val pttViewModel: PttViewModel = hiltViewModel()

    /**
     * Compose에서 해당 뷰를 조작하는 변수(visible)는 remember 타입으로 해야하지만,
     * 이렇게 하게 되면 해당 뷰를 조작하는 변수를 다른곳에서 조작을 하지 못하는 이슈가 있음.
     * 그래서 정답은 아니지만, viewModel에서 visible 변수를 만들어 관리함
     */
//    var visible by remember { mutableStateOf(false) }
    val domainWindowVisibleState by viewModel.domainWindowVisible.collectAsStateWithLifecycle()

    val targetFillMaxHeight by remember { mutableStateOf(Animatable(0f)) }

    LaunchedEffect(domainUiState) {
        val newTargetValue = when (domainUiState.screenSizeType) {
            is ScreenSizeType.Zero -> 0f
            is ScreenSizeType.Small -> 0.15f
            is ScreenSizeType.Middle -> 0.268f
            is ScreenSizeType.Large -> 0.433f
        }
        targetFillMaxHeight.animateTo(newTargetValue)
        Log.d("@@ 현재 스크린 타입 사이즈 ", "${domainUiState.screenSizeType} / ${newTargetValue}")
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                viewModel.onDomainEvent(MainEvent.CloseDomainWindowEvent)
            }
    ) {
//        when (vrUiState) {
//            is VRUiState.NoneWindow -> {
//            }
//
//            is VRUiState.VRWindow -> {
//                VRWindow(
//                    vrUiState = vrUiState as VRUiState.VRWindow,
//                    contentColor = Color.Green,
//                    onChangeWindowSize = { screenSizeType ->
//                        viewModel.onVREvent(VREvent.ChangeVRWindowSizeEvent(screenSizeType))
//                    },
//                    onCloseVRWindow = {
//                        viewModel.onVREvent(VREvent.CloseVRWindowEvent)
//                    },
//                    onCloseAllWindow = {
//                        viewModel.onVREvent(VREvent.CloseAllVRWindowsEvent)
//                    }
//                )
//            }
//        }

        /**
         * AnimatedVisibility() 안에 Crossfade()를 넣어,
         * 화면 띄우기 및 닫을 때  AnimatedVisibility 로 인해 아래에서 위로 올라오는 효과 / 위에서 아래로 내려가는 효과
         * 화면(콘텐츠)을 교체(전환)할 때는 Crossfade 로 인해 fade 효과가 적용됨
         * [주의사항] 각 스크린(화면)의 매개변수인 domainUiState에 값을 할당할 때 uiState 값을 넣는게 아니라 Crossfade 로부터 발행된 currDomainUiState 값을 넣어야함
         */
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
//            Crossfade(
//                targetState = domainUiState,
//                label = "",
//                animationSpec = tween(durationMillis = 1500)
//            ) { currDomainUiState ->
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
                            color = Color.Transparent,
                            shape = RoundedCornerShape(15.dp)
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {},
                ) {
                    when (domainUiState) {
                        is DomainUiState.NoneWindow -> {
                        }

                        is DomainUiState.PttWindow -> {
                            ComposePttScreen(
                                domainUiState = domainUiState as DomainUiState.PttWindow,
                                contentColor = Color.White
                            )

                        }

                        is DomainUiState.HelpWindow -> {
                            ComposeHelpScreen(
                                domainUiState = domainUiState as DomainUiState.HelpWindow,
                                contentColor = Color.White,
                                backgroundColor = Color.DarkGray
                            )
                        }

                        is DomainUiState.AnnounceWindow -> {

                        }

                        is DomainUiState.DomainMenuWindow -> {

                        }

                        is DomainUiState.CallWindow -> {

                        }

                        is DomainUiState.NavigationWindow -> {

                        }

                        is DomainUiState.RadioWindow -> {

                        }

                        is DomainUiState.WeatherWindow -> {

                        }

                        is DomainUiState.SendMessageWindow -> {

                        }
                    }
                }
            }
//            }
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.End
        ) {
            PttButton(
                modifier = Modifier.fillMaxSize(0.13f),
                contentText = "PTT Open",
                onClick = {
                    multipleEventsCutter.processEvent {

                    }

                    viewModel.onDomainEvent(
                        event = MainEvent.OpenDomainWindowEvent(
                            domainType = SealedDomainType.Ptt,
                            screenType = ScreenType.PttListen,
                            data = "음성 인식",
                            isError = false,
                            screenSizeType = ScreenSizeType.Small
                        )
                    )
                }
            )

            PttButton(
                modifier = Modifier.fillMaxSize(0.13f),
                contentText = "PTT Speak",
                onClick = {
                    scope.launch {
                        pttViewModel.onPttEvent(PttEvent.SetSpeakType)
                    }
                }
            )

            PttButton(
                modifier = Modifier.fillMaxSize(0.13f),
                contentText = "PTT Loading",
                onClick = {
                    scope.launch {
                        pttViewModel.onPttEvent(PttEvent.SetLoadingType)
                    }
                }
            )

            PttButton(
                modifier = Modifier.fillMaxSize(0.13f),
                contentText = "PTT Announce",
                onClick = {
                    scope.launch {
                        pttViewModel.onPttEvent(PttEvent.SetAnnounceType)
                    }
                }
            )

            PttButton(
                modifier = Modifier.fillMaxSize(0.13f),
                contentText = "PTT Close",
                onClick = {
                    scope.launch {
                        viewModel.closeDomainWindow()
                    }
                }
            )

            PttButton(
                modifier = Modifier.fillMaxSize(0.13f),
                contentText = "Error",
                onClick = {
                    multipleEventsCutter.processEvent {
                        viewModel.onDomainEvent(
                            event = MainEvent.OpenDomainWindowEvent(
                                domainType = SealedDomainType.Ptt,
                                screenType = ScreenType.PttListen,
                                data = "에러",
                                isError = true,
                                screenSizeType = ScreenSizeType.Small
                            )
                        )
                    }
                }
            )

            PttButton(
                modifier = Modifier.fillMaxSize(0.13f),
                contentText = "VR Result",
                onClick = {
                    multipleEventsCutter.processEvent {
                    }
                    scope.launch {
                        viewModel.vRmwManager.setVRResult()
                    }
                }
            )
        }
    }
}


