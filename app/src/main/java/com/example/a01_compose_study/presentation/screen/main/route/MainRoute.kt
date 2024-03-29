package com.example.a01_compose_study.presentation.screen.main.route

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.a01_compose_study.R
import com.example.a01_compose_study.data.HVRError
import com.example.a01_compose_study.data.VRResult
import com.example.a01_compose_study.domain.model.ScreenType
import com.example.a01_compose_study.domain.model.SealedDomainType
import com.example.a01_compose_study.domain.util.ScreenSizeType
import com.example.a01_compose_study.presentation.components.button.PttButton
import com.example.a01_compose_study.presentation.components.lottie.LottieAssetAnimationHandler
import com.example.a01_compose_study.presentation.components.lottie.LottieRawAnimationHandler
import com.example.a01_compose_study.presentation.data.UiState.onDomainEvent
import com.example.a01_compose_study.presentation.data.UiState.onVREvent
import com.example.a01_compose_study.presentation.screen.SelectVRResult
import com.example.a01_compose_study.presentation.screen.announce.AnnounceScreen
import com.example.a01_compose_study.presentation.screen.call.screen.CallScreen
import com.example.a01_compose_study.presentation.screen.help.screen.ComposeHelpScreen
import com.example.a01_compose_study.presentation.screen.main.DomainUiState
import com.example.a01_compose_study.presentation.screen.main.MainEvent
import com.example.a01_compose_study.presentation.screen.main.MainViewModel
import com.example.a01_compose_study.presentation.screen.main.VREvent
import com.example.a01_compose_study.presentation.screen.ptt.ComposePttScreen
import com.example.a01_compose_study.presentation.screen.ptt.PttEvent
import com.example.a01_compose_study.presentation.screen.ptt.PttViewModel
import com.example.a01_compose_study.presentation.screen.sendMsg.SendMsgScreen
import com.example.a01_compose_study.presentation.util.MultipleEventsCutter
import com.example.a01_compose_study.presentation.util.get
import com.example.a01_compose_study.ui.theme.Black2
import kotlinx.coroutines.launch

@Composable
fun MainRoute(
    viewModel: MainViewModel = hiltViewModel(),
    pttViewModel: PttViewModel = hiltViewModel(),
) {
    val domainUiState by viewModel.domainUiState.collectAsStateWithLifecycle()
    val domainWindowVisibleState by viewModel.domainWindowVisible.collectAsStateWithLifecycle()
    val vrUiState by viewModel.vrUiState.collectAsStateWithLifecycle()

    val scope = rememberCoroutineScope()

    val multipleEventsCutter = remember { MultipleEventsCutter.get() }

    val announceString by pttViewModel.announceString.collectAsStateWithLifecycle()

    /**
     * 원래라고 하면은 Compose에서 뷰를 조작하는 변수(visible)는 remember 타입으로 선언해야 함..
     * 그러나 이 방법은 각 뷰의 가시성을 개별적으로 관리해야 한다는 점임..
     * 이 방식은 화면 전환 구조에는 적합하지만, 하나의 창 위에 여러 개의 뷰를 관리할 때 여러 개의 visible을 각각 관리를 해줘야 하므로 비효율적이라고 생각..
     * 따라서 정확한 해결책은 아니지만, flow 타입의 visible 변수를 싱글톤으로 만들어 최상단의 뷰를 관리하려고 함..
     * 이렇게 하면 하나의 AnimatedVisibility() 안에 여러 개의 창을 관리할 수 있다는 장점이 있음!
     * 실제로 이렇게 사용하게 된다면,
     * AnimatedVisibility() 안에서 UiState의 조건문을 타고 들어간 후
     * 해당 Window를 닫기 위해선 가장 상단의 AnimatedVisibility()의 visible 변수를 false로 바꾸면 해당 Window를 닫을 수 있다..
     */

    WindowFrame(
        domainUiState = domainUiState,
        domainWindowVisible = domainWindowVisibleState,
        onCloseDomainWindow = {
            onDomainEvent(MainEvent.CloseDomainWindowEvent)
        }) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            VrUiAnimationHandler(vrUiState)
        }

        Log.d("@@ domainUiState When문 위에서 시작", "${domainUiState}")
        Log.d("@@ domainUiState When문 위에서 시작", "${domainWindowVisibleState}")
        when (domainUiState) {
            is DomainUiState.NoneWindow -> {
            }

            is DomainUiState.PttWindow -> {
                Log.d("@@ PttWindow 진입", "몇번 실행?")
                ComposePttScreen(
                    domainUiState = domainUiState as DomainUiState.PttWindow,
                    contentColor = Color.White,
                    displayText = announceString
                )
            }

            is DomainUiState.HelpWindow -> {
                Log.d("@@ HelpWindow 진입", "몇번 실행?")
                Box(modifier = Modifier.fillMaxSize()) {
                    ComposeHelpScreen(
                        domainUiState = domainUiState as DomainUiState.HelpWindow,
                        vrUiState = vrUiState,
                        contentColor = Color.Gray,
                        backgroundColor = Black2
                    )
                    onVREvent(
                        VREvent.ChangeVRUIEvent(
                            VRUiState.PttLoading(
                                active = true,
                                isError = false
                            )
                        )
                    )
                }
            }

            is DomainUiState.AnnounceWindow -> {
                Log.d("@@ AnnounceWindow 진입", "몇번 실행?")
                AnnounceScreen((domainUiState as DomainUiState.AnnounceWindow).text)
            }

            is DomainUiState.CallWindow -> {
                Log.d("@@ CallWindow 진입", "몇번 실행?")
                Box(modifier = Modifier.fillMaxSize()) {
                    CallScreen(
                        domainUiState = domainUiState as DomainUiState.CallWindow,
                        vrUiState = vrUiState,
                        vrDynamicBackground = if (vrUiState.active) Color.Transparent else Color.Black,
                        fixedBackground = Black2
                    )
//                    onVREvent(
//                        VREvent.ChangeVRUIEvent(
//                            VRUiState.PttLoading(
//                                active = true,
//                                isError = false
//                            )
//                        )
//                    )
                }
            }

            is DomainUiState.DomainMenuWindow -> {

            }

            is DomainUiState.NavigationWindow -> {

            }

            is DomainUiState.RadioWindow -> {

            }

            is DomainUiState.WeatherWindow -> {

            }

            is DomainUiState.SendMessageWindow -> {
                SendMsgScreen(
                    domainUiState = domainUiState as DomainUiState.SendMessageWindow
                )
            }

            else -> {}
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
                pttViewModel.onPttEvent(PttEvent.StartVR(SelectVRResult.PttResult))
            }
        )

        PttButton(
            modifier = Modifier.fillMaxSize(0.13f),
            contentText = "PTT Prepare",
            onClick = {
                pttViewModel.onPttEvent(PttEvent.PreparePtt)
            }
        )

        PttButton(
            modifier = Modifier.fillMaxSize(0.13f),
            contentText = "PTT Speak",
            onClick = {
                pttViewModel.onPttEvent(PttEvent.SetSpeakType)
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
                    onDomainEvent(
                        event = MainEvent.OpenDomainWindowEvent(
                            domainType = SealedDomainType.Announce,
                            screenType = ScreenType.Prepare,
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
                    onDomainEvent(MainEvent.CloseDomainWindowEvent)
                }
            }
        )

        PttButton(
            modifier = Modifier.fillMaxSize(0.13f),
            contentText = "PTT Help",
            onClick = {
                pttViewModel.onPttEvent(PttEvent.StartVR(SelectVRResult.HelpResult))
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
                onDomainEvent(
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
                onDomainEvent(
                    MainEvent.ChangeDomainWindowSizeEvent(
                        resultScreenSizeType
                    )
                )
            }
        )
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(end = 250.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.End
    ) {


//            PttButton(
//                modifier = Modifier.fillMaxSize(0.13f),
//                contentText = "VR Loading",
//                onClick = {
//                    onVREvent(
//                        VREvent.ChangeVRUIEvent(
//                            VRUiState.PttLoading(
//                                active = true,
//                                isError = false
//                            )
//                        )
//                    )
//                }
//            )
//
//            PttButton(
//                modifier = Modifier.fillMaxSize(0.13f),
//                contentText = "VR Speak",
//                onClick = {
//                    onVREvent(
//                        VREvent.ChangeVRUIEvent(
//                            VRUiState.PttSpeak(
//                                active = true,
//                                isError = false
//                            )
//                        )
//                    )
//                }
//            )
//
//            PttButton(
//                modifier = Modifier.fillMaxSize(0.13f),
//                contentText = "VR Listen",
//                onClick = {
//                    onVREvent(
//                        VREvent.ChangeVRUIEvent(
//                            VRUiState.PttListen(
//                                active = true,
//                                isError = false
//                            )
//                        )
//                    )
//                }
//            )
//
//            PttButton(
//                modifier = Modifier.fillMaxSize(0.13f),
//                contentText = "VR Not",
//                onClick = {
//                    onVREvent(
//                        VREvent.ChangeVRUIEvent(
//                            VRUiState.PttNone(
//                                false,
//                                isError = false
//                            )
//                        )
//                    )
//                }
//            )
//
//            PttButton(
//                modifier = Modifier
//                    .fillMaxWidth(0.13f)
//                    .fillMaxHeight(0.3f),
//                contentText = "VR isError",
//                onClick = {
//                    onVREvent(
//                        VREvent.ChangeVRUIEvent(
//                            VRUiState.PttNone(
//                                true,
//                                isError = true
//                            )
//                        )
//                    )
//                }
//            )

        PttButton(
            modifier = Modifier
                .fillMaxWidth(0.12f)
                .fillMaxHeight(0.13f),
            contentText = "VR Call Index List Result",
            onClick = {
                pttViewModel.onPttEvent(PttEvent.StartVR(SelectVRResult.CallIndexListResult))
            }
        )

        PttButton(
            modifier = Modifier
                .fillMaxWidth(0.12f)
                .fillMaxHeight(0.13f),
            contentText = "VR Line Number Result",
            onClick = {
                scope.launch {
                    viewModel.vrmwManager.setVRResult(VRResult(), SelectVRResult.ScrollIndexResult)
                }
            }
        )

        PttButton(
            modifier = Modifier
                .fillMaxWidth(0.12f)
                .fillMaxHeight(0.13f),
            contentText = "Change ScrollIndex 5",
            onClick = {
                onDomainEvent(
                    MainEvent.ChangeScrollIndexEvent(5)
                )
            }
        )
        PttButton(
            modifier = Modifier
                .fillMaxWidth(0.12f)
                .fillMaxHeight(0.13f),
            contentText = "Change ScrollIndex 10",
            onClick = {
                onDomainEvent(
                    MainEvent.ChangeScrollIndexEvent(10)
                )
            }
        )
    }
}

/**
 * 계속해서 리컴포지션이 발생되는 오류가 있음 추후 수정해야함..
 */
@Composable
fun VrUiAnimationHandler(vrUiState: VRUiState) {
    when {
        vrUiState.active && vrUiState.isError -> {
            LottieAssetAnimationHandler(
                modifier = Modifier.fillMaxSize(),
                lottieJsonAssetPath = "bg_glow/frame_error_glow_l_lt.json",
                lottieImageAssetFolder = "bg_glow/images/error",
                infiniteLoop = true
            )
        }

        vrUiState.active -> {
            LottieAssetAnimationHandler(
                modifier = Modifier.fillMaxSize(),
                lottieJsonAssetPath = "bg_glow/09_tsd_frame_glow_l_lt.json",
                lottieImageAssetFolder = "bg_glow/images/default",
                infiniteLoop = true
            )
            when (vrUiState) {
                is VRUiState.PttLoading -> {
                    LottieRawAnimationHandler(
                        modifier = Modifier.fillMaxSize(),
                        rawResId = R.raw.tsd_thinking_loop_fix_lt_03_2,
                        infiniteLoop = true,
                        onFrameChanged = { currentFrame ->
                            // 필요한 경우 처리
                        }
                    )
                }

                is VRUiState.PttListen -> {
                    LottieRawAnimationHandler(
                        modifier = Modifier
                            .fillMaxSize(),
                        rawResId = R.raw.tsd_listening_passive_loop_lt_01_2,
                        infiniteLoop = true,
                        onFrameChanged = { currentFrame ->
                            // 필요한 경우 처리
                        }
                    )
                }

                is VRUiState.PttSpeak -> {
                    LottieRawAnimationHandler(
                        modifier = Modifier.fillMaxSize(),
                        rawResId = R.raw.loop,
                        infiniteLoop = true,
                        onFrameChanged = { currentFrame ->
                            // 필요한 경우 처리
                        }
                    )
                }

                else -> {
                    // 다른 상태에 대한 처리
                }
            }
        }
    }
}


