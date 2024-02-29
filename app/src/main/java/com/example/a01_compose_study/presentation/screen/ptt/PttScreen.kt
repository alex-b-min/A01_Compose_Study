package com.example.a01_compose_study.presentation.screen.ptt

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.a01_compose_study.R
import com.example.a01_compose_study.domain.model.ScreenType
import com.example.a01_compose_study.domain.model.SealedDomainType
import com.example.a01_compose_study.domain.util.ScreenSizeType
import com.example.a01_compose_study.presentation.components.lottie.LottieAssetAnimationHandler
import com.example.a01_compose_study.presentation.components.lottie.LottieRawAnimationHandler
import com.example.a01_compose_study.presentation.screen.main.DomainUiState
import com.example.a01_compose_study.presentation.util.TextModifier.normalize

@Composable
fun ComposePttScreen(
    domainUiState: DomainUiState.PttWindow,
    contentColor: Color,
    displayText: String,
) {
    val scope = rememberCoroutineScope()

    // 글자 투명도에 대한 상태 관리 변수(현재 사용 안함)
    var textAlpha: Float by remember { mutableStateOf(0f) }

    // 화면 크기(세로) 변경 애니메이션 상태 관리 변수(현재 사용안함)
    // ==> 화면(Window)의 세로 크기는 가장 상단에 있는 AnimatedVisibility()의 높이를 조절하기로 규칙을 정함
    //    val targetFillMaxHeight by remember { mutableStateOf(Animatable(0f)) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(shape = RoundedCornerShape(15.dp))
            .background(Color.Black)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            //error에 따른 애니메이션 재생 분기
            if (domainUiState.isError) {
                // Lottie Animation 배경 재생
                LottieAssetAnimationHandler(
                    modifier = Modifier.fillMaxSize(),
                    lottieJsonAssetPath = "bg_glow/frame_error_glow_l_lt.json",
                    lottieImageAssetFolder = "bg_glow/images/error",
                    infiniteLoop = true
                )
            } else {
                //error에 따른 애니메이션 재생 분기
                LottieAssetAnimationHandler(
                    modifier = Modifier.fillMaxSize(),
                    lottieJsonAssetPath = "bg_glow/09_tsd_frame_glow_l_lt.json",
                    lottieImageAssetFolder = "bg_glow/images/default",
                    infiniteLoop = true
                )

                //guideText의 유무를 체크해서 해서 Text를 띄움
                if (domainUiState.guideText != "") {
                    Text(
                        text = domainUiState.guideText,
                        modifier = Modifier.align(Alignment.Center),
                        color = contentColor
                    )
                }

                when(domainUiState.screenType) {
                    is ScreenType.PttListen -> {
                        LottieRawAnimationHandler(
                            modifier = Modifier.fillMaxSize(),
                            rawResId = R.raw.tsd_listening_passive_loop_lt_01_2,
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
                    is ScreenType.PttLoading -> {
                        LottieRawAnimationHandler(
                            modifier = Modifier.fillMaxSize(),
                            rawResId = R.raw.tsd_thinking_loop_fix_lt_03_2,
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
                    is ScreenType.PttSpeak -> {
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
                    else -> {
                    }
                }
            }
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (!(domainUiState.isError)) displayText else domainUiState.errorText,
                modifier = Modifier
//                    .alpha(textAlpha)
                    .padding(bottom = 10.dp),
                color = if (domainUiState.isError) Color.Red else contentColor,
                fontSize = 25.sp
//                when (targetFillMaxHeight.value) {
//                    0.2f -> 15.sp
//                    0.4f -> 20.sp
//                    else -> 25.sp
//                }
            )
        }
    }
}

@Preview(device = Devices.TABLET)
@Composable
fun CustomSizeAlertDialogPreview() {
    ComposePttScreen(
        domainUiState = DomainUiState.PttWindow(
            domainType = SealedDomainType.Ptt,
            errorText = "string",
            screenType = ScreenType.PttSpeak,
            screenSizeType = ScreenSizeType.Middle
        ),
        contentColor = Color.Magenta,
        displayText = "PTT"
    )
}