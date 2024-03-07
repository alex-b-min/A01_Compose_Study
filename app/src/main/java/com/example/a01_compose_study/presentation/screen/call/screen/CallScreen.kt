package com.example.a01_compose_study.presentation.screen.call.screen

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationEndReason
import androidx.compose.animation.core.AnimationResult
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.a01_compose_study.R
import com.example.a01_compose_study.data.Contact
import com.example.a01_compose_study.data.custom.call.fetchAllContacts
import com.example.a01_compose_study.domain.model.ScreenType
import com.example.a01_compose_study.domain.model.SealedDomainType
import com.example.a01_compose_study.domain.util.ScreenSizeType
import com.example.a01_compose_study.presentation.components.text.TextView
import com.example.a01_compose_study.presentation.data.UiState.closeDomainWindow
import com.example.a01_compose_study.presentation.data.UiState.popUiState
import com.example.a01_compose_study.presentation.screen.call.CallBusinessEvent
import com.example.a01_compose_study.presentation.screen.call.CallEvent
import com.example.a01_compose_study.presentation.screen.call.CallViewModel
import com.example.a01_compose_study.presentation.screen.main.DomainUiState
import com.example.a01_compose_study.presentation.screen.main.route.VRUiState
import com.example.a01_compose_study.ui.theme.Black2
import kotlinx.coroutines.launch

@Composable
fun CallScreen(
    callViewModel: CallViewModel = viewModel(),
    domainUiState: DomainUiState.CallWindow,
    vrUiState: VRUiState,
    vrDynamicBackground: Color,
    fixedBackground: Color,
) {
    if (domainUiState.screenType is ScreenType.CallList) {
        CallListWindow(
            domainUiState = domainUiState,
            vrUiState = vrUiState,
            vrDynamicBackground = vrDynamicBackground,
            fixedBackground = fixedBackground,
            onDismiss = { closeDomainWindow() },
            onBackButton = { popUiState() },
            onItemClick = { contact ->
                callViewModel.onCallEvent(CallEvent.ContactListItemOnClick(selectedContactItem = contact))
            }
        )
    } else if (domainUiState.screenType is ScreenType.CallIndexedList) {
        CallIndexedListWindow(
            domainUiState = domainUiState,
            vrUiState = vrUiState,
            vrDynamicBackground = vrDynamicBackground,
            fixedBackground = fixedBackground,
            onDismiss = { closeDomainWindow() },
            onBackButton = { popUiState() },
            onItemClick = { contact ->
                callViewModel.onCallEvent(CallEvent.ContactListItemOnClick(selectedContactItem = contact))
            }
        )
    } else if (domainUiState.screenType is ScreenType.CallYesNo) {
        CallYesNoScreen(
            domainUiState = domainUiState,
            vrUiState = vrUiState,
            vrDynamicBackground = vrDynamicBackground,
            fixedBackground = fixedBackground,
            onDismiss = { closeDomainWindow() },
            onBackButton = { popUiState() },
            onCalling = { callViewModel.onCallBusinessEvent(CallBusinessEvent.Calling) }
        )
    }
}

@Composable
fun CallListWindow(
    domainUiState: DomainUiState.CallWindow,
    vrUiState: VRUiState,
    vrDynamicBackground: Color,
    fixedBackground: Color,
    onDismiss: () -> Unit,
    onBackButton: () -> Unit,
    onItemClick: (Contact) -> Unit,
) {
    val contactList = domainUiState.data

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(15.dp))
            .background(vrDynamicBackground)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_guidance_btn_back),
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier
                        .clickable {
                            onBackButton()
                        }
                )
                Icon(
                    painter = painterResource(id = R.drawable.ic_close),
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier
                        .padding(end = 10.dp, top = 10.dp)
                        .clickable {
                            onDismiss()
                        }
                )
            }
            CallList(
                contactList = contactList,
                vrDynamicBackground = vrDynamicBackground,
                fixedBackground = fixedBackground,
                onItemClick = { contact ->
                    onItemClick(contact)
                })
        }
    }
}

@Composable
fun CallIndexedListWindow(
    domainUiState: DomainUiState.CallWindow,
    vrUiState: VRUiState,
    vrDynamicBackground: Color,
    fixedBackground: Color,
    onDismiss: () -> Unit,
    onBackButton: () -> Unit,
    onItemClick: (Contact) -> Unit,
) {
    val contactList = domainUiState.data
    val selectedIndex = domainUiState.scrollIndex

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(15.dp))
            .background(vrDynamicBackground)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_guidance_btn_back),
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier
                        .clickable {
                            onBackButton()
                        }
                )
                Icon(
                    painter = painterResource(id = R.drawable.ic_close),
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier
                        .padding(end = 10.dp, top = 10.dp)
                        .clickable {
                            onDismiss()
                        }
                )
            }
            CallIndexedList(
                contactList = contactList,
                vrDynamicBackground = vrDynamicBackground,
                selectedIndex = selectedIndex,
                fixedBackground = fixedBackground,
                onItemClick = { contact ->
                    onItemClick(contact)
                })
        }
    }
}

@Composable
fun CallYesNoScreen(
    domainUiState: DomainUiState.CallWindow,
    vrUiState: VRUiState,
    vrDynamicBackground: Color,
    fixedBackground: Color,
    onDismiss: () -> Unit,
    onBackButton: () -> Unit,
    onCalling: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    var isYesSelected by remember { mutableStateOf(false) }
    var isNoSelected by remember { mutableStateOf(false) }

    val yesAnimatableValue = remember { Animatable(0f) }
    var yesAnimationResult by remember {
        mutableStateOf<AnimationResult<Float, AnimationVector1D>?>(
            null
        )
    }

    val noAnimatableValue = remember { Animatable(0f) }
    var noAnimationResult by remember {
        mutableStateOf<AnimationResult<Float, AnimationVector1D>?>(
            null
        )
    }

    // 화면에 처음 들어왔을 때 자동으로 yes 애니메이션 게이지가 차오르게 하는 코드
    LaunchedEffect(Unit) {
        isYesSelected = true
        yesAnimationResult = yesAnimatableValue.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 5000) // 여기에 duration을 설정
        )
    }

    // yes 애니메이션 결과값에 대한 처리
    when (yesAnimationResult?.endReason) {
        AnimationEndReason.Finished -> {
            // 애니메이션이 성공적으로 완료되었을 때 실행할 로직
            onCalling()
        }

        AnimationEndReason.BoundReached -> {
            // 애니메이션이 취소되었을때 실행
        }

        else -> {
        }
    }

    // no 애니메이션 결과값에 대한 처리
    when (noAnimationResult?.endReason) {
        AnimationEndReason.Finished -> {
            // 애니메이션이 성공적으로 완료되었을 때 실행할 로직
            onBackButton()
        }

        AnimationEndReason.BoundReached -> {
            // 애니메이션이 취소되었을때 실행
        }

        else -> {
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(15.dp))
            .background(vrDynamicBackground)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_guidance_btn_back),
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier
                        .clickable {
                            onBackButton()
                        }
                )
                Icon(
                    painter = painterResource(id = R.drawable.ic_close),
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier
                        .padding(end = 10.dp, top = 10.dp)
                        .clickable {
                            onDismiss()
                        }
                )
            }
            TextView(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .wrapContentHeight(),
                text = domainUiState.detailData.name
            )
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.navi_list_poi_name_margin_top)))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextView(
                    modifier = Modifier
                        .wrapContentSize()
                        .alignByBaseline(),
                    text = domainUiState.detailData.number,
                    textSize = dimensionResource(R.dimen.confirm_navi_info_text_size),
                    textColor = colorResource(id = R.color.white_a50),
                )
                Spacer(modifier = Modifier.width(6.dp))
                Image(
                    painter = painterResource(id = R.drawable.ic_comm_office),
                    contentDescription = "OtherNumber_Office",
                    modifier = Modifier
                        .width(dimensionResource(R.dimen.dp_16))
                        .height(dimensionResource(R.dimen.dp_16))
                )
            }
            Spacer(modifier = Modifier.fillMaxHeight(0.25f))

            Row(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(70.dp)
                    .wrapContentWidth(Alignment.CenterHorizontally),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    OutlinedButton(
                        onClick = {
                            scope.launch {
                                /**
                                 * 직접 Yes 버튼 클릭 제어
                                 */
                                isYesSelected = true
                                yesAnimationResult = yesAnimatableValue.animateTo(
                                    targetValue = 1f,
                                    animationSpec = tween(durationMillis = 700)
                                )
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                        , colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.Black,
                            backgroundColor = Black2,
                        ),
                        shape = MaterialTheme.shapes.medium,
                        border = BorderStroke(0.5.dp, Color.Gray),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            LinearProgressIndicator(
                                progress = yesAnimatableValue.value,
                                modifier = Modifier.fillMaxSize(),
                                color = if (isYesSelected) Color.DarkGray else Color.Transparent,
                                backgroundColor = Color.Transparent,
                            )

                            Text(
                                text = "Yes",
                                fontSize = 22.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    OutlinedButton(
                        onClick = {
                            scope.launch {
                                /**
                                 * 직접 No 버튼 클릭 제어
                                 */
                                isNoSelected = true
                                noAnimationResult = noAnimatableValue.animateTo(
                                    targetValue = 1f,
                                    animationSpec = tween(durationMillis = 700)
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxSize()
                        ,colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.Black,
                            backgroundColor = Black2,
                        ),
                        shape = MaterialTheme.shapes.medium,
                        border = BorderStroke(0.5.dp, Color.Gray),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                        ) {
                            LinearProgressIndicator(
                                progress = noAnimatableValue.value,
                                modifier = Modifier.fillMaxSize(),
                                color = if (isNoSelected) Color.DarkGray else Color.Transparent,
                                backgroundColor = Color.Transparent
                            )
                            Text(
                                text = "No",
                                fontSize = 22.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(15.dp))

            OutlinedButton(
                onClick = {
                    scope.launch {
                        /**
                         * 직접 No 버튼 클릭 제어
                         */
                        isNoSelected = true
                        noAnimationResult = noAnimatableValue.animateTo(
                            targetValue = 1f,
                            animationSpec = tween(durationMillis = 700)
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(70.dp)
                ,colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.Black,
                    backgroundColor = Black2,
                ),
                shape = MaterialTheme.shapes.medium,
                border = BorderStroke(0.5.dp, Color.Gray),
                contentPadding = PaddingValues(0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    LinearProgressIndicator(
                        progress = noAnimatableValue.value,
                        modifier = Modifier.fillMaxSize(),
                        color = if (isNoSelected) Color.DarkGray else Color.Transparent,
                        backgroundColor = Color.Transparent
                    )
                    Text(
                        text = "Other numbers",
                        fontSize = 22.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }

//            Column(
//                modifier = Modifier.fillMaxWidth(0.85f),
//                verticalArrangement = Arrangement.spacedBy(12.dp),
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                OutlinedButton(
//                    onClick = {
//                        scope.launch {
//                            /**
//                             * 직접 Yes 버튼 클릭 제어
//                             */
//                            isYesSelected = true
//                            yesAnimationResult = yesAnimatableValue.animateTo(
//                                targetValue = 1f,
//                                animationSpec = tween(durationMillis = 700)
//                            )
//                        }
//                    },
//                    modifier = Modifier
//                        .width(400.dp)
//                        .height(70.dp),
//                    colors = ButtonDefaults.outlinedButtonColors(
//                        contentColor = Color.Black,
//                        backgroundColor = Black2,
//                    ),
//                    shape = MaterialTheme.shapes.medium,
//                    border = BorderStroke(0.5.dp, Color.Gray),
//                    contentPadding = PaddingValues(0.dp)
//                ) {
//                    Box(
//                        modifier = Modifier
//                            .fillMaxSize()
//                            .padding(0.dp)
//                    ) {
//                        LinearProgressIndicator(
//                            progress = yesAnimatableValue.value,
//                            modifier = Modifier.fillMaxSize(),
//                            color = if (isYesSelected) Color.DarkGray else Color.Transparent,
//                            backgroundColor = Color.Transparent,
//                        )
//
//                        Text(
//                            text = "Yes",
//                            fontSize = 22.sp,
//                            color = Color.White,
//                            fontWeight = FontWeight.Bold,
//                            modifier = Modifier.align(Alignment.Center)
//                        )
//                    }
//                }
//                OutlinedButton(
//                    onClick = {
//                        scope.launch {
//                            /**
//                             * 직접 No 버튼 클릭 제어
//                             */
//                            isNoSelected = true
//                            noAnimationResult = noAnimatableValue.animateTo(
//                                targetValue = 1f,
//                                animationSpec = tween(durationMillis = 700)
//                            )
//                        }
//                    },
//                    modifier = Modifier
//                        .width(400.dp)
//                        .height(70.dp),
//                    colors = ButtonDefaults.outlinedButtonColors(
//                        contentColor = Color.Black,
//                        backgroundColor = Black2,
//                    ),
//                    shape = MaterialTheme.shapes.medium,
//                    border = BorderStroke(0.5.dp, Color.Gray),
//                    contentPadding = PaddingValues(0.dp)
//                ) {
//                    Box(
//                        modifier = Modifier
//                            .fillMaxSize()
//                            .padding(0.dp)
//                    ) {
//                        LinearProgressIndicator(
//                            progress = noAnimatableValue.value,
//                            modifier = Modifier.fillMaxSize(),
//                            color = if (isNoSelected) Color.DarkGray else Color.Transparent,
//                            backgroundColor = Color.Transparent
//                        )
//                        Text(
//                            text = "No",
//                            fontSize = 22.sp,
//                            color = Color.White,
//                            fontWeight = FontWeight.Bold,
//                            modifier = Modifier.align(Alignment.Center)
//                        )
//                    }
//                }
//            }
        }
    }
}


@Preview
@Composable
fun CallListWindowPreview() {
    CallListWindow(
        domainUiState = DomainUiState.CallWindow(
            domainType = SealedDomainType.Call,
            data = fetchAllContacts(),
            screenType = ScreenType.CallIndexedList,
            screenSizeType = ScreenSizeType.Middle
        ),
        vrUiState = VRUiState.PttSpeak(active = true, isError = false),
        vrDynamicBackground = Color.Black,
        fixedBackground = Color.Black,
        onDismiss = {},
        onBackButton = {},
        onItemClick = {}
    )
}

@Preview
@Composable
fun CallIndexedListWindowPreview() {
    CallIndexedListWindow(
        domainUiState = DomainUiState.CallWindow(
            domainType = SealedDomainType.Call,
            data = fetchAllContacts(),
            screenType = ScreenType.CallIndexedList,
            screenSizeType = ScreenSizeType.Middle
        ),
        vrUiState = VRUiState.PttSpeak(active = true, isError = false),
        vrDynamicBackground = Color.Black,
        fixedBackground = Color.Black,
        onDismiss = {},
        onBackButton = {},
        onItemClick = {}
    )
}

@Preview(Devices.TABLET)
@Composable
fun CallYesNoPreview() {
    CallYesNoScreen(
        domainUiState = DomainUiState.CallWindow(
            domainType = SealedDomainType.Call,
            data = fetchAllContacts(),
            detailData = Contact(id = "1", name = "문재민", number = "010-1111-2222"),
            screenType = ScreenType.CallIndexedList,
            screenSizeType = ScreenSizeType.Middle
        ),
        vrUiState = VRUiState.PttSpeak(active = true, isError = false),
        vrDynamicBackground = Color.Black,
        fixedBackground = Color.Black,
        onDismiss = {},
        onBackButton = {},
        onCalling = {}
    )
}