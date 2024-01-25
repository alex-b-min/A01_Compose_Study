//package com.ftd.ivi.cerence.ui.compose
//
//import android.provider.ContactsContract
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.defaultMinSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.layout.width
//import androidx.compose.foundation.layout.wrapContentHeight
//import androidx.compose.foundation.layout.wrapContentSize
//import androidx.compose.foundation.layout.wrapContentWidth
//import androidx.compose.material.Text
//import androidx.compose.material3.Icon
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.alpha
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.res.colorResource
//import androidx.compose.ui.res.dimensionResource
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.style.TextOverflow
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.sp
//import com.ftd.ivi.cerence.R
//import com.ftd.ivi.cerence.data.model.Contact
//import com.ftd.ivi.cerence.ui.theme.fonts
//
//@Composable
//fun CallRowContent(
//    index: Int,
//    data: Contact,
//    focused: Boolean = false,
//    enableIndex: Boolean = true,
//) {
//    Box(
//        modifier = Modifier
//            .fillMaxWidth()
//            .defaultMinSize(minHeight = dimensionResource(R.dimen.dp_80))
//            .alpha(if (focused) 1.0f else 0.3f)
//    ) {
//
//        Row(
//            modifier = Modifier
//                .defaultMinSize(minHeight = dimensionResource(R.dimen.dp_80))
//                .padding(
//                    top = dimensionResource(R.dimen.dp_10),
//                    bottom = dimensionResource(R.dimen.dp_10)
//                )
//        ) {
//            if (enableIndex) {
//                Box(
//                    modifier = Modifier
//                        .width(dimensionResource(id = R.dimen.navi_list_item_start_guide))
//                        .wrapContentHeight(),
//                    contentAlignment = Alignment.TopCenter
//
//                ) {
//                    Text(
//                        text = "${index + 1}.",
//                        fontFamily = fonts,
//                        fontWeight = FontWeight.Normal,
//                        color = colorResource(id = R.color.white),
//                        fontSize = dimensionResource(id = R.dimen.chrome_common_fg_text_size).value.sp,
//                        modifier = Modifier.wrapContentSize(Alignment.Center)
//                    )
//                }
//            } else {
//                Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.row_start_margin)))
//            }
//
//            Column(
//                modifier = Modifier.defaultMinSize(minHeight = dimensionResource(id = R.dimen.navi_list_item_start_guide))
//            ) {
//
//                var isTextOverflow by remember { mutableStateOf(false) }
//
//                Text(
//
//                    text = "${data.name}",
//                    fontFamily = fonts,
//                    fontWeight = FontWeight.Normal,
//                    color = colorResource(id = R.color.white),
//                    fontSize = dimensionResource(id = R.dimen.chrome_common_fg_text_size).value.sp,
//                    modifier = Modifier
//                        .defaultMinSize(minHeight = dimensionResource(R.dimen.dp_32))
//                        .padding(end = dimensionResource(R.dimen.dp_36_7)),
//                    maxLines = 2,
//                    overflow = TextOverflow.Ellipsis,
//
//                    )
//                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.dp_6_7)))
//                Row(
//                    modifier = Modifier.height(dimensionResource(R.dimen.dp_28)),
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Text(
//                        text = "${data.number}",
//                        fontFamily = fonts,
//                        fontWeight = FontWeight.Normal,
//                        color = colorResource(id = R.color.white_a50),
//                        fontSize = dimensionResource(id = R.dimen.confirm_navi_info_text_size).value.sp,
//                        modifier = Modifier.wrapContentWidth()
//                    )
//
//                    var resource = painterResource(id = R.drawable.ic_comm_home)
//                    when (data.type) {
//                        ContactsContract.CommonDataKinds.Phone.TYPE_HOME -> {
//
//                        }
//
//                        ContactsContract.CommonDataKinds.Phone.TYPE_WORK -> {
//                            resource = painterResource(id = R.drawable.ic_comm_office)
//                        }
//
//                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE -> {
//                            resource = painterResource(id = R.drawable.ic_comm_mobile)
//                        }
//
//                        ContactsContract.CommonDataKinds.Phone.TYPE_OTHER -> {
//                            resource = painterResource(id = R.drawable.ic_comm_others)
//                        }
//                    }
//
//                    Icon(
//                        //imageVector = painterResource(id = R.drawable.ic_comm_home),
//                        painter = resource,
//                        contentDescription = null,
//                        tint = Color.White,
//                        modifier = Modifier
//                            .padding(start = dimensionResource(id = R.dimen.comm_category_icon_margin_start))
//                            .size(dimensionResource(id = R.dimen.comm_category_icon_size))
//                    )
//                }
//
//            }
//
//        }
//    }
//}
//
//@Preview
//@Composable
//fun CallRowPreview() {
//
//    val data = Contact()
//    data.name = "sampleNameasfsdrgsedrthtrjrtjtrkjxfghxdrtgerxgxdfg"
//    data.number = "010-1234-5678"
//
//    CallRowContent(index = 2, data = data)
//}
