package com.example.a01_compose_study.data.pasing

import kotlinx.coroutines.flow.MutableStateFlow

enum class NaviFavoriteIconType {
    NONE,
    HOME,
    OFFICE,
    FAVORITE1,
    FAVORITE2,
    FAVORITE3
}

enum class NaviStationIconType {
    NONE,
    EVSTATION,
    STATION,
    GASSTATION
}

enum class NaviScenarioType {
    POI,
    SEARCHLIST,
    FAVORITE,
    PRESET1,
    PRESET2,
    PRESET3,
    HOME,
    OFFICE,
    CHANGEHOME,
    CHANGEOFFICE,
    WAYPOINT,
    PREVIOUS_DESTINATION,
    CANDLE_ROUTE,
    ROUTE_OVERVIEW,
    MAP,
    DESTINATION_INFORMATION,
    MAX
}

enum class NaviScreenType {
    SEARCHLIST,
    Favorite,
    Waypoint,
    PREVLIST,
    MAX
}

data class NavigationModel(var intent: String) : BaseModel(intent) {

    var selectedNaviItem: NaviItem? = null
    var scenarioType: NaviScenarioType = NaviScenarioType.MAX
    var naviScreenType: NaviScreenType = NaviScreenType.MAX
    val naviGuideString = MutableStateFlow("")
    var phrase = ""
    var selectedNaviData: NaviData? = null
    var isDestinationSearch: Boolean = false

    override fun toString(): String {
        val itemList = items.joinToString("\n")
        return "${super.toString()}, items:[$itemList]"
    }

    fun getNaviItems(): MutableList<NaviItem> {
        return items as MutableList<NaviItem>
    }

    fun getNaviData(): MutableList<NaviData> {
        return items as MutableList<NaviData>
    }
}

// TODO 필요한 아이템들 늘려나가야함.
data class NaviItem(
    val name: String,
    val address: String,
    val phone: String,
    val lat: Double,
    val lon: Double
) {
    var isFavorite: Boolean = false
    var isEnableParking: Boolean = false
    var distance: String = ""
    override fun toString(): String {
        return "${super.toString()} name[$name] address[$address] phone[$phone] distance[$distance] lon[$lon], lat[$lat] isFavorite[$isFavorite] isEnableParking[$isEnableParking]"
    }
}

data class NaviData(var id: Int) {
    var address: String = ""
    var poi_name: String = ""
    var distance: Double = 0.0
    var station: NaviStationIconType = NaviStationIconType.NONE
    var favorite: NaviFavoriteIconType = NaviFavoriteIconType.NONE
    var nIndex: Int = 0
    var option: String = ""

    override fun toString(): String {
        return "${super.toString()} nIndex [$nIndex] address [$address] poi_name [$poi_name] distance [$distance] station [$station] favorite [$favorite]"
    }
}