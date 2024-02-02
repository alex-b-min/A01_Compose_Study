package com.example.a01_compose_study.data.cerence

import com.ftd.ivi.cerence.data.model.server.cerence.contentResult.navigation.ParkingInfoResult
import com.ftd.ivi.cerence.data.model.server.cerence.contentResult.navigation.ServerSearchResult
import com.ftd.ivi.cerence.data.model.server.cerence.contentResult.weather.ContentProviderInfo
import com.ftd.ivi.cerence.data.model.server.cerence.contentResult.weather.CurrentConditions
import com.ftd.ivi.cerence.data.model.server.cerence.contentResult.weather.DailyForecast
import com.ftd.ivi.cerence.data.model.server.cerence.contentResult.weather.Location

data class ContentResult(
    // 공통 데이터
    val requestID: String,
    val rtnCode: Int,
    val src: String,

    // 날씨 데이터
    val content_provider_info: ContentProviderInfo,
    val currentConditions: CurrentConditions?,
    // 주간 날씨 데이터
    val dailyForecasts: List<DailyForecast>?,
    val location: Location?,

    // 주차장 관련 결과 (Navigation)
    val parkingInfoResultList: List<ParkingInfoResult>?,

    // 위치 결과 (Navigation)
    val serverSearchResult: List<ServerSearchResult>?

)