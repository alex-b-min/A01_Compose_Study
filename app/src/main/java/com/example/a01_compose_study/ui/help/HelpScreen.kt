package com.example.a01_compose_study.ui.help

import com.example.a01_compose_study.domain.model.HelpItemData
import com.example.a01_compose_study.domain.model.HelpModel

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import com.example.a01_compose_study.data.DomainType
import com.example.a01_compose_study.data.ScreenData
import com.example.a01_compose_study.data.ScreenType
import com.example.a01_compose_study.domain.model.ServiceViewModel
import com.example.a01_compose_study.domain.util.CustomLogger
import com.example.a01_compose_study.presentation.compose.MultipleEventsCutter
import com.example.a01_compose_study.presentation.compose.get
import com.ftd.ivi.cerence.ui.compose.AnimationRow
import com.ftd.ivi.cerence.ui.compose.ComposeListView

@Composable
fun ComposeHelpScreen(
    viewModel: ServiceViewModel,
    screenData: ScreenData
) {
    CustomLogger.i("ComposeHelpScreen Called")
    val screenType = screenData.screenType
    val manager = screenData.manager as HelpViewModel
    CustomLogger.i("ComposeHelpScreen ScreenType :${screenType} screenData:${screenData.hashCode()}")

    //val managerViewModel : CommViewModel = hiltViewModel()
    Box(
        modifier = Modifier
            .width(dimensionResource(R.dimen.agent_width))
            .fillMaxHeight()
            .defaultMinSize(minHeight = dimensionResource(R.dimen.agent_min_height)),
        contentAlignment = Alignment.TopStart
    ) {

        if (screenType == ScreenType.HelpList) {
            var helpModel = screenData.model as? HelpModel
            helpModel?.let { model ->
                val multipleEventsCutter = remember { MultipleEventsCutter.get() }
                val itemList = model.domainIdList
                ComposeListView(
                    screenData = screenData,
                    modifier = Modifier.wrapContentSize(),
                    listItem = itemList,
                ) { index, isSelected, focusIndex, item ->
                    val data = item as? HelpItemData
                    data?.let {
                        AnimationRow(
                            clickListener = {
                                multipleEventsCutter.processEvent {
                                    screenData.screenState.selectedIndex.value = index
                                    manager.onEvent(HelpEvent.SelectIndex(index))
                                }
                            },
                            selected = isSelected,
                            index = index,
                            aniCancel = focusIndex.value != index,
                            preClick = {
                                focusIndex.value = index
                            },
                            clickable = true,
                        ) {
                            HelpItem(
                                index = index,
                                data = data,
                                focused = true
                            )
                        }
                    }
                }
            }
        } else if (screenType == ScreenType.HelpDetailList) {
            var helpModel = screenData.model as? HelpModel
            helpModel?.let { model ->
                val multipleEventsCutter = remember { MultipleEventsCutter.get() }
                val itemList = model.domainIdList

                val onBackClick = {
                    multipleEventsCutter.processEvent {
                        CustomLogger.d("onBackClick")
                        manager.onEvent(HelpEvent.SetHelpListView)
                    }
                }
                Column(
                    modifier = Modifier
                        .width(dimensionResource(R.dimen.agent_width))
                ) {
                    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.dp_16_7)))
                    HelpDetailHeader(
                        domainId = itemList[manager.detailIndex].domainId,
                        backClickListener = onBackClick,
                    )
                    ComposeListView(
                        screenData = screenData,
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(),
                        listItem = itemList[manager.detailIndex].commandIdList,
                        dividerShow = false
                    ) { index, isSelect, focusIndex, item ->
                        val data = item as? String
                        item.let {
                            CustomLogger.e("index:${index} commandId : $item")
                            HelpDetailCommandsView(item as String)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HelpItem(
    index: Int,
    data: HelpItemData,
    focused: Boolean = false
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
                        DomainView(data.domainId)
                        CommandView(
                            helpItemData = data
                        )
                    }
                }
            }
        }
    }
}

private fun convertDomainRadio(domainId: String, domainText: String): String {

    var domainConvertText = ""

    CustomLogger.d("convertDomainRadio domainId $domainId")
    // 라디오, 미디어 처리
    if (domainId == "TID_CMN_GUID_42_02") {
        if (domainText.contains(",")) {
            CustomLogger.d("convertDomainRadio contain colon")
            domainConvertText = domainText.split(",").first()
        } else if (domainText.contains(" ")) {
            CustomLogger.d("convertDomainRadio contain space ")
            domainConvertText = domainText.split(" ").first()
        } else if (domainText.contains("/")) {
            CustomLogger.d("convertDomainRadio contain slash ")
            domainConvertText = domainText.split("/").first()
        }
    } else {
        domainConvertText = domainText
    }

    return domainConvertText
}

@Composable
private fun DomainView(domainId: String) {
    val context = LocalContext.current
    val getResDomainId = context.resources.getIdentifier(domainId, "string", context.packageName)
    val domainText = context.getString(getResDomainId)
    var domainConvertText = convertDomainRadio(domainId, domainText)

    Text(
        text = domainConvertText,
        modifier = Modifier
            .defaultMinSize(minHeight = dimensionResource(R.dimen.dp_24)),
        color = colorResource(id = R.color.guidance_domain_text_color),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        style = MaterialTheme.typography.h2,
        textAlign = TextAlign.Center,
        fontSize = 16.sp,
        letterSpacing = (-0.32).sp
    )
}

@Composable
private fun CommandView(
    helpItemData: HelpItemData,
) {
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
            helpItemData.commandIdList[0],
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
                        append(commandText.substring(startIndex, range.first))

                        // Apply style to the match
                        withStyle(style = SpanStyle(color = Color.White)) {
                            append(match.substring(1, match.length - 1)) // Exclude {}
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
                style = MaterialTheme.typography.h2.plus(
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

@Composable
private fun HelpDetailHeader(
    domainId: String,
    backClickListener: () -> Unit,
) {
    Row {
        Image(
            painterResource(id = R.drawable.ic_guidance_btn_back),
            contentDescription = null,
            modifier = Modifier
                .padding(start = dimensionResource(R.dimen.dp_5_3))
                .clickable {
                    backClickListener.invoke()
                },
        )

        val context = LocalContext.current
        val getResDomainId =
            context.resources.getIdentifier(domainId, "string", context.packageName)
        val domainText = context.getString(getResDomainId)
        var domainConvertText = convertDomainRadio(domainId, domainText)

        Text(
            text = domainConvertText,
            modifier = Modifier
                .padding(start = dimensionResource(R.dimen.dp_5_4))
                .align(Alignment.CenterVertically),
            color = colorResource(id = R.color.white_a80),
            maxLines = 1,
            fontSize = 21.3.sp,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.body2,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun HelpDetailCommandsView(commandId: String) {
    val context = LocalContext.current
    val getResCommandId = context.resources.getIdentifier(commandId, "string", context.packageName)
    val commandText = context.getString(getResCommandId)
    val pattern = Regex("\\{([^}]*)\\}")
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = dimensionResource(R.dimen.dp_80)),
        contentAlignment = Alignment.CenterStart
    ) {
        Row {
            Spacer(modifier = Modifier.width(dimensionResource(R.dimen.dp_33)))
            Text(
                text = buildAnnotatedString {
                    var startIndex = 0
                    pattern.findAll(commandText).forEach { result ->
                        val match = result.value
                        val range = result.range

                        // Append text before the match
                        append(commandText.substring(startIndex, range.first))

                        // Apply style to the match
                        withStyle(style = SpanStyle(color = Color.White)) {
                            append(match.substring(1, match.length - 1)) // Exclude {}
                        }

                        startIndex = range.last + 1
                    }

                    // Append the remaining text
                    if (startIndex < commandText.length) {
                        append(commandText.substring(startIndex))
                    }
                },
                modifier = Modifier.padding(
                    top = dimensionResource(R.dimen.dp_15),
                    bottom = dimensionResource(R.dimen.dp_15),
                    end = dimensionResource(R.dimen.dp_33)
                ),
                color = colorResource(id = R.color.white),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.h2,
                letterSpacing = (-0.43).sp,
                fontSize = 21.3.sp,
                textAlign = TextAlign.Start
            )
        }
    }
}

@Preview
@Composable
fun HelpItemPreview() {

    val data = HelpItemData()
    val communicationList =
        mutableListOf("TID_CMN_GUID_13_12", "TID_CMN_GUID_13_13", "TID_CMN_GUID_13_13_1")

    data.domainId = "Contacts"
    data.commandIdList = communicationList

    HelpItem(index = 2, data = data)
}

@Preview
@Composable
fun HelpCommandsViewPreview() {

    Box(
        modifier = Modifier
            .width(dimensionResource(R.dimen.agent_width))
            .wrapContentHeight()
            .defaultMinSize(minHeight = dimensionResource(R.dimen.agent_min_height)),
        contentAlignment = Alignment.TopStart
    ) {
        val model = HelpModel("")

        model.domainIdList.add(
            0,
            HelpItemData(
                "Navigation",
                mutableListOf(
                    "{Find} Filling station in London",
                    "{Find} Charging station in London",
                    "Go Home",
                    "Go to office",
                    "Previous destinations",
                    "Cancel route",
                    "Destination information"
                )
            )
        )
        model.domainIdList.add(
            0,
            HelpItemData(
                "Contact",
                mutableListOf(
                    "{Call} John Smith",
                    "{Call} John Smith on mobile / at home / in office",
                    "{Send Message to} John Smith"
                )
            )
        )
        model.domainIdList.add(
            0,
            HelpItemData(
                "Weather",
                mutableListOf(
                    "How is the weather?",
                    "How is the weather this week?",
                    "What’s the weather forecast for Tuesday?",
                    "Is it going to rain tomorrow?"
                )
            )
        )
        model.domainIdList.add(
            0,
            HelpItemData(
                "Radio",
                mutableListOf(
                    "DAB/FM List",
                    "FM List",
                    "AM List",
                    "{Play} BBC Radio 1 {on Radio}"
                )
            )
        )
        val screenData = ScreenData(DomainType.Radio, ScreenType.RadioList)
        val itemList = model.domainIdList
        Box(
            modifier = Modifier
                .width(dimensionResource(R.dimen.agent_width))
                .fillMaxHeight()
                .defaultMinSize(minHeight = dimensionResource(R.dimen.agent_min_height)),
            contentAlignment = Alignment.TopStart
        ) {
            ComposeListView(
                screenData = screenData,
                modifier = Modifier.wrapContentSize(),
                listItem = itemList,
            ) { index, isSelected, focusIndex, item ->
                val data = item as? HelpItemData
                data?.let {
                    AnimationRow(
                        clickListener = {
                        },
                        selected = isSelected,
                        index = index,
                        aniCancel = focusIndex.value != index,
                        preClick = {
                            focusIndex.value = index
                        },
                        clickable = true,
                    ) {
                        HelpItem(
                            index = index,
                            data = data,
                            focused = true
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun HelpDetailCommandsViewPreview() {

    val data = HelpItemData()
    val naviList = mutableListOf("Destination information")

    data.domainId = "Navi"
    data.commandIdList = naviList
    HelpDetailCommandsView(data.commandIdList[0])
}

@Preview
@Composable
fun HelpDetailPreview() {

    Box(
        modifier = Modifier
            .width(dimensionResource(R.dimen.agent_width))
            .wrapContentHeight()
            .defaultMinSize(minHeight = dimensionResource(R.dimen.agent_min_height)),
        contentAlignment = Alignment.TopStart
    ) {

        val model = HelpModel("")

        model.domainIdList.add(
            0,
            HelpItemData(
                "Navigation",
                mutableListOf(
                    "{Find} Filling station in London",
                    "{Find} Charging station in London",
                    "Go Home",
                    "Go to office",
                    "Previous destinations",
                    "Cancel route",
                    "Destination information",
                    "{Play} BBC Radio 1 {on Radio}"
                )
            )
        )
        val screenData = ScreenData(DomainType.Radio, ScreenType.RadioList)
        val itemList = model.domainIdList

        val onBackClick = {
        }

        Column(
            modifier = Modifier
                .width(dimensionResource(R.dimen.agent_width))
        ) {
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.dp_16_7)))
            HelpDetailHeader(
                domainId = itemList[0].domainId,
                backClickListener = onBackClick,
            )
            ComposeListView(
                screenData = screenData,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                listItem = itemList[0].commandIdList,
                dividerShow = false
            ) { index, isSelected, focusIndex, item ->

                val data = item as? String
                item.let {
                    CustomLogger.e("index:${index} commandId : $item")
                    HelpDetailCommandsView(item as String)
                }
            }
        }
    }
}
