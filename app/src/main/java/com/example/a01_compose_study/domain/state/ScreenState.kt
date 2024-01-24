package com.example.a01_compose_study.domain.state

import androidx.compose.foundation.lazy.LazyListState
import kotlinx.coroutines.flow.MutableStateFlow

class ScreenState {
    val selectedIndex = MutableStateFlow(-1)
    val scrollToIndex = MutableStateFlow(-1)
    var scrolledPos = 0f
    var nextPage = MutableStateFlow(false)
    var prevPage = MutableStateFlow(false)
    var listState: LazyListState? = null
    var triggerYes = MutableStateFlow(false)
    var triggerNo = MutableStateFlow(false)
}

class ScreenStateSnapshot{
    val selectedIndex = -1
    val scrollToIndex = -1
    var scrolledPos = 0f
    var nextPage = false
    var prevPage = false
    var listState: LazyListState? = null
    var triggerYes = false
    var triggerNo = false
}