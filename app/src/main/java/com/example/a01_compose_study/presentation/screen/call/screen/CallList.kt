package com.example.a01_compose_study.presentation.screen.call.screen

import android.provider.ContactsContract
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
fun CallList(
    contactList: List<Contact>,
    currentIndex: Int? = null,
    vrDynamicBackground: Color,
    fixedBackground: Color,
    onItemClick: (Contact, Int) -> Unit,
) {
    val scrollState = rememberLazyListState()

    LaunchedEffect(currentIndex) {
        val scrollIndex = currentIndex ?: 0
        scrollState.animateScrollToItem(scrollIndex)
    }

    LazyColumn(state = scrollState) {
        itemsIndexed(contactList) { index, callItem ->
            CallListItem(
                contactItem = callItem,
                vrDynamicBackground = vrDynamicBackground,
                fixedBackground = fixedBackground,
                itemIndex = index,
                onItemClick = onItemClick,
            )
        }
    }
}

@Composable
fun CallListItem(
    contactItem: Contact,
    itemIndex: Int,
    vrDynamicBackground: Color,
    fixedBackground: Color,
    onItemClick: (Contact, Int) -> Unit,
) {
    val scope = rememberCoroutineScope()

    var isSelected by remember { mutableStateOf(false) }

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
                        onItemClick(contactItem, itemIndex)
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

                                            val categoryIcon = when(contactItem.type) {
                                                ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE -> {
                                                    painterResource(id = R.drawable.ic_comm_mobile)
                                                }
                                                ContactsContract.CommonDataKinds.Phone.TYPE_HOME -> {
                                                    painterResource(id = R.drawable.ic_comm_home)
                                                }
                                                ContactsContract.CommonDataKinds.Phone.TYPE_WORK -> {
                                                    painterResource(id = R.drawable.ic_comm_office)
                                                }
                                                ContactsContract.CommonDataKinds.Phone.TYPE_OTHER -> {
                                                    painterResource(id = R.drawable.ic_comm_others)
                                                }
                                                else -> {
                                                    painterResource(id = R.drawable.ic_comm_others)
                                                }
                                            }
                                            Image(
                                                painter = categoryIcon,
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
fun CallListItemPreview() {
    val contact = Contact(id = "1", name = "문재민", number = "010-1111-2222")

    CallListItem(contactItem = contact,
        vrDynamicBackground = Color.Black,
        fixedBackground = Color.Black,
        itemIndex = 1,
        onItemClick = { contact: Contact, i: Int ->  }
    )
}