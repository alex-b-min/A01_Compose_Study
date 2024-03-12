package com.example.a01_compose_study.presentation.components

import android.util.Log
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import com.example.a01_compose_study.presentation.data.UiState


fun Modifier.clickableWithTapGestures(
    onClick: () -> Unit = {},
): Modifier {
    return this.pointerInput(Unit) {
        detectTapGestures(
            onPress = {
                UiState.isVrActive.value = false
                Log.d("sendMsg", "isVrActive.value: ${UiState.isVrActive}")
                onClick()
            },
            onTap = {
                UiState.isVrActive.value = true
                Log.d("sendMsg", "isVrActive.value: ${UiState.isVrActive}")
            },
            onLongPress = {
                UiState.isVrActive.value = true
            }
        )
    }
}