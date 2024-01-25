//package com.ftd.ivi.cerence.ui.compose
//
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.clickable
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
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import com.ftd.ivi.cerence.R
//import com.ftd.ivi.cerence.data.model.ui.NaviData
//import com.ftd.ivi.cerence.data.model.ui.NaviFavoriteIconType
//import com.ftd.ivi.cerence.data.model.ui.NaviStationIconType
//import com.ftd.ivi.cerence.ui.theme.fonts
//import java.math.BigDecimal
//import java.math.RoundingMode
//
//
//@Composable
//fun NaviRowContent(
//    index: Int,
//    data: NaviData,
//    focused: Boolean = false,
//    isListed: Boolean = true,
//    isRecentList: Boolean = false
//) {
//    var textAlignment = Alignment.Start
//    var boxAlignment = Alignment.TopStart
//
//    val dist = if (data.distance > 1000) {
//        val number = BigDecimal(data.distance / 1000)
//        val result = number.setScale(1, RoundingMode.DOWN)
//        result.toString() + "KM"
//    } else {
//        val number = BigDecimal(data.distance)
//        val result = number.setScale(1, RoundingMode.DOWN)
//        result.toString() + "M"
//    }
//
//    val name = if (data.poi_name != "") {
//        data.poi_name
//    } else {
//        data.address
//    }
//
//    if (!isListed) {
//        textAlignment = Alignment.CenterHorizontally
//        boxAlignment = Alignment.Center
//    }
//
//    Box(
//        modifier = Modifier
//            .fillMaxWidth()
//            .defaultMinSize(minHeight = dimensionResource(R.dimen.dp_80))
//            .alpha(if (focused) 1.0f else 0.3f),
//        contentAlignment = boxAlignment
//    ) {
//
//        Row(
//            modifier = Modifier
//                .defaultMinSize(minHeight = dimensionResource(R.dimen.dp_80))
//                .padding(
//                    top = dimensionResource(R.dimen.dp_10),
//                    bottom = dimensionResource(R.dimen.dp_10)
//                )
//                .clickable { }
//        ) {
//            if (isListed) {
//                Box(
//                    modifier = Modifier
//                        .width(dimensionResource(id = R.dimen.navi_list_item_start_guide))
//                        .wrapContentHeight(), contentAlignment = Alignment.TopCenter
//
//                ) {
//                    Text(
//                        text = data.nIndex.toString(),
//                        fontFamily = fonts,
//                        fontWeight = FontWeight.Normal,
//                        color = colorResource(id = R.color.white),
//                        fontSize = dimensionResource(id = R.dimen.chrome_common_fg_text_size).value.sp,
//                        modifier = Modifier.wrapContentSize(Alignment.Center)
//                    )
//
//                }
//            }
//            Column(horizontalAlignment = textAlignment) {
//                val modifier = if (isListed) {
//                    Modifier
//                        .defaultMinSize(minHeight = dimensionResource(R.dimen.dp_32))
//                        .padding(end = dimensionResource(R.dimen.dp_36_7))
//                } else {
//                    Modifier.wrapContentSize(Alignment.Center)
//                }
//                Text(
//                    text = name,
//                    fontFamily = fonts,
//                    fontWeight = FontWeight.Normal,
//                    color = colorResource(id = R.color.white),
//                    fontSize = dimensionResource(id = R.dimen.chrome_common_fg_text_size).value.sp,
//                    modifier = modifier,
//                    maxLines = 2,
//                    overflow = TextOverflow.Ellipsis
//                )
//                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.dp_6_7)))
//                Row(
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    if (NaviFavoriteIconType.NONE != data.favorite) {
//                        Icon(
//                            painter = painterResource(R.drawable.ic_navi_favorite),
//                            contentDescription = null,
//                            tint = Color.Unspecified,
//                            modifier = Modifier
//                                .padding(end = dimensionResource(id = R.dimen.dp_6_7))
//                                .size(dimensionResource(id = R.dimen.dp_16))
//                        )
//                    }
//
//                    if (NaviStationIconType.NONE != data.station) {
//                        Icon(
//                            painter = painterResource(R.drawable.ic_navi_my_evstation),
//                            contentDescription = null,
//                            tint = Color.Unspecified,
//                            modifier = Modifier
//                                .padding(end = dimensionResource(id = R.dimen.dp_6_7))
//                                .size(dimensionResource(id = R.dimen.dp_16))
//                        )
//                    }
//
//                    if (isRecentList) {
//                        Icon(
//                            painter = painterResource(R.drawable.ic_navi_recent),
//                            contentDescription = null,
//                            tint = Color.Unspecified,
//                            modifier = Modifier
//                                .padding(end = dimensionResource(id = R.dimen.dp_6_7))
//                                .size(dimensionResource(id = R.dimen.dp_16))
//                        )
//                    }
//
//                    Text(
//                        text = dist,
//                        fontFamily = fonts,
//                        fontWeight = FontWeight.Normal,
//                        color = colorResource(id = R.color.white_a50),
//                        fontSize = dimensionResource(id = R.dimen.confirm_navi_info_text_size).value.sp,
//                        modifier = Modifier.wrapContentWidth()
//                    )
//                    Image(
//                        painter = painterResource(id = R.drawable.ic_confirm_navi_dot_indicator),
//                        contentDescription = null,
//                        modifier = Modifier
//                            .padding(
//                                start = dimensionResource(id = R.dimen.confirm_navi_charge_text_side_margin),
//                                end = dimensionResource(id = R.dimen.confirm_navi_charge_text_side_margin)
//                            )
//                            .size(3.3.dp)
//                    )
//                    Text(
//                        text = data.address,
//                        fontFamily = fonts,
//                        fontWeight = FontWeight.Normal,
//                        color = colorResource(id = R.color.white_a50),
//                        fontSize = dimensionResource(id = R.dimen.confirm_navi_info_text_size).value.sp,
//                        modifier = Modifier.wrapContentWidth(),
//                        maxLines = 1,
//                        overflow = TextOverflow.Ellipsis
//                    )
//                }
//            }
//        }
//    }
//}
//
//@Preview
//@Composable
//fun NaviRowPreview() {
//    val data = NaviData(1)
//    data.poi_name = "서울역 경부선 부산방향 (고속철도 ktx) 최대 두줄까지 표시해주세요 가운데 정렬도 해야해요"
//    data.address = "서울 용산구 동자동 (남영동) 43-205"
//    data.distance = 61862.438391
//    data.station = NaviStationIconType.NONE
//    data.favorite = NaviFavoriteIconType.FAVORITE1
//    data.nIndex = 1
//
//    NaviRowContent(
//        index = 2,
//        data = data,
//        isListed = true,
//        isRecentList = false
//    )
//
//}
