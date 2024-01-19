package com.example.a01_compose_study.presentation.main.component

import android.annotation.SuppressLint
import android.util.Log
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieClipSpec
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieAnimatable
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.a01_compose_study.R
import com.example.a01_compose_study.domain.util.ScreenSizeType
import com.example.a01_compose_study.presentation.main.MainUiState
import com.example.a01_compose_study.presentation.util.TextModifier.normalize
import kotlinx.coroutines.launch


@SuppressLint("UnrememberedMutableState", "UnrememberedAnimatable")
@Composable
fun CustomSizeAlertDialog(
    uiState: MainUiState.TwoScreen,
    contentColor: Color,
    onDismiss: () -> Unit,
) {
    val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.loop))
    val scope = rememberCoroutineScope()

    val progress by animateLottieCompositionAsState(composition = composition) // Progress(진행도)를 직접 구해 사용하는 방식 ==> 재생시키기만을 위해서라면 이것을 사용해도 무방
    val lottieAnimatable =
        rememberLottieAnimatable() // 의도한 타이밍에 실행시키거나, 원하는 지점으로 이동시켜 실행하기 위해 사용되는 방식 ==> 구체적인 재생을 위해서 사용
    val currentFrame =
        composition?.getFrameForProgress(progress) // 현재 실행되고 있는 애니메이션의 프레임 값을 알 수 있음, 참고로 rememberLottieAnimatable을 통해서만 현재 프레임을 얻어올 수 있음.
    val textAlpha = currentFrame?.let { //현재 프레임에 따라 글자 투명도(Alpha)가 변하도록 설정한 변수
        when (it) {
            in 0F..10F -> 0F
            in 10F..20F -> it.normalize(10F, 20F)
            in 20F..40F -> 1F - (it.normalize(20F, 40F) * 0.5F)
            else -> 0F
        }
    } ?: 0F

    var targetFillMaxHeight by remember { mutableStateOf(Animatable(0f)) }

    // isVisible의 값에 따라 일회성으로 한번 호출하여 사용하기 위해 LaunchedEffect를 사용
    LaunchedEffect(uiState.isVisible) {
        //uiState로부터의 screenSizeType을 얻어 해당 화면 크기 설정
        targetFillMaxHeight = when (uiState.screenSizeType) {
            is ScreenSizeType.Small -> Animatable(0.2f)
            is ScreenSizeType.Middle -> Animatable(0.4f)
            is ScreenSizeType.Large -> Animatable(0.6f)
        }
        /**
         * Frame 값을 알 수 있도록 실행되는 애니메이션 설정(LottieAnimatable)
         */
        lottieAnimatable.animate(
            composition = composition, // 어떤 애니메이션을 실행할 지 결정
            clipSpec = LottieClipSpec.Frame(0, 40), // Lottie 애니메이션의 재생 범위 정의(Frame 단위)
            initialProgress = 0f, // 어디서 부터 시작할 지 초기 진행도 설정
            iterations = LottieConstants.IterateForever, // 몇 번 반복 할 지를 설정
        )
    }

    AnimatedVisibility(
        visible = uiState.isVisible,
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
                            onDismiss()
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
                        /**
                         * 애니메이션 한번 재생
                         */
                        LottieAnimation(
                            composition = composition,
                            contentScale = ContentScale.FillHeight,
                            progress = progress
                        )
                        /**
                         * 애니메이션 프레임 값 얻을 수 있음
                         */
//                        LottieAnimation(
//                            composition = composition,
//                            progress = lottieAnimatable.progress,
//                            contentScale = ContentScale.Crop
//                        )
                        /**
                         * 애니메이션 무한 반복
                         */
//                        LottieAnimation(
//                            composition = composition,
//                            contentScale = ContentScale.FillHeight,
//                            iterations = LottieConstants.IterateForever
//                        )
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
                            fontSize = 30.sp
                        )
                    }
                }
            }
        }
    }
}