package com.example.a01_compose_study.presentation.screen.main.route

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
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
import com.example.a01_compose_study.presentation.screen.sendMsg.SendMsgScreen
import com.example.a01_compose_study.presentation.util.MultipleEventsCutter
import com.example.a01_compose_study.presentation.util.get
import com.example.a01_compose_study.ui.theme.Black2
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MainRoute(
    viewModel: MainViewModel = hiltViewModel(),
//    pttViewModel: PttViewModel = hiltViewModel(),
) {
    val domainUiState by viewModel.domainUiState.collectAsStateWithLifecycle()
    val domainWindowVisibleState by viewModel.domainWindowVisible.collectAsStateWithLifecycle()
    val vrUiState by viewModel.vrUiState.collectAsStateWithLifecycle()

    val scope = rememberCoroutineScope()

    val multipleEventsCutter = remember { MultipleEventsCutter.get() }

    val announceString by viewModel.announceString.collectAsStateWithLifecycle()

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
            viewModel.onDomainEvent(MainEvent.CloseDomainWindowEvent)
        }) {
        VrUiAnimationHandler(vrUiState)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(if (vrUiState.active) Color.Transparent else Color.Black)
        ) {
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
                    }
                }

                is DomainUiState.AnnounceWindow -> {
                    Log.d("@@ AnnounceWindow 진입", "몇번 실행?")
                    AnnounceScreen((domainUiState as DomainUiState.AnnounceWindow).text)
                }

                is DomainUiState.CallWindow -> {
                    Log.d("@@ CallWindow 진입", "진입함 / index : ${domainUiState.scrollIndex}")
                    Box(modifier = Modifier.fillMaxSize()) {
                        CallScreen(
                            domainUiState = domainUiState as DomainUiState.CallWindow,
                            vrUiState = vrUiState,
                            vrDynamicBackground = if (vrUiState.active) Color.Transparent else Color.Black,
                            fixedBackground = Black2
                        )
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
                viewModel.onPttEvent(PttEvent.StartVR(SelectVRResult.PttResult))
            }
        )

        PttButton(
            modifier = Modifier.fillMaxSize(0.13f),
            contentText = "PTT Prepare",
            onClick = {
                viewModel.onPttEvent(PttEvent.PreparePtt)
            }
        )

        PttButton(
            modifier = Modifier.fillMaxSize(0.13f),
            contentText = "PTT Speak",
            onClick = {
                viewModel.onPttEvent(PttEvent.SetSpeakType)
            }
        )

        PttButton(
            modifier = Modifier.fillMaxSize(0.13f),
            contentText = "PTT Loading",
            onClick = {
                scope.launch {
                    viewModel.onPttEvent(PttEvent.SetLoadingType)
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
                    viewModel.onDomainEvent(MainEvent.CloseDomainWindowEvent)
                }
            }
        )

        PttButton(
            modifier = Modifier.fillMaxSize(0.13f),
            contentText = "PTT Help",
            onClick = {
                viewModel.onPttEvent(PttEvent.StartVR(SelectVRResult.HelpResult))
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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(end = 250.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.End
    ) {
        PttButton(
            modifier = Modifier.fillMaxSize(0.13f),
            contentText = "VR Loading",
            onClick = {
                onVREvent(
                    VREvent.ChangeVRUIEvent(
                        VRUiState.PttLoading(
                            active = true,
                            isError = false
                        )
                    )
                )
            }
        )

        PttButton(
            modifier = Modifier.fillMaxSize(0.13f),
            contentText = "VR Speak",
            onClick = {
                onVREvent(
                    VREvent.ChangeVRUIEvent(
                        VRUiState.PttSpeak(
                            active = true,
                            isError = false
                        )
                    )
                )
            }
        )

        PttButton(
            modifier = Modifier.fillMaxSize(0.13f),
            contentText = "VR Listen",
            onClick = {
                onVREvent(
                    VREvent.ChangeVRUIEvent(
                        VRUiState.PttListen(
                            active = true,
                            isError = false
                        )
                    )
                )
            }
        )

        PttButton(
            modifier = Modifier.fillMaxSize(0.13f),
            contentText = "VR Not",
            onClick = {
                onVREvent(
                    VREvent.ChangeVRUIEvent(
                        VRUiState.PttNone(
                            false,
                            isError = false
                        )
                    )
                )
            }
        )

        PttButton(
            modifier = Modifier
                .fillMaxWidth(0.13f)
                .fillMaxHeight(0.3f),
            contentText = "VR isError",
            onClick = {
                onVREvent(
                    VREvent.ChangeVRUIEvent(
                        VRUiState.PttNone(
                            true,
                            isError = true
                        )
                    )
                )
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(end = 470.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.End
    ) {
        PttButton(
            modifier = Modifier
                .fillMaxWidth(0.12f)
                .fillMaxHeight(0.13f),
            contentText = "VR Call List Result",
            onClick = {
                viewModel.onPttEvent(PttEvent.StartVR(SelectVRResult.CallListResult))
            }
        )

        PttButton(
            modifier = Modifier
                .fillMaxWidth(0.12f)
                .fillMaxHeight(0.13f),
            contentText = "VR Call Index List Result",
            onClick = {
                viewModel.onPttEvent(PttEvent.StartVR(SelectVRResult.CallIndexListResult))
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
                /**
                 * 발화로 스크롤이 변경되는 환경이라면 CallViewModel의 Event에 있어야 하지만,
                 * 현재 버튼 클릭으로 index 값을 직접 주입해야 하는 환경이 MainEvent에 임의로 정의해서 사용하고 있는 상황
                 */
                viewModel.onDomainEvent(
                    MainEvent.ChangeScrollIndexEvent(selectedScrollIndex = 5 - 1)
                )
            }
        )
        PttButton(
            modifier = Modifier
                .fillMaxWidth(0.12f)
                .fillMaxHeight(0.13f),
            contentText = "Change ScrollIndex 8",
            onClick = {
                viewModel.onDomainEvent(
                    MainEvent.ChangeScrollIndexEvent(selectedScrollIndex = 8 - 1)
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
    val scope = rememberCoroutineScope()

    var isTouchDown by remember { mutableStateOf(false) }
    var isTouchUp by remember { mutableStateOf(false) }
    if (isTouchUp) {
        scope.launch {
            delay(2000)
            onVREvent(
                VREvent.ChangeVRUIEvent(
                    VRUiState.PttLoading(
                        active = true,
                        isError = false
                    )
                )
            )
        }
        isTouchUp = false
    }
    Log.d("@@isTouchUp", "${isTouchUp}")

    Box(
        modifier = Modifier
            .fillMaxSize()
            /**
             * [pointerInput를 이용하여 터치를 했을 시 VR의 Active 상태를 판별하여 애니메이션을 시작하는 코드]
             * ==> 현재 개선해야할 부분은 VR의 Active 상태를 판별하여 VR 애니매이션을 실행하거나 종료하거나 하는데 이때 리컴포지션이 일어나 각 Domain별 Screen 화면도 재구성이 된다는 점이다.
             * ==> 이러한 초기화 문제가 발생하여 현재 보고 있는 스크롤이 0번째 Index로 초기화되어, 화면이 최상단(0번째 인덱스)으로 스크롤되는 현상이 발생함
             * ==> 다른것부터 하고 난 후 이것을 해결해봐야겠음..(시간이 많이 걸릴 이슈..)
             */
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = {
                        // 긴 터치가 시작되었을 때 실행할 코드
                        Log.d("@@ onLongPress", "Touch started at offset: $it")
                    },
                    onPress = { offset ->
                        // 터치가 시작되었을 때 실행할 코드
                        Log.d("@@ ACTION_DOWN", "Touch started at offset: $offset")
                        onVREvent(
                            VREvent.ChangeVRUIEvent(
                                VRUiState.PttLoading(
                                    active = false,
                                    isError = false
                                )
                            )
                        )
                        isTouchUp = true
                    },
                    onTap = { offset ->
                        // 빠른 터치일 때 실행할 코드
                        Log.d("@@ TAP", "Quick tap at offset: $offset")
                    }
                )
            },
                /**
                 * [pointerInteropFilter를 이용하여 터치를 했을 시 VR의 Active 상태를 판별하여 애니메이션을 시작하는 코드]
                 * ==> 작동이 잘되지 않아 위의 pointerInput을 사용하였음
                 */
//                .pointerInteropFilter { event ->
//                    when (event.actionMasked) {
//                        MotionEvent.ACTION_DOWN -> {
//                            Log.d("@@ ACTION_DOWN", "${event.actionMasked}")
//                            isTouchDown = true
//                        }
//                        MotionEvent.AXIS_SCROLL -> {
//                            Log.d("@@ AXIS_SCROLL", "${event.actionMasked}")
//                            onVREvent(
//                                VREvent.ChangeVRUIEvent(
//                                    VRUiState.PttLoading(
//                                        active = false,
//                                        isError = false
//                                    )
//                                )
//                            )
//                        }
//
//                        MotionEvent.ACTION_UP -> {
//                            Log.d("@@ ACTION_UP", "${event.actionMasked}")
//                            if (isTouchDown) {
//                                // 손을 뗐을 때, 터치 다운 이벤트가 있었으면 실행할 코드
//                                onVREvent(
//                                    VREvent.ChangeVRUIEvent(
//                                        VRUiState.PttLoading(
//                                            active = false,
//                                            isError = false
//                                        )
//                                    )
//                                )
//                                isTouchUp = true
//                            }
//                            isTouchDown = false
//                        }
//                    }
//                    true // 이벤트를 소비하지 않고 하위로 전달
//                },
        contentAlignment = Alignment.BottomCenter,
    ) {
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
}


