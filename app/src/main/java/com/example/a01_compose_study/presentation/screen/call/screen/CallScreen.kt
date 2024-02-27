package com.example.a01_compose_study.presentation.screen.call.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.a01_compose_study.R
import com.example.a01_compose_study.data.custom.call.fetchAllContacts
import com.example.a01_compose_study.domain.model.HelpItemData
import com.example.a01_compose_study.domain.model.ScreenType
import com.example.a01_compose_study.domain.model.SealedDomainType
import com.example.a01_compose_study.domain.util.ScreenSizeType
import com.example.a01_compose_study.presentation.screen.call.CallEvent
import com.example.a01_compose_study.presentation.screen.call.CallViewModel
import com.example.a01_compose_study.presentation.screen.main.DomainUiState
import com.example.a01_compose_study.presentation.screen.main.route.VRUiState

@Composable
fun CallScreen(
    callViewModel: CallViewModel = hiltViewModel(),
    domainUiState: DomainUiState.CallWindow,
    vrUiState: VRUiState,
    vrDynamicBackground: Color,
    fixedBackground: Color,
//    onChangeScrollIndex: () -> Unit
) {
    if (domainUiState.screenType is ScreenType.CallList) {
        Box(modifier = Modifier.fillMaxSize()) {
            CallListWindow(
                domainUiState = domainUiState,
                vrUiState = vrUiState,
                vrDynamicBackground = vrDynamicBackground,
                fixedBackground = fixedBackground,
                onDismiss = { /*TODO*/ },
                onBackButton = { /*TODO*/ },
                onChangeScrollIndex = {
                    /**
                     * 이렇게 domainUiState의 scrollIndex의 값을 넣어주고 하위뷰인 CallListWindow()에서 사용해줘야함
                     * 하지만 현재 domainUiState의 scrollIndex의 값을 변경 시켜주는 이
                     */
//                    domainUiState.scrollIndex?.let {
//                        CallEvent.ChangeScrollIndexEvent(
//                            it
//                        )
//                    }?.let { callViewModel.onCallEvent(it) }

                },
                onCalling = {
                    callViewModel.onCallEvent(event = CallEvent.Calling)
                },
                onItemClick = { /*TODO*/ }
            )
        }
    } else if (domainUiState.screenType is ScreenType.CallYesNo) {

    }
}

@Composable
fun CallListWindow(
    domainUiState: DomainUiState.CallWindow,
    vrUiState: VRUiState,
    vrDynamicBackground: Color,
    fixedBackground: Color,
    onDismiss: () -> Unit,
    onBackButton: () -> Unit,
    onChangeScrollIndex: () -> Unit,
    onCalling: () -> Unit,
    onItemClick: (HelpItemData) -> Unit,
) {
    val contactList = domainUiState.data
    val selectedIndex = domainUiState.scrollIndex

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(15.dp))
            .background(vrDynamicBackground)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_close),
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier
                    .padding(end = 8.dp, top = 8.dp)
                    .clickable {
                    }
            )

            CallList(contactList = contactList,
                vrDynamicBackground = vrDynamicBackground,
                selectedIndex = selectedIndex,
                fixedBackground = fixedBackground,
                onCalling = onCalling,
                onItemClick = {
                })
        }
    }
}

@Preview
@Composable
fun CallListWindowPreview() {
    CallListWindow(
        domainUiState = DomainUiState.CallWindow(
            domainType = SealedDomainType.Call,
            data = fetchAllContacts(),
            screenType = ScreenType.CallList,
            screenSizeType = ScreenSizeType.Middle
        ),
        vrUiState = VRUiState.PttSpeak(active = true, isError = false),
        vrDynamicBackground = Color.Black,
        fixedBackground = Color.Black,
        onDismiss = {},
        onBackButton = {},
        onChangeScrollIndex = {},
        onCalling = { },
        onItemClick = {}
    )
}