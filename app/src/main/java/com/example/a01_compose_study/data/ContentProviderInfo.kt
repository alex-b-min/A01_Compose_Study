package com.example.a01_compose_study.data

data class ContentProviderInfo(
    val response_format: String,
    val service: String,
    val weather_focus: WeatherFocus
)