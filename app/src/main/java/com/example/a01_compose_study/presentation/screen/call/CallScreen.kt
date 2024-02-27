package com.example.a01_compose_study.presentation.screen.call

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.a01_compose_study.R
import com.example.a01_compose_study.data.custom.call.fetchAllContacts
import com.example.a01_compose_study.domain.model.HelpItemData
import com.example.a01_compose_study.domain.model.ScreenType
import com.example.a01_compose_study.presentation.screen.main.DomainUiState
import com.example.a01_compose_study.presentation.screen.main.route.VRUiState
import com.example.a01_compose_study.ui.theme.Black2

@Composable
fun CallScreen(
    domainUiState: DomainUiState.CallWindow,
    vrUiState: VRUiState,
    vrDynamicBackground: Color,
    fixedBackground: Color
) {
    if (domainUiState.screenType is ScreenType.CallList) {
        CallListWindow(
            domainUiState = domainUiState,
            vrUiState = vrUiState,
            vrDynamicBackground = vrDynamicBackground,
            fixedBackground = fixedBackground,
            onDismiss = { /*TODO*/ },
            onBackButton = { /*TODO*/ },
            onItemClick = { /*TODO*/ }
        )
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
    onItemClick: (HelpItemData) -> Unit,
) {
    val contactList = domainUiState.data

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
                    .align(Alignment.End)
            )

            CallList(contactList = contactList,
                vrDynamicBackground = vrDynamicBackground,
                fixedBackground = fixedBackground,
                onItemClick = {
                })
        }
    }
}

@Preview
@Composable
fun CallListWindowPreview() {
    CallListWindow(
        domainUiState = DomainUiState.CallWindow(data = fetchAllContacts()),
        vrUiState = VRUiState.PttSpeak(active = true, isError = false),
        vrDynamicBackground = Color.Black,
        fixedBackground = Color.Black,
        onDismiss = {},
        onBackButton = {},
        onItemClick = {}
    )
}