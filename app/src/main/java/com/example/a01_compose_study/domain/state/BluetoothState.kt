package com.example.a01_compose_study.domain.state

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothProfile
import com.example.a01_compose_study.data.HfpDevice
import kotlinx.coroutines.flow.MutableStateFlow

class BluetoothState {

    //Bluetooth 상태관리
    val hfpDevice = MutableStateFlow(HfpDevice("", false, false))
    val hfpConnectState = MutableStateFlow(BluetoothProfile.STATE_DISCONNECTED)
    val pbapConnectState = MutableStateFlow(BluetoothProfile.STATE_DISCONNECTED)
    val prevHfpDevice = MutableStateFlow("")
    val bluetoothBondState = MutableStateFlow(BluetoothDevice.BOND_NONE)
    val mapConnectState = MutableStateFlow(BluetoothProfile.STATE_DISCONNECTED)
    val mapConnected = MutableStateFlow(Pair("", false)) //btId, connect

}