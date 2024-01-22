package com.example.a01_compose_study.presentation.main.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.a01_compose_study.R
import com.example.a01_compose_study.domain.util.ScreenSizeType
import com.example.a01_compose_study.presentation.lottie.LottieAnimationHandler
import com.example.a01_compose_study.presentation.main.MainUiState
import com.example.a01_compose_study.presentation.util.TextModifier.normalize
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun CustomSizeAlertDialog(
    uiState: MainUiState.TwoScreen,
    contentColor: Color,
    onDismiss: () -> Unit,
) {
    var visible by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    var textAlpha: Float by remember { mutableStateOf(0f) }

    // 화면 크기(세로) 변경 애니메이션 상태 관리 변수
    var targetFillMaxHeight by remember { mutableStateOf(Animatable(0f)) }

    //화면이 띄워질 때 Unit을 통해 한번만 실행 되도록 하는 작업을 아래와 같이 작성함
    LaunchedEffect(Unit) {
        // visible의 값을 false -> true 변경
        visible = true

        // uiState로부터의 screenSizeType을 얻어 해당 화면 크기 설정
        targetFillMaxHeight = when (uiState.screenSizeType) {
            is ScreenSizeType.Small -> Animatable(0.2f)
            is ScreenSizeType.Middle -> Animatable(0.4f)
            is ScreenSizeType.Large -> Animatable(0.6f)
        }
    }

    AnimatedVisibility(
        visible = visible,
        modifier = Modifier.fillMaxWidth(),
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it })
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomStart
        ) {
            Column(
                modifier = Modifier
                    .offset(x = 10.dp, y = (-90).dp)
                    .fillMaxHeight(targetFillMaxHeight.value)
                    .fillMaxWidth(0.3f)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF052005),
                                Color(0xFF005e53)
                            ),
                            start = Offset.Zero,
                            end = Offset.Infinite
                        ),
                        shape = RoundedCornerShape(15.dp),
                        alpha = 0.9f
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
                                tint = contentColor
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
                                    0.2f -> 0.4f
                                    0.4f -> 0.6f
                                    else -> targetFillMaxHeight.value
                                }
                                targetFillMaxHeight.animateTo(newTargetValue)
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowUp,
                                contentDescription = null,
                                tint = contentColor
                            )
                        }
                        IconButton(onClick = {
                            scope.launch {
                                val newTargetValue = when (targetFillMaxHeight.value) {
                                    0.4f -> 0.2f
                                    0.6f -> 0.4f
                                    else -> targetFillMaxHeight.value
                                }
                                targetFillMaxHeight.animateTo(newTargetValue)
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = contentColor
                            )
                        }
                    }
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        //Lottie Animation 재생
                        LottieAnimationHandler(
                            rawResId = R.raw.loop,
                            infiniteLoop = false,
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
                            })
                    }
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = uiState.text,
                            modifier = Modifier
                                .alpha(textAlpha)
                                .padding(bottom = 10.dp),
                            color = contentColor,
                            fontSize = when (targetFillMaxHeight.value) {
                                0.2f -> 25.sp
                                0.4f -> 45.sp
                                else -> 63.sp
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
        uiState = MainUiState.TwoScreen(
            text = "string",
            screenSizeType = ScreenSizeType.Middle
        ),
        contentColor = Color.Magenta,
        onDismiss = {
        })
}