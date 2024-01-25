//package com.ftd.ivi.cerence.ui.compose
//
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.width
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.res.dimensionResource
//import androidx.hilt.navigation.compose.hiltViewModel
//import com.airbnb.lottie.compose.LottieAnimation
//import com.airbnb.lottie.compose.LottieCompositionSpec
//import com.airbnb.lottie.compose.LottieConstants
//import com.airbnb.lottie.compose.rememberLottieComposition
//import com.ftd.ivi.cerence.R
//import com.ftd.ivi.cerence.data.HVRState
//import com.ftd.ivi.cerence.util.CustomLogger
//import com.ftd.ivi.cerence.viewmodel.ServiceViewModel
//
//@Composable
//fun VoiceChromeAnimation(viewModel: ServiceViewModel = hiltViewModel()) {
//
//
//    val mwState = viewModel.mwState
//    val vrState = mwState.vrState.collectAsState()
//    val userSpeaking = mwState.userSpeaking.collectAsState().value
//    val loadingProgress = viewModel.loadingProgress.collectAsState().value
//
//    val passiveChrome by rememberLottieComposition(
//        LottieCompositionSpec.Asset("voice_chrome/01_2_tsd_listening passive_loop_lt.json"),
//        imageAssetsFolder = "images"
//    )
//    val prepareChrome by rememberLottieComposition(
//        LottieCompositionSpec.Asset("voice_chrome/00_tsd_wake up_lt.json"),
//        imageAssetsFolder = "images"
//    )
//    val listeningChrome by rememberLottieComposition(
//        LottieCompositionSpec.Asset("voice_chrome/02_2_tsd_listening active_loop_lt.json"),
//        imageAssetsFolder = "images"
//    )
//    val thingkingChrome by rememberLottieComposition(
//        LottieCompositionSpec.Asset("voice_chrome/03_2_tsd_thinking_loop_fix_lt.json"),
//        imageAssetsFolder = "images"
//    )
//    val currentChrome = remember { mutableStateOf(passiveChrome) }
//    LaunchedEffect(userSpeaking) {
//        CustomLogger.e("VoiceChromeAnimation userSpeaking :${mwState.vrState.value.name} ${userSpeaking}")
//        when (mwState.vrState.value) {
//            HVRState.LISTENING -> {
//                if (userSpeaking) {
//                    currentChrome.value = listeningChrome
//                } else {
//                    currentChrome.value = passiveChrome
//                }
//            }
//
//            else -> {
//
//            }
//        }
//    }
//    LaunchedEffect(vrState.value, loadingProgress) {
//        //CustomLogger.e("VoiceChromeAnimation onVRStateChanged :${mwState.vrState.value.name} loadingProgress:${loadingProgress}")
//        if (loadingProgress) {
//            currentChrome.value = thingkingChrome
//        } else {
//            when (mwState.vrState.value) {
//
//                HVRState.INITIALIZING -> {
//                    currentChrome.value = prepareChrome
////                    if (domainType == DomainType.MainMenu) {
////                        currentChrome.value = prepareChrome
////                    } else {
////                        currentChrome.value = null
////                    }
//                }
//
//                HVRState.IDLE -> {
//                    currentChrome.value = null
//                }
//
//                HVRState.PREPARING -> {
//                    currentChrome.value = prepareChrome
//                }
//
//                HVRState.SPOTTING -> {
//                    currentChrome.value = thingkingChrome
//                }
//
//                HVRState.SPEAKING -> {
//                    currentChrome.value = thingkingChrome
//                }
//
//                HVRState.LISTENING -> {
//
//                    if (userSpeaking) {
//                        currentChrome.value = listeningChrome
//                    } else {
//                        currentChrome.value = passiveChrome
//                    }
//
//                }
//
//                HVRState.THINKING -> {
//                    currentChrome.value = thingkingChrome
//                }
//
//                HVRState.FINISHING -> {
//                    currentChrome.value = passiveChrome
//                }
//
//                else -> {
//
//                }
//            }
//        }
//
//    }
//
//    if (vrState.value != HVRState.IDLE || loadingProgress) {
//
//        Box(
//            modifier = Modifier.fillMaxSize(),
//            contentAlignment = Alignment.BottomCenter
//
//        ) {
//
//            if (currentChrome.value != null) {
//                LottieAnimation(
//                    composition = currentChrome.value,
//                    iterations = LottieConstants.IterateForever,
//                    modifier = Modifier
//                        .width(dimensionResource(R.dimen.agent_width))
//                        .height(dimensionResource(R.dimen.agent_min_height))
//                )
//            }
//
//        }
//
//    }
//
//
//}