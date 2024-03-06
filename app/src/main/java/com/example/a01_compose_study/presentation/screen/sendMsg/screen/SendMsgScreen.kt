package com.example.a01_compose_study.presentation.screen.sendMsg.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.a01_compose_study.domain.model.ScreenType
import com.example.a01_compose_study.presentation.components.top_bar.TopAppBarContent
import com.example.a01_compose_study.presentation.screen.help.HelpViewModel
import com.example.a01_compose_study.presentation.screen.main.DomainUiState

@Composable
fun SendMsgScreen (
    viewModel: HelpViewModel = hiltViewModel(),
    domainUiState: DomainUiState.SendMessageWindow,
){

    Box(modifier = Modifier.fillMaxSize()){
        Column {
            TopAppBarContent(showNavigationIcon = false)
            if (domainUiState.screenType is ScreenType.SayMessage){
                MessageView(modifier = Modifier)
            } else if (domainUiState.screenType is ScreenType.SendMessage){
                MessageView(modifier = Modifier)
            } else if (domainUiState.screenType is ScreenType.MessageAllList){
                MsgAllListItem()
            }else if (domainUiState.screenType is ScreenType.MessageSelectNameList){
                MsgNameListItem()
            }else if (domainUiState.screenType is ScreenType.MessageSelectCategoryList){
                MsgCategoryListItem()
            }
        }
    }
}