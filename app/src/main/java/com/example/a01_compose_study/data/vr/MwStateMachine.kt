package com.example.a01_compose_study.data.vr

import com.example.a01_compose_study.data.repository.JobManager
import com.example.a01_compose_study.domain.util.CustomLogger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MwStateMachine : JobManager() {
    val keyString = "VRMWChangeSystem"
    var state: MwBaseState

    private var _currentState = MutableStateFlow(VRMWState.UNLOAD)
    val currentState = _currentState.asStateFlow()

    var m_controller: IMwController? = null

    init {
        state = MwStateFactory.create(currentState.value)
    }

    fun init(controller: IMwController) {
        m_controller = controller
    }

    fun addEvent(eventInfo: VRMWEventInfo) {
        CustomLogger.i("addEvent - $eventInfo")
        addSyncJob(keyString = keyString) {
            execute(eventInfo)
        }
    }


    fun execute(eventInfo: VRMWEventInfo) {
        CustomLogger.i("execute - eventInfo [$eventInfo]")
        // m_controller는 VRMwManager라서 없을수 없음!
        val nextState = state.handleEvent(eventInfo, m_controller!!)
        CustomLogger.i("inputEventInfo [$eventInfo], currentState [$currentState], nextState[$nextState]")
        if (currentState.value != nextState) {
            CustomLogger.i("CurrentState[$currentState], nextState[$nextState] difference")
            _currentState.value = nextState
            state = MwStateFactory.create(currentState.value)
        }
    }
}