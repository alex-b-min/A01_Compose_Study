package com.example.a01_compose_study.presentation.components.top_bar

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.example.a01_compose_study.R

@Composable
fun TopAppBarContent(
    onNavigationIconClick: () -> Unit = {},
    onActionIconClick: () -> Unit = {},
    title: String = ""
) {
    TopAppBar(
        backgroundColor = Color.Black,
        title = { Text(text = title) },
        navigationIcon = {
            IconButton(onClick = onNavigationIconClick) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_guidance_btn_back),
                    contentDescription = null,
                    tint = Color.Gray
                )
            }
        },
        actions = {
            IconButton(onClick = onActionIconClick) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_close),
                    contentDescription = null,
                    tint = Color.Gray
                )
            }
        }
    )
}