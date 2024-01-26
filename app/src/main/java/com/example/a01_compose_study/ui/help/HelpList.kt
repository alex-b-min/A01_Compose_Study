package com.example.a01_compose_study.ui.help

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.a01_compose_study.domain.model.HelpItemData

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
fun HelpDetailListItem(domainId: String, commandDetail: String) {
    Column {
        Text(text = commandDetail)
    }
}

@Composable
fun HelpListItem(domainId: String, command: String) {
    Column {
        Text(text = domainId)
        Text(text = command)
    }
}



@Composable
fun HelpListPreview() {
    val helpItemDataList = listOf(
        HelpItemData(domainId = "Domain1", command = "Command1", commandsDetail = listOf("Detail1", "Detail2")),
        HelpItemData(domainId = "Domain2", command = "Command2", commandsDetail = listOf("Detail3", "Detail4")),
        HelpItemData(domainId = "Domain3", command = "Command3", commandsDetail = listOf("Detail5", "Detail6"))
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