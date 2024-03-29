package com.example.a01_compose_study.presentation.screen.help.screen

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.example.a01_compose_study.domain.model.HelpItemData
import com.example.a01_compose_study.domain.model.SealedDomainType
import com.example.a01_compose_study.presentation.components.list.LazyColumnList
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun HelpList(
    helpList: List<HelpItemData>,
    backgroundColor: Color,
    onItemClick: (HelpItemData) -> Unit,
) {
//    LazyColumnList(list = helpList) { helpItemData ->
//        HelpListItem(
//            helpItemData = helpItemData,
//            onItemClick = onItemClick,
//        )
//    }
    LazyColumn() {
        itemsIndexed(helpList) { index, helpItemData ->
            HelpListItem(
                helpItemData = helpItemData,
                backgroundColor = backgroundColor,
                onItemClick = onItemClick,
            )
        }
    }
}

@Composable
fun HelpDetailList(helpItemData: HelpItemData) {
    LazyColumnList(list = helpItemData.commandsDetail) { commandDeatail ->
        HelpDetailListItem(
            commandDetail = commandDeatail
        )
    }
}


@Composable
fun HelpListItem(
    helpItemData: HelpItemData,
    backgroundColor: Color,
    onItemClick: (HelpItemData) -> Unit,
) {
    val scope = rememberCoroutineScope()

    var isSelected by remember { mutableStateOf(false) }

    val progress by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0f,
        animationSpec = tween(durationMillis = 800),
        label = "LinearProgressAnimation"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxSize()
        ) {
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(75.dp),
                color = if (isSelected) backgroundColor else Color.Transparent,
                backgroundColor = Color.Transparent
            )
        }

        Row(
            modifier = Modifier
                .fillMaxSize()
                .clickable {
                    scope.launch {
                        isSelected = true
                        delay(850)
                        onItemClick(helpItemData)
                    }
                },
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
                                    text = helpItemData.domainId.text,
                                    modifier = Modifier.defaultMinSize(
                                        minHeight = dimensionResource(
                                            R.dimen.dp_24
                                        )
                                    ),
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
                                        helpItemData.command, "string", context.packageName
                                    )

                                    Box(
                                        modifier = Modifier.width(dimensionResource(R.dimen.dp_316))
                                    ) {
                                        Text(
                                            text = helpItemData.command,
                                            color = Color.White,
                                            maxLines = 3,
                                            overflow = TextOverflow.Ellipsis,
                                            style = MaterialTheme.typography.h5.plus(
                                                TextStyle(
                                                    letterSpacing = (-0.48).sp, lineHeight = 32.sp
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
}

@Composable
fun HelpDetailListItem(commandDetail: String) {
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

@Preview
@Composable fun HelpListItemPreview() {
    val helpItem = HelpItemData(
        domainId = SealedDomainType.Help,
        command = "Call",
        commandsDetail = listOf("Call [Contact Name]", "Dial [Phone Number]")
    )

    HelpListItem(helpItemData = helpItem, backgroundColor = Color.Black, onItemClick = {

    })
}