package com.example.a01_compose_study.presentation.screen.sendMsg.screen

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import com.example.a01_compose_study.R
import com.example.a01_compose_study.presentation.components.gauge.clickableWithTapGestures
import kotlinx.coroutines.delay


@Composable
fun MessageView(
    modifier: Modifier,
    isSayMessage: Boolean,
    name: String = "",
    phoneNum: String = "",
    msgData: String = "",
    onButtonClick: () -> Unit,
) {
    Box(
        modifier = modifier
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            ContactInfoView(name, phoneNum)
            Spacer(Modifier.height(dimensionResource(R.dimen.dp_21_3)))
            MsgTextField(
                modifier = Modifier.height(dimensionResource(R.dimen.dp_130)),
                isSayMessage = isSayMessage,
                msgData = msgData,
            )
            Spacer(Modifier.height(dimensionResource(R.dimen.dp_33_3)))
            MessageButton(
                isSayMessage = isSayMessage,
                onClick = onButtonClick
            )
        }
    }
}

@Composable
fun ContactInfoView(
    name: String,
    phoneNum: String,
) {
    val constraintSet = ConstraintSet {
        val name = createRefFor("name")
        val phoneNum = createRefFor("phoneNum")
        val phoneIcon = createRefFor("phoneIcon")

        constrain(name) {
            start.linkTo(parent.start, 50.dp)
            top.linkTo(parent.top, 35.dp)
        }
        constrain(phoneNum) {
            start.linkTo(parent.start, 50.dp)
            top.linkTo(name.bottom)
        }
        constrain(phoneIcon) {
            start.linkTo(phoneNum.end, 5.dp)
            top.linkTo(name.bottom)
            bottom.linkTo(phoneNum.bottom)
        }
    }
    ConstraintLayout(
        constraintSet,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = name,
            color = Color.White,
            style = MaterialTheme.typography.h6,
            maxLines = 1,
            modifier = Modifier
                .layoutId("name")
        )
        Text(
            text = phoneNum,
            color = colorResource(id = R.color.white_a50),
            style = MaterialTheme.typography.body2,
            modifier = Modifier
                .layoutId("phoneNum")
        )
        Image(
            painter = painterResource(id = R.drawable.ic_comm_mobile),
            contentDescription = null,
            modifier = Modifier
                .height(17.3.dp)
                .layoutId("phoneIcon")
        )
    }
}

@Composable
fun MsgTextField(
    modifier: Modifier = Modifier,
    msgData: String,
    isSayMessage: Boolean,
    clickListener: (() -> Unit)? = null,
) {
    Box(modifier = modifier
        .fillMaxWidth()
        .padding(horizontal = 50.dp)
        .clip(RoundedCornerShape(5.dp))
        .background(
            color = colorResource(id = R.color.confirm_btn_bg_color),
        )
        .border(
            border = BorderStroke(1.dp, colorResource(id = R.color.white_a15)),
            shape = RoundedCornerShape(dimensionResource(id = R.dimen.agent_bg_radius))
        )
        .clickable(
            onClick = { clickListener?.invoke() }

        )
    ) {
        if (isSayMessage) {
            Text(
                text = "Say the Message",
                modifier = Modifier.align(Alignment.Center),
                maxLines = 3,
                color = Color.White,
                style = MaterialTheme.typography.body1
            )
        } else {
            Text(
                text = msgData,
                modifier = Modifier.align(Alignment.Center),
                maxLines = 3,
                color = Color.White,
                style = MaterialTheme.typography.body1
            )
        }
    }
}

//@Composable
//fun MessageButton(isSayMessage: Boolean, onClick: () -> Unit) {
//    Button(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(horizontal = 50.dp)
//            .height(45.dp),
//        onClick = onClick,
//        colors = ButtonDefaults.buttonColors(
//            backgroundColor = colorResource(id = R.color.confirm_btn_bg_color)
//        )
//    ) {
//        Text(
//            text = if (isSayMessage) "NO" else "YES",
//            style = MaterialTheme.typography.button,
//            textAlign = TextAlign.Center,
//            color = Color.White
//        )
//    }
//}
//@Composable
//fun CustomButton(
//    onClick: () -> Unit,
//    modifier: Modifier = Modifier,
//    content: @Composable () -> Unit
//) {
//    var isPressed by remember { mutableStateOf(false) }
//
//    Box(
//        modifier = modifier
//            .pointerInput(Unit) {
//                detectTapGestures(
//                    onPress = {
//                        isPressed = true
//                    },
//                    onTap = {
//                        isPressed = false
//                        onClick()
//                    }
//                )
//            }
//    ) {
//        ProvideTextStyle(MaterialTheme.typography.button) {
//            content()
//        }
//        // 버튼이 눌린 상태일 때 배경색을 변경하는 효과 추가
//        if (isPressed) {
//            Box(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .background(Color.Gray.copy(alpha = 0.4f))
//            )
//        }
//    }
//}

@Composable
fun MessageButton(isSayMessage: Boolean, onClick: () -> Unit) {
    var clicked by remember { mutableStateOf(false) }
    val progress by animateFloatAsState(
        if (clicked) 1f else 0f,
        animationSpec = tween(durationMillis = 3000)
    )

    Button(
        onClick = {
            onClick()
        },
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color.Gray.copy(alpha = 0.4f)
        ),
        elevation = ButtonDefaults.elevation(0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 50.dp)
            .height(45.dp),
        contentPadding = PaddingValues(0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickableWithTapGestures(
                    onClick = { clicked = true }
                )
        ) {
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.CenterStart),
                color = colorResource(id = R.color.confirm_btn_bg_color)
            )
            Text(
                text = if (isSayMessage) "NO" else "YES",
                style = MaterialTheme.typography.button,
                textAlign = TextAlign.Center,
                color = Color.White,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}


//@Composable
//fun MessageButton(isSayMessage: Boolean, onClick: () -> Unit) {
//    var clicked by remember { mutableStateOf(false) }
//    val progress by animateFloatAsState(
//        if (clicked) 1f else 0f,
//        animationSpec = tween(durationMillis = 500)
//    )
//
//    Button(
//        onClick = {
//            onClick()
//            clicked = true
//        },
//        colors = ButtonDefaults.buttonColors(
//            backgroundColor = colorResource(id = R.color.confirm_btn_bg_color)
//        ),
//        elevation = ButtonDefaults.elevation(0.dp),
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(0.dp)
//            .height(45.dp),
//    ) {
//        Box(
//            modifier = Modifier.fillMaxSize()
//        ) {
//            LinearProgressIndicator(
//                progress = progress,
//                modifier = Modifier
//                    .fillMaxSize()
//                    .align(Alignment.CenterStart),
//                color = colorResource(id = R.color.black_01)
//            )
//            Text(
//                text = if (isSayMessage) "NO" else "YES",
//                style = MaterialTheme.typography.button,
//                textAlign = TextAlign.Center,
//                color = Color.White,
//                modifier = Modifier.align(Alignment.Center)
//            )
//        }
//    }
//}


@Composable
fun animateButton(currentAlpha: Animatable<Float, AnimationVector1D>) {
    LaunchedEffect(Unit) {
        currentAlpha.animateTo(
            targetValue = 0.5f,
            animationSpec = tween(durationMillis = 300, easing = LinearOutSlowInEasing)
        )
        delay(300)
        currentAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 300, easing = LinearOutSlowInEasing)
        )
    }
}


@Preview
@Composable
fun MessageViewPreview() {
    MessageView(
        modifier = Modifier.fillMaxWidth(),
        isSayMessage = false,
        name = "Name",
        phoneNum = "010-0000-00000",
        msgData = "Hi",
        onButtonClick = {}
    )

}