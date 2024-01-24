package com.ftd.ivi.cerence.data.model.server.kakao.weather

data class Data(
    val background: Background,
    val button1: Button1,
    val meta: Meta,
    val multimedia: Multimedia,
    val quickReplies: List<QuickReply>,
    val source: Source,
    val subtitle: Subtitle,
    val title: Title,
    val ttl: Int,
    val utterance: String
)