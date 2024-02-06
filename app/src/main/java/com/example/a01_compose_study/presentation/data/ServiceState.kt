package com.example.a01_compose_study.presentation.data

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
}