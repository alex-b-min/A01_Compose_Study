package com.example.a01_compose_study.ui.help

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.a01_compose_study.R
import com.example.a01_compose_study.domain.ScreenType
import com.example.a01_compose_study.domain.SealedDomainType
import com.example.a01_compose_study.domain.model.HelpItemData
import com.example.a01_compose_study.domain.util.ScreenSizeType
import com.example.a01_compose_study.presentation.components.lottie.LottieAssetAnimationHandler
import com.example.a01_compose_study.presentation.components.lottie.LottieRawAnimationHandler
import com.example.a01_compose_study.presentation.main.MainUiState
import com.example.a01_compose_study.presentation.util.TextModifier.normalize
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ComposeHelpScreen(
    mainUiState: MainUiState.HelpWindow,
    contentColor: Color,
    onDismiss: () -> Unit,
    onBackButton: () -> Unit
) {
    var targetFillMaxHeight by remember { mutableStateOf(Animatable(0f)) }
    val scope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
//        visible = true
        
        
        // uiState로부터의 screenSizeType을 얻어 해당 화면 크기 설정
        targetFillMaxHeight = when (mainUiState.screenSizeType) {
            is ScreenSizeType.Small -> Animatable(0.15f)
            is ScreenSizeType.Middle -> Animatable(0.268f)
            is ScreenSizeType.Large -> Animatable(0.433f)
        }
    }
    
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomStart
    ) {
        Column(
            modifier = Modifier
                .offset(x = 10.dp, y = (10).dp)
                .fillMaxHeight(targetFillMaxHeight.value)
                .fillMaxWidth(0.233f)
                .background(
                    color = Color.DarkGray,
                    shape = RoundedCornerShape(15.dp)
                ),
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                if (mainUiState.screenType is ScreenType.HelpList) {
                    Log.d("@@ mainUiState.data" ,"${mainUiState.data}")
                    HelpList(helpList = mainUiState.data as List<HelpItemData>)
                } else {
                    
                }
                
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.End
                ) {
                    IconButton(onClick = {
                        scope.launch {
//                            visible = false
                            delay(500)
                            onDismiss()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                            tint = if (mainUiState.isError) Color.Red else contentColor
                        )
                    }
                }
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.Start
                ) {
                    IconButton(onClick = {
                        scope.launch {
//                            visible = false
                            delay(500)
                            onBackButton()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = null,
                            tint = if (mainUiState.isError) Color.Red else contentColor
                        )
                    }
                }
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.Center
                ) {
                    IconButton(onClick = {
                        scope.launch {
                            val newTargetValue = when (targetFillMaxHeight.value) {
                                0.15f -> 0.268f
                                0.268f -> 0.433f
                                else -> targetFillMaxHeight.value
                            }
                            targetFillMaxHeight.animateTo(newTargetValue)
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowUp,
                            contentDescription = null,
                            tint = if (mainUiState.isError) Color.Red else contentColor
                        )
                    }
                    IconButton(onClick = {
                        scope.launch {
                            val newTargetValue = when (targetFillMaxHeight.value) {
                                0.268f -> 0.15f
                                0.433f -> 0.268f
                                else -> targetFillMaxHeight.value
                            }
                            targetFillMaxHeight.animateTo(newTargetValue)
                        }
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
fun HelpList(helpList: List<HelpItemData>) {
    List(helpList = helpList) { helpItemData ->
        HelpListItem(domainId = helpItemData.domainId, command = helpItemData.command)
    }

}

@Composable
fun HelpDetailList(helpItemData: HelpItemData) {
    List(helpList = helpItemData.commandsDetail) { commandDeatail ->
        HelpDetailListItem(domainId = helpItemData.domainId.text, commandDetail = commandDeatail)
    }
}


@Composable
fun HelpListItem(domainId: SealedDomainType, command: String, focused: Boolean = false) {
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

    HelpList(helpList = helpItemDataList)
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