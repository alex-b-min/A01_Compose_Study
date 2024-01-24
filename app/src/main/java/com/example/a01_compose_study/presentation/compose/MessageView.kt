package com.ftd.ivi.cerence.ui.compose

import android.provider.ContactsContract
import android.telephony.PhoneNumberUtils
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ftd.ivi.cerence.R
import com.ftd.ivi.cerence.data.model.Contact
import com.ftd.ivi.cerence.ui.theme.CerenceTheme
import com.ftd.ivi.cerence.util.StringUtils

@Composable
fun TextButton(
    buttonText: String,
    modifier: Modifier = Modifier,
    isAutoSize: Boolean = false,
    fractionWidth: Float = 1.0f,
    fontSize: TextUnit = TextUnit.Unspecified,
    clickListener: (String) -> Unit = {}
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val bgColor =
        if (isPressed) colorResource(id = R.color.confirm_fill_color) else colorResource(id = R.color.confirm_btn_bg_color)
    val textColor =
        if (isPressed) colorResource(id = R.color.confirm_text_inverted_color) else Color.White
    Button(modifier = Modifier
        .fillMaxWidth(fractionWidth)
        .then(modifier),
        interactionSource = interactionSource,
        colors = ButtonDefaults.textButtonColors(
            containerColor = bgColor
        ),
        shape = RoundedCornerShape(dimensionResource(id = R.dimen.agent_bg_radius)),
        border = BorderStroke(1.dp, colorResource(id = R.color.white_a15)),
        onClick = {
            clickListener.invoke(buttonText)
        }) {

        if (isAutoSize) {
            AutoSizeText(
                textAlign = TextAlign.Center,
                text = AnnotatedString(buttonText),
                style = MaterialTheme.typography.headlineMedium,
                color = textColor,
                fontSize = fontSize
            )
        } else {
            Text(
                textAlign = TextAlign.Center,
                text = buttonText,
                style = MaterialTheme.typography.headlineMedium,
                color = textColor
            )
        }

    }
}

@Composable
fun SendButton(clickListener: (String) -> Unit = {}) {
    TextButton(
        buttonText = stringResource(id = R.string.TID_CMN_BUTN_01_04),
        clickListener = clickListener,
        modifier = Modifier
            .height(dimensionResource(R.dimen.dp_60))
    )
}

@Composable
fun CancelButton(clickListener: (String) -> Unit = {}) {
    TextButton(
        buttonText = stringResource(id = R.string.TID_CMN_BUTN_01_05),
        clickListener = clickListener,
        modifier = Modifier
            .height(dimensionResource(R.dimen.dp_60))
    )
}

//@Composable
//fun ReplyButton(clickListener: (String) -> Unit = {}) {
//    TextButton(
//        buttonText = stringResource(id = R.string.message_comm_confirm_reply),
//        clickListener = clickListener,
//        modifier = Modifier
//            .padding(
//                start = dimensionResource(id = R.dimen.confirm_btn_margin_side),
//                end = dimensionResource(id = R.dimen.confirm_btn_margin_side)
//            )
//            .height(dimensionResource(R.dimen.dp_60))
//    )
//}

@Composable
fun MsgTextField(
    modifier: Modifier = Modifier,
    inputText: String = StringUtils.EMPTY,
    tapListener: (() -> Unit)? = null,
    nextListener: (() -> Unit)? = null
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = colorResource(id = R.color.confirm_btn_bg_color),
                shape = RoundedCornerShape(dimensionResource(id = R.dimen.agent_bg_radius))
            )
            .border(
                border = BorderStroke(1.dp, colorResource(id = R.color.white_a15)),
                shape = RoundedCornerShape(dimensionResource(id = R.dimen.agent_bg_radius))
            )
            .clickable(
                onClick = {
                    tapListener?.invoke()
                    nextListener?.invoke()
                },
                enabled = (tapListener != null || nextListener != null)
            )
    ) {
        Text(
            modifier = Modifier.padding(dimensionResource(R.dimen.dp_20)),
            text = if (inputText == "nextMsg") "" else inputText.ifEmpty { "|" },
            color = Color.White,
            style = MaterialTheme.typography.bodyLarge.copy(
                letterSpacing = (-0.4).sp,
                lineHeight = 30.sp
            ),
            maxLines = 3,
        )
        if (tapListener != null) {
            Image(
                painter = painterResource(id = R.drawable.ic_msg_edit),
                contentDescription = "msg edit",
                modifier = Modifier
                    .padding(
                        end = dimensionResource(R.dimen.dp_6_7),
                        bottom = dimensionResource(R.dimen.dp_6_7)
                    )
                    .width(dimensionResource(R.dimen.dp_20))
                    .height(dimensionResource(R.dimen.dp_20))
                    .align(Alignment.BottomEnd),
            )
        }
    }
}

@Composable
fun ContactInfoView(
    contact: Contact,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = contact.name.ifEmpty { formatNumber(contact.number) },
            style = MaterialTheme.typography.headlineSmall.copy(
                lineHeight = 32.sp,
                letterSpacing = (-0.48).sp
            ),
            color = Color.White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis

        )

        Spacer(Modifier.height(5.dp))
        Row(modifier = Modifier.alpha(if (contact.name.isEmpty()) 0f else 1f)) {
            Text(
                text = formatNumber(contact.number),
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Normal,
                    letterSpacing = (-0.87).sp
                ),
                color = colorResource(id = R.color.white_a50)
            )
            PhoneTypeIcon(contact.type)
        }
    }
}

@Composable
private fun formatNumber(number: String) =
    if (number.length <= 2) number else PhoneNumberUtils.formatNumber(
        number.take(20),
        "KR"
    )

@Composable
fun PhoneTypeIcon(type: Int) {
    val typeId = when (type) {
        ContactsContract.CommonDataKinds.Phone.TYPE_HOME -> R.drawable.ic_comm_home
        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE -> R.drawable.ic_comm_mobile
        ContactsContract.CommonDataKinds.Phone.TYPE_WORK -> R.drawable.ic_comm_office
        else -> R.drawable.ic_comm_others
    }
    Image(
        painter = painterResource(id = typeId),
        contentDescription = "icon type",
        modifier = Modifier
            .height(17.3.dp)
            .padding(
                start = dimensionResource(id = R.dimen.comm_category_icon_margin_start), top = 2.dp
            )
    )
}

@Composable
fun GetMsgContactMsgFilledView(
    modifier: Modifier = Modifier,
    msgBody: String = StringUtils.EMPTY,
    displayText: String = StringUtils.EMPTY,
    clickListener: ((String) -> Unit) = {}
) {
    Column(modifier = modifier) {
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.agent_start_margin)))
        MainDisplayText(displayText)
        Spacer(modifier = Modifier.height(50.7.dp))
        MsgTextField(
            inputText = msgBody,
            modifier = Modifier
                .height(dimensionResource(R.dimen.dp_130))
        )
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.dp_33_3)))
        CancelButton(clickListener)
    }
}

@Composable
fun GetMsgBodyView(
    contact: Contact,
    modifier: Modifier = Modifier,
    msgBody: String = StringUtils.EMPTY,
    clickListener: ((String) -> Unit) = {}
) {
    Column(modifier = modifier) {
        ContactInfoView(
            contact = contact,
            modifier = Modifier.padding(top = dimensionResource(R.dimen.agent_start_margin))
        )
        Spacer(Modifier.height(dimensionResource(R.dimen.dp_21_3)))
        MsgTextField(
            inputText = msgBody,
            modifier = Modifier
                .height(dimensionResource(R.dimen.dp_130))
        )
        Spacer(Modifier.height(dimensionResource(R.dimen.dp_33_3)))
        CancelButton(clickListener)
    }
}


@Composable
fun SendButtonMsgView(
    contact: Contact,
    modifier: Modifier = Modifier,
    msgBody: String = StringUtils.EMPTY,
    clickListener: ((String) -> Unit) = {},
    msgBodyTapListener: (() -> Unit)? = null,
) {
    Column(modifier = modifier) {
        ContactInfoView(
            contact = contact,
            modifier = Modifier.padding(top = dimensionResource(R.dimen.agent_start_margin))
        )
        Spacer(Modifier.height(dimensionResource(R.dimen.dp_21_3)))
        MsgTextField(
            inputText = msgBody,
            tapListener = msgBodyTapListener,
            modifier = Modifier
                .height(dimensionResource(R.dimen.dp_130))
        )
        Spacer(Modifier.height(dimensionResource(R.dimen.dp_33_3)))
        SendButton(clickListener)
    }
}

//@Composable
//fun SendingMsgView(
//    contact: Contact,
//    modifier: Modifier = Modifier,
//    msgBody: String = StringUtils.EMPTY
//) {
//    Column {
//        ContactInfoView(
//            contact = contact,
//            modifier = modifier.padding(top = dimensionResource(R.dimen.agent_start_margin))
//        )
//        Spacer(Modifier.height(dimensionResource(R.dimen.dp_21_3)))
//        MsgTextField(
//            inputText = msgBody,
//            modifier = Modifier
//                .height(dimensionResource(R.dimen.dp_130))
//                .then(modifier)
//        )
//        Spacer(Modifier.height(dimensionResource(R.dimen.dp_33_3)))
//        SendingStatusText(text = stringResource(id = R.string.message_comm_sending_msg))
//    }
//}

@Composable
fun SendingStatusText(text: String) {
    Row(
        modifier = Modifier.height(dimensionResource(R.dimen.dp_60)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = text,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineMedium.plus(
                TextStyle(
                    letterSpacing = (-0.48).sp,
                    lineHeight = 32.sp
                )
            ),
            color = Color.White,
        )
    }
}

//@Composable
//fun ReadMsgView(
//    contact: Contact,
//    modifier: Modifier = Modifier,
//    msgBody: String = StringUtils.EMPTY,
//    clickListener: ((String) -> Unit) = {},
//    nextEventListener: (() -> Unit)? = null,
//    continued: Boolean = false
//) {
//    var onPostFling by remember { mutableStateOf(false) }
//    val nestedScrollConnection = remember {
//        object : NestedScrollConnection {
//            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
//                if (continued && !onPostFling) {
//                    CustomLogger.e("onPostFling - swipe listener invoke")
//                    nextEventListener?.invoke()
//                    onPostFling = true
//                }
//                return super.onPostFling(consumed, available)
//            }
//        }
//    }
//
//    Column(modifier = modifier) {
//        Box(Modifier.nestedScroll(nestedScrollConnection)) {
//            LazyRow(
//                contentPadding = PaddingValues(horizontal = dimensionResource(R.dimen.dp_33_3)),
//                horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.dp_16_7)),
//                state = rememberLazyListState()
//            ) {
//                if (!onPostFling) {
//                    item {
//                        Column {
//                            ContactInfoView(
//                                contact = contact,
//                                modifier = Modifier.padding(top = dimensionResource(R.dimen.agent_start_margin))
//                            )
//                            Spacer(Modifier.height(dimensionResource(R.dimen.dp_21_3)))
//                            MsgTextField(
//                                inputText = msgBody,
//                                modifier = Modifier
//                                    .width(dimensionResource(R.dimen.dp_360))
//                                    .height(dimensionResource(R.dimen.dp_130)),
//                                nextListener = if (continued) nextEventListener else null
//                            )
//                        }
//                    }
//                }
//
//                if (continued) {
//                    item {
//                        Column {
//                            ContactInfoView(
//                                contact = Contact(),
//                                modifier = Modifier.padding(top = dimensionResource(R.dimen.agent_start_margin))
//                            )
//                            Spacer(Modifier.height(dimensionResource(R.dimen.dp_21_3)))
//                            MsgTextField(
//                                inputText = "nextMsg",
//                                modifier = Modifier
//                                    .height(dimensionResource(R.dimen.dp_130))
//                                    .width(dimensionResource(R.dimen.dp_360))
//                            )
//                        }
//                    }
//                }
//            }
//        }
//
//        Spacer(Modifier.height(dimensionResource(R.dimen.dp_33_3)))
//        ReplyButton(clickListener)
//    }
//}

@Preview
@Composable
fun MsgFilledPreview() {
    CerenceTheme {
        Surface(
            color = Color.Black,
            modifier = Modifier
                .width(dimensionResource(R.dimen.agent_width))
                .height(dimensionResource(R.dimen.agent_max_height))
        ) {
            GetMsgContactMsgFilledView(
                modifier = Modifier.padding(
                    start = dimensionResource(id = R.dimen.confirm_btn_margin_side),
                    end = dimensionResource(id = R.dimen.confirm_btn_margin_side)
                ),
                msgBody = "자니자냐고 야야야야야야야야야야야야야야야야야야야야야야야야야야야야야야야야야야야야야오오오오오오오오오오오오",
                displayText = "누구에게 보낼까요?"
            )
        }
    }
}

@Preview
@Composable
fun SendMsgPreview() {
    CerenceTheme {
        Surface(
            color = Color.Black,
            modifier = Modifier
                .width(dimensionResource(R.dimen.agent_width))
                .height(dimensionResource(R.dimen.agent_max_height))
        ) {
            SendButtonMsgView(
                msgBody = "있잖아 내가 올리브영에서 사준 선크림 다시 돌려줄래?",
                modifier = Modifier.padding(
                    start = dimensionResource(id = R.dimen.confirm_btn_margin_side),
                    end = dimensionResource(id = R.dimen.confirm_btn_margin_side)
                ),
                contact = Contact(
                    id = "1",
                    name = "전여친여친여친여친여친여친여친여친여친여친",
                    number = "010-1234-5678-1234556667",
                    type = ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE
                ),
                msgBodyTapListener = {}
            )
        }
    }
}

//@Preview
//@Composable
//fun SendingMsgPreview() {
//    EDITHTheme {
//        Surface(
//            color = Color.Black,
//            modifier = Modifier
//                .width(dimensionResource(R.dimen.agent_width))
//                .height(dimensionResource(R.dimen.agent_max_height))
//        ) {
//            SendingMsgView(
//                msgBody = "있잖아 내가 올리브영에서 사준 선크림 다시 돌려줄래?",
//                modifier = Modifier.padding(
//                    start = dimensionResource(id = R.dimen.confirm_btn_margin_side),
//                    end = dimensionResource(id = R.dimen.confirm_btn_margin_side)
//                ),
//                contact = Contact(
//                    id = "1",
//                    name = "전여친",
//                    number = "010-1234-5678",
//                    type = ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE
//                )
//            )
//        }
//    }
//}

//@Preview
//@Composable
//fun ReadMsgPreview() {
//    EDITHTheme {
//        Surface(
//            color = Color.Black,
//            modifier = Modifier
//                .width(dimensionResource(R.dimen.agent_width))
//                .height(dimensionResource(R.dimen.agent_max_height))
//        ) {
//            ReadMsgView(
//                msgBody = "있잖아 내가 올리브영에서 사준 선크림 다시 돌려줄래?",
//                contact = Contact(
//                    id = "",
//                    name = "",
//                    number = "+82 10-5078-1398",
//                    type = -1
//                ),
//                continued = true
//            )
//        }
//    }
//}