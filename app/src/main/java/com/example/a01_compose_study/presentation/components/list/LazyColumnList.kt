package com.example.a01_compose_study.presentation.components.list

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import java.lang.reflect.Modifier


@Composable
fun <T> LazyColumnList(
    state: LazyListState = rememberLazyListState(),
    list: List<T>,
    listContent: @Composable (index: Int, item: T) -> Unit,
    index: Int = 0,
    onPressed: ((Boolean) -> Unit) = {},
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
