package com.example.a01_compose_study.presentation.screen.sendMsg.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
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
import com.example.a01_compose_study.presentation.components.top_bar.TopAppBarContent

@Composable
fun MessageView(modifier: Modifier) {
    Box(
        modifier = modifier
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBarContent(showNavigationIcon = false)
            ContactInfoView()
            Spacer(Modifier.height(dimensionResource(R.dimen.dp_21_3)))
            MsgTextField(
                modifier = Modifier
                    .height(dimensionResource(R.dimen.dp_130))
            )
            Spacer(Modifier.height(dimensionResource(R.dimen.dp_33_3)))
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 50.dp)
                    .height(45.dp),
                onClick = { /*TODO*/ },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = colorResource(id = R.color.confirm_btn_bg_color)
                )) {
                Text(
                    text = "YES",
                    style = MaterialTheme.typography.button,
                    textAlign = TextAlign.Center,
                    color = Color.White
                )
            }


        }
    }
}

@Composable
fun ContactInfoView() {
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
            text = "이름",
            color = Color.White,
            style = MaterialTheme.typography.h6,
            maxLines = 1,
            modifier = Modifier
                .layoutId("name")
        )
        Text(
            text = "010-0000-0000",
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
        Text(
            text = "Say the Message",
            modifier = Modifier.align(Alignment.Center),
            maxLines = 3,
            color = Color.White,
            style = MaterialTheme.typography.body1
        )
    }
}

@Preview
@Composable
fun MessageViewPreview() {
    MessageView(modifier = Modifier.fillMaxWidth())
}