package com.example.a01_compose_study.presentation.screen.main.route

import android.util.Log
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.a01_compose_study.data.VRResult
import com.example.a01_compose_study.presentation.components.button.PttButton
import com.example.a01_compose_study.presentation.compose.VrUiAnimationHandler
import com.example.a01_compose_study.presentation.data.UiState
import com.example.a01_compose_study.presentation.data.UiState.isVrActive
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
import com.example.a01_compose_study.presentation.screen.sendMsg.screen.SendMsgScreen
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

    val isVrActive by UiState.isVrActive.collectAsState()
//    var isVrActive by remember { mutableStateOf(true) }


    WindowFrame(
        domainUiState = domainUiState,
        domainWindowVisible = domainWindowVisibleState,
        onCloseDomainWindow = {
            viewModel.onDomainEvent(MainEvent.CloseDomainWindowEvent)
        }) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            UiState.isVrActive.value = false
                            Log.d("sendMsg", "isVrActive.value: ${isVrActive}")
                        },
                        onTap = {
                            UiState.isVrActive.value = true
                            Log.d("sendMsg", "isVrActive.value: ${isVrActive}")
                        }
                    )
                },
            contentAlignment = Alignment.BottomCenter
        ) {
            Log.d("sendMsg", "handler isVrActive.value: ${isVrActive}")
//            if (isVrActive.value) VrUiAnimationHandler(vrUiState)
//            LaunchedEffect(Unit) {
//                UiState.isVrActive.collect { isVrActive ->
//                    if (isVrActive) {
//                        VrUiAnimationHandler(vrUiState)
//                    }
//                }
//            }
            if (isVrActive) VrUiAnimationHandler(vrUiState)


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
                        domainUiState = domainUiState as DomainUiState.SendMessageWindow,
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
            modifier = Modifier
                .fillMaxWidth(0.13f)
                .fillMaxHeight(0.07f),
            contentText = "speak: SendMsg",
            onClick = {
                scope.launch {
//                    viewModel.vrmwManager.setVRResult(VRResult(), SelectVRResult.SendMsgResult)
                    pttViewModel.onPttEvent(PttEvent.StartVR(selectVRResult = SelectVRResult.SendMsgResult))
                }
            }
        )
        PttButton(
            modifier = Modifier
                .fillMaxWidth(0.13f)
                .fillMaxHeight(0.07f),
            contentText = "speak: SendMsg Name",
            onClick = {
                scope.launch {
//                    viewModel.vrmwManager.setVRResult(VRResult(), SelectVRResult.SendMsgResult)
                    pttViewModel.onPttEvent(PttEvent.StartVR(selectVRResult = SelectVRResult.SendMsgNameResult))
                }
            }
        )

        PttButton(
            modifier = Modifier
                .fillMaxWidth(0.13f)
                .fillMaxHeight(0.07f),
            contentText = "speak: SendMsg Name Msg",
            onClick = {
                scope.launch {
//                    viewModel.vrmwManager.setVRResult(VRResult(), SelectVRResult.SendMsgResult)
                    pttViewModel.onPttEvent(PttEvent.StartVR(selectVRResult = SelectVRResult.SendMsgNameMsgResult))
                }
            }
        )

        PttButton(
            modifier = Modifier
                .fillMaxWidth(0.13f)
                .fillMaxHeight(0.07f),
            contentText = "Line 1",
            onClick = {
                scope.launch {
                    viewModel.vrmwManager.setVRResult(VRResult(), SelectVRResult.ScrollIndexResult)
                }
            }
        )

        PttButton(
            modifier = Modifier
                .fillMaxWidth(0.13f)
                .fillMaxHeight(0.07f),
            contentText = "sayMsg : No",
            onClick = {
                scope.launch {
//                    pttViewModel.onPttEvent(PttEvent.StartVR(selectVRResult = SelectVRResult.NoResult))
                    viewModel.vrmwManager.setVRResult(
                        vrResult = VRResult(),
                        selectVRResult = SelectVRResult.NoResult(isSayMessage = true)
                    )
                }
            }
        )
        PttButton(
            modifier = Modifier
                .fillMaxWidth(0.13f)
                .fillMaxHeight(0.07f),
            contentText = "sendMsg : No",
            onClick = {
                scope.launch {
//                    pttViewModel.onPttEvent(PttEvent.StartVR(selectVRResult = SelectVRResult.NoResult))
                    viewModel.vrmwManager.setVRResult(
                        vrResult = VRResult(),
                        selectVRResult = SelectVRResult.NoResult(isSayMessage = false)
                    )
                }
            }
        )
        PttButton(
            modifier = Modifier
                .fillMaxWidth(0.13f)
                .fillMaxHeight(0.07f),
            contentText = "Yes",
            onClick = {
                scope.launch {
                    viewModel.vrmwManager.setVRResult(
                        vrResult = VRResult(),
                        selectVRResult = SelectVRResult.YesResult
                    )
                }
            }
        )

        PttButton(
            modifier = Modifier
                .fillMaxWidth(0.13f)
                .fillMaxHeight(0.08f),
            contentText = "say message",
            onClick = {
                scope.launch {
                    viewModel.vrmwManager.setVRResult(
                        vrResult = VRResult(),
                        selectVRResult = SelectVRResult.MessageReult
                    )
                }
            }
        )
        PttButton(
            modifier = Modifier
                .fillMaxWidth(0.13f)
                .fillMaxHeight(0.09f),
            contentText = "change message",
            onClick = {
                scope.launch {
//                    viewModel.vrmwManager.setVRResult(VRResult(), SelectVRResult.ScrollIndexResult)
                    viewModel.vrmwManager.setVRResult(
                        vrResult = VRResult(),
                        selectVRResult = SelectVRResult.ChangeMessage
                    )
                }
            }
        )
//        PttButton(
//            modifier = Modifier.fillMaxSize(0.13f),
//            contentText = "PTT Open",
//            onClick = {
//                viewModel.onDomainEvent(
//                    event = MainEvent.OpenDomainWindowEvent(
//                        domainType = SealedDomainType.Ptt,
//                        screenType = ScreenType.PttListen,
//                        mwContext = null,
//                        data = announceString,
//                        isError = false,
//                        screenSizeType = ScreenSizeType.Small
//                    )
//                )
////                pttViewModel.onPttEvent(PttEvent.StartVR(selectVRResult = SelectVRResult.SendMsgResult))
//            }
//        )
//
//        PttButton(
//            modifier = Modifier.fillMaxSize(0.13f),
//            contentText = "PTT Prepare",
//            onClick = {
//                pttViewModel.onPttEvent(PttEvent.PreparePtt)
//            }
//        )
//
//        PttButton(
//            modifier = Modifier.fillMaxSize(0.13f),
//            contentText = "PTT Speak",
//            onClick = {
//                pttViewModel.onPttEvent(PttEvent.SetSpeakType)
//            }
//        )
//
//        PttButton(
//            modifier = Modifier.fillMaxSize(0.13f),
//            contentText = "PTT Loading",
//            onClick = {
//                scope.launch {
//                    pttViewModel.onPttEvent(PttEvent.SetLoadingType)
//                }
//            }
//        )
//
//        PttButton(
//            modifier = Modifier.fillMaxSize(0.13f),
//            contentText = "PTT Announce",
//            onClick = {
//                scope.launch {
//                    viewModel.onDomainEvent(
//                        event = MainEvent.OpenDomainWindowEvent(
//                            domainType = SealedDomainType.Announce,
//                            screenType = ScreenType.Prepare,
//                            mwContext = null,
//                            data = "Help",
//                            isError = false,
//                            screenSizeType = ScreenSizeType.Small
//                        )
//                    )
//                }
//            }
//        )
//
//        PttButton(
//            modifier = Modifier.fillMaxSize(0.13f),
//            contentText = "PTT Close",
//            onClick = {
//                scope.launch {
//                    viewModel.onDomainEvent(MainEvent.CloseDomainWindowEvent)
//                }
//            }
//        )
//
//        PttButton(
//            modifier = Modifier.fillMaxSize(0.13f),
//            contentText = "PTT Help",
//            onClick = {
//                pttViewModel.onPttEvent(PttEvent.StartVR(SelectVRResult.HelpResult))
//            }
//        )
//
//        PttButton(
//            modifier = Modifier
//                .fillMaxWidth(0.13f)
//                .fillMaxHeight(0.2f),
//            contentText = "ERROR_HMI",
//            onClick = {
//                multipleEventsCutter.processEvent {
////                        viewModel.onDomainEvent(
////                            event = MainEvent.OpenDomainWindowEvent(
////                                domainType = SealedDomainType.Ptt,
////                                screenType = ScreenType.PttListen,
////                                data = "에러",
////                                isError = true,
////                                screenSizeType = ScreenSizeType.Small
////                            )
////                        )
//                    viewModel.vrmwManager.setVRError(HVRError.ERROR_HMI)
//                }
//            }
//        )
//
//        PttButton(
//            modifier = Modifier
//                .fillMaxWidth(0.13f)
//                .fillMaxHeight(0.25f),
//            contentText = "Size Up",
//            onClick = {
//                val resultScreenSizeType = when (domainUiState.screenSizeType) {
//                    is ScreenSizeType.Zero -> ScreenSizeType.Zero
//                    is ScreenSizeType.Small -> ScreenSizeType.Middle
//                    is ScreenSizeType.Middle -> ScreenSizeType.Large
//                    is ScreenSizeType.Large -> ScreenSizeType.Full
//                    is ScreenSizeType.Full -> ScreenSizeType.Full
//                }
//                viewModel.onDomainEvent(
//                    MainEvent.ChangeDomainWindowSizeEvent(
//                        resultScreenSizeType
//                    )
//                )
//            }
//        )
//
//        PttButton(
//            modifier = Modifier
//                .fillMaxWidth(0.13f)
//                .fillMaxHeight(0.25f),
//            contentText = "Size Down",
//            onClick = {
//                val resultScreenSizeType = when (domainUiState.screenSizeType) {
//                    is ScreenSizeType.Zero -> ScreenSizeType.Zero
//                    is ScreenSizeType.Small -> ScreenSizeType.Small
//                    is ScreenSizeType.Middle -> ScreenSizeType.Small
//                    is ScreenSizeType.Large -> ScreenSizeType.Middle
//                    is ScreenSizeType.Full -> ScreenSizeType.Large
//                }
//                viewModel.onDomainEvent(
//                    MainEvent.ChangeDomainWindowSizeEvent(
//                        resultScreenSizeType
//                    )
//                )
//            }
//        )
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
                viewModel.onDomainEvent(
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
                viewModel.onDomainEvent(
                    MainEvent.ChangeScrollIndexEvent(10)
                )
            }
        )
    }
}

