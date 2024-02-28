package com.example.a01_compose_study.presentation.data

import com.example.a01_compose_study.data.HLanguageType
import com.example.a01_compose_study.data.HVRG2PMode
import com.example.a01_compose_study.data.PhonebookDownloadState
import com.example.a01_compose_study.data.VrConfig
import com.example.a01_compose_study.domain.state.BluetoothState
import com.example.a01_compose_study.domain.state.MWState
import com.example.a01_compose_study.domain.state.SettingState
import com.example.a01_compose_study.domain.state.SystemState
import kotlinx.coroutines.flow.MutableStateFlow

object ServiceState {
    val bluetoothState = BluetoothState()
    val mwState = MWState()
    val systemState = SystemState()
    var settingState = SettingState()
    val isWaitingPBG2PState = MutableStateFlow(false)
    var languageType = MutableStateFlow(HLanguageType.MAX)
    val vrConfig = MutableStateFlow(VrConfig())
    val contactDownloadState = MutableStateFlow(PhonebookDownloadState.ACTION_PULL_NOT_REQUEST)
    var isDevelopMode = MutableStateFlow(true)
    val g2pCompleteCnt = hashMapOf(
        HVRG2PMode.PHONE_BOOK to MutableStateFlow(-1),
        HVRG2PMode.SXM to MutableStateFlow(-1),
        HVRG2PMode.DAB to MutableStateFlow(-1),
        HVRG2PMode.SETTING to MutableStateFlow(-1),
        HVRG2PMode.NAVIGATION to MutableStateFlow(-1)
    )
}