package com.ftd.ivi.cerence.ui.compose

import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import com.example.a01_compose_study.domain.util.CustomLogger

sealed class AutoSizeConstraint(open val min: TextUnit = TextUnit.Unspecified) {
    data class Width(override val min: TextUnit = TextUnit.Unspecified) : AutoSizeConstraint(min)
    data class Height(override val min: TextUnit = TextUnit.Unspecified) : AutoSizeConstraint(min)
    data class Both(override val min: TextUnit = TextUnit.Unspecified) : AutoSizeConstraint(min)
}

@Composable
fun AutoSizeText(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Visible,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    style: TextStyle = LocalTextStyle.current,
    constraint: AutoSizeConstraint = AutoSizeConstraint.Both(),
) {
    var textSize by remember { mutableStateOf(fontSize) }
    var textStyle by remember { mutableStateOf(style) }
    var readyToDraw by remember { mutableStateOf(false) }
    var lastSize by remember { mutableFloatStateOf(0f) }


    LaunchedEffect(textStyle) {
        CustomLogger.e("LaunchedEffect(textStyle)")
    }

    Text(
        modifier = modifier.drawWithContent {
            if (readyToDraw) drawContent()
        },
        text = text,
        color = color,
        fontSize = textSize,
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

            fun constrain() {
                val reducedSize = textStyle.fontSize * 0.9f
                if (constraint.min != TextUnit.Unspecified && reducedSize <= constraint.min) {
                    textStyle = textStyle.copy(fontSize = constraint.min)
                    //readyToDraw = true
                } else {
                    //textStyle = textStyle.copy(fontSize = textStyle.fontSize * 0.9f)
                    if (fontSize.type != TextUnitType.Unspecified) {
                        textSize = TextUnit(textSize.value * 0.9f, textSize.type)
                    } else {
                        textStyle = textStyle.copy(fontSize = textStyle.fontSize * 0.9f)
                    }

                }

            }

            when (constraint) {
                is AutoSizeConstraint.Height -> {
                    //CustomLogger.e("constrain AutoSizeConstraint.Height")
                    if (result.didOverflowHeight) {
                        constrain()
                    } else {
                        readyToDraw = true
                    }
                }

                is AutoSizeConstraint.Width -> {
                    //CustomLogger.e("constrain AutoSizeConstraint.Width")
                    if (result.didOverflowWidth) {
                        constrain()
                    } else {
                        readyToDraw = true
                    }
                }

                is AutoSizeConstraint.Both -> {
                    //CustomLogger.e("constrain AutoSizeConstraint.Both isOverflow:${result.hasVisualOverflow} text:${text} constraint.min:${constraint.min} size:${textStyle.fontSize} fontSize:${textSize.value}")
                    if (result.hasVisualOverflow) {
                        constrain()
                    } else {
                        readyToDraw = true
                    }
                }
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