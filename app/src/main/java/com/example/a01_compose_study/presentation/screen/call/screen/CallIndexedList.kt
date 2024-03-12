package com.example.a01_compose_study.presentation.screen.call.screen

import android.provider.ContactsContract
import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationEndReason
import androidx.compose.animation.core.AnimationResult
import androidx.compose.animation.core.AnimationVector1D
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
import kotlinx.coroutines.launch

@Composable
fun CallIndexedList(
    contactList: List<Contact>,
    callListEvent: CallListEvent,
    currentIndex: Int? = null,
    enableScroll: Boolean,
    vrDynamicBackground: Color,
    fixedBackground: Color,
    onItemClick: (Contact, Int) -> Unit,
) {
    val scrollState = rememberLazyListState()

    /**
     * 인자로 받은 currentIndex가 null이 아니라면, Line Number 음성인식으로 index의 값을 받은 상황
     * 만약 null 이라면, Line Number 음성인식을 하지 않은 상태 즉, 보여지고만 있는 상태
     */
    val voiceMatchedContact: Contact? = currentIndex?.let {
        if (it >= 0 && it < contactList.size) {
            contactList[it]
        } else {
            null
        }
    }

    /**
     * currentIndex의 값 변화에 따른 LazyColumn 스크롤 처리
     */
    LaunchedEffect(currentIndex) {
        val scrollIndex = currentIndex ?: 0
        scrollState.animateScrollToItem(scrollIndex)
    }

    /**
     * LineNumber 음성인식 결과값 성공적으로 받아오면 scrollAble의 값을 true로 바꾸고 그 값에 따라 스크롤 위치 처리
     * [LineNumber 음성인식 성공시] : scrollAble 값 true 할당하여 해당 애니메이션 재생된다.
     * [다른 화면으로 갔다가 뒤로가기를 통해 되돌아올 때] : 해당 스택에 저장되어 있는 scrollAble의 값이 true로 되어 있을 것 이기에 false로 replace 한다.
     */
    LaunchedEffect(enableScroll) {
        if (enableScroll) {
            val scrollIndex = currentIndex ?: 0
            scrollState.animateScrollToItem(scrollIndex)
        }
    }

    LazyColumn(state = scrollState) {
        itemsIndexed(contactList) { index, callItem ->
            /**
             * selectedContact(선택한 아이템)의 id와 수신객체인 callItem의 id가 일치하는지의 Boolean값
             * 일치한다면, 게이지 채워진 후 YesNo 화면으로 넘어가고
             * 일치하지 않는다면, 아무일도 벌어지지 않는다.
             */
            val isSelected = callItem.id == voiceMatchedContact?.id
            Log.d("@@-- voiceMatchedContact" ,"${voiceMatchedContact}")
            Log.d("@@-- isSelected" ,"${isSelected} / callItem: ${callItem.id} / voiceMatchedContact: ${voiceMatchedContact?.id}")
            CallIndexedListItem(
                contactItem = callItem,
                callListEvent = callListEvent,
                itemIndex = index,
                voiceMatchedContact = if (isSelected) voiceMatchedContact else null,
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
    callListEvent: CallListEvent,
    itemIndex: Int,
    voiceMatchedContact: Contact? = null,
    vrDynamicBackground: Color,
    fixedBackground: Color,
    onItemClick: (Contact, Int) -> Unit,
) {
    Log.d("@@ voiceMatchedContact2222", "${voiceMatchedContact}")
    val scope = rememberCoroutineScope()

    var isSelected by remember { mutableStateOf(false) }

    val selectAnimatableValue = remember { Animatable(0f) }
    var selectAnimationResult by remember {
        mutableStateOf<AnimationResult<Float, AnimationVector1D>?>(
            null
        )
    }

    LaunchedEffect(callListEvent) {
        Log.d("@@ listProcessingResult", "${callListEvent}")
        when (callListEvent) {
            CallListEvent.Click -> {
                Log.d("@@-- Click" ,"${isSelected} / callItem: ${contactItem.id} / voiceMatchedContact: ${voiceMatchedContact?.id}")
                if (voiceMatchedContact != null) {
                    Log.d("@@-- voiceMatchedContact not Null 33333", "${voiceMatchedContact}")
                    isSelected = true
                    selectAnimationResult = selectAnimatableValue.animateTo(
                        targetValue = 1f,
                        animationSpec = tween(durationMillis = 700)
                    )
                } else {
                    Log.d("@@-- voiceMatchedContact null 4444", "${voiceMatchedContact}")
                }
            }
            else -> { }
        }
    }

    /**
     * ProgressIndicator의 상태 관리 변수인 selectAnimationResult의 상태에 따라,
     * ProgressIndicator가 완료되었다면(AnimationEndReason.Finished) 아이템 클릭 이벤트를 실행
     */
    LaunchedEffect(selectAnimationResult) {
        when (selectAnimationResult?.endReason) {
            AnimationEndReason.Finished -> {
                /**
                 * Line Number를 통해 받은 인덱스의 데이터인 voiceMatchedContact가 null이 아니라면,
                 * 해당 값을 통해 onItemClick()을 실행하고,
                 * Line Number를 통해 받은 인덱스의 데이터인 voiceMatchedContact가 null이라면,
                 * contactItem 직접 클릭으로 실행을 하는것이기에 onItemClick()에 contactItem를 넣어 실행한다.
                 * [voiceMatchedContact: LineNumber를 통해 받은 index값과 일치하는 데이터]
                 * [contactItem: 현재 보여지고 있는 Item]
                 */
                if (voiceMatchedContact != null) {
                    onItemClick(voiceMatchedContact, itemIndex)
                } else {
                    onItemClick(contactItem, itemIndex)
                }
            }

            AnimationEndReason.BoundReached -> {
                // 클릭 애니메이션이 취소되었을때 실행
            }

            else -> {
            }
        }
    }

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
                progress = selectAnimatableValue.value,
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
                        selectAnimationResult = selectAnimatableValue.animateTo(
                            targetValue = 1f,
                            animationSpec = tween(durationMillis = 700)
                        )
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
                                            text = "${itemIndex+1}.",
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
fun CallIndexedListItemPreview() {
    val contact = Contact(id = "1", name = "문재민", number = "010-1111-2222")

    CallIndexedListItem(
        itemIndex = 1,
        callListEvent = CallListEvent.None,
        contactItem = contact,
        voiceMatchedContact = null,
        vrDynamicBackground = Color.Black,
        fixedBackground = Color.Black,
        onItemClick = {contact: Contact, i: Int -> })
}