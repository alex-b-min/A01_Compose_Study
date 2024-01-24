package com.example.a01_compose_study.domain.state

import android.os.ConditionVariable
import kotlinx.coroutines.flow.MutableStateFlow

class TestState {
    var isRecording = MutableStateFlow(false)
    var isTesting = false
    var animationDisabled = false
    var doNext: ConditionVariable? = null
    var scenarioCount: Int = 0
    fun isAnimationTestDisabled(): Boolean {
        return (isTesting && animationDisabled)
    }
}