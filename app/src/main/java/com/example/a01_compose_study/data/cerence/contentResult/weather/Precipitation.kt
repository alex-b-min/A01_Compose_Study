package com.ftd.ivi.cerence.data.model.server.cerence.contentResult.weather

data class Precipitation(
    val amount: Amount,
    val ice: Int,
    val probability: Int,
    val rain: Int,
    val rainAmount: RainAmount,
    val snow: Int,
    val snowAmount: SnowAmount
)