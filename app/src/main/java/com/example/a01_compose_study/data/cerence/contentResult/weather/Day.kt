package com.ftd.ivi.cerence.data.model.server.cerence.contentResult.weather

data class Day(
    val cloudCover: Int,
    val icon: Icon,
    val precipitation: Precipitation,
    val pressure: Pressure,
    val tts: String,
    val wind: Wind
)