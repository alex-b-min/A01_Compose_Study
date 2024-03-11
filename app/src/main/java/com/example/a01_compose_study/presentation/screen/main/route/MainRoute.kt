package com.example.a01_compose_study.presentation.screen.main.route

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.wrapContentWidth
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
import com.example.a01_compose_study.data.HVRError
import com.example.a01_compose_study.data.VRResult
import com.example.a01_compose_study.domain.model.ScreenType
import com.example.a01_compose_study.domain.model.SealedDomainType
import com.example.a01_compose_study.domain.util.ScreenSizeType
import com.example.a01_compose_study.presentation.components.button.PttButton
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
        vrUiState = vrUiState,
        onCloseDomainWindow = {
            viewModel.onDomainEvent(MainEvent.CloseDomainWindowEvent)
        }) {
//        VrUiAnimationHandler(vrUiState)

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

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.TopEnd
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight(0.9f)
                .fillMaxWidth(0.11f)
            ,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.End
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
            ) {
                PttButton(
                    modifier = Modifier.fillMaxSize(),
                    contentText = "PTT Open",
                    onClick = {
                        viewModel.onPttEvent(PttEvent.StartVR(SelectVRResult.PttResult))
                    }
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
            ) {
                PttButton(
                    modifier = Modifier.fillMaxSize(),
                    contentText = "PTT Prepare",
                    onClick = {
                        viewModel.onPttEvent(PttEvent.PreparePtt)
                    }
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
            ) {
                PttButton(
                    modifier = Modifier.fillMaxSize(),
                    contentText = "PTT Speak",
                    onClick = {
                        viewModel.onPttEvent(PttEvent.SetSpeakType)
                    }
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
            ) {
                PttButton(
                    modifier = Modifier.fillMaxSize(),
                    contentText = "PTT Loading",
                    onClick = {
                        scope.launch {
                            viewModel.onPttEvent(PttEvent.SetLoadingType)
                        }
                    }
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
            ) {
                PttButton(
                    modifier = Modifier.fillMaxSize(),
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
            }

            Box(
                modifier = Modifier
                    .weight(1f)
            ) {
                PttButton(
                    modifier = Modifier.fillMaxSize(),
                    contentText = "PTT Close",
                    onClick = {
                        scope.launch {
                            viewModel.onDomainEvent(MainEvent.CloseDomainWindowEvent)
                        }
                    }
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
            ) {
                PttButton(
                    modifier = Modifier.fillMaxSize(),
                    contentText = "PTT Help",
                    onClick = {
                        viewModel.onPttEvent(PttEvent.StartVR(SelectVRResult.HelpResult))
                    }
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
            ) {
                PttButton(
                    modifier = Modifier.fillMaxSize(),
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
            }

            Box(
                modifier = Modifier
                    .weight(1f)
            ) {
                PttButton(
                    modifier = Modifier.fillMaxSize(),
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
            }

            Box(
                modifier = Modifier
                    .weight(1f)
            ) {
                PttButton(
                    modifier = Modifier.fillMaxSize(),
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
    Box(
        modifier = Modifier
            .fillMaxSize()
            .offset(x = -220.dp),
        contentAlignment = Alignment.TopEnd
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight(0.5f)
                .fillMaxWidth(0.1f)
            ,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.End
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
            ) {
                PttButton(
                    modifier = Modifier.fillMaxSize(),
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
            }

            Box(
                modifier = Modifier
                    .weight(1f)
            ) {
                PttButton(
                    modifier = Modifier.fillMaxSize(),
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
            }

            Box(
                modifier = Modifier
                    .weight(1f)
            ) {
                PttButton(
                    modifier = Modifier.fillMaxSize(),
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
            }

            Box(
                modifier = Modifier
                    .weight(1f)
            ) {
                PttButton(
                    modifier = Modifier.fillMaxSize(),
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
            }

            Box(
                modifier = Modifier
                    .weight(1f)
            ) {
                PttButton(
                    modifier = Modifier.fillMaxSize(),
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
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .offset(x = -420.dp),
        contentAlignment = Alignment.TopEnd
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight(0.8f)
                .fillMaxWidth(0.1f)
                .wrapContentWidth(Alignment.End),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
            ) {
                PttButton(
                    modifier = Modifier.fillMaxSize(),
                    contentText = "인식된 이름 없는 경우",
                    onClick = {
                        viewModel.onPttEvent(PttEvent.StartVR(SelectVRResult.CallListResult))
                    }
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
            ) {
                PttButton(
                    modifier = Modifier.fillMaxSize(),
                    contentText = "인식된 이름 2개 이상인 경우",
                    onClick = {
                        viewModel.onPttEvent(PttEvent.StartVR(SelectVRResult.CallIndexListResult))
                    }
                )

            }

            Box(
                modifier = Modifier
                    .weight(1f)
            ) {
                PttButton(
                    modifier = Modifier.fillMaxSize(),
                    contentText = "인식된 이름 1개 경우",
                    onClick = {
                        scope.launch {
                            viewModel.onPttEvent(PttEvent.StartVR(SelectVRResult.CallRecognizedContact))
                        }
                    }
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
            ) {
                PttButton(
                    modifier = Modifier.fillMaxSize(),
                    contentText = "VR Line Number Result(5)",
                    onClick = {
                        scope.launch {
                            viewModel.vrmwManager.setVRResult(
                                VRResult(),
                                SelectVRResult.ScrollIndexResult(inputIndex = 5)
                            )
                        }
                    }
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
            ) {
                PttButton(
                    modifier = Modifier.fillMaxSize(),
                    contentText = "VR Line Number Result(Null)",
                    onClick = {
                        scope.launch {
                            viewModel.vrmwManager.setVRResult(
                                VRResult(),
                                SelectVRResult.ScrollIndexResult(inputIndex = null)
                            )
                        }
                    }
                )
            }


            Box(
                modifier = Modifier
                    .weight(1f)
            ) {
                PttButton(
                    modifier = Modifier.fillMaxSize(),
                    contentText = "VR Result: Yes",
                    onClick = {
                        viewModel.vrmwManager.setVRResult(
                            VRResult(),
                            SelectVRResult.CallYesResult
                        )
                    }
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
            ) {
                PttButton(
                    modifier = Modifier.fillMaxSize(),
                    contentText = "VR Result: No",
                    onClick = {
                        viewModel.vrmwManager.setVRResult(
                            VRResult(),
                            SelectVRResult.CallNoResult
                        )
                    }
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
            ) {
                PttButton(
                    modifier = Modifier.fillMaxSize(),
                    contentText = "VR Result: OtherName",
                    onClick = {
                        viewModel.vrmwManager.setVRResult(
                            VRResult(),
                            SelectVRResult.CallOtherNameResult
                        )
                    }
                )
            }
        }
    }
}