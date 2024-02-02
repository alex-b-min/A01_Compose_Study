package com.example.a01_compose_study.data.vr

import com.example.a01_compose_study.data.HG2PStatus
import com.example.a01_compose_study.data.HVRG2PMode
import com.example.a01_compose_study.domain.util.CustomLogger
import com.example.a01_compose_study.presentation.ServiceViewModel
import javax.inject.Inject


class G2PController @Inject constructor(val viewModel: ServiceViewModel) : IG2PListener {

    external fun setG2PListener(listener: Any)
    external fun requestG2P(g2pData: String)
    external fun cancel(g2pMode: Int)
    external fun setG2PCachePath(g2pMode: Int, path: String)
    external fun removeCacheFiles(g2pMode: Int, deviceId: String)
    external fun updateCacheFiles(deviceId: String)

    override fun onG2PStatusChanged(mode: Int, state: Int, deviceId: String) {
        val g2pMode = HVRG2PMode.values()[mode]
        val g2pState = HG2PStatus.values()[state]
        CustomLogger.i("Main onG2PStatusChanged ${g2pMode.name} ${g2pState.name}")
        viewModel.systemState.g2pStatus[g2pMode]?.value = g2pState

        when (g2pState) {
            HG2PStatus.SUCCESS,
            HG2PStatus.FAILED,
            HG2PStatus.CANCELLED -> {
                viewModel.isWaitingPBG2PState.value = false
            }

            else -> {}
        }
    }

    override fun onG2PCompleted(mode: Int, deviceId: String, count: Int) {
        val g2pMode = HVRG2PMode.values()[mode]
        CustomLogger.i("Main onG2PCompleted $count")
        viewModel.systemState.g2pCompleteCnt[g2pMode]?.value = count
    }

    fun cancel(g2pMode: HVRG2PMode) {
        cancel(g2pMode.ordinal)
    }
}