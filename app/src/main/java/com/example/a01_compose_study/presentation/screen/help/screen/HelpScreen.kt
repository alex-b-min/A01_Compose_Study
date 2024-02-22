package com.example.a01_compose_study.presentation.screen.help.screen

import android.content.Context
import android.util.Log
import android.view.MotionEvent
import android.view.ViewConfiguration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.a01_compose_study.domain.model.HelpItemData
import com.example.a01_compose_study.domain.model.ScreenType
import com.example.a01_compose_study.domain.model.SealedDomainType
import com.example.a01_compose_study.domain.util.ScreenSizeType
import com.example.a01_compose_study.presentation.components.top_bar.TopAppBarContent
import com.example.a01_compose_study.presentation.data.UiState
import com.example.a01_compose_study.presentation.data.UiState.onVREvent
import com.example.a01_compose_study.presentation.screen.help.HelpEvent
import com.example.a01_compose_study.presentation.screen.help.HelpViewModel
import com.example.a01_compose_study.presentation.screen.main.DomainUiState
import com.example.a01_compose_study.presentation.screen.main.VREvent
import com.example.a01_compose_study.presentation.screen.main.route.VRUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs

@Composable
fun ComposeHelpScreen(
    viewModel: HelpViewModel = hiltViewModel(),
    domainUiState: DomainUiState.HelpWindow,
    vrUiState: VRUiState,
    contentColor: Color,
    backgroundColor: Color,
) {
    /**
     * [Help Window -> Help Detail Window 화면 전환 방법]
     * UiState를 현재 가지고 있는 정보를 기반으로 해서 HelpList 타입에서 HelpDetailList 타입으로 변경한다.
     * 이렇게 하게 되면 uiState가 변경되었기 때문에 아래의 if문에서 걸러 화면이 Recomposition이 이루어져 이동을 하게 된다.
     * ==> 뒤로가기 구현 : 현재 가지고 있는 uiState를 기반으로 다시 HelpDetailList 타입에서 HelpList 타입으로 바꾼다.
     */


    Box(modifier = Modifier.fillMaxSize()) {
        if (domainUiState.screenType is ScreenType.HelpList) {
            HelpListWindow(
                domainUiState = domainUiState,
                vrUiState = vrUiState,
                backgroundColor = backgroundColor,
                onDismiss = {
                    viewModel.onHelpEvent(HelpEvent.OnDismiss)
                },
                onBackButton = {
                    viewModel.onHelpEvent(
                        HelpEvent.OnHelpListBack(
                            isError = false,
                            screenSizeType = ScreenSizeType.Small
                        )
                    )
                },
//                onScreenSizeChange = { screenSizeType ->
//                    viewModel.onHelpEvent(event = HelpEvent.ChangeHelpWindowSizeEvent(screenSizeType))
//                },
                onItemClick = { helpItemData ->
                    viewModel.onHelpEvent(HelpEvent.HelpListItemOnClick(helpItemData))
                }
            )
        } else if (domainUiState.screenType is ScreenType.HelpDetailList) {
            HelpDetailWindow(
                domainUiState = domainUiState,
                backgroundColor = backgroundColor,
                onDismiss = {
                    viewModel.onHelpEvent(HelpEvent.OnDismiss)
                },
                onBackButton = {
                    viewModel.onHelpEvent(HelpEvent.OnHelpDetailBack)
                },
//                onScreenSizeChange = { screenSizeType ->
//                    viewModel.onHelpEvent(event = HelpEvent.ChangeHelpWindowSizeEvent(screenSizeType))
//                }
            )
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun HelpListWindow(
    domainUiState: DomainUiState.HelpWindow,
    vrUiState: VRUiState,
    backgroundColor: Color,
    onDismiss: () -> Unit,
    onBackButton: () -> Unit,
    onItemClick: (HelpItemData) -> Unit,
) {
    val context = LocalContext.current
    val helpList = domainUiState.data
    var startX by remember { mutableStateOf(0f) }
    var startY by remember { mutableStateOf(0f) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(15.dp))
            .background(if (vrUiState.active) Color.Transparent else backgroundColor)
            .pointerInteropFilter {
                when (it.action) {
                    MotionEvent.ACTION_DOWN -> {
                        // 터치 시작(VR 종료)
                        startX = it.x
                        startY = it.y
                        Log.d("@@ 터치 시작 startX / startY / endX / endY", "${startX} , ${startY} , ${it.x}, ${it.y}")
                        onVREvent(
                            VREvent.ChangeVRUIEvent(
                                VRUiState.PttLoading(
                                    active = false,
                                    isError = false
                                )
                            )
                        )
                        true // 이벤트를 소비함
                    }
                    MotionEvent.ACTION_MOVE -> {
                        Log.d("@@ 드래그 startX / startY / endX / endY", "${startX} , ${startY} , ${it.x}, ${it.y}")
                        // 이벤트를 소비하거나 하위 컴포넌트로 전달 여부를 결정할 수 있음
                        true // 이벤트를 소비함
                    }
                    MotionEvent.ACTION_UP -> {
                        // 터치 종료(기본값: VR 실행)
                        Log.d("@@ 터치 종료 startX / startY / endX / endY", "${startX} , ${startY} , ${it.x}, ${it.y}")
                        if (isClick(context = context, startX = startX, startY = startY, endX = it.x, endY = it.y)) {
                            Log.d("@@ 같은 자리(일반 터치)" ,"${true}")
                            // 일반 터치: 클릭 이벤트 실행
                            // 여기에 클릭 이벤트를 처리하는 코드를 넣어주세요
                            true
                        } else {
                            // 스크롤 터치: VR 실행
                            Log.d("@@ 다른 자리(스크롤 터치)" ,"${false}")
                            onVREvent(
                                VREvent.ChangeVRUIEvent(
                                    VRUiState.PttLoading(
                                        active = true,
                                        isError = false
                                    )
                                )
                            )
                            true // 이벤트를 소비함
                        }
                    }
                    else -> true // 기타 모든 경우에 이벤트를 소비함
                }
            }
    ) {
        onVREvent(
            VREvent.ChangeVRUIEvent(
                VRUiState.PttLoading(
                    active = true,
                    isError = false
                )
            )
        )

        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            TopAppBarContent(
                onNavigationIconClick = {
                    onBackButton()
                },
                onActionIconClick = {
                    onDismiss()
                }
            )

            HelpList(
                helpList = helpList,
                backgroundColor = backgroundColor,
                onItemClick = { helpItemData ->
                    onItemClick(helpItemData)
                })
        }
    }
}

@Composable
fun HelpDetailWindow(
    domainUiState: DomainUiState.HelpWindow,
    backgroundColor: Color,
    onDismiss: () -> Unit,
    onBackButton: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(15.dp))
            .background(backgroundColor)
    ) {
        Column {
            TopAppBarContent(
                title = domainUiState.detailData.domainId.text,
                onNavigationIconClick = {
                    onBackButton()
                },
                onActionIconClick = {
                    onDismiss()
                }
            )
            HelpDetailList(helpItemData = domainUiState.detailData)
        }
    }
}

@Preview
@Composable
fun HelpDetailListWindowPreview() {

    val helpItemDataList = listOf(
        HelpItemData(
            domainId = SealedDomainType.Help,
            command = "Command1",
            commandsDetail = listOf("Detail1", "Detail2")
        ),
        HelpItemData(
            domainId = SealedDomainType.Navigation,
            command = "Command2",
            commandsDetail = listOf("Detail3", "Detail4")
        ),
        HelpItemData(
            domainId = SealedDomainType.Call,
            command = "Command3",
            commandsDetail = listOf("Detail5", "Detail6")
        )
    )


    HelpDetailWindow(
        domainUiState = DomainUiState.HelpWindow(
            domainType = SealedDomainType.Help,
            screenType = ScreenType.HelpDetailList,
            data = helpItemDataList,
            detailData = helpItemDataList[0],
            text = "HelpWindow",
            screenSizeType = ScreenSizeType.Large
        ),
        backgroundColor = Color.DarkGray,
        onDismiss = {
        },
        onBackButton = {
        },
    )
}


@Preview
@Composable
fun HelpListWindowPreview() {
    val helpItemDataList = listOf(
        HelpItemData(
            domainId = SealedDomainType.Help,
            command = "Command1",
            commandsDetail = listOf("Detail1", "Detail2")
        ),
        HelpItemData(
            domainId = SealedDomainType.Navigation,
            command = "Command2",
            commandsDetail = listOf("Detail3", "Detail4")
        ),
        HelpItemData(
            domainId = SealedDomainType.Call,
            command = "Command3",
            commandsDetail = listOf("Detail5", "Detail6")
        )
    )

    HelpListWindow(
        domainUiState = DomainUiState.HelpWindow(
            domainType = SealedDomainType.Help,
            screenType = ScreenType.HelpList,
            data = helpItemDataList,
            text = "HelpWindow",
            screenSizeType = ScreenSizeType.Large
        ),
        vrUiState = VRUiState.PttNone(active = false, isError = false),
        backgroundColor = Color.DarkGray,
        onDismiss = {
        },
        onBackButton = {
        },
        onItemClick = {}
    )
}

// 두 점의 좌표가 같은지 확인하는 함수
private fun isClick(context: Context, startX: Float, startY: Float, endX: Float, endY: Float): Boolean {
    val touchSlop = ViewConfiguration.get(context).scaledTouchSlop.toFloat()
    return abs(startX - endX) < touchSlop && abs(startY - endY) < touchSlop
}