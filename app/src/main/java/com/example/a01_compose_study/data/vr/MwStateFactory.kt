package com.example.a01_compose_study.data.vr

import com.example.a01_compose_study.domain.util.CustomLogger

object MwStateFactory {
    fun create(state: VRMWState): MwBaseState {
        CustomLogger.i("Input state[$state]")
        return when (state) {
//            VRMWState.UNLOAD -> MwUnloadState()
//            VRMWState.TTSM_INITIALIZING -> MwTtsInitializingState()
//            VRMWState.VRMA_INITIALIZING -> MwVrInitializingState()
//            VRMWState.IDLE -> MwIdleState()
//            VRMWState.RESTARING -> MwRestartingState()
            else -> {
                CustomLogger.i("current state not supported state[$state]")
                MwBaseState(VRMWState.MAX)
            }
        }
    }
}