package com.example.a01_compose_study.data.custom.ptt

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.a01_compose_study.R
import com.example.a01_compose_study.data.DialogueMode
import com.example.a01_compose_study.data.HVRError
import com.example.a01_compose_study.data.VrConfig
import com.example.a01_compose_study.data.analyze.ParseBundle
import com.example.a01_compose_study.data.analyze.ParseDomainType
import com.example.a01_compose_study.data.custom.DataProducer
import com.example.a01_compose_study.data.custom.MWContext
import com.example.a01_compose_study.data.custom.VRResultListener
import com.example.a01_compose_study.domain.model.NoticeModel
import com.example.a01_compose_study.domain.util.CustomLogger
import com.example.a01_compose_study.presentation.data.ServiceState
import com.example.a01_compose_study.presentation.screen.ptt.VrmwManager
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.Random
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PttManager @Inject constructor(
    val context: Context,
    val vrmwManager: VrmwManager,
    val dataProducer: DataProducer
) : VRResultListener {

    var onlineRandomCommands = mutableListOf("")
    var offlineRandomCommands = mutableListOf("")
    val announceString = MutableStateFlow("")
    val vrConfig = MutableStateFlow(VrConfig())
    val guideString = MutableLiveData<String>()

    var defaultAnnounceString = getString(R.string.TID_CMN_COMM_01_02)

    override fun onReceiveBundle(bundle: ParseBundle<out Any>) {
        Log.d("@@ PttManager onReceiveBundle", "${bundle}")
        when(bundle.type) {
            ParseDomainType.CALL -> {
                dataProducer.callManager.onReceiveBundle(bundle)
            }

            ParseDomainType.NAVI -> {
                TODO("NAVI 매니저 추가")
            }

            ParseDomainType.RADIO -> {
                TODO("RADIO 매니저 추가")
            }

            ParseDomainType.WEATHER -> {
                TODO("WEATHER 매니저 추가")
            }

            ParseDomainType.SEND_MESSAGE -> {
                TODO("SEND_MESSAGE 매니저 추가")
            }

            ParseDomainType.HELP -> {
                dataProducer.helpManager.onReceiveBundle(bundle)
            }

            else -> {

            }
        }
    }

    override fun onBundleParsingErr() {
        TODO("BaseManager 클래스를 만들어 공통적으로 처리")
    }

    override fun onCancel() {
        TODO("BaseManager 클래스를 만들어 공통적으로 처리")
    }

    override fun onVRError(error: HVRError) {
        TODO("BaseManager 클래스를 만들어 공통적으로 처리")
    }

    fun pttEvent() {
        val mwContext = MWContext(DialogueMode.MAINMENU, this)
        Log.d("@@ PttManager pttEvent", "${mwContext}")
        vrmwManager.startVR(mwContext)
    }

    fun pttPrepare() {
        ServiceState.bluetoothState.hfpDevice.value.apply {
            this.recognizing = true
        }

        announceString.value = ""
        makeRandomCommands()

        if (ServiceState.settingState.isOfflineMode() ||
            !ServiceState.systemState.serverResponse.value ||
            !vrConfig.value.isSupportServer
        ) {
            val randomIndex = Random().nextInt(offlineRandomCommands.size)
            guideString.postValue("${offlineRandomCommands[randomIndex]}")
        } else {
            val randomIndex = Random().nextInt(onlineRandomCommands.size)
            guideString.postValue("${onlineRandomCommands[randomIndex]}")
        }

        announceString.value = defaultAnnounceString

        if (ServiceState.bluetoothState.hfpDevice.value.device.isNotEmpty() && !ServiceState.bluetoothState.hfpDevice.value.recognizing) {
            vrmwManager.g2pController.updateCacheFiles(ServiceState.bluetoothState.hfpDevice.value.device)
        }

    }

    fun makeRandomCommands() {

        onlineRandomCommands = mutableListOf("")
        offlineRandomCommands = mutableListOf("")

        onlineRandomCommands.clear()
        onlineRandomCommands.add(getString(R.string.TID_CMN_GUID_13_02))
        onlineRandomCommands.add(getString(R.string.TID_CMN_GUID_13_02_4))
        onlineRandomCommands.add(getString(R.string.TID_CMN_GUID_13_05_1))
        onlineRandomCommands.add(getString(R.string.TID_CMN_GUID_13_05_2))
        onlineRandomCommands.add(getString(R.string.TID_CMN_GUID_13_12))
        onlineRandomCommands.add(getString(R.string.TID_CMN_GUID_13_12_1))
        onlineRandomCommands.add(getString(R.string.TID_CMN_GUID_13_13_1))
        if (ServiceState.systemState.isSupportDAB.value) {
            onlineRandomCommands.add(getString(R.string.LID_SCR_0133))
        } else {
            onlineRandomCommands.add(getString(R.string.LID_SCR_0132_1))
        }

        onlineRandomCommands.add(getString(R.string.LID_SCR_0132))
        onlineRandomCommands.add(getString(R.string.LID_SCR_0134))
        onlineRandomCommands.add(getString(R.string.TID_CMN_GUID_43_03))
        onlineRandomCommands.add(getString(R.string.TID_CMN_GUID_43_06))
        onlineRandomCommands.add(getString(R.string.TID_CMN_GUID_54_04))
        onlineRandomCommands.add(getString(R.string.TID_CMN_GUID_54_01))

        offlineRandomCommands.clear()
        offlineRandomCommands.add(getString(R.string.TID_CMN_GUID_13_05_1))
        offlineRandomCommands.add(getString(R.string.TID_CMN_GUID_13_05_2))
        offlineRandomCommands.add(getString(R.string.TID_CMN_GUID_13_12))
        offlineRandomCommands.add(getString(R.string.TID_CMN_GUID_13_12_1))
        if (ServiceState.systemState.isSupportDAB.value) {
            offlineRandomCommands.add(getString(R.string.LID_SCR_0133))
        } else {
            offlineRandomCommands.add(getString(R.string.LID_SCR_0132_1))
        }
        offlineRandomCommands.add(getString(R.string.LID_SCR_0132))
        offlineRandomCommands.add(getString(R.string.LID_SCR_0134))
        offlineRandomCommands.add(getString(R.string.TID_CMN_GUID_43_03))
        offlineRandomCommands.add(getString(R.string.TID_CMN_GUID_43_06))

        offlineRandomCommands.forEach { it ->
            Log.d("@@ offlineRandomCommands", "$it")
        }
    }

    fun getString(id: Int, vararg args: Any?): String {
        return String.format(vrmwManager.context.getString(id), *args)
    }

    fun checkStarting(): NoticeModel? {
        defaultAnnounceString = getString(R.string.TID_CMN_COMM_01_02)

        checkSupportLanguage()?.let {
            return it
        }
        checkBootComplete()?.let {
            return it
        }
        checkNaviComplete()?.let {
            return it
        }
        checkOfflineMode()?.let {
            return it
        }

        CustomLogger.i("checkStarting defaultAnnounceString : $defaultAnnounceString")


        if (!defaultAnnounceString.equals(getString(R.string.TID_CMN_COMM_01_02))) {
            guideString.postValue("")
        }

        return null
    }

    fun checkSupportLanguage(): NoticeModel? {
        if (!(getVrConfig().isSupportASR || getVrConfig().isSupportServer)) {
            return NoticeModel().apply {
                noticeString = getString(R.string.TID_CMN_COMM_07_01)
            }
        }
        return null
    }

    fun checkBootComplete(): NoticeModel? {
        if (!ServiceState.systemState.isVRReady()) {
            CustomLogger.e("checkBootComplete FALSE")
            return NoticeModel().apply {
                noticeString = getString(R.string.LID_SCR_0144)
            }
        }
        return null
    }

    fun checkNaviComplete(): NoticeModel? {
        if (!ServiceState.systemState.naviStatus.value) {
            return NoticeModel().apply {

                noticeString = getString(R.string.LID_SCR_0145)
            }
        }
        return null
    }

    fun checkOfflineMode(): NoticeModel? {

        // 네트워크 상태 체크는 하지 않음

        if (ServiceState.settingState.isOfflineMode()) { // 0: offline on, 1: offline off , OnlineVR off , 2: offline off, OnlineVR on
            //오프라인모드인데 서버 지원
            if (getVrConfig().isSupportServer) {
                // 메세지 변경
                defaultAnnounceString = getString(R.string.TID_CCS_ERRO_02_09)

            }

            //오프라인 모드인데 임베디드 지원안함
            if (!getVrConfig().isSupportASR) {
                return NoticeModel().apply {
                    noticeString = getString(R.string.TID_CMN_COMM_07_01)
                }
            }

        } else { // 오프라인 모드 아닐때,  1: offline off , OnlineVR off , 2: offline off, OnlineVR on

            //서버 가능, 서버 사용  2: server on, use server
            if (ServiceState.settingState.offlineMode.value == 2) {
                //OnlineVR On 인데 온라인 언어 지원안함?
                // 임베디드만 사용하겠지 뭐
            } else {
                // OnlineVR OFF 일때  1: offline off , OnlineVR off
                // OnlineVR OFF 인데 임베디드도 안되
                if (!getVrConfig().isSupportASR) {
                    return NoticeModel().apply {
                        noticePromptId = "PID_CCS_ERRO_02_07"
                        noticeString = getString(R.string.TID_CCS_ERRO_02_07)
                    }
                } else {

                    //OnlineVR OFF 인데 임베디드는 되
                    // embedded 지원 o, 서버지원 o // 서버 지원 되니까 더많은 기능 안내
                    if (getVrConfig().isSupportServer) {
                        defaultAnnounceString = getString(R.string.TID_CCS_ERRO_02_08)
                    }
                    // embedded 지원 o, 서버지원 x
                    // 아무것도 안함
                }
            }
        }
        return null
    }

    fun getVrConfig(): VrConfig {
        return vrConfig.value
    }

    fun checkUnavailables(bundle: ParseBundle<out Any>): NoticeModel? {
        CustomLogger.i("")

        if (ServiceState.vrConfig.value.unavailables.contains(bundle.dialogueMode)) {
            return NoticeModel().apply {
                noticePromptId = "PID_CMN_COMM_01_22"
                noticeString = getString(R.string.TID_CMN_COMM_01_22)
            }
        }
        if (ServiceState.vrConfig.value.disables.contains(bundle.dialogueMode)) {
            return NoticeModel().apply {
                noticePromptId = "PID_VCL_COMM_01_02"
                noticeString = getString(R.string.TID_VCL_COMM_01_02)
            }
        }
        if (ServiceState.vrConfig.value.parkingOnly.contains(bundle.dialogueMode)) {
            return NoticeModel().apply {
                noticePromptId = "PID_VCL_COMM_02_01"
                noticeString = getString(R.string.TID_VCL_COMM_02_01)
            }
        }
        if (ServiceState.vrConfig.value.ignitionOnly.contains(bundle.dialogueMode)) {
            return NoticeModel().apply {
                noticePromptId = "PID_VCL_COMM_01_01"
                noticeString = getString(R.string.TID_VCL_COMM_01_01)
            }
        }
        if (ServiceState.vrConfig.value.rearUnsupported.contains(bundle.dialogueMode)) {
            return NoticeModel().apply {
                noticePromptId = "PID_0001"
                noticeString = getString(R.string.TID_0001)
            }
        }
        CustomLogger.i("pass")
        return null
    }
}