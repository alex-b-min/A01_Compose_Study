//package com.ftd.ivi.cerence.ui.compose
//
//import androidx.compose.animation.core.tween
//import androidx.compose.foundation.gestures.animateScrollBy
//import androidx.compose.foundation.gestures.scrollBy
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.IntrinsicSize
//import androidx.compose.foundation.layout.PaddingValues
//import androidx.compose.foundation.layout.fillMaxHeight
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.itemsIndexed
//import androidx.compose.foundation.lazy.rememberLazyListState
//import androidx.compose.material3.Divider
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.DisposableEffect
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.MutableState
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.mutableIntStateOf
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.rememberCoroutineScope
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.testTag
//import androidx.compose.ui.res.colorResource
//import androidx.compose.ui.res.dimensionResource
//import androidx.compose.ui.unit.dp
//import com.ftd.ivi.cerence.R
//import com.ftd.ivi.cerence.data.UxPreset
//import com.ftd.ivi.cerence.data.model.ScreenData
//import com.ftd.ivi.cerence.ui.theme.FadingEdgeStart
//import com.ftd.ivi.cerence.util.CustomLogger
//import kotlinx.coroutines.launch
//
//@Composable
//fun ComposeListView(
//    screenData: ScreenData,
//    modifier: Modifier,
//    listItem: List<Any>,
//    endScroll: () -> Unit = {
//        screenData.mwContext?.let {
//            screenData.manager?.viewModel?.stopTimer()
//            screenData.manager?.vrmwManager?.checkResumeVR(it, true)
//        }
//    },
//    dividerShow: Boolean = true,
//    content: @Composable() (Int, Boolean, MutableState<Int>, Any) -> Unit
//) {
//    val scrollToIndex = screenData.screenState.scrollToIndex.collectAsState()
//    val moreScroll = remember { mutableStateOf(-1) }
//    val moreScrollOffset = remember { mutableStateOf(-1) }
//    val isNext = screenData.screenState.nextPage.collectAsState(initial = false)
//    val isPrev = screenData.screenState.prevPage.collectAsState(initial = false)
//    val coroutineScope = rememberCoroutineScope()
//    val selectIndex = screenData.screenState.selectedIndex.collectAsState()
//
//    var fromUser = remember { mutableStateOf(true) }
//
//    val focusIndex = remember {
//        mutableIntStateOf(-1)
//    }
//
//    if (screenData.screenState.listState == null) {
//        screenData.screenState.listState = rememberLazyListState()
//    }
//    val listState = screenData.screenState.listState!!
//    listState.apply {
//
//        if (isScrollInProgress) {
//            DisposableEffect(Unit) {
//                //scrollStateListener.invoke(UserScrollState.StartScroll)
//                CustomLogger.e("StartScroll")
//                if (fromUser.value) {
//                    focusIndex.value = -1
//                    screenData.mwContext?.let {
//                        if(screenData.manager?.vrmwManager?.checkStopVR(it) == true){
//                            screenData.manager?.vrmwManager?.stop()
//                        }
//                    }
//                }
//
//                onDispose {
//                    CustomLogger.e("EndScroll : ${screenData.screenState.scrollToIndex.value} first:${listState.firstVisibleItemIndex} offet:${listState.firstVisibleItemScrollOffset} size:${listState.layoutInfo.visibleItemsInfo.first().size}")
//                    if (screenData.screenState.scrollToIndex.value >= 0) {
//                        screenData.screenState.selectedIndex.value =
//                            screenData.screenState.scrollToIndex.value
//                        screenData.screenState.scrollToIndex.value = -1
//                    }
//                    screenData.screenState.nextPage.value = false
//                    screenData.screenState.prevPage.value = false
//
//
//                    coroutineScope.launch {
//                        if (listState.firstVisibleItemIndex >= 0) {
//                            val index = listState.firstVisibleItemIndex
//                            val offset = listState.firstVisibleItemScrollOffset
//                            val half = listState.layoutInfo.visibleItemsInfo.first().size / 2
//
//                            if (offset != 0) {
//                                if (listState.canScrollForward) {
//                                    if (offset >= half) {
//                                        moreScroll.value = index + 1
//                                    } else {
//                                        moreScroll.value = index
//                                    }
//                                }
//
//                            }
//                            if (fromUser.value) {
//                                CustomLogger.e("Invoke EndScroll")
//                                endScroll.invoke()
//                            }
//                            fromUser.value = true
//                        }
//                    }
//
//
//                }
//            }
//        }
//    }
//
//    suspend fun scrollPage(isNext: Boolean) {
//        fromUser.value = true
//        val height = listState.layoutInfo.visibleItemsInfo.first().size
//        var toIndex = (listState.firstVisibleItemIndex + 4).coerceAtMost(listItem.size)
//        if (!isNext) {
//            toIndex = (listState.firstVisibleItemIndex - 4).coerceAtLeast(0)
//        }
//        CustomLogger.e("antmate page ScrollTo ${toIndex}")
//        val offset = height * (toIndex)
//        val toScroll =
//            offset - (listState.firstVisibleItemIndex * height) - listState.firstVisibleItemScrollOffset
//        screenData.screenState.scrolledPos = offset.toFloat()
//        var duration = UxPreset.animationDuration
//        if (offset < 250) {
//            duration = 0
//        }
//        listState.animateScrollBy(
//            value = toScroll.toFloat(),
//            animationSpec = tween(durationMillis = duration),
//        )
//    }
//
//    LaunchedEffect(isNext.value) {
//        if (isNext.value) {
//            scrollPage(true)
//        }
//
//    }
//    LaunchedEffect(isPrev.value) {
//        if (isPrev.value) {
//            scrollPage(false)
//        }
//    }
//    suspend fun scrollIndex(index: Int, skipAni: Boolean = false, byUser: Boolean = true) {
//        fromUser.value = byUser
//        CustomLogger.e("antmate index to :${index} first:${listState.firstVisibleItemScrollOffset} size:${listState.layoutInfo.visibleItemsInfo.first().size}")
//        val height = listState.layoutInfo.visibleItemsInfo.first().size
//        val offset = height * (index)
//        val toScroll =
//            offset - (listState.firstVisibleItemIndex * height) - listState.firstVisibleItemScrollOffset
//        screenData.screenState.scrolledPos = offset.toFloat()
//        CustomLogger.e("toScroll : ${toScroll}, toIndex:${index} toOffset:${offset} firstIndex:${listState.firstVisibleItemIndex} firstOffset:${listState.firstVisibleItemScrollOffset}")
//        var duration = UxPreset.animationDuration
//        if (offset < 250) {
//            duration = 0
//        }
//        if (skipAni) {
//            listState.scrollBy(
//                value = toScroll.toFloat(),
//            )
//        } else {
//            listState.animateScrollBy(
//                value = toScroll.toFloat(),
//                animationSpec = tween(durationMillis = duration),
//            )
//        }
//
//    }
//
//    LaunchedEffect(scrollToIndex.value) {
//        if (scrollToIndex.value >= 0) {
//            scrollIndex(scrollToIndex.value, byUser = false)
//        }
//    }
//    LaunchedEffect(moreScroll.value) {
//        if (moreScroll.value >= 0) {
//            scrollIndex(moreScroll.value, true)
//            moreScroll.value = -1
//        }
//    }
//    LaunchedEffect(selectIndex.value) {
//        focusIndex.value = selectIndex.value
//    }
//
//
//    LazyColumn(
//        state = listState,
//        modifier = modifier
//            .padding(top = dimensionResource(R.dimen.dp_23_3))
//            .fillMaxWidth()
//            .fillMaxHeight()
//            .verticalBottomFadingEdge(
//                lazyListState = listState,
//                135.dp,
//                edgeColor = FadingEdgeStart,
//            )
//            .testTag("ComposeListView"),
//        contentPadding = PaddingValues(bottom = dimensionResource(R.dimen.dp_16)),
//    ) {
//        itemsIndexed(
//            items = listItem,
//            contentType = { index, item -> "data" }
//        ) { index, item ->
//
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(IntrinsicSize.Min)
//            ) {
//                Column(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(IntrinsicSize.Min)
//                ) {
//                    content.invoke(index, selectIndex.value == index, focusIndex, item)
//                    val divider = (index < listItem.size - 1)
//                    if (divider && dividerShow) {
//                        Divider(color = colorResource(id = R.color.white_a20), thickness = 0.4.dp)
//                    }
//                }
//            }
//
//
//        }
//    }
//
//
//}