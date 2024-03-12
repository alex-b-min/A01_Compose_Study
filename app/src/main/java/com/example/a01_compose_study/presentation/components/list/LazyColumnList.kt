package com.example.a01_compose_study.presentation.components.list

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.colorResource
import com.example.a01_compose_study.R
import com.example.a01_compose_study.presentation.data.UiState
import kotlinx.coroutines.launch



@Composable
fun <T> LazyColumnList(
    state: LazyListState = rememberLazyListState(),
    list: List<T>,
    listContent: @Composable (index: Int, item: T) -> Unit,
    index: Int = 0,
) {
    val scope = rememberCoroutineScope()

    scope.launch {
        state.animateScrollToItem(index = index)
    }

    LazyColumn(state = state) {
        itemsIndexed(list) { index, item ->
            listContent(index,item)
        }
    }
}