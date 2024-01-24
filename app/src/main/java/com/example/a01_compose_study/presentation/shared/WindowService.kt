package com.example.a01_compose_study.presentation.shared

import android.view.View
import androidx.compose.animation.core.AnimationState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.dimensionResource
import androidx.core.view.drawToBitmap
import com.example.a01_compose_study.R
import com.example.a01_compose_study.data.DialogueMode
import com.example.a01_compose_study.data.DomainType
import com.example.a01_compose_study.data.MWContext
import com.example.a01_compose_study.domain.model.NoticeModel
import com.example.a01_compose_study.data.ScreenData
import com.example.a01_compose_study.data.ScreenType
import com.example.a01_compose_study.data.WindowMode
import com.example.a01_compose_study.domain.state.AdditionalInfo
import com.example.a01_compose_study.domain.state.Tracker
import com.example.a01_compose_study.domain.util.CustomLogger
import com.example.a01_compose_study.presentation.shared.SharedProperty.addJob
import com.example.a01_compose_study.presentation.shared.SharedProperty.bluetoothState
import com.example.a01_compose_study.presentation.shared.SharedProperty.currScreen
import com.example.a01_compose_study.presentation.shared.SharedProperty.serviceManager
import com.example.a01_compose_study.presentation.shared.SharedProperty.systemState
import com.example.a01_compose_study.presentation.shared.SharedProperty.testState
import com.example.a01_compose_study.presentation.shared.SharedProperty.uiState
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

object WindowService {

    var pendingEvent: Runnable? = null

    val screenDeuque = ArrayDeque<ScreenData>()
    var offScreenView: ComposeView? = null
    var addScreenJob: Job? = null
    var jobRunner: Job? = null

    // 현재 앱 흐름을 관찰하는 Data class
    val m_tracker = MutableStateFlow(Tracker(DomainType.None, ScreenType.None, DialogueMode.NONE))

    val loadingProgress = MutableStateFlow(false)
    var makeBitmapDeffered: CompletableDeferred<Any>? = null
    val bitmapReady = MutableStateFlow(false)

    fun hideWindow(
        beep: Boolean = false,
        clear: Boolean = true,
        pendingEvent: Runnable? = null,
        immediate: Boolean = false,
        naviHide: Boolean = false
    ) {
        CustomLogger.i("hideWindow immediate:${immediate} clear:${clear} pendingEvent:${pendingEvent} tempHide:${uiState.tempHide.value} visible:${uiState.contentVisible.value}")
        addJob(clear = true, keyString = "hideWindow") {

            addScreenJob?.cancel()
            addScreenJob = null

            if (systemState.isVRReady()) {
                serviceManager?.vrmwManager?.stop()
            }

            if (immediate) {
                immediateHide()
            }

            if (naviHide) {
                uiState.animationState.value = AnimationState.DONE
            }

            if (uiState.contentVisible.value) {
                uiState.contentVisible.value = false
            } else {
                if (!uiState.tempHide.value) {
                    uiState.showWindow.value = false
                }
            }

            uiState.showError.value = false
            //hfpDevice.value = HfpDevice(hfpDevice.value.device, hfpDevice.value.connect, false)
            bluetoothState.hfpDevice.value.apply {
                this.recognizing = false
            }
            bluetoothState.prevHfpDevice.value = bluetoothState.hfpDevice.value.device

            if (systemState.isVRReady()) {
                if (beep) {
                    serviceManager?.vrmwManager?.playBeep()
                }
            }
            if (clear) {
                clearScreen()
            }
            if (!immediate && pendingEvent != null) {
                this@WindowService.pendingEvent = pendingEvent
            }

            if (!immediate) {
                // ?? 왜 ptt눌렀을때 immediate true로 내려주는지 모르겠음. 그래서 다음 tc시작할때 여기가 add가 되는문제가있음.
                val info = AdditionalInfo()
                info.m_isHideWindow = true
                info.m_playBeep = beep
                addTracker(null, null, info)
            }

            changeWindowMode(WindowMode.SMALL)

            // printScreenList("after hideWindow")
        }
    }

    private fun immediateHide() {
        uiState.tempHide.value = true
        uiState.pendingVisibleEvent = kotlinx.coroutines.Runnable {
            CustomLogger.e("pendingVisibleEvent :")
            if (uiState.tempHide.value) {
                // 즉시없애고 다시 올라오는 애니메이션 처리
                uiState.tempHide.value = false
                uiState.contentVisible.value = true
            }
            pendingEvent?.run()
            uiState.pendingVisibleEvent = null
        }
        CustomLogger.e("        setAniState:${AnimationState.DISABLED} in hideWindow")
        uiState.animationState.value = AnimationState.DISABLED
    }

    fun clearScreen() {
        CustomLogger.i("clearScreen start")
        currScreen.value?.let {
            screenDeuque.add(it)
        }
        currScreen.value = null
        screenDeuque.forEach {
            it.release()
        }
        screenDeuque.clear()
        CustomLogger.i("clearScreen end")
    }

    fun addTracker(
        screenData: ScreenData?,
        mwContext: MWContext?,
        additionalInfo: AdditionalInfo? = null,
        disableWait: Boolean = true
    ) {
        CustomLogger.i("addTracker screenData: $screenData")
        CustomLogger.i("addTracker mwContext : $mwContext")
        CustomLogger.i("addTracker additionalInfo: $additionalInfo")
        val tracker =
            Tracker(screenData?.domainType, screenData?.screenType, mwContext?.dialogueMode)
        if (additionalInfo != null) {
            tracker.additionalInfo = additionalInfo
        }
        m_tracker.value = tracker
        m_tracker.value.cnt.isCurrentScenarioData = true
        m_tracker.value.cnt.currentIdx = testState.scenarioCount++
        CustomLogger.i("after addTracker ${m_tracker.value}")
        if (testState.isTesting && disableWait) {
            CustomLogger.i("Wait block")
            testState.doNext?.block(10000)
            testState.doNext?.close()
        }
        CustomLogger.i("end addTracker")
    }

    fun changeWindowMode(mode: WindowMode, noAnimation: Boolean = false) {
        uiState.noAnimation = noAnimation
        if (uiState.windowMode.value != mode) {
            CustomLogger.e("changeWindowMode curr:${uiState.windowMode.value} to ${mode} noAnimation:${noAnimation} ")
            //uiState.boxAnimationFinish.value = false
            uiState.windowMode.value = mode
        }
    }

    fun addScreen(screenData: ScreenData, clear: Boolean = false) {
        CustomLogger.i("addScreen : ${screenData.domainType} ${screenData.screenType} clear:${clear} screenData:${screenData.hashCode()}")

        addScreenJob?.cancel()
        //화면이 변경될 때는 메인스레드를 사용하도록 한다
        addScreenJob = CoroutineScope(Dispatchers.Main).launch {
            if (clear) {
                clearScreen()
            }
            checkListBitmap(screenData)

            if (!this.isActive) {
                return@launch
            }

            screenData.windowMode?.let {
                if (it != uiState.windowMode.value) {
                    val isNotice = screenData.model as? NoticeModel
                    isNotice?.let { model ->
                        changeWindowMode(it, it == WindowMode.SMALL)
                        hideWindowImmediately(pendingEvent = {
                            serviceManager?.launchPtt(screenData)
                        })
                        return@launch
                    } ?: run {
                        changeWindowMode(it, it == WindowMode.SMALL)
                    }

                }
            }


            currScreen.value?.let {
                screenDeuque.add(it)
            }
            currScreen.value = screenData
            CustomLogger.e("makeBitmap screenData Changed")

            if (loadingProgress.value) {
                loadingProgress.value = false
            }

            // 이벤트 수행은 메인스레드를 타지 않아도 될 듯?
            addJob("addScreen", interruptable = true) {
                var additionalInfo = AdditionalInfo()
                additionalInfo.m_uiText = sttString.value
                additionalInfo.noticeModel = screenData.model as? NoticeModel
                addTracker(screenData, screenData.mwContext, additionalInfo)
                screenData.onStart()
            }

            if (!uiState.showWindow.value) {
                uiState.showWindow.value = true
            }
        }
        printScreenList("after addScreen")
    }

    private suspend fun checkListBitmap(screenData: ScreenData) {
        if (screenData.screenType.name.contains(
                "List",
                ignoreCase = true
            )
        ) {
            //return
            // UI 프레임 이슈 있을때 사용
            if (screenData.bitmap == null && !screenData.skipBitmap) {
                makeBitmapDeffered = CompletableDeferred()
                loadingProgress.value = true
                makeBitmap(screenData) {
                    CustomLogger.i("makeBitmap checkListBitmap")
//                    doDisplayScreen(
//                        screenData = screenData,
//                        viewModel = this@WindowSeviceViewModel
//                    )
                }
                makeBitmapDeffered?.await()
            }
        }
    }

    fun hideWindowImmediately(pendingEvent: Runnable? = null) {
        CustomLogger.e("** hideWindowImmediately Called ** ")
        uiState.cancelVisibleAni.value = ++uiState.cancelVisibleAni.value
        hideWindow(pendingEvent = pendingEvent, immediate = true)
    }

    fun printScreenList(msg: String) {
        CustomLogger.e("    printScreenList from [${msg}]")
        CustomLogger.e("        currScreen : [${currScreen.value?.domainType}, ${currScreen.value?.screenType}]")
        try {
            if (screenDeuque.isNotEmpty()) {
                screenDeuque.forEachIndexed { index, screenData ->
                    CustomLogger.e("            deque ${index} : [${screenData.domainType}, ${screenData.screenType}]")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun makeBitmap(screenData: ScreenData, content: @Composable () -> Unit) {
        bitmapReady.value = false
        CustomLogger.i("makeBitmap ${offScreenView != null}")
        val start_t = System.currentTimeMillis()
        // 비트맵 생성용으로 만들어 둔 오프스크린 컴포즈뷰에서 컴포즈를 렌더링
        offScreenView?.let {
            CustomLogger.i("makeBitmap offScreenView apply viewSize:${offScreenView?.width} ${offScreenView?.height}")
            it.apply {
                CustomLogger.i("makeBitmap apply")
                it.visibility = View.INVISIBLE
                disposeComposition()
                setContent {
                    val bitmapReady = bitmapReady.collectAsState()
                    // 비트맵이 없는 상태면 컴포즈 렌더링 시작
                    if (!bitmapReady.value) {
                        CerenceTheme {
                            Box(
                                modifier = Modifier
                                    .width(dimensionResource(R.dimen.agent_width))
                                    //.wrapContentHeight()
                                    .height(dimensionResource(R.dimen.agent_max_height))
                            ) {
                                CustomLogger.i("makeBitmap recompose")
                                content.invoke()
                            }
                        }

                    }
                }
            }
            it.invalidate()
            it.post {
                //it.invalidate()
                //bitmap = DummyBitmap(it.drawToBitmap(), screenType)
                val getBitmap_t = System.currentTimeMillis()
                val bitmap = it.drawToBitmap()
                CustomLogger.i("makeBitmap drawToBitmap bitmapSize: ${bitmap.width} ${bitmap.height} time:${System.currentTimeMillis() - getBitmap_t}")

                screenData.bitmap = bitmap
                it.visibility = View.GONE
                bitmapReady.value = true
                makeBitmapDeffered?.complete("")
                CustomLogger.i("makeBitmap Done bitmapSize: ${screenData.bitmap?.width} ${screenData.bitmap?.height} time:${System.currentTimeMillis() - start_t}")
            }
        } ?: run {
            CustomLogger.e("makeBitmap fail compose null")
        }
    }

    fun popScreen(): ScreenData? {
        CustomLogger.i("beforePop :${screenDeuque.size}")
        val popData = screenDeuque.removeLastOrNull()
        CustomLogger.i("popScreen :${popData?.domainType} ${popData?.screenType} remain: ${screenDeuque.size}")
        CustomLogger.i("currScreen :${currScreen.value?.domainType} ${currScreen.value?.screenType}")
        screenDeuque.forEach {
            CustomLogger.i("remain :${it.domainType} ${it.screenType}")
        }
        currScreen.value?.onPop()
        popData?.let {
            CustomLogger.i("popScreen popData?: ${popData != null} ")
            it.windowMode?.let { mode ->
                if (mode != uiState.windowMode.value) {
                    changeWindowMode(mode, true)
                }
            }
            currScreen.value = it
            addJob("popScreen", interruptable = true) {
                var additionalInfo = AdditionalInfo()
                additionalInfo.m_uiText = sttString.value
                addTracker(currScreen.value, currScreen.value?.mwContext)
                it.onResume()
            }
        } ?: run {
            CustomLogger.i("popScreen popData: null")
            currScreen.value?.release()
            currScreen.value = null
            hideWindow(true)
        }
        printScreenList("after popScreen")
        return popData
    }

    fun changeCurrScreen(screenData: ScreenData) {

        CustomLogger.i("changeCurrScreen beforeChange :${screenDeuque.size}")
        CustomLogger.i("changeCurrScreen screenData :${screenData.domainType} ${screenData.screenType}")
        CustomLogger.i("changeCurrScreen currScreen :${currScreen.value?.domainType} ${currScreen.value?.screenType}")
        screenDeuque.forEach {
            CustomLogger.i("remain : ${it.screenType}")
        }
        currScreen.value = screenData
        var additionalInfo = AdditionalInfo()
        additionalInfo.m_uiText = sttString.value
        addTracker(screenData, screenData.mwContext, additionalInfo)
        screenData.onStart()
        CustomLogger.i("after changeCurrScreen currScreen :${currScreen.value?.domainType} ${currScreen.value?.screenType}")
        printScreenList("after changeCurrScreen")
    }

    fun prevScreen(): ScreenData {
        CustomLogger.i("current screen :${currScreen.value?.screenType}")
        var prevScreen = ScreenData(DomainType.None, ScreenType.None)
        if (screenDeuque.size > 1) {
            prevScreen = screenDeuque[screenDeuque.size - 1]
            CustomLogger.i("find prevScreen [${prevScreen.screenType}]")
        }
        return prevScreen
    }

    fun cancelCurrentScreen() {
        addScreenJob?.cancel()
        addScreenJob = null
        clearJob()
        if (systemState.isVRReady()) {
            serviceManager?.vrmwManager?.stop()
        }

        //clearScreen()
    }


    // Message value 초기화를 위해 사용 (시나리오가 시작되었는지 판단)
    fun getScreenDequeSize(): Int {
        return screenDeuque.size
    }

    // SendMsg 시나리오 화면 체크 -> onChangeMessage에서 사용
    // Call 시나리오 화면 체크
    fun checkScreenQueue(screenType: ScreenType): Boolean {
        CustomLogger.i("checkScreenQueue screenType : $screenType")
        CustomLogger.i("screenDeuque size : ${screenDeuque.size}")

        screenDeuque.forEach {
            CustomLogger.i("screenDeuque check domainType :[${it.domainType}], screenType : [${it.screenType}]")
        }

        var checkScreen: ScreenData
        // SendMsg 에서는 message 발화 화면(messageName)이 screenDeuque에 있는지 체크
        // Call 에서는 YesNo화면이 screenDeuque에 있는지 체크
        return when (screenDeuque.size) {
            // SendMsg : PTT(0), List(1), List(2), List(3), messageName
            // Call X
            5 -> {
                checkScreen = screenDeuque.elementAt(4)
                CustomLogger.i("checkScreenQueue checkScreen screenType : ${checkScreen.screenType}")
                checkScreen.screenType == screenType
            }
            // SendMsg : PTT(0), List(1), List(2), messageName
            // Call : PTT(0), List(1), List(2), YesNo
            4 -> {
                checkScreen = screenDeuque.elementAt(3)
                CustomLogger.i("checkScreenQueue checkScreen screenType : ${checkScreen.screenType}")
                checkScreen.screenType == screenType
            }
            // SendMsg : PTT(0), List(1), messageName
            // Call : PTT(0), List(1), YesNo
            3 -> {
                checkScreen = screenDeuque.elementAt(2)
                CustomLogger.i("checkScreenQueue checkScreen screenType : ${checkScreen.screenType}")
                checkScreen.screenType == screenType
            }
            // SendMsg : PTT(0), messageName
            // Call : PTT(0), YesNo
            2 -> {
                checkScreen = screenDeuque.elementAt(1)
                CustomLogger.i("checkScreenQueue screen_1 screenType : ${checkScreen.screenType}")
                checkScreen.screenType == screenType
            }

            else -> {
                return false
            }
        }
    }

    /**
     * UI를 숨길때 (윈도우 숨김)
     * PendingEvent Runnable 객체가 있을 경우
     * 해당 객체를 수행하고 제거 함
     */
    fun runPendedEvent() {
        CustomLogger.e("runPendedEvent()")
        pendingEvent?.run()
        pendingEvent = null
    }

    /**
     * 화면이 추가 될때, 화면 타입이 List 인 경우 Bitmap을 생성 하도록 함
     * UI 성능이 좋지 않은 NIRO 단말에서의 이슈 이나 A01에서 충분히 성능이 나온다면
     * bitmap 생성의 부하때문에 이 메서드는 실행 하지 않는 편이 반응성에서 더 좋을 수 있음
     *
     */
//    private suspend fun checkListBitmap(screenData: ScreenData) {
//        if (screenData.screenType.name.contains(
//                "List",
//                ignoreCase = true
//            )
//        ) {
//            //return
//            // UI 프레임 이슈 있을때 사용
//            if (screenData.bitmap == null && !screenData.skipBitmap) {
//                makeBitmapDeffered = CompletableDeferred()
//                loadingProgress.value = true
//                makeBitmap(screenData) {
//                    CustomLogger.i("makeBitmap checkListBitmap")
//                    doDisplayScreen(screenData = screenData, viewModel = this@ServiceViewModel)
//                }
//                makeBitmapDeffered?.await()
//            }
//        }
//    }
}