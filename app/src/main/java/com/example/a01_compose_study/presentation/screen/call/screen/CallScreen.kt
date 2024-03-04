package com.example.a01_compose_study.presentation.screen.call.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.a01_compose_study.R
import com.example.a01_compose_study.data.custom.call.fetchAllContacts
import com.example.a01_compose_study.domain.model.HelpItemData
import com.example.a01_compose_study.domain.model.ScreenType
import com.example.a01_compose_study.domain.model.SealedDomainType
import com.example.a01_compose_study.domain.util.ScreenSizeType
import com.example.a01_compose_study.presentation.screen.call.CallBusinessEvent
import com.example.a01_compose_study.presentation.screen.call.CallViewModel
import com.example.a01_compose_study.presentation.screen.main.DomainUiState
import com.example.a01_compose_study.presentation.screen.main.route.VRUiState

@Composable
fun CallScreen(
    callViewModel: CallViewModel = viewModel(),
    domainUiState: DomainUiState.CallWindow,
    vrUiState: VRUiState,
    vrDynamicBackground: Color,
    fixedBackground: Color,
) {
    if (domainUiState.screenType is ScreenType.CallList) {

    }

    else if (domainUiState.screenType is ScreenType.CallIndexedList) {
        Box(modifier = Modifier.fillMaxSize()) {
            CallIndexedListWindow(
                domainUiState = domainUiState,
                vrUiState = vrUiState,
                vrDynamicBackground = vrDynamicBackground,
                fixedBackground = fixedBackground,
                onDismiss = { /*TODO*/ },
                onBackButton = { /*TODO*/ },
                onCalling = {
                    callViewModel.onCallBusinessEvent(event = CallBusinessEvent.Calling)
                },
                onItemClick = { /*TODO*/ }
            )
        }
    } else if (domainUiState.screenType is ScreenType.CallYesNo) {

    }
}

@Composable
fun CallIndexedListWindow(
    domainUiState: DomainUiState.CallWindow,
    vrUiState: VRUiState,
    vrDynamicBackground: Color,
    fixedBackground: Color,
    onDismiss: () -> Unit,
    onBackButton: () -> Unit,
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

            CallList(
                contactList = contactList,
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
    CallIndexedListWindow(
        domainUiState = DomainUiState.CallWindow(
            domainType = SealedDomainType.Call,
            data = fetchAllContacts(),
            screenType = ScreenType.CallIndexedList,
            screenSizeType = ScreenSizeType.Middle
        ),
        vrUiState = VRUiState.PttSpeak(active = true, isError = false),
        vrDynamicBackground = Color.Black,
        fixedBackground = Color.Black,
        onDismiss = {},
        onBackButton = {},
        onCalling = { },
        onItemClick = {}
    )
}