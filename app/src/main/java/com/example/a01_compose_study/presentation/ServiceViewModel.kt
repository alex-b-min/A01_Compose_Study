//package com.example.a01_compose_study.presentation
//
//import android.app.ActivityManager
//import android.content.Context
//import android.content.Intent
//import android.content.pm.PackageManager
//import android.content.res.Configuration
//import android.telephony.TelephonyManager
//import androidx.compose.ui.platform.ComposeView
//import androidx.lifecycle.MutableLiveData
//import com.example.a01_compose_study.data.DialogueMode
//import com.example.a01_compose_study.data.DomainType
//import com.example.a01_compose_study.data.HLanguageType
//import com.example.a01_compose_study.data.MWContext
//import com.example.a01_compose_study.data.ScreenData
//import com.example.a01_compose_study.data.ScreenType
//import com.example.a01_compose_study.data.UxPreset
//import com.example.a01_compose_study.data.VrConfig
//import com.example.a01_compose_study.data.WindowMode
//import com.example.a01_compose_swns_1618
//import com.example.a01_compose_study.data.repositoryImpl.JobManager
//import com.example.a01_compose_study.domain.model.NoticeModel
//import com.example.a01_compose_study.domain.model.RequestType
//import com.example.a01_compose_study.domain.model.WeatherModel
//import com.example.a01_compose_study.domain.state.AdditionalInfo
//import com.example.a01_compose_study.domain.state.AnimationState
//import com.example.a01_compose_study.domain.state.BluetoothState
//import com.example.a01_compose_study.domain.state.MWState
//import com.example.a01_compose_study.domain.state.NetworkState
//import com.example.a01_compose_study.domain.state.SettingState
//import com.example.a01_compose_study.domain.state.SystemState
//import com.example.a01_compose_study.domain.state.TestState
//import com.example.a01_compose_study.domain.state.Tracker
//import com.example.a01_compose_study.domain.state.UIState
//import com.example.a01_compose_study.domain.util.CustomLogger
//import com.example.a01_compose_study.domain.util.CustomTimer
//import com.example.a01_compose_study.domain.util.ParseBundle
//import kotlinx.coroutines.CompletableDeferred
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.Job
//import kotlinx.coroutines.Runnable
//import kotlinx.coroutines.delay
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.isActive
//import kotlinx.coroutines.launch
//import java.io.File
//import java.util.Locale
//import javax.inject.Inject
//import javax.inject.Singleton
//import kotlin.coroutines.Continuation
//
//
//@Singleton
//class ServiceViewModel @Inject constructor(
//): JobManager() {
//    val TAG = this.javaClass.simpleName
//
//    var timer: CustomTimer? = null
//
//    val isActivityShow = false
//    val bundleDelegate = MutableStateFlow<ParseBundle<out Any>?>(null)
//
//
//    val announceString = MutableLiveData("")
//    private val sttString = MutableStateFlow("")
//    val guideString = MutableLiveData<String>()
//
//    //system state
//    val systemState = SystemState()
//
//    //window ui 관련
//    val uiState = UIState()
//
//    //Blutooth 상태
//    val bluetoothState = BluetoothState()
//
//    val mwState = MWState()
//
//    val vrConfig = MutableStateFlow(VrConfig())
//
//    val requestPTT = MutableStateFlow(false)
//    val currScreen = MutableStateFlow<ScreenData?>(null)
//    val screenDeuque = ArrayDeque<ScreenData>()
//
//    var weatherDataModel = MutableStateFlow(WeatherModel("MAX"))
//    var languageType = MutableStateFlow(HLanguageType.MAX)
//
//    // Navi 상태 관리
//    val isNaviRouteSearching = MutableStateFlow(0)
//    val isNaviDestinationState = MutableStateFlow(0)
//
//    val requestMap = mutableMapOf<RequestType, Continuation<Intent?>>()
//
//    // Radio 상태 관리
//    val isBroadcastCanReceive = MutableStateFlow(true)
//
//    // NetworkHeader 상태관리
//    val networkState = NetworkState()
//
//    // Telephony phone 상태 관리
//    val phoneState = MutableStateFlow(TelephonyManager.EXTRA_STATE_IDLE)
//
//    val testState = TestState()
//
//    // 현재 앱 흐름을 관찰하는 Data class
//    val m_tracker = MutableStateFlow(Tracker(DomainType.None, ScreenType.None, DialogueMode.NONE))
//
////    var playBeep: (() -> Unit?)? = null
////    var testListShow: (() -> Unit?)? = null
////    var stopVr: (() -> Unit?)? = null
////    var resumeVr: ((MWContext, Boolean) -> Unit?)? = null
////    var vrClear: (() -> Unit?)? = null
//
////    var testRecorder: TestRecorder? = null
//
////    var bitmap: DummyBitmap? = null
//    val bitmapReady = MutableStateFlow(false)
//    var bitmapRequested = MutableStateFlow(false)
//    var offScreenView: ComposeView? = null
//    var pendingEvent: Runnable? = null
//
////    var isDevelopMode = MutableStateFlow(BuildConfig.IS_SHOW_DEBUG_SCREEN)
//    var isNotUseCCU = MutableStateFlow(false)
//
//    var serviceManager: ServiceManager? = null
//
//
//    // System state
//    var settingState = SettingState()
//
//    // G2P
//    val isWaitingPBG2PState = MutableStateFlow(false)
//
//    var jobRunner: Job? = null
//
//    var addScreenJob: Job? = null
//
////    override fun onCleared() {
////        jobRunner?.cancel()
////        super.onCleared()
////    }
//
//    fun getVrConfig(): VrConfig {
//        return vrConfig.value
//    }
//
//    val loadingProgress = MutableStateFlow(false)
//    var makeBitmapDeffered: CompletableDeferred<Any>? = null
//
//    /**
//     * 화면이 추가 될때, 화면 타입이 List 인 경우 Bitmap을 생성 하도록 함
//     * UI 성능이 좋지 않은 NIRO 단말에서의 이슈 이나 A01에서 충분히 성능이 나온다면
//     * bitmap 생성의 부하때문에 이 메서드는 실행 하지 않는 편이 반응성에서 더 좋을 수 있음
//     *
//     */
////    private suspend fun checkListBitmap(screenData: ScreenData) {
////        if (screenData.screenType.name.contains(
////                "List",
////                ignoreCase = true
////            )
////        ) {
////            //return
////            // UI 프레임 이슈 있을때 사용
////            if (screenData.bitmap == null && !screenData.skipBitmap) {
////                makeBitmapDeffered = CompletableDeferred()
////                loadingProgress.value = true
////                makeBitmap(screenData) {
////                    CustomLogger.i("makeBitmap checkListBitmap")
////                    doDisplayScreen(screenData = screenData, viewModel = this@ServiceViewModel)
////                }
////                makeBitmapDeffered?.await()
////            }
////        }
////    }
//
//    /**
//     * 입력받은 ScreenData를 토대로 UI를 구성하도록 하고
//     * 구성된 이벤트에 따라 이벤트를 수행 시킴
//     * 기존에 남아있던 데이터를 덱으로 보내고
//     * 새로 입력된 데이터를 기준으로 화면을 갱신하도록 함
//     *
//     * @param screenData
//     * @param clear : 기존 화면을 전부 삭제하고 현재 화면을 SingleTop 으로 할지 여부
//     *
//     */
//    fun addScreen(screenData: ScreenData, clear: Boolean = false) {
//        CustomLogger.i("addScreen : ${screenData.domainType} ${screenData.screenType} clear:${clear} screenData:${screenData.hashCode()}")
//
//        addScreenJob?.cancel()
//        //화면이 변경될 때는 메인스레드를 사용하도록 한다
//        addScreenJob = CoroutineScope(Dispatchers.Main).launch {
//            if (clear) {
//                clearScreen()
//            }
//            //checkListBitmap(screenData)
//
//            if (!this.isActive) {
//                return@launch
//            }
//
//            screenData.windowMode?.let {
//                if (it != uiState.windowMode.value) {
//                    val isNotice = screenData.model as? NoticeModel
//                    isNotice?.let { model ->
//                        changeWindowMode(it, it == WindowMode.SMALL)
//                        hideWindowImmediately(pendingEvent = {
//                            serviceManager?.launchPtt(screenData)
//                        })
//                        return@launch
//                    } ?: run {
//                        changeWindowMode(it, it == WindowMode.SMALL)
//                    }
//
//                }
//            }
//
//
//            currScreen.value?.let {
//                screenDeuque.add(it)
//            }
//            currScreen.value = screenData
//            CustomLogger.e("makeBitmap screenData Changed")
//
//            if (loadingProgress.value) {
//                loadingProgress.value = false
//            }
//
//            // 이벤트 수행은 메인스레드를 타지 않아도 될 듯?
//            addJob("addScreen", interruptable = true) {
//                var additionalInfo = AdditionalInfo()
//                additionalInfo.m_uiText = sttString.value
//                additionalInfo.noticeModel = screenData.model as? NoticeModel
//                addTracker(screenData, screenData.mwContext, additionalInfo)
//                screenData.onStart()
//            }
//
//            if (!uiState.showWindow.value) {
//                uiState.showWindow.value = true
//            }
//        }
//        printScreenList("after addScreen")
//    }
//
//    fun addTracker(
//        screenData: ScreenData?,
//        mwContext: MWContext?,
//        additionalInfo: AdditionalInfo? = null,
//        disableWait: Boolean = true
//    ) {
//        CustomLogger.i("addTracker screenData: $screenData")
//        CustomLogger.i("addTracker mwContext : $mwContext")
//        CustomLogger.i("addTracker additionalInfo: $additionalInfo")
//        val tracker =
//            Tracker(screenData?.domainType, screenData?.screenType, mwContext?.dialogueMode)
//        if (additionalInfo != null) {
//            tracker.additionalInfo = additionalInfo
//        }
//        m_tracker.value = tracker
//        m_tracker.value.cnt.isCurrentScenarioData = true
//        m_tracker.value.cnt.currentIdx = testState.scenarioCount++
//        CustomLogger.i("after addTracker ${m_tracker.value}")
//        if (testState.isTesting && disableWait) {
//            CustomLogger.i("Wait block")
//            testState.doNext?.block(10000)
//            testState.doNext?.close()
//        }
//        CustomLogger.i("end addTracker")
//    }
//
////    fun getContextPath(fileName: String): String {
////        val context = BaseApplication.appContext
////        return "${context.getExternalFilesDir("")}/${fileName}"
////    }
//
//    /**
//     * 테스트용 녹화 시작
//     * 권한 획득용 Activity를 수행 하며, 확인 버튼을 눌러줘야 녹화가 시작 됨
//     * @param outFilePath : 녹화파일 출력 경로 및 파일명 .mp4 까지 입력 해야 함
//     *
//     */
////    fun startTestRecording(outFilePath: String?) {
////        val context = BaseApplication.appContext
////        val broadcastReceiver = object : BroadcastReceiver() {
////            override fun onReceive(context: Context?, intent: Intent?) {
////                if (intent?.action == "ACTION_SCREEN_CAPTURE_PERMISSION_RESULT") {
////                    val resultCode = intent.getIntExtra("resultCode", Activity.RESULT_CANCELED)
////                    val dataIntent: Intent? = intent.getParcelableExtra("data")
////                    // 이제 여기서 resultCode와 data를 사용하여 MediaProjection을 설정하고 녹화를 시작할 수 있습니다.
////                    uiState.showWindow.value = true
////                    dataIntent?.let {
////                        startRecord(resultCode, dataIntent, outFilePath)
////                    }
////                    context?.let {
////                        LocalBroadcastManager.getInstance(it).unregisterReceiver(this)
////                    }
////
////                }
////            }
////        }
////
////        LocalBroadcastManager.getInstance(context).registerReceiver(
////            broadcastReceiver,
////            IntentFilter("ACTION_SCREEN_CAPTURE_PERMISSION_RESULT")
////        )
////        uiState.showWindow.value = false
////
////        val intent = Intent(context, TransparentActivity::class.java)
////        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
////        context.startActivity(intent)
////    }
//
//    /**
//     * 실질적으로 녹화 객체를 생성하여 녹화 시작
//     */
////    fun startRecord(resultCode: Int, data: Intent, outFilePath: String?) {
////        testRecorder = TestRecorder(
////            serviceManager!!.vrmwManager.mediaOut,
////            serviceManager!!.vrmwManager.mediaIn
////        ).apply {
////            this.initRecord(BaseApplication.appContext, resultCode, data, outFilePath)
////            testState.isRecording.value = true
////        }
////    }
//
//    /**
//     * 녹화 종료
//     */
////    fun stopRecording() {
////        testRecorder?.stopRecording()
////        testState.isRecording.value = false
////    }
//
//    /**
//     * 현재 화면 및 덱에 쌓인 화면들 정리
//     */
//    fun clearScreen() {
//        CustomLogger.i("clearScreen start")
//        currScreen.value?.let {
//            screenDeuque.add(it)
//        }
//        currScreen.value = null
//        screenDeuque.forEach {
//            it.release()
//        }
//        screenDeuque.clear()
//        CustomLogger.i("clearScreen end")
//    }
//
//    /**
//     * 뒤로 가기 이벤트
//     * 현재화면을 제거하고 Pop 이벤트를 수행 함
//     * 덱에 마지막으로 쌓인 화면을 출력 하고 Resume 이벤트를 수행 함
//     * 덱에 쌓인 화면이 없다면 윈도우 숨김 이벤트를 수행 함
//     */
//    fun popScreen(): ScreenData? {
//        CustomLogger.i("beforePop :${screenDeuque.size}")
//        val popData = screenDeuque.removeLastOrNull()
//        CustomLogger.i("popScreen :${popData?.domainType} ${popData?.screenType} remain: ${screenDeuque.size}")
//        CustomLogger.i("currScreen :${currScreen.value?.domainType} ${currScreen.value?.screenType}")
//        screenDeuque.forEach {
//            CustomLogger.i("remain :${it.domainType} ${it.screenType}")
//        }
//        currScreen.value?.onPop()
//        popData?.let {
//            CustomLogger.i("popScreen popData?: ${popData != null} ")
//            it.windowMode?.let { mode ->
//                if (mode != uiState.windowMode.value) {
//                    changeWindowMode(mode, true)
//                }
//            }
//            currScreen.value = it
//            addJob("popScreen", interruptable = true) {
//                var additionalInfo = AdditionalInfo()
//                additionalInfo.m_uiText = sttString.value
//                addTracker(currScreen.value, currScreen.value?.mwContext)
//                it.onResume()
//            }
//        } ?: run {
//            CustomLogger.i("popScreen popData: null")
//            currScreen.value?.release()
//            currScreen.value = null
//            hideWindow(true)
//        }
//        printScreenList("after popScreen")
//        return popData
//    }
//
//    fun changeCurrScreen(screenData: ScreenData) {
//
//        CustomLogger.i("changeCurrScreen beforeChange :${screenDeuque.size}")
//        CustomLogger.i("changeCurrScreen screenData :${screenData.domainType} ${screenData.screenType}")
//        CustomLogger.i("changeCurrScreen currScreen :${currScreen.value?.domainType} ${currScreen.value?.screenType}")
//        screenDeuque.forEach {
//            CustomLogger.i("remain : ${it.screenType}")
//        }
//        currScreen.value = screenData
//        var additionalInfo = AdditionalInfo()
//        additionalInfo.m_uiText = sttString.value
//        addTracker(screenData, screenData.mwContext, additionalInfo)
//        screenData.onStart()
//        CustomLogger.i("after changeCurrScreen currScreen :${currScreen.value?.domainType} ${currScreen.value?.screenType}")
//        printScreenList("after changeCurrScreen")
//    }
//
//    fun prevScreen(): ScreenData {
//        CustomLogger.i("current screen :${currScreen.value?.screenType}")
//        var prevScreen = ScreenData(DomainType.None, ScreenType.None)
//        if (screenDeuque.size > 1) {
//            prevScreen = screenDeuque[screenDeuque.size - 1]
//            CustomLogger.i("find prevScreen [${prevScreen.screenType}]")
//        }
//        return prevScreen
//    }
//
////    fun printLocaleInfo() {
////
////        val currentLocale: Locale = Locale.getDefault()
////        val currentLanguage: String = currentLocale.language
////        val currentCountry: String = currentLocale.country
////
////        CustomLogger.e("Locale :")
////        CustomLogger.e("    currentLocale :${currentLocale}")
////        CustomLogger.e("    currentLanguage :${currentLanguage}")
////        CustomLogger.e("    currentCountry :${currentCountry}")
////
////        val context = BaseApplication.appContext
////        val vrConfigString = readJsonWithLocale(context, "vr_config", currentLocale)
////        val vrConfig = GsonWrapper.fromJson(vrConfigString, VrConfig::class.java)
////        CustomLogger.e("VRConfig :")
////        CustomLogger.e("    vrConfig :${vrConfig}")
////        CustomLogger.e("    isSupportASR :${vrConfig.isSupportASR}")
////        CustomLogger.e("    isSupportTTS :${vrConfig.isSupportTTS}")
////        CustomLogger.e("    isSupportServer :${vrConfig.isSupportServer}")
////
////
////        isAppInForeground(context)
////    }
////
////    fun getLocaleConfig(): VrConfig {
////        val currentLocale: Locale = Locale.getDefault()
////        val context = BaseApplication.appContext
////        val vrConfigString = readJsonWithLocale(context, "vr_config", currentLocale)
////        return GsonWrapper.fromJson(vrConfigString, VrConfig::class.java)
////    }
//
//    // JSON 파일 읽기
//    fun readJsonFromRawResource(context: Context, resourceId: Int): String {
//        return context.resources.openRawResource(resourceId).bufferedReader().use {
//            it.readText()
//        }
//    }
//
//    // 다국어 리소스를 지원하는 JSON 파일 읽기
//    fun readJsonWithLocale(context: Context, fileName: String, locale: Locale): String {
//        val resourceId = context.resources.getIdentifier(
//            fileName,
//            "raw",
//            context.packageName
//        )
//        return context.createConfigurationContext(Configuration(context.resources.configuration).apply {
//            setLocale(locale)
//        }).resources.openRawResource(resourceId).bufferedReader().use {
//            it.readText()
//        }
//    }
//
//    fun printScreenList(msg: String) {
//
//        CustomLogger.e("    printScreenList from [${msg}]")
//        CustomLogger.e("        currScreen : [${currScreen.value?.domainType}, ${currScreen.value?.screenType}]")
//        try {
//            if (screenDeuque.isNotEmpty()) {
//                screenDeuque.forEachIndexed { index, screenData ->
//                    CustomLogger.e("            deque ${index} : [${screenData.domainType}, ${screenData.screenType}]")
//                }
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//
//    }
//
//    init {
//        CustomLogger.i("$TAG Constructor Hash[${this.hashCode()}]")
//    }
//
//    /**
//     * 현재 보이는 윈도우의 사이즈를 변경한다
//     *
//     * @param mode : WindowMode - Small, Medium, Large
//     * @param noAnimation : 애니메이션 없이 즉시 화면 변경 여부
//     */
//    fun changeWindowMode(mode: WindowMode, noAnimation: Boolean = false) {
//        uiState.noAnimation = noAnimation
//        if (uiState.windowMode.value != mode) {
//            CustomLogger.e("changeWindowMode curr:${uiState.windowMode.value} to ${mode} noAnimation:${noAnimation} ")
//            //uiState.boxAnimationFinish.value = false
//            uiState.windowMode.value = mode
//        }
//    }
//
//    /**
//     * @brief 현재 윈도우를 숨겼다가 나타내기 위한 용도
//     * @param pendingEvent : 숨김 처리 이후 수행 할 이벤트
//     */
//    fun hideWindowImmediately(pendingEvent: Runnable? = null) {
//        CustomLogger.e("** hideWindowImmediately Called ** ")
//        uiState.cancelVisibleAni.value = ++uiState.cancelVisibleAni.value
//        hideWindow(pendingEvent = pendingEvent, immediate = true)
//    }
//
////    fun cancelCurrentScreen() {
////        addScreenJob?.cancel()
////        addScreenJob = null
////        JobManager.clearJob()
////        if (systemState.isVRReady()) {
////            serviceManager?.vrmwManager?.stop()
////        }
////
////        //clearScreen()
////    }
//
//    /**
//     * @brief 현재 윈도우를 내린다 (애니메이션 후 숨김)
//     *
//     * @param beep : 종료시 비프음 출력 여부
//     * @param clear : 숨길때 현재 화면 스택을 비울지 여부
//     * @param pendingEvent : 숨김 처리 이후 수행 할 이벤트
//     * @param immediate : 내려가는 애니메이션 없이 즉시 숨길지 여부
//     * @param naviHide : 내비 동작에서 맵 이동 시 즉시 숨김 처리 여부
//     *
//     */
//    fun hideWindow(
//        beep: Boolean = false,
//        clear: Boolean = true,
//        pendingEvent: Runnable? = null,
//        immediate: Boolean = false,
//        naviHide: Boolean = false
//    ) {
//        CustomLogger.i("hideWindow immediate:${immediate} clear:${clear} pendingEvent:${pendingEvent} tempHide:${uiState.tempHide.value} visible:${uiState.contentVisible.value}")
//        addJob(clear = true, keyString = "hideWindow") {
//
//            addScreenJob?.cancel()
//            addScreenJob = null
//
//            if (systemState.isVRReady()) {
//                serviceManager?.vrmwManager?.stop()
//            }
//
//            if (immediate) {
//                uiState.tempHide.value = true
//                uiState.pendingVisibleEvent = Runnable {
//                    CustomLogger.e("pendingVisibleEvent :")
//                    if (uiState.tempHide.value) {
//                        // 즉시없애고 다시 올라오는 애니메이션 처리
//                        uiState.tempHide.value = false
//                        uiState.contentVisible.value = true
//                    }
//                    pendingEvent?.run()
//                    uiState.pendingVisibleEvent = null
//                }
//                CustomLogger.e("        setAniState:${AnimationState.DISABLED} in hideWindow")
//                uiState.animationState.value = AnimationState.DISABLED
//            }
//
//            if (naviHide) {
//                uiState.animationState.value = AnimationState.DONE
//            }
//
//            if (uiState.contentVisible.value) {
//                uiState.contentVisible.value = false
//            } else {
//                if (!uiState.tempHide.value) {
//                    uiState.showWindow.value = false
//                }
//            }
//
//            uiState.showError.value = false
//            //hfpDevice.value = HfpDevice(hfpDevice.value.device, hfpDevice.value.connect, false)
//            bluetoothState.hfpDevice.value.apply {
//                this.recognizing = false
//            }
//            bluetoothState.prevHfpDevice.value = bluetoothState.hfpDevice.value.device
//
//            if (systemState.isVRReady()) {
//                if (beep) {
//                    serviceManager?.vrmwManager?.playBeep()
//                }
//            }
//            if (clear) {
//                clearScreen()
//            }
//            if (!immediate && pendingEvent != null) {
//                this@ServiceViewModel.pendingEvent = pendingEvent
//            }
//
//            if (!immediate) {
//                // ?? 왜 ptt눌렀을때 immediate true로 내려주는지 모르겠음. 그래서 다음 tc시작할때 여기가 add가 되는문제가있음.
//                val info = AdditionalInfo()
//                info.m_isHideWindow = true
//                info.m_playBeep = beep
//                addTracker(null, null, info)
//            }
//
//
//            changeWindowMode(WindowMode.SMALL)
//
//            // printScreenList("after hideWindow")
//        }
//
//    }
//
//    /**
//     * UI를 숨길때 (윈도우 숨김)
//     * PendingEvent Runnable 객체가 있을 경우
//     * 해당 객체를 수행하고 제거 함
//     */
//    fun runPendedEvent() {
//        CustomLogger.e("runPendedEvent()")
//        pendingEvent?.run()
//        pendingEvent = null
//    }
//
//    /**
//     * UI 성능이 느린 상황에서 List와 같이 하위 Draw 항목이 많은 객체를
//     * 애니메이션 시킬때 상당한 FrameDrop이 발생 하게 되므로
//     * 애니메이션 시작 전 Bitmap을 생성하여
//     * 애니메이션 수행 시 자식 까지 Draw 할 필요 없이 bitmap 만 Draw할 수 있도록 하기 위함
//     *
//     * 호출전 CompletableDeferred로 호출위치에서 이 메소드가 수행이 완료될때 까지 대기하도록 함
//     *
//     * @param screenData - 그리고자 하는 데이터가 명시된 ScreenData,
//     * @param content - 그리고자 하는 Compose
//     *
//     */
////    private fun makeBitmap(screenData: ScreenData, content: @Composable () -> Unit) {
////        bitmapReady.value = false
////        CustomLogger.i("makeBitmap ${offScreenView != null}")
////        val start_t = System.currentTimeMillis()
////        // 비트맵 생성용으로 만들어 둔 오프스크린 컴포즈뷰에서 컴포즈를 렌더링
////        offScreenView?.let {
////            CustomLogger.i("makeBitmap offScreenView apply viewSize:${offScreenView?.width} ${offScreenView?.height}")
////            it.apply {
////                CustomLogger.i("makeBitmap apply")
////                it.visibility = View.INVISIBLE
////                disposeComposition()
////                setContent {
////                    val bitmapReady = bitmapReady.collectAsState()
////                    // 비트맵이 없는 상태면 컴포즈 렌더링 시작
////                    if (!bitmapReady.value) {
////                        CerenceTheme {
////                            Box(
////                                modifier = Modifier
////                                    .width(dimensionResource(R.dimen.agent_width))
////                                    //.wrapContentHeight()
////                                    .height(dimensionResource(R.dimen.agent_max_height))
////                            ) {
////                                CustomLogger.i("makeBitmap recompose")
////                                content.invoke()
////                            }
////                        }
////
////                    }
////                }
////            }
////            it.invalidate()
////            it.post {
////                //it.invalidate()
////                //bitmap = DummyBitmap(it.drawToBitmap(), screenType)
////                val getBitmap_t = System.currentTimeMillis()
////                val bitmap = it.drawToBitmap()
////                CustomLogger.i("makeBitmap drawToBitmap bitmapSize: ${bitmap.width} ${bitmap.height} time:${System.currentTimeMillis() - getBitmap_t}")
////
////                screenData.bitmap = bitmap
////                it.visibility = View.GONE
////                bitmapReady.value = true
////                makeBitmapDeffered?.complete("")
////                CustomLogger.i("makeBitmap Done bitmapSize: ${screenData.bitmap?.width} ${screenData.bitmap?.height} time:${System.currentTimeMillis() - start_t}")
////            }
////        } ?: run {
////            CustomLogger.e("makeBitmap fail compose null")
////        }
////    }
//
//
//    /**
//     *  Error 애니메이션 테스트용
//     */
//    fun showErrorGlow(durationMs: Long = 1000) {
//        CoroutineScope(Dispatchers.Default).launch {
//            uiState.showError.value = true
//            delay(durationMs)
//            CoroutineScope(Dispatchers.Default).launch {
//                uiState.showError.value = false
//            }
//        }
//    }
//
////    fun getVRManager(): VrmwManager? {
////        return serviceManager?.vrmwManager
////    }
//
//    fun isAppInForeground(context: Context): Boolean {
//        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
//        if (activityManager != null) {
//            // 모든 설치된 앱의 정보를 가져옴
//            val packageManager = context.packageManager
//            val appInfos = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
////            CustomLogger.e("Intalled Package:")
////            for (appInfo in appInfos) {
////                // 앱 정보에서 패키지명을 가져와 입력받은 패키지명과 비교
////                CustomLogger.e("     ${appInfo.packageName}[${ appInfo.uid}] ")
////
//////                if (appInfo.packageName == packageName) {
//////                    val uid = appInfo.uid
//////                    // 현재 foreground에 있는 앱의 UID를 가져옴
//////                    val foregroundUid = activityManager.runningAppProcesses[0].uid
//////                    // 현재 foreground에 있는 앱의 UID와 입력받은 앱의 UID를 비교하여 디스플레이 여부를 판단
//////                    return uid == foregroundUid
//////                }
////            }
//            CustomLogger.e("runningAppProcesses :")
//            activityManager.runningAppProcesses.forEach {
//                CustomLogger.e("     ${it.processName}[${it.uid}][${it.pid}] ")
//            }
//        }
//        return false
//    }
//
//    fun stopTimer() {
//        CustomLogger.e("stopTimer")
//        timer?.stop()
//        timer = null
//    }
//
//    fun restartTimer() {
//        CustomLogger.e("restartTimer")
//        timer?.restart()
//    }
//
//    fun startTimer(action: () -> Unit) {
//        CustomLogger.e("startTimer")
//        timer?.stop()
//        timer = null
//        timer = CustomTimer(action = action)
//        timer?.start()
//    }
//
//    var listPCM = MutableStateFlow(listOf<File>())
//    var skipStore = false
////    fun getPcmList(): List<File> {
////        ///data/user/10/com.example.ndkconnectapp/files/VRMW/test/vrmanager/pcm
////        val context = BaseApplication.appContext
////
////        val builtinList = listFilesInDirectory("${context.filesDir}/VRMW/test/vrmanager/pcm")
////        val developFile = listFilesInDirectory("${context.filesDir}/developPcm")
////        val sumList = developFile.toMutableList()
////        sumList.addAll(builtinList)
////        listPCM.value = sumList.toList()
////        return listPCM.value
////    }
//
//    fun listFilesInDirectory(path: String): List<File> {
//        val directory = File(path)
//        val result = mutableListOf<File>()
//        if (directory.exists() && directory.isDirectory) {
//            val files = directory.listFiles()
//
//            files?.let {
//                for (file in it) {
//                    if (file.isDirectory) {
//                        println("Directory: ${file.name}")
//                        result.add(file)
//                    } else {
//                        println("File: ${file.name}")
//                        result.add(file)
//                    }
//                }
//            }
//        } else {
//            println("Specified path either doesn't exist or is not a directory.")
//        }
//        return result.sorted()
//    }
//
//    // SendMsg 시나리오 화면 체크 -> onChangeMessage에서 사용
//    // Call 시나리오 화면 체크
//    fun checkScreenQueue(screenType: ScreenType): Boolean {
//        CustomLogger.i("checkScreenQueue screenType : $screenType")
//        CustomLogger.i("screenDeuque size : ${screenDeuque.size}")
//
//        screenDeuque.forEach {
//            CustomLogger.i("screenDeuque check domainType :[${it.domainType}], screenType : [${it.screenType}]")
//        }
//
//        var checkScreen: ScreenData
//        // SendMsg 에서는 message 발화 화면(messageName)이 screenDeuque에 있는지 체크
//        // Call 에서는 YesNo화면이 screenDeuque에 있는지 체크
//        return when (screenDeuque.size) {
//            // SendMsg : PTT(0), List(1), List(2), List(3), messageName
//            // Call X
//            5 -> {
//                checkScreen = screenDeuque.elementAt(4)
//                CustomLogger.i("checkScreenQueue checkScreen screenType : ${checkScreen.screenType}")
//                checkScreen.screenType == screenType
//            }
//            // SendMsg : PTT(0), List(1), List(2), messageName
//            // Call : PTT(0), List(1), List(2), YesNo
//            4 -> {
//                checkScreen = screenDeuque.elementAt(3)
//                CustomLogger.i("checkScreenQueue checkScreen screenType : ${checkScreen.screenType}")
//                checkScreen.screenType == screenType
//            }
//            // SendMsg : PTT(0), List(1), messageName
//            // Call : PTT(0), List(1), YesNo
//            3 -> {
//                checkScreen = screenDeuque.elementAt(2)
//                CustomLogger.i("checkScreenQueue checkScreen screenType : ${checkScreen.screenType}")
//                checkScreen.screenType == screenType
//            }
//            // SendMsg : PTT(0), messageName
//            // Call : PTT(0), YesNo
//            2 -> {
//                checkScreen = screenDeuque.elementAt(1)
//                CustomLogger.i("checkScreenQueue screen_1 screenType : ${checkScreen.screenType}")
//                checkScreen.screenType == screenType
//            }
//
//            else -> {
//                return false
//            }
//        }
//    }
//
//    // Message value 초기화를 위해 사용 (시나리오가 시작되었는지 판단)
//    fun getScreenDequeSize(): Int {
//        return screenDeuque.size
//    }
//
//    fun doYesNoTrigger(isYes: Boolean) {
//        addJob("doYesNoTrigger", interruptable = true) {
//            // 선택 애니메이션 대기
//            delay(UxPreset.animationDuration.toLong())
//            // 선택 애니메이션 종료 후 클릭 이벤트 시작 여부
//            if (isYes) {
//                currScreen.value?.screenState?.triggerYes?.value = true
//            } else {
//                currScreen.value?.screenState?.triggerNo?.value = true
//            }
//        }
//    }
//
//    /**
//     * sttString에 RadioStationGarbage 일 때 <channel name>이 출력 되어 추가함
//     */
//    fun printSttString(str: String) {
//        CustomLogger.i("printSttString [$str]")
//        // RadioStationGarbage
//        if (!str.contains("<channel name>")) {
//            sttString.value = str
//        }
//    }
//
//    fun getSttString(): MutableStateFlow<String> = sttString
//
////    fun ttsToInput(ttsString: String) {
////        CustomLogger.e("recv ttsToInput :${ttsString}")
////        val ttsListener = object : TTSInterface {
////            override fun startPlay(sampleRate: Int, sourceType: Int): Boolean {
////                return true
////            }
////
////            override fun stopPlay(): Boolean {
////                serviceManager?.vrmwManager?.mediaOut?.setTTSInput(null)
////                serviceManager?.vrmwManager?.mediaIn?.clearTTSBuffer()
////                return true
////            }
////
////            override fun write(buffer: ByteArray, bufferSize: Int, sourceType: Int): Boolean {
////                serviceManager?.vrmwManager?.mediaIn?.addTTSBuffer(buffer)
////                return true
////            }
////
////        }
////        serviceManager?.vrmwManager?.mediaOut?.setTTSInput(ttsListener)
////        CoroutineScope(Dispatchers.Default).launch {
////            serviceManager?.vrmwManager?.startTTS(ttsString, "test[${System.currentTimeMillis()}]")
////        }
////    }
//}