package com.example.a01_compose_study.presentation.screen.sendMsg.screen

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import com.example.a01_compose_study.R
import com.example.a01_compose_study.data.Contact
import com.example.a01_compose_study.presentation.components.gauge.clickableWithTapGestures
import com.example.a01_compose_study.presentation.components.list.LazyColumnList
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MsgAllList(
    lineIndex: Int,
    contactList: List<Contact>,
    onItemClick: (Contact) -> Unit,
) {
    LazyColumnList(
        list = contactList,
        index = lineIndex,
        listContent = { index, contact ->
            MsgAllListItem(
                contact = contact,
                onItemClick = onItemClick

            )
        })
}

@Composable
fun MsgNameList(
    lineIndex: Int,
    contactList: List<Contact>,
    onItemClick: (Contact) -> Unit,
) {
//    val scrollState = rememberLazyListState()
    LazyColumnList(
        list = contactList,
        index = lineIndex,
        listContent = { index, contact ->
            MsgNameListItem(
                id = index + 1,
                contact = contact,
                onItemClick = onItemClick,
            )
        })
}


@Composable
fun MsgCategoryList(
    lineIndex: Int,
    contactList: List<Contact>,
    onItemClick: (Contact) -> Unit,
) {
    LazyColumnList(
        list = contactList,
        index = lineIndex,
        listContent = { index, contact ->
            MsgCategoryListItem(
                id = index + 1,
                contact = contact,
                onItemClick = onItemClick
            )
        })
}


@Composable
fun MsgAllListItem(
    contact: Contact,
    onItemClick: (Contact) -> Unit,
) {
    val scope = rememberCoroutineScope()
    var isSelected by remember { mutableStateOf(false) }
    var clicked by remember { mutableStateOf(false) }
    val progress by animateFloatAsState(
        if (clicked) 1f else 0f,
        animationSpec = tween(durationMillis = 3000)
    )

    val constraintSet = ConstraintSet {
        val name = createRefFor("name")
        val phoneNum = createRefFor("phoneNum")
        val profileIcon = createRefFor("profileIcon")

        constrain(name) {
            start.linkTo(parent.start, 10.dp)
            top.linkTo(parent.top, 10.dp)
        }
        constrain(phoneNum) {
            start.linkTo(parent.start, 10.dp)
            top.linkTo(name.bottom)
            bottom.linkTo(parent.bottom, 10.dp)
        }
        constrain(profileIcon) {
            end.linkTo(parent.end, 10.dp)
            top.linkTo(parent.top)
            bottom.linkTo(parent.bottom)
        }
    }

    Box(modifier = Modifier
        .padding(4.dp)
        .clickableWithTapGestures(
            onClick = {
                scope.launch {
                    isSelected = true
                    delay(3000)
                    onItemClick(contact)
                    clicked = true
                }
                clicked = true
            }
        )) {
        ConstraintLayout(constraintSet, modifier = Modifier.fillMaxWidth()) {
            Text(
                text = contact.name,
                color = Color.White,
                style = MaterialTheme.typography.h6,
                maxLines = 1,
                modifier = Modifier
                    .layoutId("name")
            )
            Text(
                text = contact.number,
                color = colorResource(id = R.color.white_a50),
                style = MaterialTheme.typography.body2,
                modifier = Modifier
                    .layoutId("phoneNum")
            )
            Image(
                painter = painterResource(id = R.drawable.ic_user_logout),
                contentDescription = null,
                modifier = Modifier
                    .layoutId("profileIcon")
                    .size(width = 40.dp, height = 50.dp)
            )
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxSize()
                    .height(65.dp)
                    .alpha(if (progress == 0f) 0f else 1f),
                color = colorResource(id = R.color.confirm_btn_bg_color)
            )
        }
    }
}


@Composable
fun MsgNameListItem(
    id: Int,
    contact: Contact,
    onItemClick: (Contact) -> Unit,
) {
    val scope = rememberCoroutineScope()
    var isSelected by remember { mutableStateOf(false) }
    var clicked by remember { mutableStateOf(false) }
    val progress by animateFloatAsState(
        if (clicked) 1f else 0f,
        animationSpec = tween(durationMillis = 3000)
    )

    val constraintSet = ConstraintSet {
        val num = createRefFor("num")
        val name = createRefFor("name")
        val phoneNum = createRefFor("phoneNum")
        val profileIcon = createRefFor("profileIcon")

        constrain(num) {
            start.linkTo(parent.start, 10.dp)
            top.linkTo(parent.top)
            bottom.linkTo(parent.bottom)
        }
        constrain(name) {
            start.linkTo(num.start, 30.dp)
            top.linkTo(parent.top, 10.dp)
        }
        constrain(phoneNum) {
            start.linkTo(num.start, 30.dp)
            top.linkTo(name.bottom)
            bottom.linkTo(parent.bottom, 10.dp)
        }
        constrain(profileIcon) {
            end.linkTo(parent.end, 10.dp)
            top.linkTo(parent.top)
            bottom.linkTo(parent.bottom)
        }
    }
    ConstraintLayout(
        constraintSet,
        modifier = Modifier
            .fillMaxWidth()
            .clickableWithTapGestures(
                onClick = {
                    scope.launch {
                        isSelected = true
                        delay(3000)
                        onItemClick(contact)
                        clicked = true
                    }
                    clicked = true
                }
            )
    ) {
        Text(
            text = id.toString(),
            color = Color.White,
            style = MaterialTheme.typography.h5,
            maxLines = 1,
            modifier = Modifier
                .layoutId("num")
        )
        Text(
            text = contact.name,
            color = Color.White,
            style = MaterialTheme.typography.h6,
            maxLines = 1,
            modifier = Modifier
                .layoutId("name")
        )
        Text(
            text = contact.number,
            color = colorResource(id = R.color.white_a50),
            style = MaterialTheme.typography.body2,
            modifier = Modifier
                .layoutId("phoneNum")
        )
        Image(
            painter = painterResource(id = R.drawable.ic_user_logout),
            contentDescription = null,
            modifier = Modifier
                .layoutId("profileIcon")
                .size(width = 40.dp, height = 50.dp)
        )
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier
                .fillMaxSize()
                .height(65.dp)
                .alpha(if (progress == 0f) 0f else 1f),
            color = colorResource(id = R.color.confirm_btn_bg_color)
        )
    }
}

@Composable
fun MsgCategoryListItem(
    id: Int,
    contact: Contact,
    onItemClick: (Contact) -> Unit,
) {
    val scope = rememberCoroutineScope()
    var isSelected by remember { mutableStateOf(false) }
    var clicked by remember { mutableStateOf(false) }
    val progress by animateFloatAsState(
        if (clicked) 1f else 0f,
        animationSpec = tween(durationMillis = 3000)
    )

    val constraintSet = ConstraintSet {
        val num = createRefFor("num")
        val name = createRefFor("name")
        val phoneNum = createRefFor("phoneNum")

        constrain(num) {
            start.linkTo(parent.start, 10.dp)
            top.linkTo(parent.top)
            bottom.linkTo(parent.bottom)
        }
        constrain(name) {
            start.linkTo(num.start, 30.dp)
            top.linkTo(parent.top, 10.dp)
        }
        constrain(phoneNum) {
            start.linkTo(num.start, 30.dp)
            top.linkTo(name.bottom)
            bottom.linkTo(parent.bottom, 10.dp)
        }
    }
    ConstraintLayout(
        constraintSet,
        modifier = Modifier
            .fillMaxWidth()
            .clickableWithTapGestures(
                onClick = {
                    scope.launch {
                        isSelected = true
                        delay(3000)
                        onItemClick(contact)
                        clicked = true
                    }
                    clicked = true
                }
            )
    ) {
        Text(
            text = id.toString(),
            color = Color.White,
            style = MaterialTheme.typography.h5,
            maxLines = 1,
            modifier = Modifier
                .layoutId("num")
        )
        Text(
            text = contact.name,
            color = Color.White,
            style = MaterialTheme.typography.h6,
            maxLines = 1,
            modifier = Modifier
                .layoutId("name")
        )
        Text(
            text = contact.number,
            color = colorResource(id = R.color.white_a50),
            style = MaterialTheme.typography.body2,
            modifier = Modifier
                .layoutId("phoneNum")
        )
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier
                .fillMaxSize()
                .height(65.dp)
                .alpha(if (progress == 0f) 0f else 1f),
            color = colorResource(id = R.color.confirm_btn_bg_color)
        )
    }
}

@Preview
@Composable
fun MsgAllListItemPreview() {
    val list = mutableListOf<Contact>()
    list.add(Contact(id = "1", name = "riley", number = "010-0000"))
    list.add(Contact(id = "2", name = "kim", number = "010-0000"))
    MsgAllList(
        lineIndex = 0,
        contactList = list,
        onItemClick = {})
}

@Preview
@Composable
fun MsgNameListItemPreview() {
    val list = mutableListOf<Contact>()
    list.add(Contact(id = "1", name = "riley", number = "010-0000"))
    list.add(Contact(id = "2", name = "kim", number = "010-0000"))
    MsgNameList(
        lineIndex = 0,
        contactList = list,
        onItemClick = {})
}

@Preview
@Composable
fun MsgCategoryListItemPreview() {
    val list = mutableListOf<Contact>()
    list.add(Contact(id = "1", name = "riley", number = "010-0000"))
    list.add(Contact(id = "2", name = "kim", number = "010-0000"))
    MsgCategoryList(
        lineIndex = 0,
        contactList = list,
        onItemClick = {})
}