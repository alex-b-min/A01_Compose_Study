package com.example.a01_compose_study.data

data class LosPoiResult(
    val address: String,
    val address2: String,
    val category: String,
    val classCode: String,
    val coord: Coord,
    val distance: Distance,
    val hlosCoord: HlosCoord,
    val name: String,
    val phone: String,
    val placeSubType: Int
)