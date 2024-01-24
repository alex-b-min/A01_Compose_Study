package com.ftd.ivi.cerence.data.model.server.cerence.contentResult.weather

data class ContentProviderInfo(
    val response_format: String,
    val service: String,
    val weather_focus: WeatherFocus
)