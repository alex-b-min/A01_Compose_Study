package com.example.a01_compose_study.presentation.screen.ptt

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.a01_compose_study.R
import com.example.a01_compose_study.domain.model.ScreenType
import com.example.a01_compose_study.presentation.components.lottie.LottieAssetAnimationHandler
import com.example.a01_compose_study.presentation.components.lottie.LottieRawAnimationHandler
import com.example.a01_compose_study.presentation.data.UiState.sealedParsedData
import com.example.a01_compose_study.presentation.screen.main.DomainUiState
import com.example.a01_compose_study.presentation.util.TextModifier.normalize
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ComposePttScreen(
    domainUiState: DomainUiState.PttWindow,
    contentColor: Color,
) {
    // 애니메이션을 제어하기 위한 visible 변수, 애니메이션 효과가 없다면 uiState의 visible값을 바로 사용해도 무방하다.
//    var visible by remember { mutableStateOf(domainUiState.visible.not()) }
    val scope = rememberCoroutineScope()

    // 글자 투명도에 대한 상태 관리 변수
    var textAlpha: Float by remember { mutableStateOf(0f) }

    // 화면 크기(세로) 변경 애니메이션 상태 관리 변수
    val targetFillMaxHeight by remember { mutableStateOf(Animatable(0f)) }


    /**
     * [vrUiState의 visible] - VRWindow 화면을 제어하는 주요 데이터
     * [remember타입의 내부 visible] - vrUiState의 visible 통해 값이 제어되며 이 값을 통해 애니메이션 제어를 활용
     *
     * [참고] 애니메이션 효과를 보기 위해서 false -> true / true -> false 로 전환이 되어야 애니메이션 효과가 적용 된다.
     * vrUiState의 visible 값이 true 일 때는 닫는 애니메이션을 위해 remember 타입의 visible = false 로 설정 / 반대로 false일 때는 띄우는 애니메이션을 위해 remember 타입의 visible = true 로 설정
     * 그렇기에  remember 타입의 visible의 값을 결정짓는 외부 데이터 uiState를 업데이트 시킬때 반대로 업데이트를 해야한다.
     *  ==> 예를 들어 열 때는 vrUiState의 visible을 false로 업데이트 하고 닫을때는 vrUiState의 visible을 true로 업데이트 한다.
     */
//    if (domainUiState.visible) { // uiState.visible가 true 일 때는 remember 타입의 visible을 false에서 true로 바꿔 띄우는 애니메이션 효과를 준다.
//        //screenSizeType이 변할때 마다 현재 사이즈 타입에 따라 적절한 크기를 검사하고 그 크기로 애니메이션화하여 변화시킨다.
//        LaunchedEffect(domainUiState.screenSizeType) {
////            visible = true
//
//            // uiState로부터의 screenSizeType을 얻어 해당 화면 크기 설정
//            val newTargetValue = when (domainUiState.screenSizeType) {
//                is ScreenSizeType.Zero -> 0f
//                is ScreenSizeType.Small -> 0.15f
//                is ScreenSizeType.Middle -> 0.268f
//                is ScreenSizeType.Large -> 0.433f
//            }
//
//            Log.d("@@ newTargetValue", "${newTargetValue}")
//            Log.d(
//                "@@ ScreenSizeType",
//                "${domainUiState.screenType} / ${domainUiState.screenSizeType}"
//            )
//            targetFillMaxHeight.animateTo(newTargetValue)
//        }
//    }
    if (domainUiState.screenType is ScreenType.PttAnounce) {
        AnnounceView("Help")

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.End
        ) {
            IconButton(onClick = {
                scope.launch {
                    delay(500)
                }
            }) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    tint = if (domainUiState.isError) Color.Red else contentColor
                )
            }

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomCenter
            ) {
                //error에 따른 애니메이션 재생 분기
                if (domainUiState.isError) {
                    // Lottie Animation 배경 재생
                    LottieAssetAnimationHandler(
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
                } else if (domainUiState.screenType is ScreenType.PttLoading) {
                    LottieRawAnimationHandler(
                        modifier = Modifier.fillMaxSize(),
                        rawResId = R.raw.tsd_error_loop_lt_00_2,
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


                } else if (domainUiState.screenType is ScreenType.PttSpeak) {
                    // Lottie Animation 배경 재생
//                        Log.d("@@ 에러 아님", "${domainUiState.visible}")
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
        }

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = domainUiState.text,
                    modifier = Modifier
                        .padding(bottom = 10.dp),
                    color = if (domainUiState.isError) Color.Red else contentColor,
                    fontSize = when (targetFillMaxHeight.value) {
                        0.2f -> 15.sp
                        0.4f -> 20.sp
                        else -> 25.sp
                    }
                )
            }
        }
    }
}

@Composable
fun AnnounceView(text: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(10.dp))
            .background(Color.Black),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.h3,
            color = Color.White
        )
    }
}

//@Preview(device = Devices.TABLET)
//@Composable
//fun CustomSizeAlertDialogPreview() {
//    PttScreen(
//        domainUiState = DomainUiState.PttWindow(
//            visible = true,
//            text = "string",
//            screenSizeType = ScreenSizeType.Middle
//        ),
//        contentColor = Color.Magenta,
//}