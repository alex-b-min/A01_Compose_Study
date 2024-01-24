package com.example.a01_compose_study.domain.state

import com.example.a01_compose_study.data.HTextToSpeechError
import com.example.a01_compose_study.data.HTextToSpeechEvent
import com.example.a01_compose_study.data.HTextToSpeechState
import com.example.a01_compose_study.data.HVRConfigEvent
import com.example.a01_compose_study.data.HVRError
import com.example.a01_compose_study.data.HVRState
import kotlinx.coroutines.flow.MutableStateFlow

class MWState {

    var vrError = MutableStateFlow(HVRError.NONE)
    var vrState = MutableStateFlow(HVRState.IDLE)
    var ttsState = MutableStateFlow(HTextToSpeechState.IDLE)
    var userSpeaking = MutableStateFlow(false)
    var speechDetected = MutableStateFlow(false)
    var vrConfigEvent = MutableStateFlow(HVRConfigEvent.NONE)
    var ttsConfigEvent = MutableStateFlow(HTextToSpeechEvent.NONE)
    var isVRManagerInitializeDone = false
    var isTTSManagerInitializeDone = false
    var ttsErrorEvent = MutableStateFlow<HTextToSpeechError?>(null)
    var ttsEvent = MutableStateFlow(HTextToSpeechEvent.NONE)


    override fun equals(other: Any?): Boolean {
        return this.hashCode() == other.hashCode()
    }
}