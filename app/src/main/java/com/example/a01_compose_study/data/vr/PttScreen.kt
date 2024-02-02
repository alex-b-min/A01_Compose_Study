package com.example.a01_compose_study.data.vr

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.a01_compose_study.R
import com.example.a01_compose_study.data.HVRState
import com.example.a01_compose_study.domain.model.ScreenType
import com.example.a01_compose_study.presentation.ServiceViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PttScreen @Inject constructor(
    @ApplicationContext val context: Context,
    val viewModel: ServiceViewModel,
) {
    val TAG : String? = this.javaClass.simpleName
    fun showPtt() {
        //viewModel.getSttString().value = ""
    }
}

@Composable
fun ComposePttScreen(viewModel: ServiceViewModel = hiltViewModel()) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                //start = dimensionResource(R.dimen.chrome_common_fg_text_start_margin),
                start = dimensionResource(R.dimen.dp_16_7),
                //end = dimensionResource(R.dimen.chrome_common_fg_text_end_margin),
                end = dimensionResource(R.dimen.dp_16_7),
                top = dimensionResource(R.dimen.agent_navigation_waypoint_guide_top),
                bottom = dimensionResource(R.dimen.agent_navigation_waypoint_guide_top),
            )
            .semantics {
                contentDescription = "TBD"
                testTag = "${ScreenType.Ptt}"
            }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().apply {
                wrapContentHeight()
                    .defaultMinSize(minHeight = dimensionResource(R.dimen.agent_min_height))
            },
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
//            val isListening =
//               // viewModel.mwState.vrState.collectAsState().value != HVRState.IDLE
//            val sttString = viewModel.getSttString().collectAsState()
//            if (sttString.value.isNotEmpty()) {
                //SttView(viewModel)
//                if (isListening) {
//                    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.dp_30)))
//                }
//            } else {
//
//                AnnounceTextView(viewModel)
//                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.dp_10)))
//                GuidanceTextView(viewModel, autoSize = true)
//                if (isListening) {
//                    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.dp_30)))
//                }
//            }

        }
    }


}
