package com.example.a01_compose_study.presentation.screen.ptt

import android.content.Context
import android.os.LocaleList
import androidx.lifecycle.ViewModel
import com.example.a01_compose_study.data.HTextToSpeechState
import com.example.a01_compose_study.data.HVRState
import com.example.a01_compose_study.data.custom.MWContext
import com.example.a01_compose_study.data.vr.MwStateMachine
import com.example.a01_compose_study.data.vr.VRMWController
import com.example.a01_compose_study.domain.model.ScreenType
import com.example.a01_compose_study.domain.state.BluetoothState
import com.example.a01_compose_study.domain.state.MWState
import com.example.a01_compose_study.domain.util.CustomLogger
import com.example.a01_compose_study.presentation.data.ServiceState
import com.example.a01_compose_study.presentation.data.UiState
import com.example.a01_compose_study.presentation.screen.main.DomainUiState
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.update
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PttViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    val controller: VRMWController
) : ViewModel() {

    private val _domainUiState = UiState._domainUiState
    var promptId = mutableListOf<String>()
    var currContext: MWContext? = null
    var vrState = HVRState.IDLE
    var ttsState = HTextToSpeechState.IDLE
    var m_stateMachine: MwStateMachine = MwStateMachine()




    private val _sealedParsedData = UiState._sealedParsedData

    init {
        CustomLogger.i("VrmwManager Constructor Hash[${this.hashCode()}]")
//        m_stateMachine.init(this)
    }

    fun init() {
        val localeList: LocaleList = context.resources.configuration.locales
        val primaryLocale: Locale? = localeList.get(0)
        CustomLogger.i("ACTION_LOCALE_CHANGED $primaryLocale")
//        val languageType = if (BuildConfig.INSTALL_TYPE != "TABLET") {
//            primaryLocale?.let { Util.parseLanguageType(it.toString()) } ?: HLanguageType.UK_ENGLISH
//        } else {
//            // tablet일때에는 한국어가 대부분이므로 eng로 바꾸게함.
////            HLanguageType.UK_ENGLISH
//        }
        val dirPath = ""//applicationInfo.nativeLibraryDir
        //CustomLogger.i("dir $dirPath, languageType $languageType, userIdx ${viewModel.settingState.userIdx.value}")

        controller.setEventListener(this)
//        controller.initSystem(
//            dirPath,
//            languageType.id,
//           viewModel.settingState.userIdx.value.toString()
//        )
//        CustomLogger.i("call setG2PListener")
//        g2pController.setG2PListener(g2pController)
//
//        CustomLogger.i("call setMediaInListener")
//        mediaIn.setMediaInListener(mediaIn)
//        CustomLogger.i("call setMediaOutListener")
//        mediaOut.setMediaOutListener(mediaOut)
    }

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

            is PttEvent.StartVR -> {

                currContext?.let { context ->
                    stop()
                    controller.callRecogStart(
                        context.dialogueMode.value,
                        promptId,
                        getDeviceId(),
                        event.promptArgs,
                        promptString = event.promptString
                    )
                }
            }
        }
    }

    fun getMwState(): MWState {
        return ServiceState.mwState
    }

    fun getDeviceId(): String {
        val deviceId = if (getBluetoothState().hfpDevice.value.connect) {
            if (!getBluetoothState().hfpDevice.value.recognizing) {
                getBluetoothState().hfpDevice.value.device
            } else {
                getBluetoothState().prevHfpDevice.value
            }
        } else {
            ""
        }

        return deviceId
    }


    fun getBluetoothState(): BluetoothState {
        return ServiceState.bluetoothState
    }

    fun vrStop() {
        controller.recogStop()
    }

    fun playStop() {
        controller.playStop()
    }


    fun stop() {
        CustomLogger.i("STOP tts:${getMwState().ttsState.value.name} vr:${getMwState().vrState.value.name} vrError:${getMwState().vrError.value.name}")
        vrStop()
        playStop()
        cancel()
    }

    fun cancel() {
        currContext = null
        setTTSState(HTextToSpeechState.IDLE)
        setVRState(HVRState.IDLE)
    }

    fun setTTSState(state: HTextToSpeechState, force: Boolean = false) {
        try {
            CustomLogger.i("setTTSState getMwState().ttsState.value ${getMwState().ttsState.value}")
            if (!force) {
                if (getMwState().ttsState.value == HTextToSpeechState.TERMINATED) {
                    return
                }
            }
            CustomLogger.i("setTTSState ${state.name} to ${currContext?.dialogueMode}")
            getMwState().ttsState.value = state
            currContext?.onTTSState(state)
        } catch (e: Exception) {
            e.message?.let { CustomLogger.e(it) }
        }
    }

    fun setVRState(state: HVRState, force: Boolean = false) {
        CustomLogger.i("setVRState ${state.name} to ${currContext?.dialogueMode}")

        if (!force) {
            if (getMwState().vrState.value == HVRState.TERMINATED) {
                return
            }
        }

        getMwState().vrState.value = state
        if (state == HVRState.PREPARING) {
            getMwState().userSpeaking.value = false
        }
        currContext?.onVRState(state)
    }


}

