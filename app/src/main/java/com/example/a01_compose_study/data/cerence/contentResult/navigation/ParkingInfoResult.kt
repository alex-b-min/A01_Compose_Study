package com.ftd.ivi.cerence.data.model.server.cerence.contentResult.navigation

data class ParkingInfoResult(
    val company: String,
    val coord: Coord,
    val countryCode: String,
    val cprices: List<Cprice>,
    val ctype: String,
    val feature: String,
    val grestr: String,
    val height: String,
    val name: String,
    val openingHours: List<OpeningHour>,
    val parkingAddr: String,
    val parkingID: String,
    val parkingLocation: ParkingLocation,
    val parkingType: String,
    val phone: String?
)