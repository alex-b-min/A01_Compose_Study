package com.ftd.ivi.cerence.ui.compose

import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.hilt.navigation.compose.hiltViewModel
import com.ftd.ivi.cerence.R
import com.ftd.ivi.cerence.util.CustomLogger
import com.ftd.ivi.cerence.viewmodel.ServiceViewModel

@Composable
fun ParentSizeText(
    viewModel: ServiceViewModel = hiltViewModel(),
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = TextAlign.Center,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Visible,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    style: TextStyle = LocalTextStyle.current,
    constraint: AutoSizeConstraint = AutoSizeConstraint.Both(),
) {
    var textStyle by remember { mutableStateOf(style) }
    var readyToDraw by remember { mutableStateOf(false) }
    var multiplier by remember { mutableStateOf(1f) }
    var beforeHeight by remember { mutableStateOf(0f) }
    val minHeight = dimensionResource(id = R.dimen.agent_min_height)
    val uiState = viewModel.uiState

    LaunchedEffect(textStyle) {
        CustomLogger.e("LaunchedEffect(textStyle)")
    }

    Text(
        modifier = modifier.drawWithContent {
            if (readyToDraw) drawContent()
        },
        text = text,
        color = color,
        fontSize = fontSize,
        fontStyle = fontStyle,
        fontWeight = fontWeight,
        fontFamily = fontFamily,
        letterSpacing = letterSpacing,
        textDecoration = textDecoration,
        textAlign = textAlign,
        lineHeight = lineHeight,
        overflow = overflow,
        softWrap = softWrap,
        maxLines = maxLines,
        style = textStyle,
        onTextLayout = { result ->

            if (result.didOverflowHeight || beforeHeight > result.size.height) {
                if (uiState.boxAnimationFinish.value) {
                    uiState.wishHeight.value =
                        result.multiParagraph.height.coerceAtLeast(minHeight.value)
                    CustomLogger.e("wishHeight: ${result.multiParagraph.height} min:${minHeight.value}")
                } else {
                    CustomLogger.e("else wishHeight: ${uiState.wishHeight.value} anim:${uiState.boxAnimationFinish.value} min:${minHeight.value}")
                }
                readyToDraw = true
            } else {
                beforeHeight = result.multiParagraph.height
                uiState.wishHeight.value = 0f
                CustomLogger.e("wishHeight done: ${result.multiParagraph.height}, ${result.size.height} beforeHeight:${beforeHeight}")
                readyToDraw = true
            }

        }
    )
}
//
//@Composable
//fun AutoSizeText(
//    text: String,
//    modifier: Modifier = Modifier,
//    color: Color = Color.Unspecified,
//    fontSize: TextUnit = TextUnit.Unspecified,
//    fontStyle: FontStyle? = null,
//    fontWeight: FontWeight? = null,
//    fontFamily: FontFamily? = null,
//    letterSpacing: TextUnit = TextUnit.Unspecified,
//    textDecoration: TextDecoration? = null,
//    textAlign: TextAlign? = null,
//    lineHeight: TextUnit = TextUnit.Unspecified,
//    overflow: TextOverflow = TextOverflow.Clip,
//    softWrap: Boolean = true,
//    maxLines: Int = Int.MAX_VALUE,
//    style: TextStyle = LocalTextStyle.current,
//    constraint: AutoSizeConstraint = AutoSizeConstraint.Both(),
//) {
//    AutoSizeText(
//        modifier = modifier,
//        text = AnnotatedString(text),
//        color = color,
//        fontSize = fontSize,
//        fontStyle = fontStyle,
//        fontWeight = fontWeight,
//        fontFamily = fontFamily,
//        letterSpacing = letterSpacing,
//        textDecoration = textDecoration,
//        textAlign = textAlign,
//        lineHeight = lineHeight,
//        overflow = overflow,
//        softWrap = softWrap,
//        maxLines = maxLines,
//        style = style,
//        constraint = constraint
//    )
//}