package com.example.a01_compose_study.data.pasing

import com.example.a01_compose_study.domain.util.CustomLogger
import com.google.gson.Gson

data class RadioModel(var intent: String) : BaseModel(intent) {

    var band: String = ""
    var enableIndex = true

    override fun equals(other: Any?): Boolean {
        var otherModel = other as? WeatherModel
        var itemsEquals = true
        otherModel?.let {
            itemsEquals = (it.items == items)
        }
        return super.equals(other) && itemsEquals
    }

    fun getRadioItems(): MutableList<RadioItem> {
        return items as MutableList<RadioItem>
    }
}


data class RadioItem(
    var id: String = "",
    var band: String = "",
    var channelName: String = "",
    var landhList: ArrayList<String> = arrayListOf(),
    var frequencyInKhz: List<String> = listOf()
) {
    override fun toString(): String {
        return "${super.toString()},id : [$id] ,band : [$band], channelName : [$channelName] frequencyInKhz : $frequencyInKhz landhList : $landhList"
    }
}

data class RadioFeatureBundle(
    val radioItemList: List<RadioItem> = listOf()
) {
    override fun toString(): String {
        // list.toString()으로 하면 RadioItem의 toString()이 호출됨
        return Gson().toJson(radioItemList)
    }

    fun toJsonString(): String {
        val result = StringBuilder()
        result.append("{\"mode\":\"dab\",\"data\":[")
        result.append("")

        radioItemList.forEachIndexed { rIdx, item ->
            if (rIdx > 0) {
                result.append(",")
            }
            result.append("{\"id\":${item.id},")
            result.append("\"${item.channelName}\":[")
            item.landhList.forEachIndexed { lIdx, value ->
                if (lIdx > 0) {
                    result.append(",")
                }
                // backslash가 있는 경우 g2p쪽에서 syntax error가 발생해서 추가함
                if (value.contains("\\")) {
                    result.append("\"${value.replace("\\", "\\\\")}\"")
                } else {
                    result.append("\"$value\"")
                }
            }
            result.append("]}")
        }
        result.append("]}")

        CustomLogger.i("toJsonString(): $result")
        return result.toString()
    }
}
