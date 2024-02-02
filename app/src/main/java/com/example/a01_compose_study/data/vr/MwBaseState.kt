package com.example.a01_compose_study.data.vr

import com.example.a01_compose_study.domain.util.CustomLogger

open class MwBaseState(var m_state: VRMWState) {
    var restarting = false

    open fun handleEvent(info: VRMWEventInfo, mwController: IMwController): VRMWState {
        CustomLogger.i("Current State[$m_state], input Event[$info]")
        return m_state
    }
}