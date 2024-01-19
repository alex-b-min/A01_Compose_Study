package com.example.a01_compose_study.presentation.main.screen

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.a01_compose_study.domain.util.ScreenSizeType
import com.example.a01_compose_study.presentation.main.MainEvent
import com.example.a01_compose_study.presentation.main.MainUiState
import com.example.a01_compose_study.presentation.main.MainViewModel
import com.example.a01_compose_study.presentation.main.component.BottomMenuBar
import com.example.a01_compose_study.presentation.main.component.CustomAlertDialog
import com.example.a01_compose_study.presentation.main.component.CustomSizeAlertDialog

@Composable
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel(),
) {
    var visible by remember { mutableStateOf(false) }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = slideInVertically(initialOffsetY = { -it }),
            exit = slideOutVertically(targetOffsetY = { -it }),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomEnd
            ) {
                Column(
                    modifier = Modifier
                        .offset(x = 10.dp, y = (-90).dp)
                        .fillMaxHeight(0.2f)
                        .fillMaxWidth(0.3f)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF052005),
                                    Color(0xFF005e53)
                                ),
                                start = Offset.Zero,
                                end = Offset.Infinite
                            ),
                            shape = RoundedCornerShape(15.dp),
                            alpha = 0.9f
                        ),
                ) {
                }
            }
        }

        when (uiState) {
            is MainUiState.DoneScreen -> {

            }

            is MainUiState.OneScreen -> {
                CustomAlertDialog(
                    uiState = uiState as MainUiState.OneScreen,
                    contentColor = Color.White,
                    onDismiss = {
                        viewModel.onEvent(MainEvent.Close)
                    })
            }

            is MainUiState.TwoScreen -> {
                Log.d("@@ TwoScreen", "${uiState}")
                CustomSizeAlertDialog(
                    uiState = uiState as MainUiState.TwoScreen,
                    contentColor = Color.Black,
                    onDismiss = {
                        viewModel.onEvent(MainEvent.Close)
                    })
            }
        }

        BottomMenuBar(
            isVisible = true,
            oneScreenOnClick = {
                viewModel.onEvent(event = MainEvent.OneScreenOpen(text = "OneScreenOnClick"))
            },
            twoScreenOnClick = {
                viewModel.onEvent(
                    event = MainEvent.TwoScreenOpen(
                        text = "TwoScreenOnClick",
                        screenSizeType = ScreenSizeType.Large
                    )
                )
            },
            examScreenOnClick = {
                visible = true
            })
    }
}