package com.example.a01_compose_study.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.DarkGray


private val DarkColorPalette = darkColors(
    primary = Color.Black,
    background = DarkGray,
    onBackground = Color.Black,
    surface = LightBlue,
    onSurface = DarkGray
)

@Composable
fun A01_Compose_StudyTheme(
    darkTheme: Boolean = true,
    content: @Composable() () -> Unit
) {
    MaterialTheme(
        colors = DarkColorPalette,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}