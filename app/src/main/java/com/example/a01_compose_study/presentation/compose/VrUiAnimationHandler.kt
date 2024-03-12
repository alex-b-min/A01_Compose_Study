package com.example.a01_compose_study.presentation.compose

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.a01_compose_study.R
import com.example.a01_compose_study.presentation.components.lottie.LottieAssetAnimationHandler
import com.example.a01_compose_study.presentation.components.lottie.LottieRawAnimationHandler
import com.example.a01_compose_study.presentation.screen.main.route.VRUiState

@Composable
fun VrUiAnimationHandler(vrUiState: VRUiState) {
    when {
        vrUiState.active && vrUiState.isError -> {
            LottieAssetAnimationHandler(
                modifier = Modifier.fillMaxSize(),
                lottieJsonAssetPath = "bg_glow/frame_error_glow_l_lt.json",
                lottieImageAssetFolder = "bg_glow/images/error",
                infiniteLoop = true
            )
        }

        vrUiState.active -> {
            LottieAssetAnimationHandler(
                modifier = Modifier.fillMaxSize(),
                lottieJsonAssetPath = "bg_glow/09_tsd_frame_glow_l_lt.json",
                lottieImageAssetFolder = "bg_glow/images/default",
                infiniteLoop = true
            )
            when (vrUiState) {
                is VRUiState.PttLoading -> {
                    LottieRawAnimationHandler(
                        modifier = Modifier.fillMaxSize(),
                        rawResId = R.raw.tsd_thinking_loop_fix_lt_03_2,
                        infiniteLoop = true,
                        onFrameChanged = { currentFrame ->
                            // 필요한 경우 처리
                        }
                    )
                }

                is VRUiState.PttListen -> {
                    LottieRawAnimationHandler(
                        modifier = Modifier
                            .fillMaxSize(),
                        rawResId = R.raw.tsd_listening_passive_loop_lt_01_2,
                        infiniteLoop = true,
                        onFrameChanged = { currentFrame ->
                            // 필요한 경우 처리
                        }
                    )
                }

                is VRUiState.PttSpeak -> {
                    LottieRawAnimationHandler(
                        modifier = Modifier.fillMaxSize(),
                        rawResId = R.raw.loop,
                        infiniteLoop = true,
                        onFrameChanged = { currentFrame ->
                            // 필요한 경우 처리
                        }
                    )
                }

                else -> {
                    // 다른 상태에 대한 처리
                }
            }
        }
    }
}

//@Composable
//fun Modifier.detectTapAndPressGestures(
//    isVrActive: Boolean,
//): Modifier = composed {
//    this.then(
//        Modifier.pointerInput(Unit) {
//            detectTapGestures(
//                onPress = {
//                    isVrActive = false
//                },
//                onTap = {
//                    isVrActive = true
//                }
//            )
//        }
//    )
//}