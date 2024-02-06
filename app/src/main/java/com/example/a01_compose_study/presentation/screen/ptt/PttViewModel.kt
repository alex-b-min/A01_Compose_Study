package com.example.a01_compose_study.presentation.screen.ptt

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.a01_compose_study.R
import com.example.a01_compose_study.data.HTextToSpeechState
import com.example.a01_compose_study.data.HVRState
import com.example.a01_compose_study.data.VrConfig
import com.example.a01_compose_study.data.custom.MWContext
import com.example.a01_compose_study.data.vr.MwStateMachine
import com.example.a01_compose_study.domain.model.ScreenType
import com.example.a01_compose_study.domain.util.CustomLogger
import com.example.a01_compose_study.presentation.data.ServiceState
import com.example.a01_compose_study.presentation.data.UiState
import com.example.a01_compose_study.presentation.screen.main.DomainUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import java.util.Random
import javax.inject.Inject

@HiltViewModel
class PttViewModel @Inject constructor(
    val vrmwManager: VrmwManager
) : ViewModel() {

    private val _domainUiState = UiState._domainUiState

    var promptId = mutableListOf<String>()
    var currContext: MWContext? = null
    var vrState = HVRState.IDLE
    var ttsState = HTextToSpeechState.IDLE
    var m_stateMachine: MwStateMachine = MwStateMachine()
    val announceString = MutableStateFlow("")
    val vrConfig = MutableStateFlow(VrConfig())
    val guideString = MutableLiveData<String>()

    var defaultAnnounceString = getString(R.string.TID_CMN_COMM_01_02)

    var onlineRandomCommands = mutableListOf("")
    var offlineRandomCommands = mutableListOf("")

    fun onPttEvent(event: PttEvent) {
        when (event) {
            is PttEvent.SetListenType -> {
                _domainUiState.update { domainUiState ->
                    (domainUiState as? DomainUiState.PttWindow)?.copy(
                        screenType = ScreenType.PttListen
                    ) ?: domainUiState
                }
            }

            is PttEvent.SetSpeakType -> {
                _domainUiState.update { domainUiState ->
                    (domainUiState as? DomainUiState.PttWindow)?.copy(
                        screenType = ScreenType.PttSpeak
                    ) ?: domainUiState
                }
            }

            is PttEvent.SetLoadingType -> {
                _domainUiState.update { domainUiState ->
                    (domainUiState as? DomainUiState.PttWindow)?.copy(
                        screenType = ScreenType.PttLoading
                    ) ?: domainUiState
                }
            }

            is PttEvent.SetAnnounceType -> {
                _domainUiState.update { domainUiState ->
                    (domainUiState as? DomainUiState.PttWindow)?.copy(
                        screenType = ScreenType.PttAnounce
                    ) ?: domainUiState
                }
            }

            is PttEvent.PreparePtt -> {
                ServiceState.bluetoothState.hfpDevice.value.apply {
                    this.recognizing = true
                }

                CustomLogger.i("pttPrepare")
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

                //val notice = checkStarting()
                announceString.value = defaultAnnounceString

//                if (notice != null) {
//                    //launchNotice(notice, true)
//                    return
//                }

                if (ServiceState.bluetoothState.hfpDevice.value.device.isNotEmpty() && !ServiceState.bluetoothState.hfpDevice.value.recognizing) {
                    vrmwManager.g2pController.updateCacheFiles(ServiceState.bluetoothState.hfpDevice.value.device)
                }
            }

            is PttEvent.StartVR -> {
                currContext?.let { context ->
                    vrmwManager.startVR(context)
                }
            }
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
}

