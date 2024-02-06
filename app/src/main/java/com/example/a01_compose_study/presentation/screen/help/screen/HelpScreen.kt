package com.example.a01_compose_study.presentation.screen.help.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.a01_compose_study.domain.model.HelpItemData
import com.example.a01_compose_study.domain.model.ScreenType
import com.example.a01_compose_study.domain.model.SealedDomainType
import com.example.a01_compose_study.domain.util.ScreenSizeType
import com.example.a01_compose_study.presentation.components.top_bar.TopAppBarContent
import com.example.a01_compose_study.presentation.screen.help.HelpEvent
import com.example.a01_compose_study.presentation.screen.help.HelpViewModel
import com.example.a01_compose_study.presentation.screen.main.DomainUiState

@Composable
fun ComposeHelpScreen(
    domainUiState: DomainUiState.HelpWindow,
    contentColor: Color,
    backgroundColor: Color,
) {
    val viewModel: HelpViewModel = hiltViewModel()
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
                contentColor = contentColor,
                backgroundColor = backgroundColor,
                onDismiss = {
                    viewModel.onHelpEvent(HelpEvent.OnDismiss)
                },
                onBackButton = {
                    viewModel.onHelpEvent(
                        HelpEvent.OnHelpListBack(
                            isError = false,
                            text = "VR 재실행",
                            screenSizeType = ScreenSizeType.Middle
                        )
                    )
                },
                onScreenSizeChange = { screenSizeType ->
                    viewModel.onHelpEvent(event = HelpEvent.ChangeHelpWindowSizeEvent(screenSizeType))
                },
                onItemClick = { helpItemData ->
                    viewModel.onHelpEvent(HelpEvent.HelpListItemOnClick(helpItemData))
                }
            )
        } else if (domainUiState.screenType is ScreenType.HelpDetailList) {
            HelpDetailWindow(
                domainUiState = domainUiState,
                contentColor = contentColor,
                backgroundColor = backgroundColor,
                onDismiss = {
                    viewModel.onHelpEvent(HelpEvent.OnDismiss)
                },
                onBackButton = {
                    viewModel.onHelpEvent(HelpEvent.OnHelpDetailBack)
                },
                onScreenSizeChange = { screenSizeType ->
                    viewModel.onHelpEvent(event = HelpEvent.ChangeHelpWindowSizeEvent(screenSizeType))
                }
            )
        }
    }
}

@Composable
fun HelpListWindow(
    domainUiState: DomainUiState.HelpWindow,
    contentColor: Color,
    backgroundColor: Color,
    onDismiss: () -> Unit,
    onBackButton: () -> Unit,
    onScreenSizeChange: (ScreenSizeType) -> Unit,
    onItemClick: (HelpItemData) -> Unit,
) {
    val helpList = domainUiState.data
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
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
                onItemClick = { helpItemData ->
                    onItemClick(helpItemData)
                })
        }
    }
}

@Composable
fun HelpDetailWindow(
    domainUiState: DomainUiState.HelpWindow,
    contentColor: Color,
    backgroundColor: Color,
    onDismiss: () -> Unit,
    onBackButton: () -> Unit,
    onScreenSizeChange: (ScreenSizeType) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
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
            visible = true,
            text = "HelpWindow",
            screenSizeType = ScreenSizeType.Large
        ),
        contentColor = Color.DarkGray,
        backgroundColor = Color.DarkGray,
        onDismiss = {
        },
        onBackButton = {
        },
        onScreenSizeChange = {
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
            visible = true,
            text = "HelpWindow",
            screenSizeType = ScreenSizeType.Large
        ),
        backgroundColor = Color.Black,
        contentColor = Color.DarkGray,
        onDismiss = {
        },
        onBackButton = {
        },
        onScreenSizeChange = {
        },
        onItemClick = {}
    )
}
