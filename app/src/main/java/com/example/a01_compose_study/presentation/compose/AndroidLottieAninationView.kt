package com.ftd.ivi.cerence.ui.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.compose.LottieAnimationState
import com.airbnb.lottie.compose.LottieCompositionSpec

@Composable
fun AndroidLottieAnimationView(
    modifier: Modifier = Modifier,
    spec: LottieCompositionSpec.RawRes,
    animationState: LottieAnimationState
) {
    AndroidView(
        modifier = modifier,
        factory = { context -> LottieAnimationView(context) },
        update = { view ->
            view.apply {
                setAnimation(spec.resId)
                progress = animationState.progress
                //frame = animationState.frame
                if (animationState.isPlaying) playAnimation()
            }
        }
    )
}