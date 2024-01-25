//package com.ftd.ivi.cerence.ui.compose
//
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.fillMaxHeight
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.layout.wrapContentWidth
//import androidx.compose.material.Text
//import androidx.compose.material3.Icon
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.res.colorResource
//import androidx.compose.ui.res.dimensionResource
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.constraintlayout.compose.ChainStyle
//import androidx.constraintlayout.compose.ConstraintLayout
//import coil.compose.rememberAsyncImagePainter
//import coil.request.ImageRequest
//import com.ftd.ivi.cerence.R
//import com.ftd.ivi.cerence.data.model.ui.WeatherIconType
//import com.ftd.ivi.cerence.data.model.ui.WeatherItem
//import com.ftd.ivi.cerence.ui.theme.fonts
//import java.time.DayOfWeek
//
//
//@Composable
//fun WeatherSingleView(
//    data: WeatherItem,
//    temperatureUnit: String
//) {
//    val iconResource = painterResource(
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
//
//    val bgResourse = when (data.iconType) {
//        WeatherIconType.SUN,
//        WeatherIconType.MOSTLYSUNNY -> {
//            R.drawable.bg_weather_sun_day
//        }
//
//        WeatherIconType.CLOUD,
//        WeatherIconType.FOG,
//        WeatherIconType.MOSTLYCLOUD -> {
//            R.drawable.bg_weather_cloud_day
//        }
//
//        WeatherIconType.RAINSNOW,
//        WeatherIconType.RAIN,
//        WeatherIconType.SHOWERS,
//        WeatherIconType.RAINSUNNY,
//        WeatherIconType.SNOW,
//        WeatherIconType.SNOWSUNNY -> {
//            R.drawable.bg_weather_rain_day
//        }
//
//        WeatherIconType.THUNDER -> {
//            R.drawable.bg_weather_thunder_day
//        }
//
//        else -> {
//            R.drawable.bg_weather_sun_day
//        }
//    }
//
//    var isCurr = false
//    var currTemp = "0°"
//    var highTemp = "0°"
//    var lowTemp = "0°"
//    var humidity = "0%"
//    data.currTemp?.let {
//        currTemp = if (temperatureUnit == "0")
//            "$it°C"
//        else
//            "$it°F"
//        isCurr = true
//    }
//    highTemp = if (temperatureUnit == "0")
//        "${data.highTemp}°C"
//    else
//        "${data.highTemp}°F"
//    lowTemp = if (temperatureUnit == "0")
//        "${data.lowTemp}°C"
//    else
//        "${data.lowTemp}°F"
//    humidity = "${data.humidity}%"
//
//    Image(
//        painter = rememberAsyncImagePainter(
//            ImageRequest.Builder(LocalContext.current)
//                .data(bgResourse)
//                .build()
//        ),
//        contentScale = ContentScale.FillBounds,
//        contentDescription = "content description",
//        modifier = Modifier
//            .background(Color.Transparent)
//    )
//
//    ConstraintLayout(
//        modifier = Modifier
//            .fillMaxWidth()
//            .fillMaxHeight()
//    ) {
//        val (addressText, weatherIcon, TempText) = createRefs()
//        Text(
//            text = data.address,
//            color = colorResource(id = R.color.white_a80),
//            fontSize = dimensionResource(id = R.dimen.agent_weather_carwash_title_size).value.sp,
//            modifier = Modifier
//                .constrainAs(addressText) {
//                    top.linkTo(parent.top)
//                    start.linkTo(parent.start)
//                }
//                .padding(start = 18.7.dp, top = 9.3.dp)
//        )
//
//        Icon(
//            painter = iconResource,
//            contentDescription = null,
//            tint = Color.Unspecified,
//            modifier = Modifier
//                .constrainAs(weatherIcon) {
//                    top.linkTo(parent.top)
//                    bottom.linkTo(parent.bottom)
//                    start.linkTo(parent.start)
//                    end.linkTo(TempText.start)
//                }
//                .padding(top = 15.dp)
//                .size(width = 114.7.dp, height = 64.dp)
//        )
//
//        if (isCurr) {
//            Box(
//                modifier = Modifier
//                    .constrainAs(TempText) {
//                        top.linkTo(parent.top)
//                        bottom.linkTo(parent.bottom)
//                        start.linkTo(weatherIcon.end)
//                        end.linkTo(parent.end)
//                    }
//                    .padding(
//                        start = dimensionResource(id = R.dimen.agent_weather_temp_margin_start),
//                        top = 15.dp
//                    )
//            ) {
//                Column(horizontalAlignment = Alignment.Start) {
//                    Text(
//                        text = currTemp,
//                        fontFamily = fonts,
//                        fontWeight = FontWeight.Normal,
//                        color = colorResource(id = R.color.white),
//                        fontSize = 30.sp,
//                        modifier = Modifier.wrapContentWidth()
//                    )
//                    Row(verticalAlignment = Alignment.CenterVertically) {
//                        Box(
//                            modifier = Modifier.size(
//                                width = dimensionResource(R.dimen.dp_10_7),
//                                height = 15.3.dp
//                            ),
//                            contentAlignment = Alignment.Center
//                        ) {
//                            Icon(
//                                painter = painterResource(R.drawable.ic_rain_amount),
//                                contentDescription = null,
//                                tint = Color.Unspecified
//                            )
//                        }
//                        Text(
//                            text = humidity,
//                            fontFamily = fonts,
//                            fontWeight = FontWeight.Normal,
//                            color = colorResource(id = R.color.white),
//                            fontSize = dimensionResource(id = R.dimen.agent_weather_carwash_title_size).value.sp,
//                            modifier = Modifier
//                                .wrapContentWidth()
//                                .padding(start = 6.6.dp)
//                        )
//                    }
//                }
//            }
//        } else {
//            Box(
//                modifier = Modifier
//                    .constrainAs(TempText) {
//                        top.linkTo(parent.top)
//                        bottom.linkTo(parent.bottom)
//                        start.linkTo(weatherIcon.end)
//                        end.linkTo(parent.end)
//                    }
//                    .padding(
//                        start = dimensionResource(id = R.dimen.agent_weather_temp_margin_start),
//                        top = 15.dp
//                    )
//            ) {
//                Column(horizontalAlignment = Alignment.Start) {
//                    Row(
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//
//                        Text(
//                            text = highTemp,
//                            fontFamily = fonts,
//                            fontWeight = FontWeight.Normal,
//                            color = colorResource(id = R.color.white),
//                            fontSize = 30.sp,
//                            modifier = Modifier.wrapContentWidth()
//                        )
//                        Image(
//                            painter = painterResource(id = R.drawable.ic_slash_min_max_temp),
//                            contentDescription = null,
//                            modifier = Modifier
//                                .padding(
//                                    start = dimensionResource(id = R.dimen.agent_weather_dot_m_size),
//                                    end = dimensionResource(id = R.dimen.agent_weather_dot_m_size)
//                                )
//                        )
//                        Text(
//                            text = lowTemp,
//                            fontFamily = fonts,
//                            fontWeight = FontWeight.Normal,
//                            color = colorResource(id = R.color.white),
//                            fontSize = 30.sp,
//                            modifier = Modifier.wrapContentWidth()
//                        )
//                    }
//                }
//            }
//        }
//
//        createHorizontalChain(weatherIcon, TempText, chainStyle = ChainStyle.Packed)
//    }
//}
//
//@Preview
//@Composable
//fun WeatherSingleViewCurrentPreview() {
//    val data =
//        WeatherItem("2023-07-07", "MONDAY", DayOfWeek.MONDAY, WeatherIconType.SUN, "20", "30", "")
//    data.currTemp = "30"
//    data.humidity = 14
//    WeatherSingleView(data = data, "0")
//}
//
//@Preview
//@Composable
//fun WeatherSingleViewTodayPreview() {
//    val data =
//        WeatherItem(
//            "2023-07-07",
//            "WEDNESDAY",
//            DayOfWeek.MONDAY,
//            WeatherIconType.RAINSUNNY,
//            "12",
//            "25",
//            ""
//        )
//    WeatherSingleView(data = data, "0")
//}
