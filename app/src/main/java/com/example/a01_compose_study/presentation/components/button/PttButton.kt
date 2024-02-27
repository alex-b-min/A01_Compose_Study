package com.example.a01_compose_study.presentation.components.button

import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PttButton(
    modifier: Modifier = Modifier,
    contentText: String,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val ripple = rememberRipple(bounded = false)

    OutlinedButton(
        onClick = {
            onClick()
        },
        modifier = modifier
            .padding(4.dp)
            .indication(interactionSource, ripple),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = Color.Black,
            backgroundColor = Color.Gray,
        ),
        shape = MaterialTheme.shapes.medium,
        interactionSource = interactionSource,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Text(
                text = contentText,
                fontSize = 20.sp
            )
        }
    }
}