package com.example.a01_compose_study.ui.help

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
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
import com.example.a01_compose_study.domain.model.HelpItemData

@Composable
fun ComposeHelpScreen(

) {

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
        HelpDetailListItem(domainId = helpItemData.domainId, commandDetail = commandDeatail)
    }
}


@Composable
fun HelpListItem(domainId: String, command: String, focused: Boolean = false) {
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
                                text = domainId,
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
                                val commandText = context.getString(commandId)
                                val pattern = Regex("\\{([^}]*)\\}")
                                Box(
                                    modifier = Modifier.width(dimensionResource(R.dimen.dp_316))
                                ) {
                                    Text(
                                        text = buildAnnotatedString {
                                            var startIndex = 0
                                            pattern.findAll(commandText).forEach { result ->
                                                val match = result.value
                                                val range = result.range

                                                // Append text before the match
                                                append(
                                                    commandText.substring(
                                                        startIndex,
                                                        range.first
                                                    )
                                                )

                                                // Apply style to the match
                                                withStyle(style = SpanStyle(color = Color.White)) {
                                                    append(
                                                        match.substring(
                                                            1,
                                                            match.length - 1
                                                        )
                                                    ) // Exclude {}
                                                }

                                                startIndex = range.last + 1
                                            }

                                            // Append the remaining text
                                            if (startIndex < commandText.length) {
                                                append(commandText.substring(startIndex))
                                            }
                                        },
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
            domainId = "Domain1",
            command = "Command1",
            commandsDetail = listOf("Detail1", "Detail2")
        ),
        HelpItemData(
            domainId = "Domain2",
            command = "Command2",
            commandsDetail = listOf("Detail3", "Detail4")
        ),
        HelpItemData(
            domainId = "Domain3",
            command = "Command3",
            commandsDetail = listOf("Detail5", "Detail6")
        )
    )

    HelpList(helpList = helpItemDataList)
}

@Composable
fun HelpDetailListPreview() {
    val helpItemData = HelpItemData(
        domainId = "Domain1",
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