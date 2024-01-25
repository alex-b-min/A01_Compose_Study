//package com.example.a01_compose_study.presentation
//
//import android.app.Service
//import android.content.Context
//import android.graphics.PixelFormat
//import android.media.AudioManager
//import android.os.Build
//import android.os.LocaleList
//import android.util.DisplayMetrics
//import android.view.Choreographer
//import android.view.Gravity
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.view.ViewTreeObserver
//import android.view.WindowManager
//import androidx.compose.runtime.Recomposer
//import androidx.compose.ui.focus.FocusState
//import androidx.compose.ui.platform.AndroidUiDispatcher
//import androidx.compose.ui.platform.ComposeView
//import androidx.compose.ui.platform.ViewCompositionStrategy
//import androidx.compose.ui.platform.compositionContext
//import androidx.lifecycle.Lifecycle
//import androidx.lifecycle.LifecycleOwner
//import androidx.lifecycle.setViewTreeLifecycleOwner
//import androidx.lifecycle.setViewTreeViewModelStoreOwner
//import androidx.savedstate.setViewTreeSavedStateRegistryOwner
//import com.example.a01_compose_study.BuildConfig
//import com.example.a01_compose_study.data.DialogueMode
//import com.example.a01_compose_study.data.HTextToSpeechEvent
//import com.example.a01_compose_study.data.HVRConfigEvent
//import com.example.a01_compose_study.data.ModuleStatus
//import com.example.a01_compose_study.data.ScreenData
//import com.example.a01_compose_study.data.WindowMode
//import com.example.a01_compose_study.databinding.LayoutToDrawBinding
//import com.example.a01_compose_study.di.ApplicationScope
//import com.example.a01_compose_study.domain.state.AnimationState
//import com.example.a01_compose_study.domain.MyLifecycleOwner
//import com.example.a01_compose_study.domain.model.BaseManager
//import com.example.a01_compose_study.domain.model.ServiceViewModel
//import com.example.a01_compose_study.domain.model.VrmwManager
//import com.example.a01_compose_study.domain.util.CustomLogger
//import com.example.a01_compose_study.ui.help.HelpViewModel
//import com.google.gson.Gson
//import dagger.hilt.android.qualifiers.ApplicationContext
//import kotlinx.coroutines.CancellationException
//import kotlinx.coroutines.CompletableDeferred
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.Job
//import kotlinx.coroutines.isActive
//import kotlinx.coroutines.launch
//import java.io.BufferedReader
//import java.io.InputStreamReader
//import java.util.Locale
//import javax.inject.Inject
//import javax.inject.Singleton
//
//@Singleton
//class ServiceManager @Inject constructor(
//    @ApplicationContext context: Context,
//    viewModel: ServiceViewModel,
//    vrmwManager: VrmwManager,
//    val helpManager:HelpViewModel,
//    @ApplicationScope private val coroutineScope: CoroutineScope
//) : BaseManager(context, viewModel, vrmwManager) {
//
//    init {
//        CustomLogger.i("ServiceManager created Hash[${this.hashCode()}] [${context.hashCode()}] [${viewModel.hashCode()}] [${vrmwManager.hashCode()}]")
//    }
//
//    override val TAG = this.javaClass.simpleName
//    override fun init() {
//    }
//
//    override fun onBundleParsingErr() {
//
//    }
//
//    lateinit var lifecycleOwner: LifecycleOwner
//    private var mWindowManager: WindowManager? = null
//    var windowParams: WindowManager.LayoutParams? = null
//    var isInit = false
//    var beforeScreen = DialogueMode.NONE
//    private val composeLifecycleOwner = MyLifecycleOwner()
//    lateinit var composeView: ComposeView
//    lateinit var offScreenView: ComposeView
//    lateinit var binding: LayoutToDrawBinding
//    var lastPttEvent = System.currentTimeMillis()
//
//
//    //@SuppressLint("ClickableViewAccessibility")
//    fun viewSystemInit(service: AwindowService, deferred: CompletableDeferred<Any>) {
//        CustomLogger.i("ServiceManager Init Start")
//
//
//        this.lifecycleOwner = service
//
//        viewModel.isDevelopMode.value = BuildConfig.isDevelopMode
//
//        val inflator = context.getSystemService(Service.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//        binding = LayoutToDrawBinding.inflate(inflator)
//
//        if (!BuildConfig.ACTIVITY_MODE) {
//
//            CoroutineScope(Dispatchers.Main).launch {
//                val startTime = System.currentTimeMillis()
//                mWindowManager = context.getSystemService(Service.WINDOW_SERVICE) as WindowManager
//                windowParams = WindowManager.LayoutParams(
//                    ViewGroup.LayoutParams.MATCH_PARENT,
//                    ViewGroup.LayoutParams.MATCH_PARENT,
//
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY else WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,  // Android O 이상인 경우 TYPE_APPLICATION_OVERLAY 로 설정
//                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
//                            or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,  //                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
//                    //                        |WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL|WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
//                    PixelFormat.TRANSLUCENT
//                )
//                windowParams?.gravity = Gravity.LEFT or Gravity.CENTER_VERTICAL
//
//
//                val metrics = DisplayMetrics()
//                mWindowManager!!.defaultDisplay.getMetrics(metrics)
//                windowParams?.x = 10
//                windowParams?.y = metrics.heightPixels - windowParams!!.height - 10
//
//                composeView = ComposeView(service)
//                offScreenView = ComposeView(service)
//                composeLifecycleOwner.performRestore(null)
//                composeLifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
//
//
//                composeView.let { view ->
//                    view.setViewTreeLifecycleOwner(lifecycleOwner)
//                    view.setViewTreeSavedStateRegistryOwner(composeLifecycleOwner)
//                    view.setViewTreeViewModelStoreOwner((service.application as BaseApplication).viewModelStoreOwner)
//                    view.setViewTreeSavedStateRegistryOwner(composeLifecycleOwner)
//                    val coroutineContext = AndroidUiDispatcher.CurrentThread
//                    val runRecomposeScope = CoroutineScope(coroutineContext)
//                    val reComposer = Recomposer(coroutineContext)
//                    view.compositionContext = reComposer
//                    runRecomposeScope.launch {
//                        reComposer.runRecomposeAndApplyChanges()
//                    }
//                    view.setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
//                    view.setContent {
//                        CerenceTheme {
//                            OverlayScreen(viewModel)
//                        }
//                    }
//                }
//
//                offScreenView.let { view ->
//                    view.setViewTreeLifecycleOwner(lifecycleOwner)
//                    view.setViewTreeSavedStateRegistryOwner(composeLifecycleOwner)
//                    view.setViewTreeViewModelStoreOwner((service.application as BaseApplication).viewModelStoreOwner)
//                    view.setViewTreeSavedStateRegistryOwner(composeLifecycleOwner)
//                    val coroutineContext = AndroidUiDispatcher.CurrentThread
//                    val runRecomposeScope = CoroutineScope(coroutineContext)
//                    val reComposer = Recomposer(coroutineContext)
//                    view.compositionContext = reComposer
//                    runRecomposeScope.launch {
//                        reComposer.runRecomposeAndApplyChanges()
//                    }
//                    view.setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
//                    view.visibility = View.GONE
//                    view.layoutParams = ViewGroup.LayoutParams(
//                        ViewGroup.LayoutParams.WRAP_CONTENT,
//                        ViewGroup.LayoutParams.WRAP_CONTENT
//                    )
//                    view.requestLayout()
//                    viewModel.offScreenView = view
//                }
//
//                CustomLogger.i("viewToDraw addview ")
//
//                binding.viewToDraw.addView(offScreenView)
//                binding.viewToDraw.addView(composeView)
//
//                CustomLogger.i("mWindowManager addview ")
//                try {
//                    mWindowManager!!.addView(binding.root, windowParams)
//                } catch (e: Exception) {
//                    CustomLogger.i("mWindowManager addview e:${e.message}")
//                    e.printStackTrace()
//                }
//
//                setObserve()
//
//                viewModel.getPcmList()
//
//                composeView.post {
//                    val initTime = System.currentTimeMillis() - startTime
//                    CustomLogger.i("View initTime:${initTime}")
//                    getSystemState().viewSystemReady.value = true
//                    deferred.complete("")
//                }
//            }
//
//        } else {
//            setObserve()
//        }
//
//
//    }
//
//    fun subManagerInit() {
//        val startTime = System.currentTimeMillis()
//        isInit = true
//        helpManager.init()
//        viewModel.serviceManager = this@ServiceManager
//        getSystemState().serviceInit.value = true
//        //context.sendBroadcast(Intent(ReceiveAction.SELVAS_ACTION_VR_READY.action),"com.selvas.permission.VR_SERVICE")
//        val initTime = System.currentTimeMillis() - startTime
//        CustomLogger.i("Manager initTime:${initTime}")
//    }
//
//    fun mwManagerInit() {
//        vrmwManager.init()
//    }
//
//    fun postInit() {
//        // fetchContacts 를 할 때 isSupportASR 판단이 필요
//        // initSystem -> setLocale 후 init 하도록 분리
//        contactsManager.init()
//    }
//
//    fun startPost(runnable: Runnable) {
//        composeView.post {
//            runnable.run()
//        }
//    }
//
//    var beforeTime = 0L
//
//    inner class MyFrameCallback : Choreographer.FrameCallback {
//        override fun doFrame(frameTimeNanos: Long) {
//            // 프레임 갱신 이벤트를 처리하는 코드를 작성합니다.
//
//            val time = (frameTimeNanos - beforeTime) / 1000000
////            if (time > 16) {
////                Log.e(TAG, "MyFrameCallback frameTimeNanos:${time}")
////            }
//            CustomLogger.i("MyFrameCallback frameTimeNanos:${time}")
//            // 작업을 수행한 후 다음 프레임 갱신을 예약합니다.
//            beforeTime = frameTimeNanos
//            Choreographer.getInstance().postFrameCallback(this)
//        }
//    }
//
//    fun delegateBundle(bundle: ParseBundle<out Any>) {
//        CustomLogger.i("delegateBundle type: ${bundle.type}")
//        when (bundle.type) {
////            ParseDomainType.CALL -> {
////                //viewModel.screenMode.value = DialogueMode.CALL
////                beforeScreen = DialogueMode.CALL
////                callManager.onReceiveBundle(bundle)
////            }
////
////            ParseDomainType.NAVI -> {
////                //viewModel.screenMode.value = DialogueMode.NAVIGATION
////                beforeScreen = DialogueMode.NAVIGATION
////                navigationManager.onReceiveBundle(bundle)
////            }
////
////            ParseDomainType.RADIO -> {
////                //viewModel.screenMode.value = DialogueMode.RADIO
////                beforeScreen = DialogueMode.RADIO
////                radioManager.onReceiveBundle(bundle)
////            }
////
////            ParseDomainType.WEATHER -> {
////                //viewModel.screenMode.value = DialogueMode.WEATHER
////                beforeScreen = DialogueMode.WEATHER
////                weatherManager.onReceiveBundle(bundle)
////            }
////
////            ParseDomainType.SEND_MESSAGE -> {
////                //viewModel.screenMode.value = DialogueMode.SEND_MESSAGE
////                beforeScreen = DialogueMode.SEND_MESSAGE
////                sendMsgManager.onReceiveBundle(bundle)
////            }
//
//            ParseDomainType.HELP -> {
//                beforeScreen = DialogueMode.HELP
//                helpManager.onReceiveBundle(bundle)
//            }
//
//            else -> {
//                beforeScreen = DialogueMode.NONE
//                //onReceiveBundle(bundle)
//            }
//        }
//    }
//
//
//    private fun setObserve() {
//        CustomLogger.i("setObserve")
//
//        coroutineScope.launch {
//            getUiState().isInTouching.collect {
//                CustomLogger.e("isInTouching:${it}")
//                if (it) {
//                    //vrmwManager.stop()
//                    viewModel.restartTimer()
////                    getUiState().isInTouching.value = false
//                }
//            }
//        }
//
//        coroutineScope.launch {
//            viewModel.requestPTT.collect {
//                if (it) {
//                    showView(true)
//                    viewModel.requestPTT.value = false
//                }
//            }
//        }
//
//
//
//        coroutineScope.launch {
//            viewModel.isNotUseCCU.collect {
//
//                if (it) {
//                    viewModel.networkState.host = "192.168.23.68"
//                } else {
//                    viewModel.networkState.host = "stg.eu-voice.hyundai.com"
//                }
//                //networkManager.testConnect()
//            }
//        }
//
//
//        coroutineScope.launch {
//            viewModel.bundleDelegate.collect {
//                CustomLogger.i("bundleDelegate from : ${it?.dialogueMode}")
//                it?.let {
//                    delegateBundle(it)
//                    viewModel.bundleDelegate.value = null
//                }
//            }
//        }
//
//
//        coroutineScope.launch {
//            getUiState().showWindow.collect {
//                CoroutineScope(Dispatchers.Main).launch {
//                    CustomLogger.i("show window :${it}")
//                    if (it) {
//                        binding.root.visibility = View.VISIBLE
//                        //audioManager.setParameters("mic_mode=vr")
//                        focusState.setStart()
//                    } else {
//                        binding.root.visibility = View.GONE
//                        focusState.setStop()
//                        viewModel.runPendedEvent()
//                    }
//                }
//            }
//        }
//    }
//
//    var pttJob: Job? = null
//
//    fun showView(
//        isKeyCode: Boolean = false,
//        force: Boolean = false,
//        screenData: ScreenData? = null
//    ) {
//
//        //viewModel.printJobMap("start showView")
//        if (!force && (System.currentTimeMillis() - lastPttEvent < 500)) {
//            CustomLogger.e("Already Start PTT Running curr:${System.currentTimeMillis()} last:${lastPttEvent} = ${System.currentTimeMillis() - lastPttEvent}")
//            return
//        }
//
//        pttJob?.cancel()
//        lastPttEvent = System.currentTimeMillis()
//        //viewModel.addJob(clear = true, keyString = "showView") {
//        pttJob = CoroutineScope(Dispatchers.Main).launch {
//
//            CustomLogger.e("Start PTT")
//            if (!isActive) {
//                CustomLogger.e("Cancel PTT")
//                return@launch
//            }
//
//            if (viewModel.currScreen.value == null && !viewModel.uiState.isAnimating()) {
//                CustomLogger.i("CurrScreen Null")
//                if (getUiState().windowMode.value != WindowMode.SMALL) {
//                    viewModel.changeWindowMode(WindowMode.SMALL, true)
//                    viewModel.hideWindowImmediately(pendingEvent = {
//                        launchPtt(screenData)
//                    })
//                } else {
//
//                    //binding.root.visibility = View.VISIBLE
//                    CustomLogger.e("curr root Visible:${binding.root.visibility == View.VISIBLE}")
//                    if (binding.root.visibility == View.VISIBLE) {
//                        getUiState().animationState.value = AnimationState.ENABLED
//                        getUiState().contentVisible.value = true
//                        launchPtt(screenData)
//
//                    } else {
//                        binding.root.viewTreeObserver.addOnPreDrawListener(object :
//                            ViewTreeObserver.OnPreDrawListener {
//                            override fun onPreDraw(): Boolean {
//                                CustomLogger.e("observe root Visible:${binding.root.visibility == View.VISIBLE}")
//
//                                if (binding.root.visibility == View.VISIBLE) {
//                                    // View가 Visible 상태입니다.
//                                    // 필요한 작업을 수행한 후 리스너를 제거합니다.
//                                    var reqPost = System.currentTimeMillis()
//                                    binding.root.post {
//                                        CustomLogger.e("root post :${System.currentTimeMillis() - reqPost}")
//                                        reqPost = System.currentTimeMillis()
//                                        composeView.post {
//                                            CustomLogger.e("composeView post :${System.currentTimeMillis() - reqPost}")
//                                            getUiState().animationState.value =
//                                                AnimationState.ENABLED
//                                            getUiState().contentVisible.value = true
//                                            launchPtt(screenData)
//                                        }
//
//                                    }
//                                    binding.root.viewTreeObserver.removeOnPreDrawListener(this)
//                                    return false
//                                }
//                                // View가 아직 Visible 상태가 아니므로 뷰의 그리기를 계속 진행합니다.
//                                return false
//                            }
//
//                        })
//                        getUiState().showWindow.value = true
//                    }
//                }
//
//
//            } else {
//                CustomLogger.i("CurrScreen Not Null")
//                if (!restartVR()) {
//                    CustomLogger.i("RestartVR : false")
//                    viewModel.hideWindowImmediately(pendingEvent = {
//                        launchPtt(screenData)
//                    })
//                }
//            }
//        }
//        pttJob?.invokeOnCompletion { throwable ->
//            when (throwable) {
//                is CancellationException -> CustomLogger.d("PttJob is Cancelled")
//                else -> {
//                    CustomLogger.d("PttJob is Completed : ${throwable}")
//                    pttJob = null
//                }
//            }
//        }
//    }
//
//    fun launchPtt(screenData: ScreenData?) {
//        screenData?.let {
//            viewModel.addScreen(it)
//        } ?: kotlin.run {
//            pttEvent()
//            getBluetoothState().hfpDevice.value.apply {
//                this.recognizing = true
//            }
//        }
//    }
//
//    fun pttEvent() {
//        CustomLogger.i("ptt EVENT")
//        pttManager.pttPrepare()
//        pttManager.pttEvent()
//    }
//
//    fun destroy() {
//        if (mWindowManager != null) {
//            composeView.let {
//                binding.root.removeView(it)
//            }
//            offScreenView.let {
//                binding.root.removeView(it)
//            }
//
//            mWindowManager!!.removeView(binding.root)
//            mWindowManager = null
//        }
//    }
//
//    fun loadVrConfig(locale: Locale): VrConfig? {
//        try {
//            val inputStream = context.resources.openRawResource(R.raw.vr_config)
//            val json = BufferedReader(InputStreamReader(inputStream)).use { it.readText() }
//            return Gson().fromJson(json, VrConfig::class.java)
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//        return null
//    }
//
//    suspend fun setLocale() {
//
//        val localeList: LocaleList = context.resources.configuration.locales
//        val primaryLocale: Locale? = localeList.get(0)
//        CustomLogger.i("ACTION_LOCALE_CHANGED $primaryLocale")
//        primaryLocale?.let { locale ->
//            val vrConfig = loadVrConfig(locale)
//            vrConfig?.let {
//                val languageType = Util.parseLanguageType(locale.toString())
//                CustomLogger.i("languageType: $languageType")
//                languageType?.let {
//                    if (it == viewModel.languageType.value) {
//                        CustomLogger.i("viewModel.languageType.value same languageType: $languageType")
//                        return
//                    } else {
//                        viewModel.mwState.vrConfigEvent.value = HVRConfigEvent.NONE
//                        viewModel.mwState.ttsConfigEvent.value = HTextToSpeechEvent.NONE
//                        viewModel.mwState.isVRManagerInitializeDone = false
//                        viewModel.mwState.isTTSManagerInitializeDone = false
//                        viewModel.systemState.moduleStatus.value = ModuleStatus.UNLOAD
//                    }
//                    viewModel.languageType.value = it
//                    val ttsSupport = Gson().fromJson(
//                        vrmwManager.getTTSSupportedLanguage().toString(),
//                        SupportLanguage::class.java
//                    )
//                    val vrSupport = Gson().fromJson(
//                        vrmwManager.getVRSupportedLanguage().toString(),
//                        SupportLanguage::class.java
//                    )
//                    CustomLogger.e("ttsSupport:$ttsSupport")
//                    CustomLogger.e("vrSupport:$vrSupport")
//                    vrConfig.isSupportTTS =
//                        vrmwManager.isTTSLanguageSupported(languageType.id) == 1
//                    CustomLogger.i("isSupportTTS: ${vrConfig.isSupportTTS}")
//                    val supportVr = vrmwManager.isVRLanguageSupported(languageType.id)
//                    vrConfig.isSupportASR = supportVr[0] == 1
//                    vrConfig.isSupportServer = supportVr[1] == 1
//                    CustomLogger.i("isSupportASR: ${vrConfig.isSupportASR}")
//
//                    viewModel.vrConfig.value = vrConfig
//                    if (languageType != null) {
//                        vrmwManager.setLanguage(languageType)
//                    }
//                }
//            }
//        }
//    }
//
//}
//// OverlayService 내에서 Composable 함수 정의