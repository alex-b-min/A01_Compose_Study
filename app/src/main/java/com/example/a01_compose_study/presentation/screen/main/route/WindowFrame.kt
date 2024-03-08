package com.example.a01_compose_study.presentation.screen.main.route

import android.util.Log
import android.view.MotionEvent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.unit.dp
import com.example.a01_compose_study.domain.util.ScreenSizeType
import com.example.a01_compose_study.presentation.data.UiState
import com.example.a01_compose_study.presentation.data.UiState.onVREvent
import com.example.a01_compose_study.presentation.screen.main.DomainUiState
import com.example.a01_compose_study.presentation.screen.main.VREvent
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun WindowFrame(
    domainUiState: DomainUiState,
    domainWindowVisible: Boolean,
    vrUiState: VRUiState,
    onCloseDomainWindow: () -> Unit,
    content: @Composable () -> Unit,
) {
    val scope = rememberCoroutineScope()

    val targetFillMaxHeight by remember { mutableStateOf(Animatable(0f)) }
    val targetFillMaxWidth by remember { mutableStateOf(Animatable(0f)) }

    var isTouchDown by remember { mutableStateOf(false) }
    var isTouchUp by remember { mutableStateOf(false) }

    LaunchedEffect(isTouchUp) {
        if (isTouchUp) {
            scope.launch {
                delay(2000)
                UiState.onVREvent(
                    VREvent.ChangeVRUIEvent(
                        VRUiState.PttLoading(
                            active = true,
                            isError = false
                        )
                    )
                )
            }
            isTouchUp = false
        }
        Log.d("@@isTouchUp", "${isTouchUp}")
    }

    LaunchedEffect(domainUiState) {
        val newTargetHeightValue = when (domainUiState.screenSizeType) {
            is ScreenSizeType.Zero -> 0f
            is ScreenSizeType.Small -> 0.15f
            is ScreenSizeType.Middle -> 0.268f
            is ScreenSizeType.Large -> 0.433f
            is ScreenSizeType.Full -> 1f
        }
        val newTargetWidthValue = when (domainUiState.screenSizeType) {
            is ScreenSizeType.Zero -> 0f
            is ScreenSizeType.Full -> 1f
            else -> 0.233f
        }

        scope.launch {
            targetFillMaxHeight.animateTo(
                targetValue = newTargetHeightValue,
                animationSpec = tween(
                    durationMillis = 800,
                    easing = FastOutSlowInEasing
                )
            )
        }
        scope.launch {
            targetFillMaxWidth.animateTo(
                targetValue = newTargetWidthValue,
                animationSpec = tween(
                    durationMillis = 800,
                    easing = FastOutSlowInEasing
                )
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { onCloseDomainWindow() }
            )
    ) {
        AnimatedVisibility(
            visible = domainWindowVisible,
            modifier = Modifier.fillMaxWidth(),
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(1000)
            ),
            exit = slideOutVertically(
                targetOffsetY = { it },
                animationSpec = tween(1000)
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomStart
            ) {
                Box(
                    modifier = Modifier
                        .offset(x = 10.dp, y = (10).dp)
                        .fillMaxHeight(targetFillMaxHeight.value)
                        .fillMaxWidth(targetFillMaxWidth.value)
                        .background(
                            color = Color.Transparent,
                            shape = RoundedCornerShape(15.dp)
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {}
                        /** 방법 1. (VR의 상태가 바껴 애니메이션이 바뀌면 겹쳐진 도메인 스크린들도 같이 리컴포지션이 되는 이슈가 있음.. 추후 수정해야함..)
                         * [pointerInput를 이용하여 터치를 했을 시 VR의 Active 상태를 판별하여 애니메이션을 시작하는 코드]
                         * ==> 현재 개선해야할 부분은 VR의 Active 상태를 판별하여 VR 애니매이션을 실행하거나 종료하거나 하는데 이때 리컴포지션이 일어나 각 Domain별 Screen 화면도 재구성이 된다는 점이다.
                         * ==> 이러한 초기화 문제가 발생하여 현재 보고 있는 스크롤이 0번째 Index로 초기화되어, 화면이 최상단(0번째 인덱스)으로 스크롤되는 현상이 발생함
                         * ==> 다른것부터 하고 난 후 이것을 해결해봐야겠음..(시간이 많이 걸릴 이슈..)
                         */
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onLongPress = {
                                    // 긴 터치가 시작되었을 때 실행할 코드
                                    Log.d("@@ onLongPress", "Touch started at offset: $it")
                                },
                                onPress = { offset ->
                                    // 터치가 시작되었을 때 실행할 코드
                                    Log.d("@@ ACTION_DOWN", "Touch started at offset: $offset")
                                    onVREvent(
                                        VREvent.ChangeVRUIEvent(
                                            VRUiState.PttLoading(
                                                active = false,
                                                isError = false
                                            )
                                        )
                                    )
                                    isTouchUp = true
                                },
                                onTap = { offset ->
                                    // 빠른 터치일 때 실행할 코드
                                    Log.d("@@ TAP", "Quick tap at offset: $offset")
                                }
                            )
                        }
                        /** 방법 2. (VR의 상태가 바껴 애니메이션이 바뀌면 겹쳐진 도메인 스크린들도 같이 리컴포지션이 되는 이슈가 있음.. 추후 수정해야함..)
                         * [pointerInteropFilter를 이용하여 터치를 했을 시 VR의 Active 상태를 판별하여 애니메이션을 시작하는 코드]
                         * ==> 현재 개선해야할 부분은 VR의 Active 상태를 판별하여 VR 애니매이션을 실행하거나 종료하거나 하는데 이때 리컴포지션이 일어나 각 Domain별 Screen 화면도 재구성이 된다는 점이다.
                         * ==> 이러한 초기화 문제가 발생하여 현재 보고 있는 스크롤이 0번째 Index로 초기화되어, 화면이 최상단(0번째 인덱스)으로 스크롤되는 현상이 발생함
                         * ==> 다른것부터 하고 난 후 이것을 해결해봐야겠음..(시간이 많이 걸릴 이슈..)
                         */
//                        .pointerInteropFilter { event ->
//                            when (event.actionMasked) {
//                                MotionEvent.ACTION_DOWN -> {
//                                    Log.d("@@ ACTION_DOWN", "${event.actionMasked}")
//                                    isTouchDown = true
//                                }
//
//                                MotionEvent.AXIS_SCROLL -> {
//                                    Log.d("@@ AXIS_SCROLL", "${event.actionMasked}")
//                                    onVREvent(
//                                        VREvent.ChangeVRUIEvent(
//                                            VRUiState.PttLoading(
//                                                active = false,
//                                                isError = false
//                                            )
//                                        )
//                                    )
//                                }
//
//                                MotionEvent.ACTION_UP -> {
//                                    Log.d("@@ ACTION_UP", "${event.actionMasked}")
//                                    if (isTouchDown) {
//                                        // 손을 뗐을 때, 터치 다운 이벤트가 있었으면 실행할 코드
//                                        onVREvent(
//                                            VREvent.ChangeVRUIEvent(
//                                                VRUiState.PttLoading(
//                                                    active = false,
//                                                    isError = false
//                                                )
//                                            )
//                                        )
//                                        isTouchUp = true
//                                    }
//                                    isTouchDown = false
//                                }
//                            }
//                            true
//                        },
                ) {
                    Box(Modifier.fillMaxSize()) {
                        /**
                         * Vr 애니메이션과 content(구체적인 도메인 스크린들)과 겹쳐서 배치
                         */
                        VrUiAnimationHandler(vrUiState)
                        content() // Window에 대한 UI를 설정한 것이므로 Window 디테일 내용(구체적인 도메인 스크린들)은 content()에 추가 작성하면 됨
                    }
                }
            }
        }
    }
}
