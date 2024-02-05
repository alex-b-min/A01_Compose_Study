//package com.example.a01_compose_study.data.vr
//
//import android.content.Context
//import com.example.a01_compose_study.data.HVRError
//import com.example.a01_compose_study.domain.model.NoticeModel
//import com.example.a01_compose_study.domain.util.CustomLogger
//import com.example.a01_compose_study.domain.util.ParseBundle
//import com.example.a01_compose_study.presentation.ServiceViewModel
//import com.example.a01_compose_study.presentation.util.BaseManager
//import dagger.hilt.android.qualifiers.ApplicationContext
//import javax.inject.Inject
//import javax.inject.Singleton
//
//@Singleton
//class PttManager @Inject constructor(
//    @ApplicationContext context: Context,
//    viewModel: ServiceViewModel,
//    vrmwManager: VrmwManager,
//    val pttScreen: PttScreen,
//) : BaseManager(context, viewModel, vrmwManager) {
//
//    init {
//        CustomLogger.i("PttManager Constructor Hash[${this.hashCode()}] [${context.hashCode()}] [${viewModel.hashCode()}] [${vrmwManager.hashCode()}]")
//    }
//
//    override val TAG: String? = this.javaClass.simpleName
//
////    var defaultAnnounceString = getString(R.string.TID_CMN_COMM_01_02)
//
//    var onlineRandomCommands = mutableListOf("")
//    var offlineRandomCommands = mutableListOf("")
//
////    fun init() {
////        CustomLogger.i("init")
////
////        makeRandomCommands()
////    }
//
//
//    override fun onReceiveBundle(bundle: ParseBundle<out Any>) {
////        CustomLogger.i("onReceiveBundle ${bundle.phrase}")
////        super.onReceiveBundle(bundle)
////
////        if (bundle.isBundleConsumed) {
////            CustomLogger.i("bundleConsumed by BaseManager")
////            return
////        }
////
////        val notice = checkUnavailables(bundle)
////        if (notice != null) {
////            launchNotice(notice, true)
////            return
////        }
////
////        viewModel.addJob("bundleDelegate", interruptable = true) {
////            bundle.phrase.let {
////                if (it.isNotEmpty()) {
////                    viewModel.printSttString(bundle.phrase)
////                    delay(UxPreset.animationDuration.toLong())
////                }
////            }
////            if (this.isActive) {
////                viewModel.bundleDelegate.value = bundle
////            }
////        }
//    }
//
//    override fun onBundleParsingErr() {
//
//    }
//
//    override fun onCancel() {
//        TODO("Not yet implemented")
//    }
//
//    override fun onVRError(error: HVRError) {
//        TODO("Not yet implemented")
//    }
//
//    fun pttPrepare() {
////        CustomLogger.i("pttPrepare")
////        viewModel.announceString.postValue("")
////
////        makeRandomCommands()
////        if (getSettingState().isOfflineMode() ||
////            !getSystemState().serverResponse.value ||
////            !viewModel.vrConfig.value.isSupportServer
////        ) {
////            val randomIndex = Random().nextInt(offlineRandomCommands.size)
////            viewModel.guideString.postValue("${offlineRandomCommands[randomIndex]}")
////        } else {
////            val randomIndex = Random().nextInt(onlineRandomCommands.size)
////            viewModel.guideString.postValue("${onlineRandomCommands[randomIndex]}")
////        }
//    }
//
//    fun checkNaviComplete(): NoticeModel? {
////        if (!getSystemState().naviStatus.value) {
////            return NoticeModel().apply {
////
////                noticeString = getString(R.string.LID_SCR_0145)
////            }
////        }
//        return null
//    }
//
//    //지원 언어 체크만 하므로 실제 동작 여부는 아래에서 체크
//    fun checkSupportLanguage(): NoticeModel? {
////        if (!(viewModel.getVrConfig().isSupportASR || viewModel.getVrConfig().isSupportServer)) {
////            return NoticeModel().apply {
////                noticeString = getString(R.string.TID_CMN_COMM_07_01)
////            }
////        }
//        return null
//    }
//
//    fun checkOfflineMode(): NoticeModel? {
//
//        // 네트워크 상태 체크는 하지 않음
////
////        if (getSettingState().isOfflineMode()) { // 0: offline on, 1: offline off , OnlineVR off , 2: offline off, OnlineVR on
////            //오프라인모드인데 서버 지원
////            if (viewModel.getVrConfig().isSupportServer) {
////                // 메세지 변경
////                defaultAnnounceString = getString(R.string.TID_CCS_ERRO_02_09)
////
////            }
////
////            //오프라인 모드인데 임베디드 지원안함
////            if (!viewModel.getVrConfig().isSupportASR) {
////                return NoticeModel().apply {
////                    noticeString = getString(R.string.TID_CMN_COMM_07_01)
////                }
////            }
////
////        } else { // 오프라인 모드 아닐때,  1: offline off , OnlineVR off , 2: offline off, OnlineVR on
////
////            //서버 가능, 서버 사용  2: server on, use server
////            if (getSettingState().offlineMode.value == 2) {
////                //OnlineVR On 인데 온라인 언어 지원안함?
////                // 임베디드만 사용하겠지 뭐
////            } else {
////                // OnlineVR OFF 일때  1: offline off , OnlineVR off
////                // OnlineVR OFF 인데 임베디드도 안되
////                if (!viewModel.getVrConfig().isSupportASR) {
////                    return NoticeModel().apply {
////                        noticePromptId = "PID_CCS_ERRO_02_07"
////                        noticeString = getString(R.string.TID_CCS_ERRO_02_07)
////                    }
////                } else {
////
////                    //OnlineVR OFF 인데 임베디드는 되
////                    // embedded 지원 o, 서버지원 o // 서버 지원 되니까 더많은 기능 안내
////                    if (viewModel.getVrConfig().isSupportServer) {
////                        defaultAnnounceString = getString(R.string.TID_CCS_ERRO_02_08)
////                    }
////                    // embedded 지원 o, 서버지원 x
////                    // 아무것도 안함
////                }
////            }
////        }
//        return null
//    }
//
//    fun checkStarting(): NoticeModel? {
////        defaultAnnounceString = getString(R.string.TID_CMN_COMM_01_02)
////
////        checkSupportLanguage()?.let {
////            return it
////        }
////        checkBootComplete()?.let {
////            return it
////        }
////        checkNaviComplete()?.let {
////            return it
////        }
////        checkOfflineMode()?.let {
////            return it
////        }
////
////        CustomLogger.i("checkStarting defaultAnnounceString : $defaultAnnounceString")
////
////
////        if (!defaultAnnounceString.equals(getString(R.string.TID_CMN_COMM_01_02))) {
////            viewModel.guideString.postValue("")
////        }
////
//        return null
//    }
//
//
//    fun pttEvent() {
////        CustomLogger.i("pttEvent")
////
////        val notice = checkStarting()
////        viewModel.announceString.postValue(defaultAnnounceString)
////
////        if (notice != null) {
////            launchNotice(notice, true)
////            return
////        }
////
////        if (getBluetoothState().hfpDevice.value.device.isNotEmpty() && !getBluetoothState().hfpDevice.value.recognizing) {
////            vrmwManager.g2pController.updateCacheFiles(getBluetoothState().hfpDevice.value.device)
////        }
////
////        pttScreen.showPtt()
////        val mwContext = MWContext(DialogueMode.MAINMENU, this)
////        val screenData = ScreenData(DomainType.MainMenu, ScreenType.Ptt).apply {
////            this.mwContext = mwContext
////            onStart = Runnable {
////                vrmwManager.startVR(mwContext)
////            }
////            onResume = Runnable {
////                //viewModel.changeWindowMode(WindowMode.SMALL)
////                vrmwManager.startVR(mwContext)
////            }
////            windowMode = WindowMode.SMALL
////        }
////        viewModel.addJob("pttEvent new", true) {
////            viewModel.addScreen(screenData, true)
////        }
//
//    }
//
//
//    fun makeRandomCommands() {
//
//        onlineRandomCommands = mutableListOf("")
//        offlineRandomCommands = mutableListOf("")
//
//        onlineRandomCommands.clear()
////        onlineRandomCommands.add(getString(R.string.TID_CMN_GUID_13_02))
////        onlineRandomCommands.add(getString(R.string.TID_CMN_GUID_13_02_4))
////        onlineRandomCommands.add(getString(R.string.TID_CMN_GUID_13_05_1))
////        onlineRandomCommands.add(getString(R.string.TID_CMN_GUID_13_05_2))
////        onlineRandomCommands.add(getString(R.string.TID_CMN_GUID_13_12))
////        onlineRandomCommands.add(getString(R.string.TID_CMN_GUID_13_12_1))
////        onlineRandomCommands.add(getString(R.string.TID_CMN_GUID_13_13_1))
////        if (getSystemState().isSupportDAB.value) {
////            onlineRandomCommands.add(getString(R.string.LID_SCR_0133))
////        } else {
////            onlineRandomCommands.add(getString(R.string.LID_SCR_0132_1))
////        }
////
////        onlineRandomCommands.add(getString(R.string.LID_SCR_0132))
////        onlineRandomCommands.add(getString(R.string.LID_SCR_0134))
////        onlineRandomCommands.add(getString(R.string.TID_CMN_GUID_43_03))
////        onlineRandomCommands.add(getString(R.string.TID_CMN_GUID_43_06))
////        onlineRandomCommands.add(getString(R.string.TID_CMN_GUID_54_04))
////        onlineRandomCommands.add(getString(R.string.TID_CMN_GUID_54_01))
////
////        offlineRandomCommands.clear()
////        offlineRandomCommands.add(getString(R.string.TID_CMN_GUID_13_05_1))
////        offlineRandomCommands.add(getString(R.string.TID_CMN_GUID_13_05_2))
////        offlineRandomCommands.add(getString(R.string.TID_CMN_GUID_13_12))
////        offlineRandomCommands.add(getString(R.string.TID_CMN_GUID_13_12_1))
////        if (getSystemState().isSupportDAB.value) {
////            offlineRandomCommands.add(getString(R.string.LID_SCR_0133))
////        } else {
////            offlineRandomCommands.add(getString(R.string.LID_SCR_0132_1))
////        }
////        offlineRandomCommands.add(getString(R.string.LID_SCR_0132))
////        offlineRandomCommands.add(getString(R.string.LID_SCR_0134))
////        offlineRandomCommands.add(getString(R.string.TID_CMN_GUID_43_03))
////        offlineRandomCommands.add(getString(R.string.TID_CMN_GUID_43_06))
//
//    }
//}