package com.example.a01_compose_study.presentation.screen.call.screen

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.a01_compose_study.R
import com.example.a01_compose_study.data.Contact
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun CallIndexedList(
    contactList: List<Contact>,
    selectedIndex: Int? = null,
    vrDynamicBackground: Color,
    fixedBackground: Color,
    onItemClick: (Contact) -> Unit,
) {
    Log.d("@@ selectedIndex 실행횟수", "${selectedIndex}")
    val scrollState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    /**
     * selectedIndex에 해당하는 Contact를 가져옴
     */
    val selectedContact: Contact? = selectedIndex?.let {
        if (it >= 0 && it < contactList.size) {
            contactList[it]
        } else {
            null
        }
    }

    /**
     * LazyColumn 스크롤 처리
     */
    coroutineScope.launch {
        val scrollIndex = selectedIndex ?: 0
        scrollState.animateScrollToItem(scrollIndex)
    }

    LazyColumn(state = scrollState) {
        itemsIndexed(contactList) { index, callItem ->
            /**
             * selectedContact(선택한 아이템)의 id와 수신객체인 callItem의 id가 일치하는지의 Boolean값
             * 일치한다면, YesNo 화면으로 게이지 채워지고 자동으로 넘어가고
             * 일치하지 않는다면, 아무일도 벌어지지 않는다.
             */
            val isSelected = callItem.id == selectedContact?.id
            CallIndexedListItem(
                contactItem = callItem,
                selectedContact = if (isSelected) selectedContact else null,
                vrDynamicBackground = vrDynamicBackground,
                fixedBackground = fixedBackground,
                onItemClick = onItemClick,
            )
        }
    }
}

@Composable
fun CallIndexedListItem(
    contactItem: Contact,
    selectedContact: Contact? = null,
    vrDynamicBackground: Color,
    fixedBackground: Color,
    onItemClick: (Contact) -> Unit,
) {
    val scope = rememberCoroutineScope()

    var isSelected by remember { mutableStateOf(false) }

    scope.launch {
        if (selectedContact != null) {
            Log.d("@@ SelectedContact", "${selectedContact}")
            isSelected = true
            delay(850)
            onItemClick(selectedContact)
        }
    }

    val progress by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0f,
        animationSpec = tween(durationMillis = 800),
        label = "LinearProgressAnimation"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxSize()
        ) {
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(75.dp),
                color = if (isSelected) Color.DarkGray else Color.Transparent,
                backgroundColor = Color.Transparent
            )
        }

        Row(
            modifier = Modifier
                .fillMaxSize()
                .clickable {
                    scope.launch {
                        isSelected = true
                        delay(850)
                        onItemClick(contactItem)
                    }
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min)
                ) {

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = dimensionResource(R.dimen.dp_80))
                    ) {
                        Row {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        top = dimensionResource(R.dimen.dp_6_7),
                                        bottom = dimensionResource(R.dimen.dp_6_7),
                                        start = dimensionResource(R.dimen.dp_33_3)
                                    )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(end = 28.7.dp)
                                        .defaultMinSize(minHeight = dimensionResource(R.dimen.dp_32)),
                                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                                ) {
                                    val context = LocalContext.current

                                    Column(
                                        modifier = Modifier
                                            .alignByBaseline()
                                    ) {
                                        Text(
                                            text = "${contactItem.id}.",
                                            color = Color.White,
                                            maxLines = 3,
                                            overflow = TextOverflow.Ellipsis,
                                            style = MaterialTheme.typography.h5.plus(
                                                TextStyle(
                                                    letterSpacing = (-0.48).sp, lineHeight = 32.sp
                                                )
                                            ),
                                            textAlign = TextAlign.Start
                                        )
                                    }

                                    Column(
                                        modifier = Modifier
                                            .alignByBaseline()
                                    ) {
                                        Text(
                                            text = contactItem.name,
                                            color = Color.White,
                                            maxLines = 3,
                                            overflow = TextOverflow.Ellipsis,
                                            style = MaterialTheme.typography.h5.plus(
                                                TextStyle(
                                                    letterSpacing = (-0.48).sp, lineHeight = 32.sp
                                                )
                                            ),
                                            textAlign = TextAlign.Start
                                        )

                                        Spacer(modifier = Modifier.height(3.dp))

                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                                        ) {
                                            Text(
                                                fontSize = 20.sp,
                                                text = contactItem.number,
                                                color = Color.Gray,
                                                maxLines = 3,
                                                overflow = TextOverflow.Ellipsis,
                                                style = MaterialTheme.typography.h5.plus(
                                                    TextStyle(
                                                        letterSpacing = (-0.48).sp, lineHeight = 32.sp
                                                    )
                                                ),
                                                textAlign = TextAlign.Start,
                                                modifier = Modifier.alignByBaseline()
                                            )
                                            Image(
                                                painter = painterResource(id = R.drawable.ic_comm_office),
                                                contentDescription = "OtherNumber_Office",
                                                modifier = Modifier
                                                    .width(dimensionResource(R.dimen.dp_16))
                                                    .height(dimensionResource(R.dimen.dp_16))
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun CallIndexedListItemPreview() {
    val contact = Contact(id = "1", name = "문재민", number = "010-1111-2222")

    CallIndexedListItem(
        contactItem = contact,
        selectedContact = null,
        vrDynamicBackground = Color.Black,
        fixedBackground = Color.Black,
        onItemClick = {
        })
}