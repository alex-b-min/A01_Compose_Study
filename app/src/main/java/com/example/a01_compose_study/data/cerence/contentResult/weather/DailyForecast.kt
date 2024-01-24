package com.ftd.ivi.cerence.data.model.server.cerence.contentResult.weather

data class DailyForecast(
    val date: String,
    val day: Day,
    val dayOfWeek: String,
    val feelsLike: FeelsLike,
    val night: Night,
    val precipitation: Precipitation,
    val sunrise: String,
    val sunset: String,
    val temperature: Temperature,
    val uv: Uv
)