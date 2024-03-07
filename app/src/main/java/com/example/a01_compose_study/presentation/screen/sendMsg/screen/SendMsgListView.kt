package com.example.a01_compose_study.presentation.screen.sendMsg.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
import com.example.a01_compose_study.domain.model.HelpItemData
import com.example.a01_compose_study.domain.util.ScreenSizeType
import com.example.a01_compose_study.presentation.components.list.LazyColumnList
import com.example.a01_compose_study.presentation.screen.main.DomainUiState

@Composable
fun SendMsgListView(
    domainUiState: DomainUiState.HelpWindow,
    contentColor: android.graphics.Color,
    backgroundColor: android.graphics.Color,
    onDismiss: () -> Unit,
    onBackButton: () -> Unit,
    onScreenSizeChange: (ScreenSizeType) -> Unit,
    onItemClick: (HelpItemData) -> Unit,
) {

}

@Composable
fun MsgAllList(
    contactList: List<Contact>,
) {
    LazyColumnList(list = contactList) {
        MsgAllListItem()
    }
}

@Composable
fun MsgNameList(
    contactList: List<Contact>,
) {
    LazyColumnList(list = contactList) {
        MsgNameListItem()
    }
}


@Composable
fun MsgCategoryList(
    contactList: List<Contact>,
) {
    LazyColumnList(list = contactList) {
        MsgCategoryListItem()
    }
}


@Composable
fun MsgAllListItem() {
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

    Box(modifier = Modifier.padding(4.dp)) {
        ConstraintLayout(constraintSet, modifier = Modifier.fillMaxWidth()) {
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
                painter = painterResource(id = R.drawable.ic_user_logout),
                contentDescription = null,
                modifier = Modifier
                    .layoutId("profileIcon")
                    .size(width = 40.dp, height = 50.dp)
            )
        }
    }
}

@Composable
fun MsgNameListItem() {
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
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "1",
            color = Color.White,
            style = MaterialTheme.typography.h5,
            maxLines = 1,
            modifier = Modifier
                .layoutId("num")
        )
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
            painter = painterResource(id = R.drawable.ic_user_logout),
            contentDescription = null,
            modifier = Modifier
                .layoutId("profileIcon")
                .size(width = 40.dp, height = 50.dp)
        )
    }

}

@Composable
fun MsgCategoryListItem() {
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
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "1",
            color = Color.White,
            style = MaterialTheme.typography.h5,
            maxLines = 1,
            modifier = Modifier
                .layoutId("num")
        )
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
    }

}

@Preview
@Composable
fun MsgAllListItemPreview() {
    val list = mutableListOf<Contact>()
    list.add(Contact(id = "1", name = "김", number = "010-0000"))
    list.add(Contact(id = "2", name = "수", number = "010-0000"))
    MsgAllList(list)
}

@Preview
@Composable
fun MsgNameListItemPreview() {
    val list = mutableListOf<Contact>()
    list.add(Contact(id = "1", name = "김", number = "010-0000"))
    list.add(Contact(id = "2", name = "수", number = "010-0000"))
    MsgNameList(list)
}

@Preview
@Composable
fun MsgCategoryListItemPreview() {
    val list = mutableListOf<Contact>()
    list.add(Contact(id = "1", name = "김", number = "010-0000"))
    list.add(Contact(id = "2", name = "수", number = "010-0000"))
    MsgCategoryList(list)
}