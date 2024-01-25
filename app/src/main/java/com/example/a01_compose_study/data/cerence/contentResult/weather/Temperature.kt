package com.ftd.ivi.cerence.data.model.server.cerence.contentResult.weather

data class Temperature(
    val type: Int,
    val unit: String,
    val value: String,
    val high: High,
    val low: Low
)