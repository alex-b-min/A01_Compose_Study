package com.example.a01_compose_study.ui.help

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.a01_compose_study.R
import com.example.a01_compose_study.domain.ScreenType
import com.example.a01_compose_study.domain.SealedDomainType
import com.example.a01_compose_study.domain.model.HelpItemData
import com.example.a01_compose_study.domain.util.ScreenSizeType
import com.example.a01_compose_study.presentation.main.MainUiState

@Composable
fun ComposeHelpScreen(
    mainUiState: MainUiState.HelpWindow,
    contentColor: Color,
    onDismiss: () -> Unit,
    onBackButton: () -> Unit,
    onScreenSizeChange: (ScreenSizeType) -> Unit
) {
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
        if (mainUiState.screenType is ScreenType.HelpList) {
            Log.d("@@ mainUiState.data", "${mainUiState.data}")
            HelpListWindow(
                mainUiState = mainUiState,
                contentColor = contentColor,
                helpList = mainUiState.data as List<HelpItemData>,
                onDismiss = {
                    onDismiss()
                },
                onBackButton = {
                },
                onScreenSizeChange = { screenSizeType ->
                    onScreenSizeChange(screenSizeType)
                }
            )
        } else {

        }
    }
}

@Composable
fun <T> List(
    helpList: List<T>,
    helpListContent: @Composable (T) -> Unit
) {
    LazyColumn {
        items(helpList.size) { index ->
            helpListContent(helpList[index])
        }
    }
}

@Composable
fun HelpListWindow(
    mainUiState: MainUiState.HelpWindow,
    contentColor: Color,
    helpList: List<HelpItemData>,
    onDismiss: () -> Unit,
    onBackButton: () -> Unit,
    onScreenSizeChange: (ScreenSizeType) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp), // 원하는 여백 설정
            verticalArrangement = Arrangement.Top
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = {
                    onDismiss()
                }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        tint = if (mainUiState.isError) Color.Red else contentColor
                    )
                }
                IconButton(onClick = {
                    /**
                     * TODO: 뒤로가기 버튼 로직 구현
                     */
                }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = null,
                        tint = if (mainUiState.isError) Color.Red else contentColor
                    )
                }
            }

            List(helpList = helpList) { helpItemData ->
                HelpListItem(
                    domainId = helpItemData.domainId,
                    command = helpItemData.command,
                )
            }
        }
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Center
        ) {
            IconButton(onClick = {
                onScreenSizeChange(ScreenSizeType.Large)
            }) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = null,
                    tint = if (mainUiState.isError) Color.Red else contentColor
                )
            }
            IconButton(onClick = {
                onScreenSizeChange(ScreenSizeType.Small)
            }) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = if (mainUiState.isError) Color.Red else contentColor
                )
            }
        }
    }
}

@Composable
fun HelpDetailList(helpItemData: HelpItemData) {
    List(helpList = helpItemData.commandsDetail) { commandDeatail ->
        HelpDetailListItem(
            domainId = helpItemData.domainId.text,
            commandDetail = commandDeatail
        )
    }
}


@Composable
fun HelpListItem(
    domainId: SealedDomainType,
    command: String,
    focused: Boolean = false,
) {
    /**
     * TODO[클릭 이벤트 처리를 해야함]
     */
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
            ) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = dimensionResource(R.dimen.dp_80))
                        .alpha(if (focused) 1.0f else 0.3f)
                ) {
                    Row {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    top = dimensionResource(R.dimen.dp_6_7),
                                    bottom = dimensionResource(R.dimen.dp_6_7),
                                    start = dimensionResource(R.dimen.dp_33_3)
                                )
                        ) {
                            Text(
                                text = domainId.text,
                                modifier = Modifier
                                    .defaultMinSize(minHeight = dimensionResource(R.dimen.dp_24)),
                                color = colorResource(id = R.color.guidance_domain_text_color),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.subtitle2,
                                textAlign = TextAlign.Center,
                                fontSize = 16.sp,
                                letterSpacing = (-0.32).sp
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(end = 28.7.dp)
                                    .defaultMinSize(minHeight = dimensionResource(R.dimen.dp_32)),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {

                                val context = LocalContext.current
                                val commandId = context.resources.getIdentifier(
                                    command,
                                    "string",
                                    context.packageName
                                )
//                                val commandText = context.getString(commandId)
//                                val pattern = Regex("\\{([^}]*)\\}")
                                Box(
                                    modifier = Modifier.width(dimensionResource(R.dimen.dp_316))
                                ) {
                                    Text(
                                        text = command,
//                                        text = buildAnnotatedString {
//                                            var startIndex = 0
//                                            pattern.findAll(commandText).forEach { result ->
//                                                val match = result.value
//                                                val range = result.range
//
//                                                // Append text before the match
//                                                append(
//                                                    commandText.substring(
//                                                        startIndex,
//                                                        range.first
//                                                    )
//                                                )
//
//                                                // Apply style to the match
//                                                withStyle(style = SpanStyle(color = Color.White)) {
//                                                    append(
//                                                        match.substring(
//                                                            1,
//                                                            match.length - 1
//                                                        )
//                                                    ) // Exclude {}
//                                                }
//
//                                                startIndex = range.last + 1
//                                            }
//
//                                            // Append the remaining text
//                                            if (startIndex < commandText.length) {
//                                                append(commandText.substring(startIndex))
//                                            }
//                                        },
                                        color = Color.White,
                                        maxLines = 3,
                                        overflow = TextOverflow.Ellipsis,
                                        style = MaterialTheme.typography.h5.plus(
                                            TextStyle(
                                                letterSpacing = (-0.48).sp,
                                                lineHeight = 32.sp
                                            )
                                        ),
                                        textAlign = TextAlign.Start
                                    )
                                }
                                Image(
                                    painter = painterResource(id = R.drawable.ic_guidance_indicator),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .width(dimensionResource(R.dimen.dp_16))
                                        .height(dimensionResource(R.dimen.dp_16))
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HelpDetailListItem(domainId: String, commandDetail: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = dimensionResource(R.dimen.dp_80)),
        contentAlignment = Alignment.CenterStart
    ) {
        Row {
            Spacer(modifier = Modifier.width(dimensionResource(R.dimen.dp_33)))
            Text(
                text = commandDetail,
                modifier = Modifier.padding(
                    top = dimensionResource(R.dimen.dp_15),
                    bottom = dimensionResource(R.dimen.dp_15),
                    end = dimensionResource(R.dimen.dp_33)
                ),
                color = colorResource(id = R.color.white),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.h3,
                letterSpacing = (-0.43).sp,
                fontSize = 21.3.sp,
                textAlign = TextAlign.Start
            )
        }
    }
}


@Composable
fun HelpListPreview() {
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
        mainUiState = MainUiState.HelpWindow(
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

@Composable
fun HelpDetailListPreview() {
    val helpItemData = HelpItemData(
        domainId = SealedDomainType.Help,
        command = "Command1",
        commandsDetail = listOf("Detail1", "Detail2")
    )

    HelpDetailList(helpItemData = helpItemData)
}

@Preview
@Composable
fun HelpListPreviewPreview() {
    HelpListPreview()
}

@Preview
@Composable
fun HelpDetailListPreviewPreview() {
    HelpDetailListPreview()
}
