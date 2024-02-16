package com.example.a01_compose_study.domain.model

sealed class ScreenType {
    override fun toString(): String {
        return javaClass.simpleName
    }

    object None : ScreenType()
    object ContentScreen : ScreenType()
    object ServiceScreen : ScreenType()
    object PttListen : ScreenType()
    object PttSpeak : ScreenType()
    object PttLoading : ScreenType()
    object PttAnounce : ScreenType()
    object Notice : ScreenType()
    object List : ScreenType()
    object YesNo : ScreenType()
    object Navigation : ScreenType()
    object NavigationNotice : ScreenType()
    object LocalSearch : ScreenType()
    object Favorite : ScreenType()
    object FavoriteConfirm : ScreenType()
    object FavoriteSearchConfirm : ScreenType()
    object NaviWaypoint : ScreenType()
    object Radio : ScreenType()
    object RadioList : ScreenType()
    object Weather : ScreenType()
    object WeatherList : ScreenType()
    object MessageName : ScreenType()
    object MessageChange : ScreenType()
    object HelpList : ScreenType()
    object HelpDetailList : ScreenType()
    object SmallText : ScreenType()
    object MediumText : ScreenType()
    object LargeText : ScreenType()
}
