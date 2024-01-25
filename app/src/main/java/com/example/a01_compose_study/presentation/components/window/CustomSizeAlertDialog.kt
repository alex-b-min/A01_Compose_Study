package com.example.a01_compose_study.presentation.components.window

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.a01_compose_study.R
import com.example.a01_compose_study.domain.util.ScreenSizeType
import com.example.a01_compose_study.presentation.components.lottie.LottieAssetAnimationHandler
import com.example.a01_compose_study.presentation.components.lottie.LottieRawAnimationHandler
import com.example.a01_compose_study.presentation.main.MainUiState
import com.example.a01_compose_study.presentation.util.TextModifier.normalize
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun CustomSizeAlertDialog(
    uiState: MainUiState.HelpWindow,
    contentColor: Color,
    onDismiss: () -> Unit,
) {
    // 애니메이션을 제어하기 위한 visible 변수, 애니메이션 효과가 없다면 uiState의 visible값을 바로 사용해도 무방하다.
    var visible by remember { mutableStateOf(uiState.visible.not()) }
    val scope = rememberCoroutineScope()

    // 글자 투명도에 대한 상태 관리 변수
    var textAlpha: Float by remember { mutableStateOf(0f) }

    // 화면 크기(세로) 변경 애니메이션 상태 관리 변수
    var targetFillMaxHeight by remember { mutableStateOf(Animatable(0f)) }

    /**
     * uiState의 visible 값이 true 일 때는 닫는 애니메이션을 위해 remember 타입의 visible = false 로 설정 / 반대로 false일 때는 띄우는 애니메이션을 위해 remember 타입의 visible = true 로 설정
     * 그렇기에  remember 타입의 visible의 값을 결정짓는 외부 데이터 uiState를 업데이트 시킬때 반대로 업데이트를 해야한다.
     *  ==> 예를 들어 열 때는 uiState의 visible을 false로 업데이트 하고 닫을때는  uiState의 visible을 true로 업데이트 한다.
     * [참고] 위에서 말하는 visible은 화면 전체 UI를 결정 짓는 uiState의 visible / 화면 내부에서 visible을 조절하는 remember 타입의 visile 두 가지가 있고 각각에 대한 설명임
     */

    if (uiState.visible) { // uiState.visible가 true 일 때는 remember 타입의 visible을 false에서 true로 바꿔 띄우는 애니메이션 효과를 준다.
        LaunchedEffect(Unit) {
            visible = true

            // uiState로부터의 screenSizeType을 얻어 해당 화면 크기 설정
            targetFillMaxHeight = when (uiState.screenSizeType) {
                is ScreenSizeType.Small -> Animatable(0.15f)
                is ScreenSizeType.Middle -> Animatable(0.268f)
                is ScreenSizeType.Large -> Animatable(0.433f)
            }
        }
    } else { // uiState.visible가 false 일 때는 remember 타입의 visible을 true에서 false로 바꿔 닫는 애니메이션 효과를 준다.
        LaunchedEffect(Unit) {
            visible = false
            delay(500)
            onDismiss()
        }
    }

    AnimatedVisibility(
        visible = visible,
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
            Column(
                modifier = Modifier
                    .offset(x = 10.dp, y = (10).dp)
                    .fillMaxHeight(targetFillMaxHeight.value)
                    .fillMaxWidth(0.233f)
//                    .width(450.dp)
//                    .height(230.dp)
                    .background(
                        color = Color.Transparent,
                        shape = RoundedCornerShape(15.dp)
                    ),
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.End
                    ) {
                        IconButton(onClick = {
                            scope.launch {
                                visible = false
                                delay(500)
                                onDismiss()
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = null,
                                tint = if (uiState.isError) Color.Red else contentColor
                            )
                        }
                    }
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.Center
                    ) {
                        IconButton(onClick = {
                            scope.launch {
                                val newTargetValue = when (targetFillMaxHeight.value) {
                                    0.15f -> 0.268f
                                    0.268f -> 0.433f
                                    else -> targetFillMaxHeight.value
                                }
                                targetFillMaxHeight.animateTo(newTargetValue)
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowUp,
                                contentDescription = null,
                                tint = if (uiState.isError) Color.Red else contentColor
                            )
                        }
                        IconButton(onClick = {
                            scope.launch {
                                val newTargetValue = when (targetFillMaxHeight.value) {
                                    0.268f -> 0.15f
                                    0.433f -> 0.268f
                                    else -> targetFillMaxHeight.value
                                }
                                targetFillMaxHeight.animateTo(newTargetValue)
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = if (uiState.isError) Color.Red else contentColor
                            )
                        }
                    }
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        //error에 따른 애니메이션 재생 분기
                        if (uiState.isError) {
                            // Lottie Animation 배경 재생
                            LottieAssetAnimationHandler(
                                modifier = Modifier.fillMaxSize(),
                                lottieJsonAssetPath = "bg_glow/frame_error_glow_l_lt.json",
                                lottieImageAssetFolder = "bg_glow/images/error",
                                infiniteLoop = true
                            )
                            LottieRawAnimationHandler(
                                modifier = Modifier.fillMaxSize(),
                                rawResId = R.raw.error_loop,
                                infiniteLoop = true,
                                onFrameChanged = { currentFrame ->
                                    // 현재 프레임에 따라 글자 투명도(Alpha)가 변하도록 설정
                                    textAlpha = currentFrame.let {
                                        when (it) {
                                            in 0F..10F -> 0F
                                            in 10F..20F -> it.normalize(10F, 20F)
                                            in 20F..40F -> 1F - (it.normalize(20F, 40F) * 0.5F)
                                            else -> 0F
                                        }
                                    } ?: 0F
                                }
                            )
                        } else {
                            // Lottie Animation 배경 재생
                            LottieAssetAnimationHandler(
                                modifier = Modifier.fillMaxSize(),
                                lottieJsonAssetPath = "bg_glow/09_tsd_frame_glow_l_lt.json",
                                lottieImageAssetFolder = "bg_glow/images/default",
                                infiniteLoop = true
                            )
                            LottieRawAnimationHandler(
                                modifier = Modifier.fillMaxSize(),
                                rawResId = R.raw.loop,
                                infiniteLoop = true,
                                onFrameChanged = { currentFrame ->
                                    // 현재 프레임에 따라 글자 투명도(Alpha)가 변하도록 설정
                                    textAlpha = currentFrame.let {
                                        when (it) {
                                            in 0F..10F -> 0F
                                            in 10F..20F -> it.normalize(10F, 20F)
                                            in 20F..40F -> 1F - (it.normalize(20F, 40F) * 0.5F)
                                            else -> 0F
                                        }
                                    } ?: 0F
                                }
                            )
                        }
                    }

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (uiState.isError) "Error" else uiState.text,
                            modifier = Modifier
                                .alpha(textAlpha)
                                .padding(bottom = 10.dp),
                            color = if (uiState.isError) Color.Red else contentColor,
                            fontSize = when (targetFillMaxHeight.value) {
                                0.2f -> 15.sp
                                0.4f -> 25.sp
                                else -> 40.sp
                            }
                        )
                    }
                }
            }
        }
    }
}

@Preview(device = Devices.TABLET)
@Composable
fun CustomSizeAlertDialogPreview() {
    CustomSizeAlertDialog(
        uiState = MainUiState.HelpWindow(
            visible = true,
            text = "string",
            screenSizeType = ScreenSizeType.Middle
        ),
        contentColor = Color.Magenta,
        onDismiss = {
        })
}