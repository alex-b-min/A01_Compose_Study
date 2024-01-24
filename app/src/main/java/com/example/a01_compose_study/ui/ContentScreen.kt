package com.example.a01_compose_study.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.tooling.preview.Preview
import com.example.a01_compose_study.R
import com.example.a01_compose_study.data.DomainType
import com.example.a01_compose_study.data.ScreenData
import com.example.a01_compose_study.data.ScreenType
import com.example.a01_compose_study.domain.model.ServiceViewModel
import com.example.a01_compose_study.domain.util.CustomLogger
import com.example.a01_compose_study.ui.help.ComposeHelpScreen
import com.ftd.ivi.cerence.ui.compose.DummyBitmap


@Composable
fun DisplayDummy(currScreenData: ScreenData) {
    CustomLogger.e("makebitmap displayDummy bitmap:${(currScreenData.isBitmapReady())}")
    if (currScreenData.isBitmapReady()) {
        currScreenData.bitmap?.let {
            DummyBitmap(bitmap = it)
        }
    }
}

@Composable
fun ContentScreen(
    modifier: Modifier = Modifier,
    viewModel:ServiceViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
) {

    val currScreenData = viewModel.currScreen.collectAsState().value
    val domainType = currScreenData?.domainType
    val screenType = currScreenData?.screenType

    CustomLogger.i("ContentScreen Called :${currScreenData?.hashCode()}")

    LaunchedEffect(currScreenData) {
        CustomLogger.i("LaunchedEffect(currDialogueMode) :${currScreenData?.screenType}")
    }

    currScreenData?.let {
        Box(
            modifier = modifier.semantics {
                contentDescription = "TBD"
                testTag = "${ScreenType.ContentScreen}"
            },
            contentAlignment = Alignment.TopStart

        ) {
            val boxAnimationFinish = viewModel.uiState.boxAnimationFinish.collectAsState()
            CustomLogger.i("makebitmap boxAnimationFinish : ${boxAnimationFinish.value} ")
            if (screenType?.name?.contains(
                    "List",
                    ignoreCase = true
                ) == true && (!boxAnimationFinish.value) && currScreenData.isBitmapReady()
            ) {
                DisplayDummy(currScreenData)
            } else {
                doDisplayScreen(it, viewModel)
            }
        }
    }

}


@Composable
fun doDisplayScreen(screenData: ScreenData, viewModel: ServiceViewModel, force: Boolean = false) {

    when (screenData.domainType) {
        DomainType.MainMenu -> {

        }

        DomainType.Call -> {

        }

        DomainType.Navigation -> {

        }

        DomainType.Radio -> {

        }

        DomainType.Weather -> {

        }

        DomainType.SendMessage -> {

        }

        DomainType.Help -> {
            Box(
                modifier = Modifier
                    .wrapContentSize()
                    .defaultMinSize(minHeight = dimensionResource(R.dimen.agent_min_height)),
                contentAlignment = Alignment.Center
            ) {
                ComposeHelpScreen(viewModel, screenData = screenData)
            }
        }

        DomainType.Announce -> {


        }

        else -> {

        }
    }
}


@Preview
@Composable
fun ContentScreenPreview() {

    ContentScreen(modifier = Modifier.wrapContentSize())

}
