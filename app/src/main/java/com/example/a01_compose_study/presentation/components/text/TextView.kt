package com.example.a01_compose_study.presentation.components.text

import androidx.compose.foundation.layout.padding
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import com.example.a01_compose_study.R

@Composable
fun TextView(
    modifier: Modifier,
    text: String,
    maxLines: Int = 2,
    textAlign: TextAlign = TextAlign.Center,
    style: TextStyle = LocalTextStyle.current,
    letterSpacing: Double = -0.01,
    textSize: Dp = dimensionResource(id = R.dimen.chrome_common_fg_text_size),
    textColor: Color = colorResource(id = R.color.white)
) {

    Text(
        text = (text),
        fontWeight = FontWeight.Medium,
        color = textColor,
        fontSize = textSize.value.sp,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis,
        letterSpacing = letterSpacing.sp,
        textAlign = textAlign,
        //lineSpacing = dimensionResource(id = R.dimen.chrome_common_fg_display_text_lineSpacingExtra).value.sp,
        style = TextStyle(
            lineHeight = (textSize.value + dimensionResource(id = R.dimen.chrome_common_fg_display_text_lineSpacingExtra).value).sp,
            platformStyle = PlatformTextStyle(
                includeFontPadding = false,
            )
        ),
        modifier = modifier.apply {
            padding(
                start = dimensionResource(R.dimen.chrome_common_fg_text_start_margin),
                end = dimensionResource(R.dimen.chrome_common_fg_text_end_margin),
            )
            semantics {
                testTag = "TextView"
            }
        }
    )
}