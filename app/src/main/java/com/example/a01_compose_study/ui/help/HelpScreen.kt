package com.example.a01_compose_study.ui.help

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.a01_compose_study.domain.ScreenType
import com.example.a01_compose_study.domain.SealedDomainType
import com.example.a01_compose_study.domain.model.HelpItemData
import com.example.a01_compose_study.domain.util.ScreenSizeType
import com.example.a01_compose_study.presentation.main.DomainUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ComposeHelpScreen(
    domainUiState: DomainUiState.HelpWindow,
    contentColor: Color,
    onDismiss: () -> Unit,
    onHelpListBackButton: () -> Unit,
    onScreenSizeChange: (ScreenSizeType) -> Unit,
) {
    val scope = rememberCoroutineScope()
    /**
     * [Help Window -> Help Detail Window 띄우기 생각한 방법]
     *
     * 1번 방법 - UiState를 None으로 바꾸고 Help Detail Window을 띄우도록 한다.
     * ==> 발생할 수 있는 사이드 이펙트는 데이터를 None으로 바꾸었기에 데이터가 리셋되어서 뒤로가기 구현을 신경쓰지 못한다.
     *
     * 2번 방법 - UiState를 현재 가지고 있는 정보를 기반으로 해서 HelpList 타입에서 HelpDetailList 타입으로 변경한다.
     * 이렇게 하게 되면 uiState가 변경되었기 때문에 아래의 if문에서 걸러 화면이 Recomposition이 이루어져 이동을 하게 된다.
     * ==> 뒤로가기 구현에 대해 생각해본 점은 현재 가지고 있는 uiState를 기반으로 다시 HelpDetailList 타입에서 HelpList 타입으로 바꾸면 뒤로가기가 구현될 것 같다.
     *
     * 뒤로가기를 고려한다면 2번 방법이 조금 더 구현 가능성이 높아 보인다.
     */
    Box(modifier = Modifier.fillMaxSize()) {
        if (domainUiState.screenType is ScreenType.HelpList) {
            HelpListWindow(
                domainUiState = domainUiState,
                contentColor = contentColor,
                helpList = domainUiState.data as List<HelpItemData>,
                onDismiss = {
                    onDismiss()
                },
                onBackButton = {
                    scope.launch {
                        onDismiss()
                        delay(500)
                        onHelpListBackButton()
                    }
                },
                onScreenSizeChange = { screenSizeType ->
                    onScreenSizeChange(screenSizeType)
                }
            )
        } else if (mainUiState.screenType is ScreenType.HelpDetailList) {
//            HelpDetailWindow(
//                mainUiState = mainUiState,
//                contentColor = contentColor,
//                helpItemData = ,
//                domainId = ,
//                onDismiss = {
//                    onDismiss()
//                },
//                onBackButton = { /*TODO*/ },
//                onScreenSizeChange =
//            )
        }
    }
}


@Composable
fun HelpListWindow(
    domainUiState: DomainUiState.HelpWindow,
    contentColor: Color,
    helpList: List<HelpItemData>,
    onDismiss: () -> Unit,
    onBackButton: () -> Unit,
    onScreenSizeChange: (ScreenSizeType) -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.End,
        ) {
            TopAppBarContent(
                title = "",
                onNavigationIconClick = {
                    // 뒤로 가기
                },
                onActionIconClick = {
                    // 화면 닫기
                }
            )
            HelpList(helpList = helpList)
//            IconButton(onClick = {
//                onScreenSizeChange(ScreenSizeType.Large)
//            }) {
//                Icon(
//                    imageVector = Icons.Default.KeyboardArrowUp,
//                    contentDescription = null,
//                    tint = if (mainUiState.isError) Color.Red else contentColor
//                )
//            }
//            IconButton(onClick = {
//                onScreenSizeChange(ScreenSizeType.Small)
//            }) {
//                Icon(
//                    imageVector = Icons.Default.KeyboardArrowDown,
//                    contentDescription = null,
//                    tint = if (mainUiState.isError) Color.Red else contentColor
//                )
//            }
        }
    }
}

@Composable
fun HelpDetailWindow(
    mainUiState: MainUiState.HelpWindow,
    contentColor: Color,
    helpItemData: HelpItemData,
    domainId: String,
    onDismiss: () -> Unit,
    onBackButton: () -> Unit,
    onScreenSizeChange: (ScreenSizeType) -> Unit
) {
    Column {
        TopAppBarContent(
            title = domainId,
            onNavigationIconClick = {
                // 뒤로 가기
            },
            onActionIconClick = {
                // 화면 닫기
            }

        )
        HelpDetailList(helpItemData = helpItemData)
    }
}

@Preview
@Composable
fun HelpDetailListWindowPreview() {

    val helpItemData = HelpItemData(
        domainId = SealedDomainType.Help,
        command = "Command1",
        commandsDetail = listOf("Detail1", "Detail2")
    )


    HelpDetailWindow(
        mainUiState = MainUiState.HelpWindow(
            domainType = SealedDomainType.Help,
            screenType = ScreenType.HelpDetailList,
            data = "",
            visible = true,
            text = "HelpWindow",
            screenSizeType = ScreenSizeType.Large
        ),
        contentColor = Color.DarkGray,
        helpItemData = helpItemData,
        onDismiss = {
        },
        onBackButton = {
        },
        onScreenSizeChange = {
        },
        domainId = "Navigation"
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
            data = "",
            visible = true,
            text = "HelpWindow",
            screenSizeType = ScreenSizeType.Large
        ),
        contentColor = Color.DarkGray,
        helpList = helpItemDataList,
        onDismiss = {
        },
        onBackButton = {
        },
        onScreenSizeChange = {
        }
    )
}
