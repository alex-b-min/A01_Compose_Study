package com.ftd.ivi.cerence.data.model.server.kakao.weather

data class Body(
    val `data`: Data,
    val token: String,
    val ttl: Int,
    val type: String
)