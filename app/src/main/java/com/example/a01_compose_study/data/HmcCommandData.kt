package com.example.a01_compose_study.data

data class HmcCommandData(
    val domain: String,
    val intention: String,
    val paramK: ParamK,
    val utterence: String
)