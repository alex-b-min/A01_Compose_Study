package com.example.a01_compose_study.presentation.screen.ptt

import androidx.lifecycle.ViewModel
import com.example.a01_compose_study.data.custom.ptt.PttManager
import com.example.a01_compose_study.domain.model.ScreenType
import com.example.a01_compose_study.presentation.data.ServiceState
import com.example.a01_compose_study.presentation.data.UiState
import com.example.a01_compose_study.presentation.screen.main.DomainUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class PttViewModel @Inject constructor(
    val pttManager: PttManager
) : ViewModel() {

    private val _domainUiState = UiState._domainUiState

    fun onPttEvent(event: PttEvent) {
        when (event) {
            is PttEvent.SetListenType -> {
                _domainUiState.update { domainUiState ->
                    (domainUiState as? DomainUiState.PttWindow)?.copy(
                        isError = false,
                        screenType = ScreenType.PttListen
                    ) ?: domainUiState
                }
            }

            is PttEvent.SetSpeakType -> {
                _domainUiState.update { domainUiState ->
                    (domainUiState as? DomainUiState.PttWindow)?.copy(
                        isError = false,
                        screenType = ScreenType.PttSpeak
                    ) ?: domainUiState
                }
            }

            is PttEvent.SetLoadingType -> {
                _domainUiState.update { domainUiState ->
                    (domainUiState as? DomainUiState.PttWindow)?.copy(
                        isError = false,
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

                pttManager.announceString.value = ""
                pttManager.makeRandomCommands()

                if (ServiceState.settingState.isOfflineMode() ||
                    !ServiceState.systemState.serverResponse.value ||
                    !pttManager.vrConfig.value.isSupportServer
                ) {
                    val randomIndex = Random().nextInt(pttManager.offlineRandomCommands.size)
                    pttManager.guideString.postValue("${pttManager.offlineRandomCommands[randomIndex]}")
                } else {
                    val randomIndex = Random().nextInt(pttManager.onlineRandomCommands.size)
                    pttManager.guideString.postValue("${pttManager.onlineRandomCommands[randomIndex]}")
                }

                val notice = pttManager.checkStarting()
                pttManager.announceString.value = pttManager.defaultAnnounceString

                if (notice != null) {
                    //launchNotice(notice, true)
                    return
                }

                if (ServiceState.bluetoothState.hfpDevice.value.device.isNotEmpty() && !ServiceState.bluetoothState.hfpDevice.value.recognizing) {
                    vrmwManager.g2pController.updateCacheFiles(ServiceState.bluetoothState.hfpDevice.value.device)
                }

                UiState.clearUiState()
            }

            is PttEvent.StartVR -> {
                pttManager.pttEvent()
            }
        }
    }
}

