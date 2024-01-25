//package com.example.a01_compose_study.domain.state
//
//import androidx.lifecycle.MutableLiveData
//import com.example.a01_compose_study.data.WindowMode
//import kotlinx.coroutines.flow.MutableStateFlow
//
//class UIState {
//
//    //window ui 관련
//    val blockingTouch = MutableLiveData(false)
//    val isInTouching = MutableStateFlow(false)
//    val windowMode = MutableStateFlow(WindowMode.SMALL)
//    val wishHeight = MutableStateFlow(0f)
//    var noAnimation = false
//    val beforeWindowMode = MutableStateFlow(WindowMode.LARGE)
//    val showWindow = MutableStateFlow(com.example.a01_compose_study.BuildConfig.IS_SHOW_DEBUG_SCREEN)
//
//    val contentVisible = MutableStateFlow(true)
//    val boxAnimationFinish = MutableStateFlow(true)
//    var showError = MutableStateFlow(false)
//    val tempHide = MutableStateFlow(false)
//    var lastServiceScreenH = 0f
//    var lastToHeight = 0f
//
//    var pendingVisibleEvent: Runnable? = null
//    val animationState = MutableStateFlow(AnimationState.ENABLED)
//    val cancelVisibleAni = MutableStateFlow(0)
//    var isShowingAnimation = false
//
//    fun isAnimating(): Boolean {
//        return animationState.value == AnimationState.PROGRESSING || isShowingAnimation
//    }
//
//}