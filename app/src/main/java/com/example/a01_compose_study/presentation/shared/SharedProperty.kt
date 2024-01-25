//package com.example.a01_compose_study.presentation.shared
//
//import com.example.a01_compose_study.data.ScreenData
//import com.example.a01_compose_study.data.repositoryImpl.JobManager
//import com.example.a01_compose_study.domain.state.BluetoothState
//import com.example.a01_compose_study.domain.state.MWState
//import com.example.a01_compose_study.domain.state.SettingState
//import com.example.a01_compose_study.domain.state.SystemState
//import com.example.a01_compose_study.domain.state.TestState
//import com.example.a01_compose_study.domain.state.UIState
//import com.example.a01_compose_study.presentation.ServiceManager
//import kotlinx.coroutines.flow.MutableStateFlow
//
//object SharedProperty : JobManager() {
//    val testState = TestState()
//    val uiState = UIState()
//    val systemState = SystemState()
//    val bluetoothState = BluetoothState()
//    var settingState = SettingState()
//    val mwState = MWState()
//
//    var serviceManager: ServiceManager? = null
//    val currScreen = MutableStateFlow<ScreenData?>(null)
//
////    override fun onCleared() {
////        WindowService.jobRunner?.cancel()
////        super.onCleared()
////    }
//}