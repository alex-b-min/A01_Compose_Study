package com.example.a01_compose_study.data.pasing

import java.time.DayOfWeek

enum class WeatherIconType {
    NONE,
    SUN,
    MOSTLYCLOUD,
    MOSTLYSUNNY,
    CLOUD,
    FOG,
    RAINSNOW,
    RAIN,
    SHOWERS,
    RAINSUNNY,
    THUNDER,
    SNOW,
    SNOWSUNNY,
    MAX
}

enum class ItemType {
    HIGH_LOW,
    SINGLE
}

data class WeatherModel(var intent: String) :
    BaseModel(intent) {

    override fun equals(other: Any?): Boolean {
        val otherModel = other as? WeatherModel
        var itemsEquals = true
        otherModel?.let {
            itemsEquals = (it.items == items)
        }
        return super.equals(other) && itemsEquals
    }

    fun getWeatherItems(): MutableList<WeatherItem> {
        return items as MutableList<WeatherItem>
    }
}

// TODO 필요한 아이템들 늘려나가야함.
data class WeatherItem(
    val date: String,
    val day: String,
    val dayOfWeek: DayOfWeek,
    val iconType: WeatherIconType,
    val lowTemp: String,
    val highTemp: String,
    val url: String
) {
    var type: ItemType = ItemType.HIGH_LOW
    var currTemp: String? = null
    var address: String = "Empty"
    var humidity: Int = 0
    override fun toString(): String {
        return "${super.toString()},date[$date], day[$day] dayOfWeek[$dayOfWeek] iconType[$iconType] lowTemp[$lowTemp] highTemp[$highTemp] url[$url] type[$type] currTemp[$currTemp] address[$address]"
    }
}