package com.example.a01_compose_study.presentation.components.list

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable

@Composable
fun <T> LazyColumnList(
    list: List<T>, listContent: @Composable (T) -> Unit
) {
    LazyColumn {
        itemsIndexed(list) { index, item ->
            listContent(item)
        }
    }
}