package com.example.a01_compose_study.presentation.screen.main.route

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
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
import com.example.a01_compose_study.data.DialogueMode
import com.example.a01_compose_study.data.HVRError
import com.example.a01_compose_study.data.VRResult
import com.example.a01_compose_study.data.custom.MWContext
import com.example.a01_compose_study.domain.model.ScreenType
import com.example.a01_compose_study.domain.model.SealedDomainType
import com.example.a01_compose_study.domain.util.ScreenSizeType
import com.example.a01_compose_study.presentation.components.button.PttButton
import com.example.a01_compose_study.presentation.components.lottie.LottieAssetAnimationHandler
import com.example.a01_compose_study.presentation.screen.announce.AnnounceScreen
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
    pttViewModel: PttViewModel = hiltViewModel(),
) {
    val domainUiState by viewModel.domainUiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val multipleEventsCutter = remember { MultipleEventsCutter.get() }

    val mwContext = MWContext(DialogueMode.MAINMENU)
    val announceString by pttViewModel.announceString.collectAsStateWithLifecycle()

    /**
     * Compose에서 뷰를 조작하는 변수(visible)는 remember 타입으로 선언해야 합니다.
     * 그러나 이 방법은 각 뷰의 가시성을 개별적으로 관리해야 합니다.
     * 이 방식은 화면 전환 구조에는 적합하지만, 하나의 창 위에 여러 개의 뷰를 관리할 때 여러 개의 visible을 관리해야 하므로 적절하지 않습니다.
     * 따라서 정확한 해결책은 아니지만, flow 타입의 visible 변수를 싱글톤으로 만들어 최상단의 뷰를 관리합니다.
     * 이렇게 하면 하나의 AnimatedVisibility() 안에 여러 개의 창을 관리할 수 있습니다.
     * 실제로 이렇게 사용하게 된다면,
     * AnimatedVisibility() 안에서 UiState의 조건문을 타고 들어간 후
     * 해당 Window를 닫기 위해선 가장 상단의 AnimatedVisibility()의 visible 변수를 false로 바꾸면 해당 Window가 닫히게 되는 효과를 얻을 수 있습니다.
     */
    val domainWindowVisibleState by viewModel.domainWindowVisible.collectAsStateWithLifecycle()

    val targetFillMaxHeight by remember { mutableStateOf(Animatable(0f)) }
    val targetFillMaxWidth by remember { mutableStateOf(Animatable(0f)) }

    LaunchedEffect(domainUiState) {
        val newTargetHeightValue = when (domainUiState.screenSizeType) {
            is ScreenSizeType.Zero -> 0f
            is ScreenSizeType.Small -> 0.15f
            is ScreenSizeType.Middle -> 0.268f
            is ScreenSizeType.Large -> 0.433f
            is ScreenSizeType.Full -> 1f
        }
        val newTargetWidthValue = when (domainUiState.screenSizeType) {
            is ScreenSizeType.Zero -> 0f
            is ScreenSizeType.Full -> 1f
            else -> 0.233f
        }

        scope.launch {
            targetFillMaxHeight.animateTo(
                targetValue = newTargetHeightValue,
                animationSpec = tween(
                    durationMillis = 800, // 애니메이션 지속 시간을 조정
                    easing = FastOutSlowInEasing // 선택적으로 easing 함수를 지정
                )
            )
        }
        scope.launch {
            targetFillMaxWidth.animateTo(
                targetValue = newTargetWidthValue,
                animationSpec = tween(
                    durationMillis = 800, // 애니메이션 지속 시간을 조정
                    easing = FastOutSlowInEasing // 선택적으로 easing 함수를 지정
                )
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                viewModel.onDomainEvent(MainEvent.CloseDomainWindowEvent)
            }
    ) {
        /**
         * 하나의 AnimatedVisibility() 안에 여러 Window를 구성
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
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomStart
            ) {
                Box(
                    modifier = Modifier
                        .offset(x = 10.dp, y = (10).dp)
                        .fillMaxHeight(targetFillMaxHeight.value)
                        .fillMaxWidth(targetFillMaxWidth.value)
                        .background(
                            color = Color.Transparent,
                            shape = RoundedCornerShape(15.dp)
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {},
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        LottieAssetAnimationHandler(
                            modifier = Modifier.fillMaxSize(),
                            lottieJsonAssetPath = "bg_glow/09_tsd_frame_glow_l_lt.json",
                            lottieImageAssetFolder = "bg_glow/images/default",
                            infiniteLoop = true
                        )
                    }


                    when (domainUiState) {
                        is DomainUiState.NoneWindow -> {
                        }

                        is DomainUiState.PttWindow -> {
                            ComposePttScreen(
                                domainUiState = domainUiState as DomainUiState.PttWindow,
                                contentColor = Color.White,
                                displayText = announceString
                            )
                        }

                        is DomainUiState.HelpWindow -> {
                            Box(modifier = Modifier.fillMaxSize()) {
                                ComposeHelpScreen(
                                    domainUiState = domainUiState as DomainUiState.HelpWindow,
                                    contentColor = Color.Gray,
                                    backgroundColor = Color.Black
                                )
                            }
                        }

                        is DomainUiState.AnnounceWindow -> {
                            AnnounceScreen((domainUiState as DomainUiState.AnnounceWindow).text)
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
                            data = announceString,
                            isError = false,
                            screenSizeType = ScreenSizeType.Small
                        )
                    )

                    pttViewModel.onPttEvent(PttEvent.PreparePtt)
                    pttViewModel.onPttEvent(PttEvent.StartVR(mwContext))
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
                        viewModel.onDomainEvent(
                            event = MainEvent.OpenDomainWindowEvent(
                                domainType = SealedDomainType.Announce,
                                screenType = ScreenType.PttAnounce,
                                data = "Help",
                                isError = false,
                                screenSizeType = ScreenSizeType.Small
                            )
                        )
                    }
                }
            )

            PttButton(
                modifier = Modifier.fillMaxSize(0.13f),
                contentText = "PTT Close",
                onClick = {
                    scope.launch {
                        viewModel.onDomainEvent(MainEvent.CloseDomainWindowEvent)
                    }
                }
            )

            PttButton(
                modifier = Modifier
                    .fillMaxWidth(0.13f)
                    .fillMaxHeight(0.2f),
                contentText = "ERROR_HMI",
                onClick = {
                    multipleEventsCutter.processEvent {
//                        viewModel.onDomainEvent(
//                            event = MainEvent.OpenDomainWindowEvent(
//                                domainType = SealedDomainType.Ptt,
//                                screenType = ScreenType.PttListen,
//                                data = "에러",
//                                isError = true,
//                                screenSizeType = ScreenSizeType.Small
//                            )
//                        )
                        viewModel.vrmwManager.setVRError(HVRError.ERROR_HMI)
                    }
                }
            )

            PttButton(
                modifier = Modifier
                    .fillMaxWidth(0.13f)
                    .fillMaxHeight(0.2f),
                contentText = "VR Result",
                onClick = {
                    multipleEventsCutter.processEvent {
                    }
                    scope.launch {
                        viewModel.vrmwManager.setVRResult(VRResult())
                    }
                }
            )

            PttButton(
                modifier = Modifier
                    .fillMaxWidth(0.13f)
                    .fillMaxHeight(0.25f),
                contentText = "Size Up",
                onClick = {
                    val resultScreenSizeType = when (domainUiState.screenSizeType) {
                        is ScreenSizeType.Zero -> ScreenSizeType.Zero
                        is ScreenSizeType.Small -> ScreenSizeType.Middle
                        is ScreenSizeType.Middle -> ScreenSizeType.Large
                        is ScreenSizeType.Large -> ScreenSizeType.Full
                        is ScreenSizeType.Full -> ScreenSizeType.Full
                    }
                    viewModel.onDomainEvent(
                        MainEvent.ChangeDomainWindowSizeEvent(
                            resultScreenSizeType
                        )
                    )
                }
            )

            PttButton(
                modifier = Modifier
                    .fillMaxWidth(0.13f)
                    .fillMaxHeight(0.25f),
                contentText = "Size Down",
                onClick = {
                    val resultScreenSizeType = when (domainUiState.screenSizeType) {
                        is ScreenSizeType.Zero -> ScreenSizeType.Zero
                        is ScreenSizeType.Small -> ScreenSizeType.Small
                        is ScreenSizeType.Middle -> ScreenSizeType.Small
                        is ScreenSizeType.Large -> ScreenSizeType.Middle
                        is ScreenSizeType.Full -> ScreenSizeType.Large
                    }
                    viewModel.onDomainEvent(
                        MainEvent.ChangeDomainWindowSizeEvent(
                            resultScreenSizeType
                        )
                    )
                }
            )
        }
    }
}


