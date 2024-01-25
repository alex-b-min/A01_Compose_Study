//package com.ftd.ivi.cerence.ui.compose
//
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.layout.width
//import androidx.compose.foundation.layout.wrapContentHeight
//import androidx.compose.foundation.layout.wrapContentWidth
//import androidx.compose.material.Text
//import androidx.compose.material3.Icon
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.res.dimensionResource
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.constraintlayout.compose.ConstraintLayout
//import com.ftd.ivi.cerence.R
//import com.ftd.ivi.cerence.data.model.ui.WeatherIconType
//import com.ftd.ivi.cerence.data.model.ui.WeatherItem
//import java.time.DayOfWeek
//
//
//@Composable
//fun WeatherRow(
//    data: WeatherItem,
//    temperatureUnit: String
//) {
//    var iconResource = painterResource(
//        id = when (data.iconType) {
//            WeatherIconType.SUN -> {
//                R.drawable.ic_sun_m
//            }
//
//            WeatherIconType.MOSTLYCLOUD -> {
//                R.drawable.ic_suncloud_m
//            }
//
//            WeatherIconType.MOSTLYSUNNY -> {
//                R.drawable.ic_mostlysunny_m
//            }
//
//            WeatherIconType.CLOUD -> {
//                R.drawable.ic_cloud_m
//            }
//
//            WeatherIconType.FOG -> {
//                R.drawable.ic_fog_m
//            }
//
//            WeatherIconType.RAINSNOW -> {
//                R.drawable.ic_rainsnow_m
//            }
//
//            WeatherIconType.RAIN -> {
//                R.drawable.ic_rain_m
//            }
//
//            WeatherIconType.SHOWERS -> {
//                R.drawable.ic_showers_m
//            }
//
//            WeatherIconType.RAINSUNNY -> {
//                R.drawable.ic_rainsunny_m
//            }
//
//            WeatherIconType.THUNDER -> {
//                R.drawable.ic_thunder_m
//            }
//
//            WeatherIconType.SNOW -> {
//                R.drawable.ic_snow_m
//            }
//
//            WeatherIconType.SNOWSUNNY -> {
//                R.drawable.ic_snowsunny_m
//            }
//
//            else -> {
//                R.drawable.ic_sun_m
//            }
//        }
//    )
//    var lowTemp = "0°"
//    data.lowTemp.let {
//        lowTemp = if (temperatureUnit == "0")
//            "$it°C"
//        else
//            "$it°F"
//    }
//    var highTemp = "0°"
//    data.highTemp.let {
//        highTemp = if (temperatureUnit == "0")
//            "$it°C"
//        else
//            "$it°F"
//    }
//
//    var fontColor = if (data.dayOfWeek != DayOfWeek.SUNDAY) Color.White else Color.Red
//
//
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .wrapContentHeight()
//    ) {
//        Box(
//            modifier = Modifier.size(
//                width = dimensionResource(R.dimen.dp_147_7),
//                height = 46.dp
//            ),
//            contentAlignment = Alignment.Center
//        ) {
//            Text(
//                text = data.day,
//                textAlign = TextAlign.Center,
//                color = fontColor,
//                fontSize = dimensionResource(id = R.dimen.agent_weather_week_item_day_text_size).value.sp,
//            )
//        }
//
//        Spacer(modifier = Modifier.width(10.dp))
//
//        Box(
//            modifier = Modifier.size(
//                width = dimensionResource(R.dimen.dp_66_7),
//                height = 50.dp
//            ),
//            contentAlignment = Alignment.Center
//        ) {
//            Icon(
//                painter = iconResource,
//                contentDescription = null,
//                tint = Color.Unspecified
//            )
//        }
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .wrapContentHeight(),
//            contentAlignment = Alignment.Center
//        ) {
//            ConstraintLayout(
//                modifier = Modifier
//                    .wrapContentWidth()
//                    .height(50.dp)
//                    .padding(start = 5.3.dp, top = 7.3.dp, bottom = 7.3.dp)
//            ) {
//                val (highTempText, tempDelimeter, lowTempText) = createRefs()
//                Box(
//                    modifier = Modifier.constrainAs(highTempText) {
//                        start.linkTo(parent.start)
//                        end.linkTo(tempDelimeter.start)
//                    },
//                    contentAlignment = Alignment.Center
//                ) {
//                    Text(
//                        text = highTemp,
//                        color = Color.White,
//                        fontSize = dimensionResource(id = R.dimen.chrome_common_fg_text_size).value.sp,
//                    )
//                }
//                Box(
//                    modifier = Modifier
//                        .constrainAs(tempDelimeter) {
//                            top.linkTo(parent.top)
//                            start.linkTo(parent.start)
//                            end.linkTo(parent.end)
//                            bottom.linkTo(parent.bottom)
//                        }
//                        .padding(
//                            start = dimensionResource(id = R.dimen.agent_weather_dot_s_size),
//                            end = dimensionResource(id = R.dimen.agent_weather_dot_s_size)
//                        )
//                        .width(dimensionResource(id = R.dimen.dp_20))
//                        .height(dimensionResource(id = R.dimen.dp_20)),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Image(
//                        painter = painterResource(id = R.drawable.ic_slash_min_max_temp),
//                        contentDescription = null,
//                    )
//                }
//                Box(
//                    modifier = Modifier.constrainAs(lowTempText) {
//                        start.linkTo(tempDelimeter.end)
//                        end.linkTo(parent.end)
//                    },
//                    contentAlignment = Alignment.Center
//                ) {
//                    Text(
//                        text = lowTemp,
//                        color = Color.White,
//                        fontSize = dimensionResource(id = R.dimen.chrome_common_fg_text_size).value.sp,
//                    )
//                }
//            }
//        }
//
//
//    }
//}
//
//@Preview
//@Composable
//fun WeatherRowPreview() {
//    val data =
//        WeatherItem("2023-07-07", "MONDAY", DayOfWeek.MONDAY, WeatherIconType.SUN, "20", "30", "")
//    data.currTemp = "30"
//    WeatherRow(data = data, "1")
//}
