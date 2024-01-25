package com.ftd.ivi.cerence.data.model.server.cerence.contentResult.weather

data class Location(
    val city: String,
    val country: String,
    val geoPosition: GeoPosition,
    val state: String
)