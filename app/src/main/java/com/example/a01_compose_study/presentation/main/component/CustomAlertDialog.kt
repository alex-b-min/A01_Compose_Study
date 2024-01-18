package com.example.a01_compose_study.presentation.main.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


@Composable
fun CustomAlertDialog(
    isVisible: Boolean,
    onDismiss: () -> Unit
) {
    
//    if (isVisible) {
//        Box(
//            modifier = Modifier.fillMaxSize(),
//            contentAlignment = Alignment.BottomStart
//        ) {
//            Column(
//                modifier = Modifier
//                    .offset(x = 10.dp, y = (-70).dp)
//                    .height(150.dp)
//                    .width(400.dp)
//                    .background(
//                        brush = Brush.linearGradient(
//                            colors = listOf(
//                                Color(0xFF3FBB3F),
//                                Color(0xFF005e53)
//                            ), start = Offset.Zero, end = Offset.Infinite
//                        ), shape = RoundedCornerShape(15.dp),
//                        alpha = 0.4f
//                    ),
//            ) {
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.End
//                ) {
//                    IconButton(onClick = { /* 처리 로직 추가 */ }) {
//                        Icon(Icons.Default.Close, contentDescription = null)
//                    }
//                }
//            }
//        }
//    }


    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it })
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomStart
        ) {
            Column(
                modifier = Modifier
                    .offset(x = 10.dp, y = (-70).dp)
                    .height(150.dp)
                    .width(400.dp)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF052005),
                                Color(0xFF005e53)
                            ), start = Offset.Zero, end = Offset.Infinite
                        ), shape = RoundedCornerShape(15.dp),
                        alpha = 0.9f
                    ),
            ) {
                Box {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(onClick = {
                            onDismiss()
                        }) {
                            Icon(Icons.Default.Close, contentDescription = null)
                        }
                    }
                }
            }
        }
    }
}
