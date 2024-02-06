package com.example.a01_compose_study.presentation.screen.ptt

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.a01_compose_study.data.HTextToSpeechState
import com.example.a01_compose_study.data.HVRState
import com.example.a01_compose_study.data.custom.MWContext
import com.example.a01_compose_study.data.vr.IMwController
import com.example.a01_compose_study.data.vr.IVRMWListener
import com.example.a01_compose_study.data.vr.MediaIn
import com.example.a01_compose_study.data.vr.MediaOut
import com.example.a01_compose_study.data.vr.MwStateMachine
import com.example.a01_compose_study.data.vr.VRMWController
import com.example.a01_compose_study.data.vr.VRMWEventInfo
import com.example.a01_compose_study.domain.model.BaseApplication
import com.example.a01_compose_study.domain.model.ScreenType
import com.example.a01_compose_study.domain.state.BluetoothState
import com.example.a01_compose_study.domain.state.MWState
import com.example.a01_compose_study.domain.util.CustomLogger
import com.example.a01_compose_study.presentation.data.ServiceState
import com.example.a01_compose_study.presentation.data.UiState
import com.example.a01_compose_study.presentation.screen.main.DomainUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@HiltViewModel
class PttViewModel @Inject constructor(
    // val vrmwManager: VrmwManager
) : ViewModel() {

    val vrmwManager: VrmwManager
        get() {
            TODO()
        }

    private val _domainUiState = UiState._domainUiState
    var promptId = mutableListOf<String>()
    var currContext: MWContext? = null
    var vrState = HVRState.IDLE
    var ttsState = HTextToSpeechState.IDLE
    var m_stateMachine: MwStateMachine = MwStateMachine()

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
                currContext?.let { context ->
                   vrmwManager.startVR(context)
                }
            }
        }
    }
}

