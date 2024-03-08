package com.example.a01_compose_study.presentation.components.vr

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.a01_compose_study.R
import com.example.a01_compose_study.presentation.components.lottie.LottieAssetAnimationHandler
import com.example.a01_compose_study.presentation.components.lottie.LottieRawAnimationHandler
import com.example.a01_compose_study.presentation.screen.main.route.VRUiState

/**
 * VR의 상태가 바껴 애니메이션이 바뀌면 겹쳐진 도메인 스크린들도 같이 리컴포지션이 되는 이슈가 있음.. 추후 수정해야함..
 */
@Composable
fun VrUiAnimationHandler(vrUiState: VRUiState) {
    var rawResId by remember { mutableStateOf(0) }

    LaunchedEffect(vrUiState) {
        Log.d("@@ VrUiAnimationHandler", "${vrUiState}")
        when {
            vrUiState.active && vrUiState.isError -> {
                rawResId = 0 // 초기화
            }

            vrUiState.active -> {
                rawResId = when (vrUiState) {
                    is VRUiState.PttLoading -> R.raw.tsd_thinking_loop_fix_lt_03_2
                    is VRUiState.PttListen -> R.raw.tsd_listening_passive_loop_lt_01_2
                    is VRUiState.PttSpeak -> R.raw.loop
                    else -> 0 // 다른 상태에 대한 처리
                }
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter,
    ) {
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
                Log.d(
                    "@@ vrUiState.active??",
                    "active: ${vrUiState.active}/ isError: ${vrUiState.isError}"
                )
                LottieAssetAnimationHandler(
                    modifier = Modifier.fillMaxSize(),
                    lottieJsonAssetPath = "bg_glow/09_tsd_frame_glow_l_lt.json",
                    lottieImageAssetFolder = "bg_glow/images/default",
                    infiniteLoop = true
                )
                if (rawResId != 0) {
                    Log.d("@@ rawResId", "rawResId : ${rawResId}")
                    Log.d(
                        "@@ rawResId(PttListen)",
                        "rawResId : ${R.raw.tsd_listening_passive_loop_lt_01_2}"
                    )
                    LottieRawAnimationHandler(
                        modifier = Modifier.fillMaxSize(),
                        rawResId = rawResId,
                        infiniteLoop = true,
                        onFrameChanged = { currentFrame ->
                            // 필요한 경우 처리
                        }
                    )
                }
            }
        }
    }
}