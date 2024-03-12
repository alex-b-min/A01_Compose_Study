package com.example.a01_compose_study.presentation.screen.sendMsg.screen

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.a01_compose_study.data.custom.sendMsg.SendMsgEvent
import com.example.a01_compose_study.domain.model.ScreenType
import com.example.a01_compose_study.presentation.components.top_bar.TopAppBarContent
import com.example.a01_compose_study.presentation.data.UiState
import com.example.a01_compose_study.presentation.screen.main.DomainUiState
import com.example.a01_compose_study.presentation.screen.main.route.VRUiState
import com.example.a01_compose_study.presentation.screen.sendMsg.SendMsgViewModel

@Composable
fun SendMsgScreen(
    viewModel: SendMsgViewModel = hiltViewModel(),
    domainUiState: DomainUiState.SendMessageWindow,

) {
    val lineIndex by viewModel.lineIndex.collectAsState()
    val isVrActive by UiState.isVrActive.collectAsState()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(10.dp))
    ) {
        Column {
            Log.d("sendMsg", "TopAppBarContent")
            TopAppBarContent(
                showNavigationIcon = false,
                onActionIconClick = { viewModel.onSendMsgEvent(SendMsgEvent.OnBack) }
            )

            if (domainUiState.screenType is ScreenType.SayMessage) {
                domainUiState.msgData?.let {
                    Log.d("sendMsg", "SayMessage MessageView")
                    MessageView(
                        name = it.name,
                        phoneNum = it.phoneNum,
                        modifier = Modifier,
                        isSayMessage = true,
                        onButtonClick = { viewModel.onSendMsgEvent(SendMsgEvent.SayMessageNo) }
                    )
                }
            } else if (domainUiState.screenType is ScreenType.SendMessage) {
                Log.d("sendMsg", "SendMessage MessageView")
                domainUiState.msgData?.let {
                    MessageView(
                        name = it.name,
                        phoneNum = it.phoneNum,
                        modifier = Modifier,
                        isSayMessage = false,
                        msgData = it.msg,
                        onButtonClick = { viewModel.onSendMsgEvent(SendMsgEvent.SendMessageYes) }
                    )
                }
            } else if (domainUiState.screenType is ScreenType.MessageAllList) {
                Log.d("sendMsg", "MessageAllList")
                domainUiState.contactList?.let {
                    Log.e("sendMsg", "Screen contactList? 안")
                    Log.e("sendMsg", "AllList:${viewModel.lineIndex}")

                    MsgAllList(
                        contactList = it,
                        lineIndex = lineIndex,
                        onItemClick = { contact ->
                            viewModel.onSendMsgEvent(SendMsgEvent.MsgAllListItemOnClick(contact))
                            Log.e("sendMsg", "Screen click 실행")
                        },
                        onPressed = {

                        }
                    )
                }
            } else if (domainUiState.screenType is ScreenType.MessageSelectNameList) {
                Log.d("sendMsg", "MessageSelectNameList")
                domainUiState.contactList?.let {
                    MsgNameList(
                        contactList = it,
                        lineIndex = lineIndex,
                        onItemClick = { contact ->
//                            isVrActive = false
                            viewModel.onSendMsgEvent(SendMsgEvent.SelectNameListItemOnClick(contact))
                            Log.e("sendMsg", "Screen click 실행")
                        },
                        onPressed = {

                        }
                    )
                }
            } else if (domainUiState.screenType is ScreenType.MessageSelectCategoryList) {
                Log.d("sendMsg", "MessageSelectCategoryList")
                domainUiState.contactList?.let {
                    MsgCategoryList(
                        contactList = it,
                        lineIndex = lineIndex,
                        onItemClick = { contact ->
//                            isVrActive = false
                            viewModel.onSendMsgEvent(
                                SendMsgEvent.SelectCategoryListItemOnClick(
                                    contact
                                )
                            )
                            Log.e("sendMsg", "Screen click 실행")
                        },
                        onPressed = {

                        }
                    )
                }
            }
        }
    }
}