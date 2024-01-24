package com.example.a01_compose_study.domain.model

enum class RequestMode(val str: String) {
    NONE("NONE"),
    SYNC("SYNC"), // 동기로 결과를 받아야할때
    VOID("VOID"), // 결과를 받을필요없이 단순하게 send할경우에만 사용
    MAX("MAX")
}

enum class RequestType(val str: String) {
    NONE("NONE"),
    NAVI_PRESET("NAVI_PRESET"),
    NAVI_LIST("NAVI_LIST"),
    RADIO("RADIO"),
    CALL("CALL"),
    MAX("MAX")
}

enum class RequestorType(val str: String) {
    INTENT("INTENT"),
    EXTERNAL("EXTERNAL"),
    SERVICE("SERVICE")
}