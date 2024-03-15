package com.example.a01_compose_study.presentation.components

import android.util.Log
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import com.example.a01_compose_study.presentation.data.UiState


fun Modifier.clickableWithTapGestures(
    onClick: () -> Unit = {},
): Modifier {
    return this.pointerInput(Unit) {
        detectTapGestures(
            onPress = {
                UiState.isVrActive.value = false
                Log.d("isVrActive", "isVrActive.value: ${UiState.isVrActive}")
                onClick()
            },
            onTap = {
                UiState.isVrActive.value = true
                Log.d("isVrActive", "isVrActive.value: ${UiState.isVrActive}")
            },
            onLongPress = {
                UiState.isVrActive.value = true
            }
        )
    }
}