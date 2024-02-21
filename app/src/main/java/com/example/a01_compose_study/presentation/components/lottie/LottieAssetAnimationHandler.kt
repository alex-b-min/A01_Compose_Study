package com.example.a01_compose_study.presentation.components.lottie

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieClipSpec
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieAnimatable
import com.airbnb.lottie.compose.rememberLottieComposition

@Composable
fun LottieAssetAnimationHandler(
    modifier: Modifier,
    lottieJsonAssetPath: String,
    lottieImageAssetFolder: String,
    infiniteLoop: Boolean = true
) {
    // 의도한 타이밍에 실행시키거나, 원하는 지점으로 이동시켜 실행하기 위해 사용되는 방식 ==> 구체적인 재생을 위해서 사용
    val lottieAnimatable = rememberLottieAnimatable()

    val listeningGlowPath = remember { mutableStateOf(lottieJsonAssetPath) }

    val glow by rememberLottieComposition(
        LottieCompositionSpec.Asset(listeningGlowPath.value),
        imageAssetsFolder = lottieImageAssetFolder
    )

    Log.d("@@ 순서", "22222")

//    val errorGlow by rememberLottieComposition(
//        LottieCompositionSpec.Asset(errGlowPath.value),
//        imageAssetsFolder = "bg_glow/images/error"
//    )

    // LaunchedEffect의 key가 composition인 이유는 composition이 제대로 설정된 후에 디테일한 애니메이션 설정을 하는 animate()를 정상적으로 설정할 수 있기 때문이다.
    LaunchedEffect(glow) {
        /**
         * 디테일 하게 애니메이션 설정을 할 수 있게 끔 도와주는 rememberLottieAnimatable()
         * 디테일 하게 설정 가능한 것은 아래와 같음
         */
        if (infiniteLoop) {
            lottieAnimatable.animate(
                composition = glow,
                clipSpec = LottieClipSpec.Frame(0, 80),
                initialProgress = 0f,
                iterations = LottieConstants.IterateForever
            )
        } else {
            lottieAnimatable.animate(
                composition = glow,
                clipSpec = LottieClipSpec.Frame(0, 80),
                initialProgress = 0f
            )
        }
    }
    LottieAnimation(
        composition = glow,
        progress = lottieAnimatable.progress,
        alignment = Alignment.TopStart,
        contentScale = ContentScale.None,
        modifier = modifier
    )
}
