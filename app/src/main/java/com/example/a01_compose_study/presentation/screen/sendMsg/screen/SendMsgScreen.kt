package com.example.a01_compose_study.presentation.screen.sendMsg.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.a01_compose_study.domain.model.ScreenType
import com.example.a01_compose_study.presentation.components.top_bar.TopAppBarContent
import com.example.a01_compose_study.presentation.screen.help.HelpViewModel
import com.example.a01_compose_study.presentation.screen.main.DomainUiState

@Composable
fun SendMsgScreen(
    viewModel: HelpViewModel = hiltViewModel(),
    domainUiState: DomainUiState.SendMessageWindow,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(10.dp))
    ) {
        Column {
            TopAppBarContent(showNavigationIcon = false)
            if (domainUiState.screenType is ScreenType.SayMessage) {
                domainUiState.msgData?.let {
                    MessageView(
                        name = it.name,
                        phoneNum = domainUiState.msgData.phoneNum,
                        modifier = Modifier,
                        isSayMessage = true
                    )
                }
            } else if (domainUiState.screenType is ScreenType.SendMessage) {
                domainUiState.msgData?.let {
                    MessageView(
                        name = it.name,
                        phoneNum = domainUiState.msgData.phoneNum,
                        modifier = Modifier,
                        isSayMessage = false,
                        msgData = it.msg,
                    )
                }
            } else if (domainUiState.screenType is ScreenType.MessageAllList) {
                domainUiState.contactList?.let { MsgAllList(it) }
            } else if (domainUiState.screenType is ScreenType.MessageSelectNameList) {
                domainUiState.contactList?.let { MsgNameList(it) }
            } else if (domainUiState.screenType is ScreenType.MessageSelectCategoryList) {
                domainUiState.contactList?.let { MsgCategoryList(it) }
            }
        }
    }
}