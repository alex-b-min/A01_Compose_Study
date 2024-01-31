package com.example.a01_compose_study.domain.model

enum class ParseDomainType(val value: String) {
    CALL("Call"),
    NAVI("Navigation"),
    WEATHER("Weather"),
    RADIO("Radio"),
    SEND_MESSAGE("SendMessage"),
    HELP("Help"),
    COMMON("Common"),
    LAUNCHAPP("LaunchApp"),
    UNSUPPORTED_DOMAIN("UnsupportedDomain"),
    MAX("MAX");

    companion object {
        fun fromString(value: String): ParseDomainType? {
            return values().find { it.value.equals(value, ignoreCase = true) }
        }
    }
}