package com.ftd.ivi.cerence.ui.compose

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.text
import androidx.compose.ui.text.AnnotatedString
import com.ftd.ivi.cerence.data.UxPreset
import com.ftd.ivi.cerence.util.CustomLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AnimationRow(
    selected: Boolean = false,
    aniCancel: Boolean = false,
    clickListener: () -> Unit,
    clickable: Boolean = false,
    index: Int = -1,
    preClick: (() -> Unit)?,
    name: String = "",
    content: @Composable () -> Unit
) {
    var animationTarget by remember { mutableFloatStateOf(1f) }
    val progressAnimDuration = UxPreset.animationDuration
    var resetAnimation by remember { mutableStateOf(false) }
    var isStarted by remember { mutableStateOf(false) }
    val progressAnimation = animateResettableFloat(initialValue = 0f,
        targetValue = animationTarget,
        resetFlag = resetAnimation,
        animationSpec = tween(
            durationMillis = progressAnimDuration, easing = LinearEasing
        ),
        finishedListener = {
            CustomLogger.e("proc finishedListener:${animationTarget}")
            clickListener.invoke()

            // 화면전환 시점에 애니메이션이 종료되어 흰색바가 없어지기 때문에 딜레이 주었는데 필요에 따라 제거
            CoroutineScope(Dispatchers.IO).launch {
                delay(50)
                resetAnimation = true
            }
        })
//    val currAniRow = aniFlow?.currAnimationRow?.collectAsState()
//    LaunchedEffect(currAniRow?.value){
//        CustomLogger.e("currAniRow Changed:${currAniRow?.value} this:${index}")
//        if(currAniRow?.value != index)
//        {
//            resetAnimation = true
//        }
//    }


    LaunchedEffect(aniCancel) {
        CustomLogger.e("currAniRow aniCancel:${aniCancel} this:${index} Started:${!resetAnimation}")
        if (aniCancel) {
            resetAnimation = true
        }
    }

    LaunchedEffect(selected) {
        //CustomLogger.e("proc selected:${selected}")
        if (selected) {
            resetAnimation = false
            animationTarget = 1f
            isStarted = true
        } else {
            resetAnimation = true
        }
    }
    Box(modifier = Modifier
        .fillMaxWidth()
        .height(IntrinsicSize.Min)
        .semantics { text = AnnotatedString(name) }) {
        content.invoke()
        if (!resetAnimation) {
            LinearProgressIndicator(
                modifier = Modifier
                    .matchParentSize()
                    .alpha(0.18f),
                trackColor = Color.Transparent,
                color = Color.White, //progress color,
                progress = progressAnimation
            )
        }
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable(
                    // no default ripple effect
                    interactionSource = MutableInteractionSource(), indication = null, onClick = {
                        preClick?.invoke()
                        resetAnimation = false
                        animationTarget = 1f
                        isStarted = true
                    }, enabled = clickable
                )
        )

    }


}