package com.ftd.ivi.cerence.ui.compose

import android.graphics.Bitmap
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils
import com.ftd.ivi.cerence.R
import com.ftd.ivi.cerence.data.UxPreset
import com.ftd.ivi.cerence.ui.theme.CerenceTheme
import com.ftd.ivi.cerence.util.CustomLogger

@Composable
@Preview
fun ButtonPreview() {

    val inverseColor = ColorUtils.compositeColors(
        Color.White.toArgb(),
        Color.Black.toArgb()
    )

    CerenceTheme {
        Surface(
            color = Color.Black,
            modifier = Modifier
                .width(dimensionResource(R.dimen.agent_width))
                .wrapContentHeight()
        ) {
            //TextButton("YES")
            AnimateButton(
                Modifier.height(dimensionResource(R.dimen.dp_60)),
                stringResource(id = R.string.TID_CMN_BUTN_01_04),
                isActive = true,
                onClick = {},
                onFinish = {},
            )
        }
    }
}

@Composable
fun SingleIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: IconButtonColors = IconButtonDefaults.iconButtonColors(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable () -> Unit
) {
    val multipleEventsCutter = remember { MultipleEventsCutter.get() }
    IconButton(
        onClick = {
            multipleEventsCutter.processEvent {
                onClick.invoke()
            }
        },
        modifier = modifier,
        enabled = enabled,
        colors = colors,
        interactionSource = interactionSource,
        content = content
    )

}

@Composable
fun SingleButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = ButtonDefaults.shape,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    elevation: ButtonElevation? = ButtonDefaults.buttonElevation(),
    border: BorderStroke? = null,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable RowScope.() -> Unit
) {
    val multipleEventsCutter = remember { MultipleEventsCutter.get() }

    Button(
        onClick = {
            multipleEventsCutter.processEvent {
                onClick.invoke()
            }
        },
        modifier = modifier,
        enabled = enabled,
        shape = shape,
        colors = colors,
        elevation = elevation,
        border = border,
        contentPadding = contentPadding,
        interactionSource = interactionSource,
        content = content
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AnimateButton(
    modifier: Modifier,
    text: String,
    isActive: Boolean = false,
    isAutoSize: Boolean = false,
    isClick: Boolean = false,
    onClick: (() -> Unit) = {},
    onFinish: (() -> Unit) = {},
) {
    var isPressed by remember { mutableStateOf(false) }
    val bgColor =
        if (isPressed) colorResource(id = R.color.confirm_fill_color) else colorResource(id = R.color.confirm_btn_bg_color)
    val multipleEventsCutter = remember { MultipleEventsCutter.get() }
    var progressAnimDuration by remember { mutableIntStateOf(UxPreset.autoSelectDuration) }
    val clickActive = remember { mutableStateOf(false) }
    var animationTarget by remember { mutableFloatStateOf(1f) }
    var isCanceled by remember { mutableStateOf(false) }
    var isStarted by remember { mutableStateOf(false) }
    var resetAnimation by remember { mutableStateOf(false) }
    var clickTrigger by remember { mutableStateOf(false) }
    val progressAnimation = animateResettableFloat(
        initialValue = 0f,
        targetValue = animationTarget,
        resetFlag = resetAnimation,
        animationSpec = tween(
            durationMillis = progressAnimDuration,
            easing = LinearEasing
        ),
        finishedListener = {
            if (isActive || clickActive.value) {
                onFinish.invoke()
                clickActive.value = false
            }
        }
    )

    fun doClick() {
        onClick.invoke()
        CustomLogger.e("click [$text] isActive:${isActive} ")
        CustomLogger.e("click [$text] isStarted:${isStarted} resetAnimation : $resetAnimation")
        if (isStarted && !resetAnimation) {
            if (isStarted) {
                isCanceled = true
            }
            resetAnimation = true
            onFinish.invoke()
        } else {
            progressAnimDuration = UxPreset.confirmDuration
            isCanceled = false
            resetAnimation = false
            //animationTarget = 1f
            clickActive.value = true
        }
    }


    LaunchedEffect(isActive, clickActive.value) {
        CustomLogger.e("calling [$text] LaunchedEffect isActive enable:${isActive} isCanceled:${isCanceled} resetAnimation:${resetAnimation} isStarted:${isStarted}, clickActive:${clickActive.value}")
        if (isActive || clickActive.value) {
            if (!isCanceled || clickActive.value) {
                resetAnimation = false
                animationTarget = 1f
                isStarted = true
            }
        } else {
//            if (isStarted) {
//                isCanceled = true
//            }
            resetAnimation = true
        }
    }
    LaunchedEffect(isClick) {
        CustomLogger.e("[$text] isClick:${isClick}")
        if (isClick) {
            doClick()
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(RoundedCornerShape(dimensionResource(id = R.dimen.agent_bg_radius)))
            .border(BorderStroke(1.dp, colorResource(id = R.color.white_a15)))
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        multipleEventsCutter.processEvent {
                            doClick()
                        }
                    },
                    onPress = {
                        try {
                            isPressed = true
                            awaitRelease()
                        } finally {
                            isPressed = false
                        }
                    },
                )
            }
            .background(bgColor),
    ) {

        if (isActive || clickActive.value) {
            CustomLogger.e("isActive enable:${isActive} progress:${progressAnimation}")
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxSize(),
                trackColor = colorResource(id = R.color.transparent),
                color = colorResource(id = R.color.radio_band_text_color), //progress color
                //progress = if (isActive && !isCanceled) progressAnimation else 0f
                progress = progressAnimation
            )
            ClipText(text, progress = 1.0f, isInvert = false, isAutoSize = isAutoSize)
            ClipText(text, isInvert = true, progress = progressAnimation, isAutoSize = isAutoSize)
        } else {
            ClipText(text, 1.0f, isInvert = false, isAutoSize)
        }
    }


}

@Composable
fun animateResettableFloat(
    initialValue: Float,
    targetValue: Float,
    resetFlag: Boolean,
    animationSpec: AnimationSpec<Float> = tween(),
    finishedListener: () -> Unit
): Float {
    val currentTarget = rememberUpdatedState(targetValue)
    val animatedValue = remember { Animatable(initialValue) }

    LaunchedEffect(currentTarget.value, resetFlag) {
        if (resetFlag) {
            animatedValue.snapTo(initialValue)  // Instantly reset the value without animation
        } else {
            animatedValue.animateTo(currentTarget.value, animationSpec).also {
                finishedListener()
            }
        }
    }

    return animatedValue.value
}

@Composable
fun DummyBitmap(bitmap: Bitmap) {
    CustomLogger.e("makebitmap bitmapRequested bitmap use height:${bitmap.height} width:${bitmap.width}")
    Box(
        modifier = Modifier
            .wrapContentWidth()
            .fillMaxHeight()
    ) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = null,
            contentScale = ContentScale.None,
            modifier = Modifier.wrapContentSize(
                unbounded = true,
                align = Alignment.TopStart
            )
        )
    }
}

@Composable
fun ClipText(text: String, progress: Float, isInvert: Boolean = true, isAutoSize: Boolean = false) {
    val textColor =
        if (isInvert) colorResource(id = R.color.confirm_text_inverted_color) else Color.White

    if (isAutoSize) {
        AutoSizeText(
            textAlign = TextAlign.Center,
            text = AnnotatedString(text),
            style = MaterialTheme.typography.headlineMedium,
            color = textColor,
            modifier = Modifier
                .fillMaxWidth()
                .drawWithContent {
                    if (layoutDirection == LayoutDirection.Rtl) {
                        clipRect(left = size.width * progress) {
                            this@drawWithContent.drawContent()
                        }
                    } else {
                        clipRect(right = size.width * progress) {
                            this@drawWithContent.drawContent()
                        }
                    }
                }
        )
    } else {
        Text(
            textAlign = TextAlign.Center,
            text = text,
            style = MaterialTheme.typography.headlineMedium,
            color = textColor,
            modifier = Modifier
                .fillMaxWidth()
                .drawWithContent {
                    if (layoutDirection == LayoutDirection.Rtl) {
                        clipRect(left = size.width * progress) {
                            this@drawWithContent.drawContent()
                        }
                    } else {
                        clipRect(right = size.width * progress) {
                            this@drawWithContent.drawContent()
                        }
                    }
                }
        )
    }


}
