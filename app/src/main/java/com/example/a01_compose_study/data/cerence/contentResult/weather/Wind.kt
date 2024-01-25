package com.ftd.ivi.cerence.data.model.server.cerence.contentResult.weather

data class Wind(
    val direction: Int,
    val directionCode: String,
    val speed: Speed,
    val strengthCode: Int
)