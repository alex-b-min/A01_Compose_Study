package com.example.a01_compose_study.presentation.components.list

import android.util.Log
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import com.example.a01_compose_study.presentation.data.UiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun <T> LazyColumnList(
    list: List<T>,
    listContent: @Composable (index: Int, item: T, isClickable: MutableState<Boolean>) -> Unit,
    index: Int = 0,
) {
    val scrollState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    val isClickable = remember { mutableStateOf(false) }
    Log.d("isClickable","isClickable : ${isClickable.value}")

    DisposableEffect(Unit) {
        val scrollJob = scope.launch {
            scrollState.animateScrollToItem(index)
            delay(100)
            isClickable.value = true
            UiState.isVrActive.value = true
        }

        onDispose {
            scrollJob.cancel()
        }
    }

    LaunchedEffect(scrollState.isScrollInProgress) {
        if (scrollState.isScrollInProgress) {
            isClickable.value = false
        } else {
            isClickable.value = true
            UiState.isVrActive.value = true
        }
    }

    LazyColumn(state = scrollState) {
        itemsIndexed(list) { idx, item ->
            listContent(idx, item, isClickable)
        }
    }
}