package com.example.a01_compose_study.presentation.screen.ptt

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.a01_compose_study.data.HTextToSpeechState
import com.example.a01_compose_study.data.HVRState
import com.example.a01_compose_study.data.custom.MWContext
import com.example.a01_compose_study.data.vr.VRMWController
import com.example.a01_compose_study.data.custom.SealedParsedData
import com.example.a01_compose_study.domain.model.ScreenType
import com.example.a01_compose_study.domain.state.BluetoothState
import com.example.a01_compose_study.domain.state.MWState
import com.example.a01_compose_study.domain.util.CustomLogger
import com.example.a01_compose_study.presentation.data.ServiceState
import com.example.a01_compose_study.presentation.data.UiState
import com.example.a01_compose_study.presentation.screen.main.DomainUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharedFlow
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.update
import javax.inject.Inject
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

    private val _sealedParsedData = UiState._sealedParsedData

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

                currContext?.let{context ->
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

