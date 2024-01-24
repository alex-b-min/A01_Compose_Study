package com.example.a01_compose_study.data


enum class ScreenType(val value: String) {
    None("None"),
    ContentScreen("ContentScreen"),
    ServiceScreen("ServiceScreen"),
    Ptt("Ptt"),
    Notice("Notice"),
    List("List"),
    YesNo("YesNo"),
    Navigation("Navigation"),
    NavigationNotice("NavigationNotice"),
    LocalSearch("LocalSearch"),
    Favorite("Favorite"),
    FavoriteConfirm("FavoriteConfirm"),
    FavoriteSearchConfirm("FavoriteSearchConfirm"),
    NaviWaypoint("NaviWaypoint"),
    Radio("Radio"),
    RadioList("RadioList"),
    Weather("Weather"),
    WeatherList("WeatherList"),
    MessageName("MessageName"),
    MessageChange("MessageChange"),
    HelpList("HelpList"),
    HelpDetailList("HelpDetailList"),
    SmallText("SmallText"),
    MediumText("MediumText"),
    LargeText("LargeText")
}

enum class DomainType(val value: String) {
    None("None"),
    Announce("Announce"),
    MainMenu("MainMenu"),
    Call("Call"),
    Navigation("Navigation"),
    Radio("Radio"),
    Weather("Weather"),
    SendMessage("SendMessage"),
    Help("Help")
}