package com.ftd.ivi.cerence.data.model.server.cerence.contentResult.weather

data class CurrentConditions(
    val cloudCover: Int,
    val dateTime: String,
    val dayTime: Boolean,
    val feelsLike: FeelsLike,
    val humidity: Int,
    val humidityCode: String,
    val icon: Icon,
    val pressure: Pressure,
    val shortText: String,
    val temperature: Temperature,
    val tts: String,
    val uv: Uv,
    val wind: Wind
)