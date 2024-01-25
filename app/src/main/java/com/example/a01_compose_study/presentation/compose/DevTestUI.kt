//package com.ftd.ivi.cerence.ui.compose
//
//import android.bluetooth.BluetoothProfile
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.width
//import androidx.compose.foundation.rememberScrollState
//import androidx.compose.foundation.verticalScroll
//import androidx.compose.material.Button
//import androidx.compose.material.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.collectAsState
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.res.dimensionResource
//import com.ftd.ivi.cerence.R
//import com.ftd.ivi.cerence.car.VehicleProperty
//import com.ftd.ivi.cerence.data.ModuleStatus
//import com.ftd.ivi.cerence.data.PhonebookDownloadState
//import com.ftd.ivi.cerence.data.model.HVRG2PMode
//import com.ftd.ivi.cerence.data.model.HfpDevice
//import com.ftd.ivi.cerence.data.model.ui.NoticeModel
//import com.ftd.ivi.cerence.viewmodel.ServiceViewModel
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//
//@Composable
//fun StartingCheckUI(viewModel: ServiceViewModel) {
//
//    val systemState = viewModel.systemState
//    val settingState = viewModel.settingState
//    val moduleStatus = systemState.moduleStatus.collectAsState()
//    val naviStatus = systemState.naviStatus.collectAsState()
//    val isOfflineMode = settingState.offlineMode.collectAsState()
//    val isNotUseCCU = viewModel.isNotUseCCU.collectAsState()
//    val isNetworkAvailable = systemState.isNetworkAvailable.collectAsState()
//    val vrConfig = viewModel.vrConfig.collectAsState()
//    val isServerAvailable = systemState.serverResponse.collectAsState()
//    val scrollState = rememberScrollState()
//    val isDevelopMode = viewModel.isDevelopMode.collectAsState()
//
//    val isIgnition = systemState.isIgnition.collectAsState()
//    val isGearParking = systemState.isGearParking.collectAsState()
//    val gearMode = systemState.gearMode.collectAsState()
//
//    val isCPConnect = systemState.isCPConnected.collectAsState()
//    val isAAConnect = systemState.isAAConnected.collectAsState()
//
//    val bluetoothState = viewModel.bluetoothState
//    val hpfState = bluetoothState.hfpConnectState.collectAsState()
//    val mapState = bluetoothState.mapConnectState.collectAsState()
//    val contactDownload = systemState.contactDownloadState.collectAsState()
//    val phoneG2p = systemState.g2pCompleteCnt[HVRG2PMode.PHONE_BOOK]?.collectAsState()
//
//
//    Row(modifier = Modifier.verticalScroll(scrollState)) {
//        Column()
//        {
//            FlagButton(onClick = {
//                viewModel.serviceManager?.launchNotice(
//                    NoticeModel().apply {
//                        noticeString = "testNotice"
//                    },
//                    true
//                )
//            }, text = "makeNotice", enabled = true)
//
//            FlagButton(
//                onClick = {
//                    viewModel.bluetoothState.hfpConnectState.value =
//                        when (viewModel.bluetoothState.hfpConnectState.value) {
//                            0 -> 1
//                            1 -> 2
//                            2 -> 3
//                            3 -> 0
//                            else -> {
//                                0
//                            }
//                        }
//                    if(viewModel.bluetoothState.hfpConnectState.value == 2){
//                        viewModel.bluetoothState.hfpDevice.value =
//                            HfpDevice("testDevID", true, false)
//                        viewModel.bluetoothState.prevHfpDevice.value = "testDevID"
//                    }else{
//                        viewModel.bluetoothState.hfpDevice.value = HfpDevice("", false, false)
//                    }
//                },
//                text = "hpfState:${hpfState.value}",
//                enabled = hpfState.value == BluetoothProfile.STATE_CONNECTED
//            )
//            FlagButton(
//                onClick = {
//                    viewModel.bluetoothState.mapConnectState.value =
//                        when (viewModel.bluetoothState.mapConnectState.value) {
//                            0 -> 1
//                            1 -> 2
//                            2 -> 3
//                            3 -> 0
//                            else -> {
//                                0
//                            }
//                        }
//                },
//                text = "mapState:${mapState.value}",
//                enabled = mapState.value == BluetoothProfile.STATE_CONNECTED
//            )
////            FlagButton(onClick = {
////                viewModel.bluetoothState.isPairedExist.value =
////                    !viewModel.bluetoothState.isPairedExist.value
////            }, text = "pairExsist", enabled = pairExsist.value)
//            FlagButton(
//                onClick = {
//                    viewModel.systemState.contactDownloadState.value =
//                        when (viewModel.systemState.contactDownloadState.value) {
//                            PhonebookDownloadState.ACTION_PULL_NOT_REQUEST -> PhonebookDownloadState.ACTION_PULL_START
//                            PhonebookDownloadState.ACTION_PULL_START -> PhonebookDownloadState.ACTION_PULL_COMPLETE
//                            PhonebookDownloadState.ACTION_PULL_COMPLETE -> PhonebookDownloadState.ACTION_PULL_FAIL
//                            PhonebookDownloadState.ACTION_PULL_FAIL -> PhonebookDownloadState.ACTION_PULL_NOT_REQUEST
//                        }
//                },
//                text = "contactDown:${contactDownload.value.value}",
//                enabled = contactDownload.value == PhonebookDownloadState.ACTION_PULL_COMPLETE
//            )
//            FlagButton(onClick = {
//                viewModel.serviceManager?.contactsManager?.clearContacts()
//            }, text = "clearContacts", enabled = false)
//            FlagButton(onClick = {
//                viewModel.serviceManager?.contactsManager?.fetchContacts()
//            }, text = "fetchContacts", enabled = false)
//            FlagButton(onClick = {
//                systemState.g2pCompleteCnt[HVRG2PMode.PHONE_BOOK]?.value = when (phoneG2p?.value) {
//                    -1 -> 0
//                    0 -> 1
//                    1 -> -1
//                    else -> -1
//                }
//            }, text = "phoneG2p", enabled = phoneG2p?.value != -1)
//            FlagButton(onClick = {
//                CoroutineScope(Dispatchers.Default).launch {
//                    viewModel.serviceManager?.vrmwManager?.startTTS(
//                        "This is Requested TTS Test " +
//                                "This is Requested TTS Test " +
//                                "This is Requested TTS Test " +
//                                "This is Requested TTS Test " +
//                                "This is Requested TTS Test This is Requested TTS Test Requested This is Requested TTS Test This is Requested TTS Test",
//                        "${System.currentTimeMillis()}"
//                    )
//                }
//
//            }, text = "testTTS", enabled = false)
//
//            FlagButton(onClick = {
//                viewModel.ttsToInput("SendMessageTo     Test Two    Say hello")
////                CoroutineScope(Dispatchers.Default).launch{
////                    viewModel.serviceManager?.vrmwManager?.startTTS(
////                        "Android Auto"
////                        ,"${System.currentTimeMillis()}")
////                }
//
//            }, text = "TTSInput", enabled = false)
//        }
//        Column {
//
//            Text(text = "lang:${vrConfig.value.language}")
//            Text(text = "loc:${vrConfig.value.locale}")
//
//            FlagButton(onClick = {
//                viewModel.serviceManager?.vehicleManager?.setPropertyValue(
//                    VehicleProperty.VehicleIGNPowerOn.value,
//                    1
//                )
//            }, text = "isIgnition", enabled = isIgnition.value)
////        FlagButton(onClick = {
////            viewModel.serviceManager?.vehicleManager?.setPropertyValue(
////                VehicleProperty.VehicleGearP.value,
////                1
////            )
////        }, text = "setGearP", enabled = isGearParking.value)
////        FlagButton(onClick = {
////            viewModel.serviceManager?.vehicleManager?.setPropertyValue(
////                VehicleProperty.VehicleGearR.value,
////                1
////            )
////        }, text = "setGearR", enabled = gearMode.value == 1)
////        FlagButton(onClick = {
////            viewModel.serviceManager?.vehicleManager?.setPropertyValue(
////                VehicleProperty.VehicleGearD.value,
////                1
////            )
////        }, text = "setGearD", enabled = gearMode.value == 2)
//
//
//            FlagButton(onClick = {
//                viewModel.isDevelopMode.value = !isDevelopMode.value
//            }, text = "isDevelopMode", enabled = isDevelopMode.value)
//
//            FlagButton(onClick = {
//                if (moduleStatus.value == ModuleStatus.READY) {
//                    systemState.moduleStatus.value = ModuleStatus.UNLOAD
//                } else {
//                    systemState.moduleStatus.value = ModuleStatus.READY
//                }
//            }, text = "moduleLoad", enabled = (moduleStatus.value == ModuleStatus.READY))
//
//            FlagButton(onClick = {
//                systemState.naviStatus.value = !naviStatus.value
//            }, text = "naviStatus", enabled = naviStatus.value)
//
//            FlagButton(onClick = {
//                viewModel.isNotUseCCU.value = !viewModel.isNotUseCCU.value
//            }, text = "isNotUseCCU", enabled = isNotUseCCU.value)
//
//            FlagButton(onClick = {
//                when (settingState.offlineMode.value) {
//
//                    0 -> {
//                        settingState.offlineMode.value = 1
//                    }
//
//                    1 -> {
//                        settingState.offlineMode.value = 2
//                    }
//
//                    2 -> {
//                        settingState.offlineMode.value = 0
//                    }
////
//                    else -> {
//                        settingState.offlineMode.value = 0
//                    }
//                }
//
//            }, text = "OfflineMode:${isOfflineMode.value}", enabled = settingState.isOfflineMode())
//
////        FlagButton(onClick = {
//////            CoroutineScope(Dispatchers.IO).launch {
//////                viewModel.serviceManager?.networkManager?.testConnect()
//////            }
////        }, text = "isServerAvailable", enabled = isServerAvailable.value)
//
//            FlagButton(onClick = {
//                viewModel.vrConfig.value.isSupportASR = !viewModel.vrConfig.value.isSupportASR
//                viewModel.vrConfig.value = viewModel.vrConfig.value.copy()
//            }, text = "isSupportASR", enabled = vrConfig.value.isSupportASR)
//
//            FlagButton(onClick = {
//                viewModel.vrConfig.value.isSupportServer = !viewModel.vrConfig.value.isSupportServer
//                viewModel.vrConfig.value = viewModel.vrConfig.value.copy()
//            }, text = "isSupportServer", enabled = vrConfig.value.isSupportServer)
//
//            FlagButton(onClick = {
//                viewModel.vrConfig.value.isSupportTTS = !viewModel.vrConfig.value.isSupportTTS
//                viewModel.vrConfig.value = viewModel.vrConfig.value.copy()
//            }, text = "isSupportTTS", enabled = vrConfig.value.isSupportTTS)
//
//            FlagButton(
//                onClick = {},
//                text = "isNetworkAvailable",
//                enabled = isNetworkAvailable.value
//            )
//
//            FlagButton(
//                onClick = { systemState.isAAConnected.value = !systemState.isAAConnected.value },
//                text = "isAAConnected", enabled = isAAConnect.value
//            )
//            FlagButton(
//                onClick = { systemState.isCPConnected.value = !systemState.isCPConnected.value },
//                text = "isCPConnected", enabled = isCPConnect.value
//            )
//        }
//    }
//
//
//}
//
//@Composable
//fun FlagButton(onClick: () -> Unit, text: String, enabled: Boolean = false) {
//    Button(
//        onClick = onClick,
//    ) {
//        Row(
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//
//            Box(
//                modifier = Modifier
//                    .width(dimensionResource(id = R.dimen.dp_10))
//                    .height(dimensionResource(id = R.dimen.dp_10))
//                    .background(if (enabled) Color.Green else Color.Red)
//            )
//            Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.dp_10)))
//            Text(text = text)
//        }
//    }
//}