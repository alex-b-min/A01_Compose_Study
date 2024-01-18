package com.example.a01_compose_study.presentation.main.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.a01_compose_study.presentation.main.component.BottomMenuBar
import com.example.a01_compose_study.presentation.main.component.CustomAlertDialog

@Composable
fun MainScreen() {
    /**
     * scaffold의 bottomAppBar를 이용을 하면.
     * 추후 다이얼로그를 띄울시 해당 다이얼로그를 제외한 다른 UI들은 뿌옇게 처리를 해야하는데 이 부분에서
     * bottomAppBar는 뿌옇게 처리가 안되는 이슈가 발생
     */
//    Scaffold(
//        modifier = Modifier.fillMaxSize(),
//        bottomBar = {
//            BottomAppBar(
//                backgroundColor = Color.Black,
//                modifier = Modifier.height(80.dp)
//            ) {
//            }
//        }
//    ) {
//    }


    var isVisible by remember { mutableStateOf(false) }

    val color = if (isVisible) {
        Color.Gray
    } else {
        Color.Transparent
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color)
    ) {
        CustomAlertDialog(isVisible = isVisible, onDismiss = {
            isVisible = false
        })
        BottomMenuBar(
            isVisible = isVisible,
            customDialogVisibleOnClick = {
                isVisible = !isVisible
            })
    }
}