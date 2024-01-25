//package com.ftd.ivi.cerence.ui.compose
//
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.border
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.width
//import androidx.compose.foundation.layout.wrapContentHeight
//import androidx.compose.material.LocalTextStyle
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Surface
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.livedata.observeAsState
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.RectangleShape
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.res.colorResource
//import androidx.compose.ui.res.dimensionResource
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.semantics.semantics
//import androidx.compose.ui.semantics.testTag
//import androidx.compose.ui.text.AnnotatedString
//import androidx.compose.ui.text.PlatformTextStyle
//import androidx.compose.ui.text.TextStyle
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.text.style.TextOverflow
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.Dp
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.hilt.navigation.compose.hiltViewModel
//import com.airbnb.lottie.compose.LottieAnimation
//import com.airbnb.lottie.compose.LottieCompositionSpec
//import com.airbnb.lottie.compose.LottieConstants
//import com.airbnb.lottie.compose.rememberLottieComposition
//import com.ftd.ivi.cerence.R
//import com.ftd.ivi.cerence.ui.theme.CerenceTheme
//import com.ftd.ivi.cerence.ui.theme.fonts
//import com.ftd.ivi.cerence.util.CustomLogger
//import com.ftd.ivi.cerence.viewmodel.ServiceViewModel
//import kotlinx.coroutines.flow.asStateFlow
//
//@Composable
//@Preview
//fun TextViewPreview() {
//    CerenceTheme {
//        Surface(
//            color = Color.Black,
//            modifier = Modifier
//                .width(dimensionResource(R.dimen.agent_width))
//                .wrapContentHeight()
//        ) {
//            TextView(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .wrapContentHeight(),
//                "현대기아제네시스 자동차 콜 두줄 테스트 이렇게 이렇게 저렇게 길게 길게 어떻게"
//            )
//
//        }
//    }
//}
//
//@Composable
//fun TextView(
//    modifier: Modifier,
//    text: String,
//    maxLines: Int = 2,
//    textAlign: TextAlign = TextAlign.Center,
//    style: TextStyle = LocalTextStyle.current,
//    letterSpacing: Double = -0.01,
//    textSize: Dp = dimensionResource(id = R.dimen.chrome_common_fg_text_size),
//    textColor: Color = colorResource(id = R.color.white)
//) {
//
//    Text(
//        text = (text),
//        fontFamily = fonts,
//        fontWeight = FontWeight.Medium,
//        color = textColor,
//        fontSize = textSize.value.sp,
//        maxLines = maxLines,
//        overflow = TextOverflow.Ellipsis,
//        letterSpacing = letterSpacing.sp,
//        textAlign = textAlign,
//        //lineSpacing = dimensionResource(id = R.dimen.chrome_common_fg_display_text_lineSpacingExtra).value.sp,
//        style = TextStyle(
//            lineHeight = (textSize.value + dimensionResource(id = R.dimen.chrome_common_fg_display_text_lineSpacingExtra).value).sp,
//            platformStyle = PlatformTextStyle(
//                includeFontPadding = false,
//            )
//        ),
//        modifier = modifier.apply {
//            padding(
//                start = dimensionResource(R.dimen.chrome_common_fg_text_start_margin),
//                end = dimensionResource(R.dimen.chrome_common_fg_text_end_margin),
//            )
//            semantics {
//                testTag = "TextView"
//            }
//        }
//    )
//}
//
//@Composable
//fun AnnounceTextView(viewModel: ServiceViewModel = hiltViewModel()) {
//
//    Box(
//        modifier = Modifier
//            .fillMaxWidth(),
//        contentAlignment = Alignment.Center
//    ) {
//        val displayText = viewModel.announceString.observeAsState().value ?: ""
//        //val isListening = viewModel.mwStateLiveData.collectAsState().value.vrState != HVRState.IDLE
//        MainDisplayText(displayText)
//    }
//}
//
//@Composable
//fun GuidanceTextView(viewModel: ServiceViewModel = hiltViewModel(), autoSize: Boolean = false) {
//
//    if (autoSize) {
//        AutoSizeText(
//            modifier = Modifier
//                .fillMaxWidth(1f),
//
//            text = AnnotatedString((viewModel.guideString.observeAsState().value ?: "")),
//            textAlign = TextAlign.Center,
//            style = MaterialTheme.typography.bodyMedium.plus(
//                TextStyle(
//                    letterSpacing = (-0.37).sp,
//                    lineHeight = 28.sp
//                )
//            ),
//            color = colorResource(id = R.color.white_a50),
//            maxLines = 1
//        )
//    } else {
//        Text(
//            modifier = Modifier.fillMaxWidth(),
//            text = (viewModel.guideString.observeAsState().value ?: ""),
//            textAlign = TextAlign.Center,
//            style = MaterialTheme.typography.bodyMedium.plus(
//                TextStyle(
//                    letterSpacing = (-0.37).sp,
//                    lineHeight = 28.sp
//                )
//            ),
//            color = colorResource(id = R.color.white_a50),
//            maxLines = 3,
//            overflow = TextOverflow.Ellipsis,
//        )
//    }
//
//}
//
//@Composable
//fun SttView(viewModel: ServiceViewModel = hiltViewModel(), isListening: Boolean = false) {
//
//    Box(
//        modifier = Modifier
//            .fillMaxWidth(),
//        contentAlignment = Alignment.Center
//    ) {
//        val displayText = viewModel.getSttString().asStateFlow().collectAsState().value
//        MainDisplayText(displayText, isListening)
//    }
//}
//
//
//@Composable
//fun BandView(band: String, modifier: Modifier = Modifier) {
//    Column(
//        modifier = Modifier
//            .padding(
//                start = dimensionResource(R.dimen.padding_16),
//                end = dimensionResource(R.dimen.dp_10_7),
//                top = dimensionResource(R.dimen.padding_16)
//            )
//            .border(
//                width = 1.3.dp,
//                color = colorResource(id = R.color.radio_band_border_color),
//                shape = RectangleShape
//            )
//            .width(dimensionResource(R.dimen.icon_width))
//            .height(dimensionResource(R.dimen.dp_26))
//            .then(modifier),
//        verticalArrangement = Arrangement.Center
//    ) {
//        Text(
//            text = band,
//            style = MaterialTheme.typography.labelLarge,
//            color = colorResource(id = R.color.radio_band_text_color),
//            textAlign = TextAlign.Center,
//            modifier = Modifier.fillMaxWidth()
//        )
//    }
//}
//
//@Composable
//fun FavoriteImageView(modifier: Modifier = Modifier) {
//    Image(
//        painter = painterResource(id = R.drawable.ic_favorite),
//        contentDescription = "icon favorite",
//        modifier = Modifier
//            .width(dimensionResource(R.dimen.icon_width))
//            .height(dimensionResource(R.dimen.icon_width))
//            .then(modifier)
//    )
//}
//
//
//@Composable
//fun LottiePlayingView(modifier: Modifier = Modifier) {
//    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.ic_nowplaying))
//    Box(
//        modifier = Modifier
//            .width(dimensionResource(R.dimen.icon_width))
//            .height(dimensionResource(R.dimen.icon_width))
//            .then(modifier)
//    ) {
//        LottieAnimation(
//            composition = composition,
//            iterations = LottieConstants.IterateForever,
//            contentScale = ContentScale.Fit,
//            modifier = Modifier.clip(
//                RectangleShape
//            )
//        )
//    }
//}
//
//@Composable
//fun CommonDisplayText(
//    displayText: String,
//    modifier: Modifier = Modifier,
//    isListening: Boolean = false
//) {
//    Row(modifier = Modifier.then(modifier), verticalAlignment = Alignment.CenterVertically) {
//        MainDisplayText(displayText, isListening)
//    }
//}
//
//
//@Composable
//fun MainDisplayText(displayText: String, isListening: Boolean = false, autoSize: Boolean = false) {
//
//    if (autoSize) {
//        AutoSizeText(
//            modifier = Modifier
//                .fillMaxSize(1f),
//            text = AnnotatedString(displayText),
//            textAlign = TextAlign.Center,
//            style = MaterialTheme.typography.headlineSmall.plus(
//                TextStyle(
//                    letterSpacing = (-0.48).sp,
//                    lineHeight = 32.sp
//                )
//            ),
//            color = Color.White,
//        )
//    } else {
//        var drawText by remember { mutableStateOf(displayText) }
//        var lastDrawLength by remember { mutableStateOf(0) }
//        // PTT 연속으로 눌렀을 경우 ... 처리 문구 아닐 때 -> ... 처리 문구 문제 있어 추가
//        drawText = displayText
//        Text(
//            modifier = Modifier
//                .fillMaxWidth(),
//            onTextLayout = { textLayoutResult ->
//                // 1. onTextLayout 추가 이유
//                // 발화 시 Partial 이 찍힐 때 ... 이후 밀림 현상이 발생
//                // 2. "lastDrawLength > 3" 이 있는 이유
//                // ... 처리를 하려고 하는데 lastDrawLength가 0 일 때 substring을 하면 StringIndexOutOfBoundsException 발생
//                val hasVisualOverflow = textLayoutResult.hasVisualOverflow
//                CustomLogger.i("onTextLayout hasVisualOverflow : [$hasVisualOverflow]")
//                CustomLogger.i("onTextLayout lastDrawLength : [$lastDrawLength]")
//                if (hasVisualOverflow && lastDrawLength > 3) {
//                    drawText = displayText.substring(0, lastDrawLength - 3) + "..."
//                } else {
//                    drawText = displayText
//                    lastDrawLength = drawText.length
//                }
//            },
//            text = drawText,
//            textAlign = TextAlign.Center,
//            style = MaterialTheme.typography.headlineSmall.plus(
//                TextStyle(
//                    letterSpacing = (-0.02).sp,
//                    lineHeight = 32.sp
//                )
//            ),
//            color = Color.White,
//            maxLines = if (isListening) 2 else 3,
//            overflow = TextOverflow.Ellipsis,
//            softWrap = true
//        )
//    }
//}
//
//
//@Composable
//fun CommonDisplayTextWithGuideText(
//    modifier: Modifier = Modifier,
//    displayText: String,
//    subText: String = "",
//    isStt: Boolean = false
//) {
//    Row(modifier = Modifier.then(modifier), verticalAlignment = Alignment.CenterVertically) {
//        Column {
//            MainDisplayText(displayText)
//            if (!isStt) {
//                Spacer(modifier = Modifier.height(4.dp))
//                SubDisplayText(subText)
//            }
//        }
//    }
//}
//
//@Composable
//fun SubDisplayText(subText: String) {
//    Text(
//        modifier = Modifier.fillMaxWidth(),
//        text = subText,
//        textAlign = TextAlign.Center,
//        style = MaterialTheme.typography.bodyMedium.plus(
//            TextStyle(
//                letterSpacing = (-0.37).sp,
//                lineHeight = 28.sp
//            )
//        ),
//        color = colorResource(id = R.color.white_a50),
//        maxLines = 3,
//        overflow = TextOverflow.Ellipsis,
//    )
//}
//
//@Preview
//@Composable
//fun CommonDisplayTextPreview() {
//    CerenceTheme {
//        Surface(
//            color = Color.Black,
//            modifier = Modifier
//                .width(dimensionResource(R.dimen.agent_width))
//                .height(dimensionResource(R.dimen.agent_min_height))
//        ) {
//            CommonDisplayText(
//                modifier = Modifier.padding(start = 46.dp, end = 46.dp),
//                displayText = "No Bluetooth phone is connected for hands-free calling. Use the screen to connect a phone."
//            )
//        }
//    }
//}
//
//@Preview
//@Composable
//fun CommonDisplayTextWithGuidePreview() {
//    CerenceTheme {
//        Surface(
//            color = Color.Black,
//            modifier = Modifier
//                .width(dimensionResource(R.dimen.agent_width))
//                .height(dimensionResource(R.dimen.agent_min_height))
//        ) {
//            CommonDisplayTextWithGuideText(
//                modifier = Modifier.padding(
//                    start = 46.dp,
//                    end = 46.dp,
//                    bottom = dimensionResource(R.dimen.dp_30)
//                ),
//                displayText = "누구에게 보낼까요?",
//                subText = "ㅇㅇ에게 ㅇㅇ라고 문자 보내줘",
//            )
//        }
//    }
//}
//
//
//
//
//
