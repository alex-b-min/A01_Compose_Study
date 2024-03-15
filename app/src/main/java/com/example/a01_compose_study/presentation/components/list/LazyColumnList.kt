package com.example.a01_compose_study.presentation.components.list

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import com.example.a01_compose_study.presentation.data.UiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun <T> LazyColumnList(
    list: List<T>,
    listContent: @Composable (index: Int, item: T, isClickable: Boolean) -> Unit,
    index: Int = 0,
) {
    val scrollState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    val isClickable = remember { mutableStateOf(false) }
    Log.d("isClickable","isClickable : ${isClickable.value}")

    DisposableEffect(Unit) {
        val scrollJob = scope.launch {
            scrollState.animateScrollToItem(index)
            delay(100) // 스크롤이 멈추는지 확인하기 위해 대기
            isClickable.value = true // 스크롤이 끝나면 클릭 가능하도록 설정.
            UiState.isVrActive.value = true // VR 활성화 상태로 변경합
        }

        onDispose {
            scrollJob.cancel() // DisposableEffect 종료 시 Job을 취소
        }
    }

    LaunchedEffect(scrollState.isScrollInProgress) {
        if (scrollState.isScrollInProgress) {
            isClickable.value = false // 스크롤 중일 때는 클릭을 비활성화
        } else {
            isClickable.value = true // 스크롤이 멈추면 클릭을 다시 활성화
            UiState.isVrActive.value = true // VR 활성화 상태로 변경
        }
    }

    LazyColumn(state = scrollState) {
        itemsIndexed(list) { idx, item ->
            listContent(idx, item, isClickable.value)
        }
    }
}