package com.ftd.ivi.cerence.ui.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ftd.ivi.cerence.R
import com.ftd.ivi.cerence.util.CustomLogger

@Composable
fun ComposeButton(
    modifier: Modifier,
    buttonText: String,
    clickEvent: () -> Unit
) {
    Box(modifier = modifier) {
        Button(
            onClick = clickEvent,
            modifier = Modifier
                .width(dimensionResource(R.dimen.dp_360))
                .height(dimensionResource(R.dimen.dp_60)),
            shape = RoundedCornerShape(5.dp)
        ) {
            Text(buttonText)
        }
    }
}

@Preview
@Composable
fun ComposeButtonPreview() {
    ComposeButton(
        modifier = Modifier.wrapContentSize(),
        "아니"
    ) { CustomLogger.d("Button 2 clicked") }

}