package com.ftd.ivi.cerence.data.model.server.cerence.contentResult.weather

data class Night(
    val cloudCover: Int,
    val icon: Icon,
    val precipitation: Precipitation,
    val pressure: Pressure,
    val tts: String,
    val wind: Wind
)