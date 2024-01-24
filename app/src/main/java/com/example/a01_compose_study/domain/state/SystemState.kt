package com.example.a01_compose_study.domain.state

import com.example.a01_compose_study.data.HG2PStatus
import com.example.a01_compose_study.data.HVRG2PMode
import com.example.a01_compose_study.data.ModuleStatus
import com.example.a01_compose_study.data.PhonebookDownloadState
import kotlinx.coroutines.flow.MutableStateFlow

class SystemState {

    val serviceInit = MutableStateFlow(false)
    val moduleStatus = MutableStateFlow(ModuleStatus.UNLOAD)
    val viewSystemReady = MutableStateFlow(false)
    val isCPConnected = MutableStateFlow(false)
    val isAAConnected = MutableStateFlow(false)
    val naviStatus = MutableStateFlow(true)
    val isSupportDAB = MutableStateFlow(true)
    val isPrivacyMode = MutableStateFlow(false)


    val isAgreement = MutableStateFlow(false)

    val isNetworkAvailable = MutableStateFlow(true)
    val ccsConnected = MutableStateFlow(false)
    val serverResponse = MutableStateFlow(false)
    val serverStatus = MutableStateFlow(false)
//    val offlineMode =
//        MutableStateFlow(0) // 0: offline on,  1: offline off , OnlineVR off , 개통 2: offline off, OnlineVR on , 개통

    val isIgnition = MutableStateFlow(false)
    val isGearParking = MutableStateFlow(false)
    val gearMode = MutableStateFlow(-1)


    val contactDownloadState = MutableStateFlow(PhonebookDownloadState.ACTION_PULL_NOT_REQUEST)

    // G2P 상태 관리
    val g2pStatus = HashMap<HVRG2PMode, MutableStateFlow<HG2PStatus>>().apply {
        put(HVRG2PMode.PHONE_BOOK, MutableStateFlow(HG2PStatus.MAX))
        put(HVRG2PMode.SXM, MutableStateFlow(HG2PStatus.MAX))
        put(HVRG2PMode.DAB, MutableStateFlow(HG2PStatus.MAX))
        put(HVRG2PMode.SETTING, MutableStateFlow(HG2PStatus.MAX))
        put(HVRG2PMode.NAVIGATION, MutableStateFlow(HG2PStatus.MAX))
    }
    val g2pCompleteCnt = HashMap<HVRG2PMode, MutableStateFlow<Int>>().apply {
        put(HVRG2PMode.PHONE_BOOK, MutableStateFlow(-1))
        put(HVRG2PMode.SXM, MutableStateFlow(-1))
        put(HVRG2PMode.DAB, MutableStateFlow(-1))
        put(HVRG2PMode.SETTING, MutableStateFlow(-1))
        put(HVRG2PMode.NAVIGATION, MutableStateFlow(-1))
    }

//    fun isOfflineMode(): Boolean {
//        return offlineMode.value <= 0
//    }

    /***
     * 개통여부 체크
     * 231031 : Raul 말씀으로는 개통이 된 상태여야만 시스템에서 VR에 이벤트를 전달 해 줄 것이기 때문에
     * 개통여부 체크와 VR 동의 체크는 하지 않아도 된다고 함
     * offline 모드 자체도 개통된 상태라고 함
     */
//    fun isCCUSubscribed(): Boolean {
//        //return offlineMode.value >= 1
//        return true
//    }
//
//    fun isOnlineVR(): Boolean {
//        return offlineMode.value == 2
//    }

    fun isVRReady(): Boolean {
        return (moduleStatus.value == ModuleStatus.READY && viewSystemReady.value)
    }
}