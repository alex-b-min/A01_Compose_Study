package com.example.a01_compose_study.presentation.components.gauge

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateInt
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType.Companion.Text
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import com.google.android.material.progressindicator.LinearProgressIndicator
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch



@Composable
fun LinearDeterminateIndicator() {
    var currentProgress by remember { mutableStateOf(0f) }
    val scope = rememberCoroutineScope() // Create a coroutine scope

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Call loadProgress when the composable is first drawn
        LaunchedEffect(key1 = true) {
            loadProgress { progress ->
                currentProgress = progress
            }
        }

        LinearProgressIndicator(
            progress = currentProgress,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

suspend fun loadProgress(updateProgress: (Float) -> Unit) {
    for (i in 1..30) {
        updateProgress(i.toFloat() / 30)
        delay(100)
    }
}


@Composable
fun AnimationGaugeBar(
    value: Int,
    color: Color,
) {
    var animationPlayed by remember { // 이 값이 true면 애니메이션 실행
        mutableStateOf(false)
    }
    val currValue = animateIntAsState( // 숫자가 0부터 목표값이 될때까지 애니메이션 진행
        targetValue = if (animationPlayed) value else 0, // 0에서 value로 변함
        animationSpec = tween( // 지속시간과 지연시갖을 tween으로 묶어 전달
            durationMillis = 3000,
            delayMillis = 0
        ),
        label = ""
    )
    // 첫 compostion에서 블록이 실행되고 recomposition에서 key값이 달라졌을 때 블록 다시 실행
    // key1값이 항상 true로 변하지 않기 때문에 recompositiion에도 애니메이션은 다시 일어나지 않는다
    LaunchedEffect(key1 = true) {
        animationPlayed = true
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.LightGray)

    ){
        Row(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(currValue.value / 100f)
                .clip(CircleShape)
                .background(color = color)
                .padding(8.dp)
        ){
        }
    }
}