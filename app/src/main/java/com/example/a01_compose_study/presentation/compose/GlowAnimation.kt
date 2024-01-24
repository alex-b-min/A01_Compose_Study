package com.ftd.ivi.cerence.ui.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.toSize
import androidx.hilt.navigation.compose.hiltViewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.ftd.ivi.cerence.data.HVRState
import com.ftd.ivi.cerence.data.WindowMode
import com.ftd.ivi.cerence.util.CustomLogger
import com.ftd.ivi.cerence.viewmodel.ServiceViewModel

@Composable
fun GlowAnimation(viewModel: ServiceViewModel = hiltViewModel()) {

    val mwState = viewModel.mwState
    val vrState = mwState.vrState.collectAsState()
    val showError = viewModel.uiState.showError.collectAsState().value
    val showGlow = remember { mutableStateOf(false) }
    val listeningGlowPath = remember { mutableStateOf("bg_glow/09_tsd_frame_glow_s_lt.json") }
    val errGlowPath = remember { mutableStateOf("bg_glow/frame_error_glow_lt.json") }
    val listeningGlow by rememberLottieComposition(
        LottieCompositionSpec.Asset(listeningGlowPath.value),
        imageAssetsFolder = "bg_glow/images/default"
    )

    val errorGlow by rememberLottieComposition(
        LottieCompositionSpec.Asset(errGlowPath.value),
        imageAssetsFolder = "bg_glow/images/error"
    )
//    val dpScale = LocalDensity.current.density
//    var size by remember { mutableStateOf(Size.Zero) }
//    var scale by remember { mutableStateOf(Size(1f,1f)) }

    LaunchedEffect(vrState.value, showError) {
        //CustomLogger.e("GlowAnimation onVRStateChanged :${vrState.value.name} ")
        //CustomLogger.e("GlowAnimation onVRStateChanged showERR: ${showError} showGlow:${showGlow.value}")

        when (viewModel.uiState.windowMode.value) {
            WindowMode.NONE,
            WindowMode.SMALL -> {
                listeningGlowPath.value = "bg_glow/09_tsd_frame_glow_s_lt.json"
                errGlowPath.value = "bg_glow/frame_error_glow_lt.json"
            }

            WindowMode.MEDIUM -> {
                listeningGlowPath.value = "bg_glow/09_tsd_frame_glow_m_lt.json"
                errGlowPath.value = "bg_glow/frame_error_glow_m_lt.json"
            }

            WindowMode.LARGE -> {
                listeningGlowPath.value = "bg_glow/09_tsd_frame_glow_l_lt.json"
                errGlowPath.value = "bg_glow/frame_error_glow_l_lt.json"
            }
        }

        var setGlow = false
        when (vrState.value) {
            HVRState.INITIALIZING,
            HVRState.IDLE,
            HVRState.PREPARING,
            HVRState.SPOTTING -> {
                setGlow = false
            }

            HVRState.SPEAKING -> {
                setGlow = false
            }

            HVRState.LISTENING -> {
                setGlow = true
            }

            HVRState.THINKING,
            HVRState.FINISHING -> {
                setGlow = false
            }

            else -> {

            }
        }

        if (setGlow != showGlow.value || !setGlow) {
            showGlow.value = setGlow
        }
    }


    var targetGlow = listeningGlow


    if (!showGlow.value) {
        targetGlow = null
    }
    if (showError) {
        //currentGlow.value = errorGlow
        targetGlow = errorGlow
    }

//        LaunchedEffect(size){
//            CustomLogger.e("AnimationSizeChanged :${size} glow Width:${targetGlow?.bounds?.width()} Height:${targetGlow?.bounds?.height()}")
//            targetGlow?.bounds?.let {
//                scale = Size( it.width()/size.width, it.height()/size.height )
//            }
//
//        }
//
//        LaunchedEffect(scale){
//            CustomLogger.e("AnimationSizeChanged Scale:${scale} ")
//        }

    targetGlow?.let {

//            LottieAnimation(
//                composition = targetGlow,
//                iterations = LottieConstants.IterateForever,
//                alignment = Alignment.TopStart,
//                contentScale = ContentScale.FillBounds,
//                modifier = Modifier
//                    .graphicsLayer(
//                        scaleX = scale.width,
//                        scaleY = scale.height,
//                        transformOrigin = TransformOrigin(0f, 0f)
//                    )
//                    .onSizeChanged { newSize ->
//                        size = newSize.toSize()
//                    }
//            )

        LottieAnimation(
            composition = targetGlow,
            iterations = LottieConstants.IterateForever,
            alignment = Alignment.TopStart,
            contentScale = ContentScale.None,
            modifier = Modifier.fillMaxSize()
        )

    }


}