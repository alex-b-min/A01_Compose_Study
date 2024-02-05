//package com.example.a01_compose_study.presentation.util
//
//import android.content.Context
//import com.example.a01_compose_study.data.vr.VrmwManager
//import com.example.a01_compose_study.domain.util.CustomLogger
//import com.example.a01_compose_study.domain.util.VRResultListener
//import com.example.a01_compose_study.presentation.ServiceViewModel
//
///**
// * 각 매니저에서 공통적으로 사용하거나 거쳐야 할 로직을 구현
// */
//abstract class BaseManager constructor(
//    val context: Context,
//    val viewModel: ServiceViewModel,
//    val vrmwManager: VrmwManager,
//) : VRResultListener {
//
//    init {
//        CustomLogger.i("BaseManager Constructor Hash[${this.hashCode()}]")
//    }
//
//    open val TAG: String? = this.javaClass.simpleName
//
//
////    open fun sendBroadCast(intent: Intent) {
////        context.sendBroadcast(intent)
////    }
////
////    /**
////     * 입력 mwcontext 에 대한 TimeOut 이벤트 처리
////     * 누적 타임아웃 횟수 마다 표시 문구, 행동이 달라짐
////     * 1 회 - 재요청
////     * 2 회 이상 - 종료
////     *
////     */
////    fun procTimeOut(context: MWContext) {
////        CustomLogger.e("procTimeOut: ${context.dialogueMode}")
////        context.timeOutCnt++
////        when (context.timeOutCnt) {
////            1 -> {
////
////                viewModel.addJob(keyString = "procTimeOut", interruptable = true) {
////                    vrmwManager.checkAndWaitIdle()
////                    val promptId = getTimeoutPrompt(context.dialogueMode)
////                    CustomLogger.e("procTimeOut  promptId: ${promptId != null}")
////
////                    promptId?.let {
////                        // 타임아웃에 대한 PromptID 가 존재 할 경우 VR 재시작 하면서 해당 문구 안내
////                        if (it.isNotEmpty()) {
////                            CustomLogger.d("TimeOut prompt : ${context.dialogueMode}, ${it.size}")
////                            vrmwManager.requestVR(context.dialogueMode, it)
////                        } else {
////                            CustomLogger.d("TimeOut prompt empty : ${context.dialogueMode} isConfirm:${context.dialogueMode.isConfirmDialog()}")
////                            // context 타입이 확인 타입이면
////                            // 게이지가 차는 경우가 있지만 게이지가 없는 확인 타입에 대한 처리
////                            if (context.dialogueMode.isConfirmDialog()) {
////                                vrmwManager.requestVR(
////                                    context.dialogueMode,
////                                    promptId = context.promptId,
////                                    promptArgs = context.promptArgs
////                                )
////                            } else {
////                                // 그 외 상황일때 VR만 재시작
////                                vrmwManager.resumeVR()
////                            }
////
////                        }
////                    }
////                }
////            }
////
////            2 -> {
////                context.timeOutCnt = 0
////                viewModel.addJob(keyString = "procTimeOut", interruptable = true) {
////                    vrmwManager.stop()
////                    viewModel.hideWindow(true)
////                }
////            }
////
////            else -> {
////
////                context.timeOutCnt = 0
////                viewModel.addJob(keyString = "procTimeOut", interruptable = true) {
////                    vrmwManager.stop()
////                    viewModel.hideWindow(true)
////                }
////            }
////        }
////    }
////
////    /**
////     * 서버오류인 경우
////     * 안내문구 출력 후 종료
////     */
////    fun procServerErr(error: HVRError, context: MWContext) {
////        CustomLogger.e("Server ERR:${error.name} ${context.dialogueMode}")
////        val noticeModel = getServerErrPrompt(error)
////        launchNotice(noticeModel, true)
////    }
////
////    /***
////     * 개통여부 체크
////     * 231031 : Raul 말씀으로는 개통이 된 상태여야만 시스템에서 VR에 이벤트를 전달 해 줄 것이기 때문에
////     * 개통여부 체크와 VR 동의 체크는 하지 않아도 된다고 함
////     */
////    fun ccsSubscribedCheck(): NoticeModel? {
////        CustomLogger.i("ccsSubscribedCheck")
////        if (!getSettingState().isCCUSubscribed()) {
////            // ccs check (kia)
////            return NoticeModel().apply {
////                noticeString = context.getString(R.string.TID_CCS_ERRO_01_32)
////                noticePromptId = "PID_CCS_ERRO_01_32"
////            }
////        }
////        return null
////    }
////
////    /**
////     * 서버체크전에 서버지원 언어인지 먼저 체크 하고,
////     * 서버 지원 인 경우 온라인 여부를 체크,
////     * 온라인이 아닐때, 오프라인 이면 오프라인 해제 요청
////     * 오프라인이 아니면 온라인 켜라고 함
////     */
////    fun onlineVRCheck(): NoticeModel? {
////        CustomLogger.i("onlineVRCheck")
////        if (!viewModel.getVrConfig().isSupportServer) {
////            return NoticeModel().apply {
////                noticeString = context.getString(R.string.TID_CMN_COMM_07_01)
////                noticePromptId = "PID_CMN_COMM_07_01"
////            }
////        } else {
////            if (!getSettingState().isOnlineVR()) {
////                return if (getSettingState().isOfflineMode()) {
////                    NoticeModel().apply {
////                        noticeString = context.getString(R.string.TID_CCS_ERRO_02_03)
////                        noticePromptId = "PID_CCS_ERRO_02_03"
////                    }
////                } else {
////                    NoticeModel().apply {
////                        noticeString = context.getString(R.string.TID_CCS_ERRO_02_07)
////                        noticePromptId = "PID_CCS_ERRO_02_07"
////                    }
////                }
////
////            }
////        }
////        return null
////    }
////
////
////    fun getServerErrPrompt(error: HVRError): NoticeModel {
////
////        when (error) {
////            HVRError.ERROR_DIALOGUE_ASR_SERVER_NO_RESPONSE,
////            HVRError.ERROR_DIALOGUE_ASR_SERVER_CONNECTION,
////            HVRError.ERROR_DIALOGUE_ASR_SERVER_UNAVAILABLE,
////            HVRError.ERROR_DIALOGUE_ASR_SERVER_RESPONSE -> {
////                return NoticeModel().apply {
////                    noticePromptId = "PID_CMN_COMM_02_17"
////                    noticeString = getString(R.string.TID_CMN_COMM_02_17)
////                }
////            }
////
////            HVRError.ERROR_DIALOGUE_ASR_NETWORK_NO_SIGNAL -> {
////                return NoticeModel().apply {
////                    noticePromptId = "PID_CMN_COMM_02_18"
////                    noticeString = getString(R.string.TID_CMN_COMM_02_18)
////                }
////            }
////
////            else -> {
////                return NoticeModel().apply {
////                    noticePromptId = "PID_CMN_COMM_02_17"
////                    noticeString = getString(R.string.TID_CMN_COMM_02_17)
////                }
////            }
////        }
////
////    }
////
////
////    /**
////     * 지원하지 않는 도메인 타입 안내
////     */
////    fun procUnknownDomain() {
////        CustomLogger.i("")
////        val noti = NoticeModel().apply {
////            noticePromptId = "PID_CMN_COMM_01_22"
////            noticeString = getString(R.string.TID_CMN_COMM_01_22)
////        }
////        launchNotice(noti, true)
////    }
////
////
////    /**
////     * VR 오류 이벤트 처리
////     * 현재 에러 타입, Dialog 타입에 따라 상황별 안내메세지 출력
////     */
////    override fun onVRError(error: HVRError) {
////        CustomLogger.e(
////            TAG,
////            "setVRError ${error.name}, context:${vrmwManager.currContext?.dialogueMode} rejectCnt:${vrmwManager.currContext?.rejectCnt} timeOutCnt:${vrmwManager.currContext?.timeOutCnt}"
////        )
////
////        vrmwManager.currContext?.let {
////            when (error) {
////                HVRError.ERROR_DIALOGUE_ASR_RECOGNITION_TIMEOUT -> {
////                    procTimeOut(it)
////                }
////
////                HVRError.ERROR_DIALOGUE_ASR_SERVER_RESPONSE,
////                HVRError.ERROR_DIALOGUE_ASR_SERVER_UNAVAILABLE,
////                HVRError.ERROR_DIALOGUE_ASR_SERVER_CONNECTION,
////                HVRError.ERROR_DIALOGUE_ASR_NETWORK_NO_SIGNAL,
////                HVRError.ERROR_DIALOGUE_ASR_SERVER_NO_RESPONSE -> {
////
////                    procServerErr(error, it)
////                }
////
////                HVRError.ERROR_DIALOGUE_ARBITRATOR_REJECTION -> {
////                    reject()
////                }
////
////                HVRError.ERROR_HMI -> {
////                    procUnknownDomain()
////                }
////
////                else -> {
////
////                }
////            }
////        }
////
////    }
////
////
////    fun getRandomList(list: List<String>): List<String> {
////        val randomIndex = Random().nextInt(list.size)
////        return listOf(list[randomIndex])
////    }
////
////    /**
////     * List타입 다이얼로그에서 의 리젝션 처리
////     */
////    open fun procListReject(context: MWContext) {
////        CustomLogger.i("")
////        context.rejectCnt++
////        when (context.rejectCnt) {
////            1 -> {
////                viewModel.addJob(keyString = "procReject", interruptable = true) {
////                    vrmwManager.checkAndWaitIdle()
////                    var additionalInfo = AdditionalInfo()
////                    additionalInfo.m_isRejection = true
////                    viewModel.addTracker(null, context, additionalInfo)
////                    val promptId = getRandomList(listOf("PID_CMN_COMM_02_22", "PID_CMN_COMM_02_49"))
////                    vrmwManager.requestVR(context.dialogueMode, promptId)
////                }
////            }
////
////            2 -> {
////                viewModel.addJob(keyString = "procReject", interruptable = true) {
////                    vrmwManager.checkAndWaitIdle()
////                    var additionalInfo = AdditionalInfo()
////                    additionalInfo.m_isRejection = true
////                    viewModel.addTracker(null, context, additionalInfo)
////                    val promptId = getRandomList(listOf("PID_CMN_COMM_02_23", "PID_CMN_COMM_02_50"))
////                    vrmwManager.requestVR(context.dialogueMode, promptId)
////                }
////            }
////
////            3 -> {
////                context.rejectCnt = 0
////                viewModel.addJob(keyString = "procReject", interruptable = true) {
////                    vrmwManager.checkAndWaitIdle()
////                    val promptId = getRandomList(listOf("PID_CMN_COMM_02_51", "PID_CMN_COMM_02_52"))
////                    vrmwManager.requestTTS(promptId, showError = true) {
////                        viewModel.hideWindow(true)
////                    }
////
////
////                }
////            }
////        }
////    }
////
////    /**
////     * 일반 타입의 dialog 리젝션 처리
////     */
////    open fun procReject(context: MWContext) {
////        CustomLogger.i("")
////        context.rejectCnt++
////        when (context.rejectCnt) {
////            1 -> {
////                viewModel.addJob(keyString = "procReject", interruptable = true) {
////                    vrmwManager.checkAndWaitIdle()
////                    val promptId = getRandomList(
////                        listOf(
////                            "PID_CMN_COMM_02_36",
////                            "PID_CMN_COMM_02_37",
////                            "PID_CMN_COMM_02_38"
////                        )
////                    )
////                    var additionalInfo = AdditionalInfo()
////                    additionalInfo.m_isRejection = true
////                    // [TBD] promptid도 리스트로 갖고 tracker로 검증할 수 있도록 추가
////                    viewModel.addTracker(null, context, additionalInfo)
////                    vrmwManager.requestVR(context.dialogueMode, promptId)
////                }
////            }
////
////            2 -> {
////                viewModel.addJob(keyString = "procReject", interruptable = true) {
////                    vrmwManager.checkAndWaitIdle()
////                    var additionalInfo = AdditionalInfo()
////                    additionalInfo.m_isRejection = true
////                    viewModel.addTracker(null, context, additionalInfo)
////                    val promptId = getRejectionPrompt(context.dialogueMode)
////                    vrmwManager.requestVR(context.dialogueMode, promptId)
////                }
////            }
////
////            3 -> {
////                context.rejectCnt = 0
////                viewModel.addJob(keyString = "procReject", interruptable = true) {
////                    vrmwManager.checkAndWaitIdle()
////                    val promptId = getRandomList(listOf("PID_CMN_COMM_02_51", "PID_CMN_COMM_02_52"))
////                    vrmwManager.requestTTS(promptId, showError = true) {
////                        viewModel.hideWindow(true)
////                    }
////                }
////            }
////        }
////    }
////
////    /**
////     * dialog 별 타임아웃 안내 문구를 가져온다.
////     */
////    fun getTimeoutPrompt(dialog: DialogueMode): List<String>? {
////        CustomLogger.i("")
////        when (dialog) {
////
////            // 길안내 목적지 ->{ }
////            // 길안내 경유지 ->{ }
////            // 내 차 위치 공유 ->{ }
////            // 곡목 검색 ->{ }
////            // 카투 홈 ->{ }
////            // 차량제어 ->{ }
////            // 설정 검색 ->{ }
////            // 카카오 ->{ }
////            // Houndify ->{ }
////
////            DialogueMode.MAINMENU -> {
////                return listOf("PID_CMN_COMM_01_01")
////            }
////
////            DialogueMode.NAVIGATION,
////            DialogueMode.NAVI_SERVER,
////            DialogueMode.NAVI_FIND_POI -> {
////                return listOf("PID_SCH_NAVI_01_01")
////            }
////
////            DialogueMode.CALL -> {
////                return listOf("PID_SCH_PHON_01_01")
////            }
////
////            DialogueMode.SEND_MESSAGE -> {
////                return listOf("PID_SCH_PHON_15_01")
////            }
////
////            DialogueMode.RADIO -> {
////                return listOf("PID_CNC_RADI_04_01")
////            }
////
////
////            DialogueMode.LIST -> {
////
////                viewModel.currScreen.value?.let {
////                    when (it.domainType) {
////                        DomainType.Navigation -> {
////                            return listOf("PID_CMN_COMM_02_30")
////                        }
////
////                        DomainType.Call -> {
////                            return if (Tags.OtherNumber.isEqual(it.tag))
////                                listOf("PID_CMN_COMM_02_32")
////                            else
////                                listOf("PID_CMN_COMM_02_31")
////                        }
////
////                        DomainType.SendMessage -> {
////                            return if (Tags.OtherNumber.isEqual(it.tag))
////                                listOf("PID_CMN_COMM_02_33")
////                            else
////                                listOf("PID_CMN_COMM_02_31")
////                        }
////
////                        DomainType.Radio -> {
////                            return listOf("PID_CNC_RADI_01_11_1")
////                        }
////
////                        else -> {
////
////                        }
////                    }
////                }
////                return listOf()
////            }
////
////            else -> {
////                return listOf()
////            }
////        }
////    }
////
////    /**
////     * dialog 별 리젝션 안내 문구를 가져온다.
////     */
////    fun getRejectionPrompt(dialog: DialogueMode): List<String> {
////        CustomLogger.i("")
////
////        when (dialog) {
////            // 길안내 목적지 ->{ }
////            // 길안내 경유지 ->{ }
////            // 내 차 위치 공유 ->{ }
////            // 곡목 검색 ->{ }
////            // 카투 홈 ->{ }
////            // 차량제어 ->{ }
////            // 설정 검색 ->{ }
////            // 카카오 ->{ }
////            // Houndify ->{ }
////
////            DialogueMode.MAINMENU -> {
////                return listOf("PID_CMN_COMM_02_39")
////            }
////
////            DialogueMode.NAVIGATION,
////            DialogueMode.NAVI_SERVER,
////            DialogueMode.NAVI_FIND_POI -> {
////                return listOf("PID_CMN_COMM_02_40")
////            }
////
////            DialogueMode.CALL -> {
////                return listOf("PID_CMN_COMM_02_42")
////            }
////
////            DialogueMode.RADIO -> {
////                return listOf("PID_CMN_COMM_02_47")
////            }
////
////            DialogueMode.YESNO -> {
////                return listOf("PID_CMN_COMM_02_53")
////            }
////
////            DialogueMode.SEND_MESSAGE -> {
////                return listOf("PID_CMN_COMM_02_43")
////            }
////
////            else -> {
////                return getRandomList(
////                    listOf(
////                        "PID_CMN_COMM_02_36",
////                        "PID_CMN_COMM_02_37",
////                        "PID_CMN_COMM_02_38"
////                    )
////                )
////            }
////        }
////
////    }
////
////    /**
////     * 리젝션 처리 시작 지점
////     * 리스트 인경우 별도 안내 문구가 있으므로 분기 한다
////     */
////    open fun reject(promptId: List<String> = listOf()) {
////        CustomLogger.i("")
////        vrmwManager.currContext?.let {
////            when (it.dialogueMode) {
////                DialogueMode.LIST -> {
////                    procListReject(it)
////                }
////                else -> {
////                    procReject(it)
////                }
////            }
////        }
////    }
////
////    /**
////     * 매니저에서 시스템 intent를 사용할 경우 대비
////     */
////    open fun startIntent(intent: Intent, systemUser: Boolean = false) {
////
////        if (UserManager.supportsMultipleUsers()) {
////            val userManager = context.getSystemService(Context.USER_SERVICE) as UserManager
////            when (systemUser) {
////                true -> {
////                    CustomLogger.e(
////                        "systemUser:${systemUser} handle:${
////                            userManager.getUserForSerialNumber(
////                                UserProfileNum.SystemUser.num.toLong()
////                            )
////                        }"
////                    )
////                    context.sendBroadcastAsUser(
////                        intent, userManager.getUserForSerialNumber(
////                            UserProfileNum.SystemUser.num.toLong()
////                        )
////                    )
////                }
////
////                else -> {
////                    CustomLogger.e("systemUser:${systemUser} handle:${userManager.getUserProfileHandle()}")
////                    context.sendBroadcastAsUser(intent, userManager.getUserProfileHandle())
////                }
////            }
////        } else {
////            context.sendBroadcast(intent)
////        }
////    }
////
////    abstract fun init()
////
////
////    /**
////     * 사용불가 dialog 인지 체크 및 안내문구 반환
////     */
////    fun checkUnavailables(bundle: ParseBundle<out Any>): NoticeModel? {
////        CustomLogger.i("")
////
////        if (viewModel.getVrConfig().unavailables.contains(bundle.dialogueMode)) {
////            return NoticeModel().apply {
////                noticePromptId = "PID_CMN_COMM_01_22"
////                noticeString = getString(R.string.TID_CMN_COMM_01_22)
////            }
////        }
////        if (viewModel.getVrConfig().disables.contains(bundle.dialogueMode)) {
////            return NoticeModel().apply {
////                noticePromptId = "PID_VCL_COMM_01_02"
////                noticeString = getString(R.string.TID_VCL_COMM_01_02)
////            }
////        }
////        if (viewModel.getVrConfig().parkingOnly.contains(bundle.dialogueMode)) {
////            return NoticeModel().apply {
////                noticePromptId = "PID_VCL_COMM_02_01"
////                noticeString = getString(R.string.TID_VCL_COMM_02_01)
////            }
////        }
////        if (viewModel.getVrConfig().ignitionOnly.contains(bundle.dialogueMode)) {
////            return NoticeModel().apply {
////                noticePromptId = "PID_VCL_COMM_01_01"
////                noticeString = getString(R.string.TID_VCL_COMM_01_01)
////            }
////        }
////        if (viewModel.getVrConfig().rearUnsupported.contains(bundle.dialogueMode)) {
////            return NoticeModel().apply {
////                noticePromptId = "PID_0001"
////                noticeString = getString(R.string.TID_0001)
////            }
////        }
////        CustomLogger.i("pass")
////        return null
////    }
////
////    /**
////     * VR 결과 처리할때, 공통적으로 체크하는 항목
////     * consume 플래그를 주어 consume이면 무시하는 로직 추가 필요
////     */
////    override fun onReceiveBundle(bundle: ParseBundle<out Any>) {
////        CustomLogger.i("BaseManager::onReceiveBundle")
////        if (bundle.isBack) {
////            viewModel.popScreen()
////            bundle.isBundleConsumed = true
////            return
////        }
////        if (bundle.isExit) {
////            viewModel.hideWindow(true)
////            bundle.isBundleConsumed = true
////            return
////        }
////
////        if( bundle.type == ParseDomainType.LAUNCHAPP){
////            bundle.isBundleConsumed = true
////            reject()
////            return
////        }
////
////        bundle.contextId?.let {
////            if (!vrmwManager.validateBundle(it)) {
////                bundle.isBundleConsumed = true
////                return
////            }
////        } ?: run {
////            bundle.isBundleConsumed = true
////            return
////        }
////
////        when (bundle.dialogueMode) {
////            DialogueMode.LIST -> {
////                val commonModel = bundle.model as? CommonModel
////                commonModel?.let { model ->
////
////                    viewModel.currScreen.value?.let {
////
////                        it.mwContext?.resetCount()
////
////                        if (model.isNext) {
////                            val scrollForward = it.screenState.listState?.canScrollForward ?: false
////                            var additionalInfo = AdditionalInfo()
////                            if (scrollForward) {
////                                it.screenState.nextPage.value = true
////                                additionalInfo.isNext = true
////                            } else {
////                                vrmwManager.requestTTS(listOf("PID_CMN_COMM_01_04"), runnable = {
////                                    vrmwManager.resumeVR()
////                                })
////                                additionalInfo.rejectionPid = "PID_CMN_COMM_01_04"
////                                additionalInfo.m_isRejection = true
////                            }
////                            viewModel.addTracker(null, it.mwContext, additionalInfo)
////                            bundle.isBundleConsumed = true
////                        }
////
////                        if (model.isPrev) {
////                            val scrollBackward =
////                                it.screenState.listState?.canScrollBackward ?: false
////                            var additionalInfo = AdditionalInfo()
////                            if (scrollBackward) {
////                                it.screenState.prevPage.value = true
////                                additionalInfo.isPrev = true
////                            } else {
////                                vrmwManager.requestTTS(listOf("PID_CMN_COMM_01_03"), runnable = {
////                                    vrmwManager.resumeVR()
////                                })
////                                additionalInfo.rejectionPid = "PID_CMN_COMM_01_03"
////                                additionalInfo.m_isRejection = true
////                            }
////                            viewModel.addTracker(null, it.mwContext, additionalInfo)
////                            bundle.isBundleConsumed = true
////                        }
////
////                        val index = model.index
////                        val screenItems = it.model?.items
////                        if (index != null) {
////                            screenItems?.let { items ->
////                                index.let { idx ->
////                                    if (items.size < idx) {
////                                        bundle.dialogueMode = DialogueMode.NONE
////                                        CustomLogger.e("line not found:${it.mwContext != null} size:${items.size} index:${idx}")
////                                        vrmwManager.requestTTS(
////                                            listOf("PID_CMN_COMM_02_21"),
////                                            runnable = {
////                                                vrmwManager.resumeVR()
////                                            })
////                                        bundle.isBundleConsumed = true
////                                    }
////                                }
////                            }
////                        }
////
////                    } ?: run {
////                        CustomLogger.d("currScreen is NULL")
////                    }
////
////
////                }
////            }
////
////            else -> {
////
////            }
////        }
////
////    }
////
////    /**
////     * 조건상태가 맞지않은 경우에 발생시키는 에러 문구 출력및 종료
////     * 안내문구 ID 가 없는 경우 음성출력 없이 문구만 출력 후 종료
////     */
////    open fun launchNotice(notice: NoticeModel, showError: Boolean, pendingEvent: Runnable? = null) {
////        viewModel.addJob("launchNotice", interruptable = true) {
////
////            if (notice.noticePromptId.isNotEmpty()) {
////                val screenData = ScreenData(DomainType.Announce, ScreenType.Notice).apply {
////                    model = notice
////                    onStart = Runnable {
////                        //viewModel.changeWindowMode(WindowMode.SMALL, true)
////                        vrmwManager.requestTTS(
////                            listOf(notice.noticePromptId),
////                            listOf(""),
////                            "",
////                            waitFinish = true,
////                            showError = showError
////                        ) {
////                            viewModel.hideWindow(false, pendingEvent = pendingEvent)
////                        }
////                    }
////                    windowMode = WindowMode.SMALL
////
////                }
////                viewModel.addScreen(screenData, true)
////            } else {
////                val screenData = ScreenData(DomainType.Announce, ScreenType.Notice).apply {
////                    model = notice
////                    onStart = Runnable {
////                        //viewModel.changeWindowMode(WindowMode.SMALL, true)
////                        getUiState().showError.value = showError
////                        viewModel.addJob("pendingHideNotice", interruptable = false) {
////                            delay(UxPreset.autoSelectDuration.toLong())
////                            if (isActive) {
////                                viewModel.hideWindow(pendingEvent = pendingEvent)
////                            } else {
////                                CustomLogger.e("pendingHideNotice not Active")
////                            }
////                        }
////                    }
////                    windowMode = WindowMode.SMALL
////                }
////                viewModel.addScreen(screenData, true)
////            }
////        }
////    }
////
////    fun checkBootComplete(): NoticeModel? {
////        if (!getSystemState().isVRReady()) {
////            CustomLogger.e("checkBootComplete FALSE")
////            return NoticeModel().apply {
////                noticeString = getString(R.string.LID_SCR_0144)
////            }
////        }
////        return null
////    }
////
////    override fun onCancel() {
////        CustomLogger.i("BaseManager::onCancel")
////    }
////
////    fun getString(id: Int, vararg args: Any?): String {
////        return String.format(context.getString(id), *args)
////    }
////
////    fun getBluetoothState(): BluetoothState {
////        return viewModel.bluetoothState
////    }
////
////    fun getSystemState(): SystemState {
////        return viewModel.systemState
////    }
////
////    fun getSettingState(): SettingState {
////        return viewModel.settingState
////    }
////
////    fun getUiState(): UIState {
////        return viewModel.uiState
////    }
////
////    fun getMWState(): MWState {
////        return viewModel.mwState
////    }
////
////    override fun toString(): String {
////        return "HashCode[${this.hashCode()}], clsName[$TAG]"
////    }
////
////    fun restartVR(): Boolean {
////
////        if (isBundleProcessing()) {
////            return true
////        }
////
////        if (checkRestartVR(viewModel.currScreen.value, getMWState(), getUiState())) {
////            viewModel.clearJob()
////            viewModel.currScreen.value?.mwContext?.let {
////                CustomLogger.e("ResumeVR : ${it.dialogueMode}")
////                if (it.dialogueMode == DialogueMode.MAINMENU) {
////                    if (getUiState().windowMode.value != WindowMode.SMALL) {
////                        return false
////                    }
////                    vrmwManager.startVR(it)
////                } else {
////                    vrmwManager.resumeVR(it)
////                }
////            }
////            return true
////        }
////        return false
////    }
////
////    fun isBundleProcessing(): Boolean {
////
////        CustomLogger.e(
////            "bundleProcessing Check addJob:${viewModel.addScreenJob?.isActive} findJob:${
////                viewModel.findJob(
////                    "onReceiveBundle"
////                )?.job?.isActive
////            }"
////        )
////
////        if (viewModel.findJob("onReceiveBundle")?.job?.isActive == true) {
////            CustomLogger.e("currScreen onReceiveBundle processing")
////            return true
////        }
////        if (viewModel.addScreenJob?.isActive == true) {
////            return true
////        }
////
////        return false
////    }
////
////    fun checkRestartVR(screenData: ScreenData?, mwState: MWState, uiState: UIState): Boolean {
////        var mwContext: MWContext? = null
////        screenData?.let { it ->
////            CustomLogger.e("currScreen exist ${it.domainType} ${it.screenType} vrState:${mwState.vrState.value} animation:${uiState.boxAnimationFinish.value} ")
////
////            if (it.domainType == DomainType.MainMenu || it.domainType == DomainType.Announce)
////                return false
////
////            if (uiState.isAnimating())
////                return false
////
////            it.mwContext?.let {
////                CustomLogger.i("mwContext exist ${it.dialogueMode}")
////                it.resetCount()
////                mwContext = it
////
////            }
////        }
////        mwContext?.let {
////            return true
////        }
////        return false
////    }
////
////    companion object
//}
