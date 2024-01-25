package com.ftd.ivi.cerence.data.model.server.cerence.contentResult.navigation

data class ServerSearchResult(
    val address: String,
    val alternativeNames: List<AlternativeName>,
    val category: String,
    val coord: Coord,
    val countryCode: String,
    val district: String,
    val guideCoord: GuideCoord,
    val house: String,
    val name: String,
    val phone: String,
    val state: String,
    val street: String,
    val website: String
)