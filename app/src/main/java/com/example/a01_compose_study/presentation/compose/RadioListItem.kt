package com.ftd.ivi.cerence.ui.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ftd.ivi.cerence.R
import com.ftd.ivi.cerence.data.model.ui.RadioItem
import com.ftd.ivi.cerence.ui.theme.CerenceTheme

@Composable
fun RadioListItem(
    index: Int,
    channelInfo: RadioItem,
    focused: Boolean = false,
) {
    val channelName = channelInfo.channelName
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = dimensionResource(R.dimen.dp_80))
            .alpha(if (focused) 1.0f else 0.3f)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(77.3.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(
                        top = dimensionResource(R.dimen.dp_16),
                        bottom = dimensionResource(R.dimen.dp_16),
                        end = 29.3.dp
                    )
            ) {
                SequenceView(index)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(45.3.dp)
                ) {
                    ChannelInfoView(channelName)
                }
            }
        }
    }
}

@Composable
private fun ChannelInfoView(channelName: String) {
    Column(
        verticalArrangement = Arrangement.Center,
    ) {
        Box(modifier = Modifier.height(45.3.dp), contentAlignment = Alignment.Center) {
            Text(
                text = channelName,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center,
                letterSpacing = (-0.53).sp
            )
        }
    }
}

@Composable
private fun SequenceView(index: Int) {
    Box(
        modifier = Modifier
            .width(dimensionResource(R.dimen.dp_66_7))
            .height(45.3.dp), contentAlignment = Alignment.Center
    ) {
        Text(
            text = "${index + 1}.",
            color = Color.White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )
    }
}


@Preview
@Composable
fun RadioListItemPreview() {
    var radioItem = RadioItem("0", "FM", "KBS 1FM RADIO STA")


    CerenceTheme {
        Surface(
            color = Color.Black,
            modifier = Modifier.width(dimensionResource(R.dimen.agent_width))
        ) {
            RadioListItem(
                index = 0,
                channelInfo = radioItem,
            )
        }
    }
}