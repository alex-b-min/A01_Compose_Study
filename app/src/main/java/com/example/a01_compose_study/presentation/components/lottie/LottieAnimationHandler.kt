package com.example.a01_compose_study.presentation.components.lottie

import androidx.annotation.RawRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.layout.ContentScale
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieClipSpec
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieAnimatable
import com.airbnb.lottie.compose.rememberLottieComposition

@Composable
fun LottieAnimationHandler(
    @RawRes rawResId: Int,
    infiniteLoop: Boolean = true,
    onFrameChanged: (Float) -> Unit,
) {
    // Lottie 애니메이션 실행의 매개체라고 생각하면 쉽다.
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(rawResId))

    // Progress(진행도)를 직접 구해 사용하는 방식 ==> 재생시키기만을 위해서라면 이것을 사용해도 무방(간단)
//    val progress by animateLottieCompositionAsState(composition = composition)

    // 의도한 타이밍에 실행시키거나, 원하는 지점으로 이동시켜 실행하기 위해 사용되는 방식 ==> 구체적인 재생을 위해서 사용
    val lottieAnimatable = rememberLottieAnimatable()

    // LaunchedEffect의 key가 composition인 이유는 composition이 제대로 설정된 후에 디테일한 애니메이션 설정을 하는 animate()를 정상적으로 설정할 수 있기 때문이다.
    LaunchedEffect(composition) {
        /**
         * 디테일 하게 애니메이션 설정을 할 수 있게 끔 도와주는 rememberLottieAnimatable()
         * 디테일 하게 설정 가능한 것은 아래와 같음
         */
        if (infiniteLoop) {
            lottieAnimatable.animate(
                composition = composition,
                clipSpec = LottieClipSpec.Frame(0, 80),
                initialProgress = 0f,
                iterations = LottieConstants.IterateForever
            )
        } else {
            lottieAnimatable.animate(
                composition = composition,
                clipSpec = LottieClipSpec.Frame(0, 80),
                initialProgress = 0f
            )
        }
    }
// 현재 실행되고 있는 애니메이션의 프레임 값을 알 수 있음, 참고로 animateLottieCompositionAsState()의 값이나 rememberLottieAnimatable()의 progress의 값만을 허용함.
    val currentFrame = composition?.getFrameForProgress(lottieAnimatable.progress)
    if (currentFrame != null) {
        onFrameChanged(currentFrame)
    }

    /**
     * 애니메이션을 디테일 하게 재생
     * progress에 rememberLottieAnimatable().animate()를 통해 디테일한 설정을 한 것을 재생함
     * [참고] 아래의 LottieAnimation()은 애니메이션 재생을 시키는 코드이고 원하는 타이밍에 애니메이션을 Skip을 하고 싶다면 아래의 코드를 이용하면 된다.
     * [EX] lottieAnimatable.snapTo(compositon, 1f)
     */
    LottieAnimation(
        composition = composition,
        progress = lottieAnimatable.progress,
        contentScale = ContentScale.FillHeight
    )

    /**
     * 애니메이션 한번 재생
     * progress 설정에 animateLottieCompositionAsState() 값을 이용하여 설정
     */
//    LottieAnimation(
//        composition = composition,
//        contentScale = ContentScale.FillHeight,
//        progress = progress
//    )

    /**
     * 애니메이션 간단하게 무한 반복
     * ==> progress 설정 없이 iterations 매개변수에 LottieConstants.IterateForever를 설정하여 무한반복 시킴
     */
//    LottieAnimation(
//        composition = composition,
//        contentScale = ContentScale.FillHeight,
//        iterations = LottieConstants.IterateForever
//    )
}
