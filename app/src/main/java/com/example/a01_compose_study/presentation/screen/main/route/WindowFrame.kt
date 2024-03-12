package com.example.a01_compose_study.presentation.screen.main.route

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.a01_compose_study.domain.util.ScreenSizeType
import com.example.a01_compose_study.presentation.compose.VrUiAnimationHandler
import com.example.a01_compose_study.presentation.data.UiState.vrUiState
import com.example.a01_compose_study.presentation.screen.main.DomainUiState
import kotlinx.coroutines.launch

@Composable
fun WindowFrame(
    domainUiState: DomainUiState,
    domainWindowVisible: Boolean,
    onCloseDomainWindow: () -> Unit,
    content: @Composable () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val targetFillMaxHeight by remember { mutableStateOf(Animatable(0f)) }
    val targetFillMaxWidth by remember { mutableStateOf(Animatable(0f)) }

    LaunchedEffect(domainUiState) {
        val newTargetHeightValue = when (domainUiState.screenSizeType) {
            is ScreenSizeType.Zero -> 0f
            is ScreenSizeType.Small -> 0.15f
            is ScreenSizeType.Middle -> 0.268f
            is ScreenSizeType.Large -> 0.433f
            is ScreenSizeType.Full -> 1f
        }
        val newTargetWidthValue = when (domainUiState.screenSizeType) {
            is ScreenSizeType.Zero -> 0f
            is ScreenSizeType.Full -> 1f
            else -> 0.233f
        }

        scope.launch {
            targetFillMaxHeight.animateTo(
                targetValue = newTargetHeightValue,
                animationSpec = tween(
                    durationMillis = 800,
                    easing = FastOutSlowInEasing
                )
            )
        }
        scope.launch {
            targetFillMaxWidth.animateTo(
                targetValue = newTargetWidthValue,
                animationSpec = tween(
                    durationMillis = 800,
                    easing = FastOutSlowInEasing
                )
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { onCloseDomainWindow() }
            )
    ) {
        AnimatedVisibility(
            visible = domainWindowVisible,
            modifier = Modifier.fillMaxWidth(),
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(1000)
            ),
            exit = slideOutVertically(
                targetOffsetY = { it },
                animationSpec = tween(1000)
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomStart
            ) {
                Box(
                    modifier = Modifier
                        .offset(x = 10.dp, y = (10).dp)
                        .fillMaxHeight(targetFillMaxHeight.value)
                        .fillMaxWidth(targetFillMaxWidth.value)
                        .background(
                            color = Color.Transparent,
                            shape = RoundedCornerShape(15.dp)
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                        ) {},
                ) {
                    content() // Window에 대한 UI를 설정한 것이므로 Window 디테일 내용은 content()에 추가 작성하면 됨
                }
            }
        }
    }
}
