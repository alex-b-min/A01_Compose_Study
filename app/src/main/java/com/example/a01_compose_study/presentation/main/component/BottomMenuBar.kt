package com.example.a01_compose_study.presentation.main.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.a01_compose_study.R


@Composable
fun BottomMenuBar(
    isVisible: Boolean,
    oneScreenOnClick: () -> Unit,
    twoScreenOnClick: () -> Unit,
    examScreenOnClick: () -> Unit
    ) {

    // 추후 코드 정리 필요
    val color = if (isVisible) {
        Color.DarkGray
    } else {
        Color.Black
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Bottom,
    ) {
        Row(
            modifier = Modifier.run {
                fillMaxWidth()
                    .background(color = color)
                    .fillMaxHeight(0.10f)
//                    .padding(top = 30.dp)
            },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(20.dp))
            IconButton(onClick = { oneScreenOnClick() }) {
                Icon(
                    Icons.Default.Info, contentDescription = null,
                    modifier = Modifier.size(30.dp),
                    tint = Color.White
                )
            }
            IconButton(onClick = { twoScreenOnClick() }) {
                Icon(
                    Icons.Default.MoreVert, contentDescription = null,
                    modifier = Modifier.size(30.dp),
                    tint = Color.White
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth(0.15f)
                    .fillMaxHeight(0.6f)
                    .border(1.2.dp, Color.DarkGray, shape = RoundedCornerShape(100.dp))
                    .padding(horizontal = 3.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { examScreenOnClick() }) {
                    Icon(
                        Icons.Default.KeyboardArrowLeft,
                        contentDescription = null,
                        tint = Color.Blue,
                        modifier = Modifier.size(30.dp)
                    )
                }

                Text(
                    text = "21.0",
                    fontWeight = FontWeight.Bold,
                    fontSize = 23.sp,
                    color = Color.White
                )

                IconButton(onClick = { /* 처리 로직 추가 */ }) {
                    Icon(
                        Icons.Default.KeyboardArrowRight, contentDescription = null,
                        tint = Color.Red,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            IconButton(onClick = { /* 처리 로직 추가 */ }) {
                Canvas(modifier = Modifier.size(50.dp)) {
                    // Draw a filled rectangle
                    drawRoundRect(
                        color = Color(0xFFE95252),
                        size = size,
                        cornerRadius = CornerRadius(10.dp.toPx(), 10.dp.toPx())
                    )
                }
                Icon(
                    Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(30.dp)
                )
            }
            IconButton(onClick = { /* 처리 로직 추가 */ }) {
                Canvas(modifier = Modifier.size(50.dp)) {
                    // Draw a filled rectangle
                    drawRoundRect(
                        color = Color(0xFFBC25F3),
                        size = size,
                        cornerRadius = CornerRadius(10.dp.toPx(), 10.dp.toPx())
                    )
                }
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(30.dp)
                )
            }
            IconButton(onClick = { /* 처리 로직 추가 */ }) {

                Canvas(modifier = Modifier.size(50.dp)) {
                    // Draw a filled rectangle
                    drawRoundRect(
                        color = Color(0xFF2FCE0C),
                        size = size,
                        cornerRadius = CornerRadius(10.dp.toPx(), 10.dp.toPx())
                    )
                }
                Icon(
                    painter = painterResource(id = R.drawable.call_ic),
                    contentDescription = null,
                    modifier = Modifier.size(30.dp)
                )
            }
            IconButton(onClick = { /* 처리 로직 추가 */ }) {
                Canvas(modifier = Modifier.size(50.dp)) {
                    // Draw a filled rectangle
                    drawRoundRect(
                        color = Color.Gray,
                        size = size,
                        cornerRadius = CornerRadius(10.dp.toPx(), 10.dp.toPx()),
                        style = Stroke(1.dp.toPx())
                    )
                }
                Icon(
                    Icons.Default.Menu,
                    contentDescription = null,
                    modifier = Modifier.size(30.dp),
                    tint = Color.White
                )
            }
            IconButton(onClick = { /* 처리 로직 추가 */ }) {
                Canvas(modifier = Modifier.size(50.dp)) {
                    // Draw a filled rectangle
                    drawRoundRect(
                        color = Color(0x9ED6CECD),
                        size = size,
                        cornerRadius = CornerRadius(10.dp.toPx(), 10.dp.toPx())
                    )
                }
                Icon(
                    Icons.Default.Settings, contentDescription = null,
                    modifier = Modifier.size(30.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth(0.2f)
                    .fillMaxHeight(0.6f)
                    .border(1.2.dp, Color.DarkGray, shape = RoundedCornerShape(100.dp)),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { /* 처리 로직 추가 */ }) {
                    Icon(
                        Icons.Default.KeyboardArrowLeft,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Icon(
                        Icons.Default.Share,
                        contentDescription = null,
                        tint = Color.White,
                    )
                    Text(
                        text = "2",
                        fontWeight = FontWeight.Bold,
                        fontSize = 25.sp,
                        color = Color.White
                    )
                }
                IconButton(onClick = { /* 처리 로직 추가 */ }) {
                    Icon(
                        Icons.Default.KeyboardArrowRight, contentDescription = null,
                        modifier = Modifier.size(30.dp),
                        tint = Color.White,
                    )
                }
            }
            IconButton(onClick = { /* 처리 로직 추가 */ }) {
                Icon(
                    Icons.Default.Person, contentDescription = null,
                    modifier = Modifier.size(30.dp),
                    tint = Color.White
                )
            }
            IconButton(onClick = { /* 처리 로직 추가 */ }) {
                Icon(
                    Icons.Default.Person, contentDescription = null,
                    modifier = Modifier.size(30.dp),
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.width(20.dp))
        }
    }
}