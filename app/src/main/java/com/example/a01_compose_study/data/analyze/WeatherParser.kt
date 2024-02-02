package com.example.a01_compose_study.data.analyze

import com.example.a01_compose_study.data.VRResult
import com.example.a01_compose_study.data.cerence.ContentResult
import com.example.a01_compose_study.data.pasing.ItemType
import com.example.a01_compose_study.data.pasing.WeatherIconType
import com.example.a01_compose_study.data.pasing.WeatherItem
import com.example.a01_compose_study.data.pasing.WeatherModel
import com.example.a01_compose_study.domain.util.CustomLogger
import com.ftd.ivi.cerence.data.model.server.cerence.contentResult.weather.CurrentConditions
import com.ftd.ivi.cerence.data.model.server.cerence.contentResult.weather.DailyForecast
import com.ftd.ivi.cerence.data.model.server.cerence.contentResult.weather.Icon
import com.ftd.ivi.cerence.data.model.server.kakao.weather.Instruction
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

enum class ServerType {
    NONE,
    KAKAO,
    CERENCE,
    MAX
}

class WeatherParser :
    BaseParser<WeatherModel>() {

    override fun analyze(result: VRResult): ParseBundle<WeatherModel> {
        CustomLogger.i("NavigationAnalyzer::analyze")
        var bundle: ParseBundle<WeatherModel> = ParseBundle(ParseDomainType.WEATHER)
        var model = WeatherModel(result.intention)
        val serverType = getServerType(result.intention)
        // TODO ServerType MAX 인경우 예외처리.
        model.items = resultToItems(serverType, result) as MutableList<Any>
        bundle.dialogue = "MAX" // TODO dialogue가 필요할까?/ 음..
        bundle.model = model
        bundle.prompt = parsePrompt(serverType, result)

        val printItems = model.items.joinToString("\n")
        CustomLogger.i("Finish Parser data - $printItems")
        return bundle
    }

    private fun getServerType(intent: String) = when (intent) {
        "KakaoCommand" -> ServerType.KAKAO
        "CerenceCommand" -> ServerType.CERENCE
        else -> ServerType.MAX
    }

    private fun parsePrompt(type: ServerType, result: VRResult) = when (type) {
        ServerType.KAKAO -> result.getKakaoTTSText()
            ?: run {
                CustomLogger.e("ServerType $type prompt is not exist")
                "No Prompt (Kakao)"
            }

        ServerType.CERENCE -> result.getCerenceTTSText() ?: run {
            CustomLogger.e("ServerType $type prompt is not exist")
            "No Prompt (Cerence)"
        }

        else -> "No Prompt"
    }

    private fun resultToItems(type: ServerType, result: VRResult): ArrayList<WeatherItem> {
        // TODO 데이터가 진짜 비어있을때 아래 앨비스 연산자가 호출되는지 검증하는 tc도 필요함.
        val items = when (type) {
            ServerType.KAKAO -> {
                result.getKakaoInstruction()?.let {
                    dummyDataParser(it)
                } ?: run {
                    CustomLogger.e("Kakao data empty")
                    ArrayList<WeatherItem>()
                }
            }

            ServerType.CERENCE -> {
                result.getCerenceContentResult()?.let {
                    cerenceParser(it)
                } ?: run {
                    CustomLogger.e("Cerence Data Empty")
                    ArrayList<WeatherItem>()
                }
            }

            else -> {
                CustomLogger.i("ServerType $type implement!!")
                ArrayList<WeatherItem>()
            }
        }
        return items
    }

    private fun dummyDataParser(instruction: Instruction): ArrayList<WeatherItem> {
        val items = ArrayList<WeatherItem>()
        items.add(
            WeatherItem(
                "2023-07-07T08: 00: 00+02: 00",
                "MONDAY",
                toDayOfWeekFromString("2023-07-07T08: 00: 00+02: 00"),
                WeatherIconType.CLOUD,
                "10",
                "20",
                instruction.body.data.multimedia.mediaUrl
            )
        )
        items.add(
            WeatherItem(
                "2023-07-08",
                "MONDAY",
                toDayOfWeekFromString("2023-07-08"),
                WeatherIconType.CLOUD,
                "12",
                "24",
                ""
            )
        )
        items.add(
            WeatherItem(
                "23-07-09",
                "MONDAY",
                toDayOfWeekFromString("23-07-09"),
                WeatherIconType.CLOUD,
                "14",
                "26",
                ""
            )
        )
        return items
    }


    private fun cerenceParser(contentResult: ContentResult): ArrayList<WeatherItem> {
        CustomLogger.i("CerenceParser")
        contentResult.content_provider_info.weather_focus.let {
            // !중요! 날씨 정보에 대해서는 세렌스 서버에서 Structure에 키값이 존재한다.
            CustomLogger.i("CerenceParser Weather data - $it")
        }
        val items = ArrayList<WeatherItem>()

        var address = ""
        contentResult.location?.city.let {
            if (it != null) {
                address = it
            }
        }

        contentResult.currentConditions?.let {
            cerenceParseCurrentCondition(it, items, address)
        }
        contentResult.dailyForecasts?.let {
            cerenceParseDailyForeCasts(it, items, address)
        }
        return items
    }

    private fun cerenceParseCurrentCondition(
        conditions: CurrentConditions,
        out: ArrayList<WeatherItem>,
        addr: String
    ): Boolean {
        CustomLogger.i("cerenceParseCurrentCondition")
//        현재 보면, currentConditions 값에 high, low가없음.
//        "temperature": {
//            "value": "21",
//            "type": 17,
//            "unit": "C"
//        },
        val date = conditions.dateTime
        val day = "MONDAY"//toDayOfWeekFromString(conditions.dateTime)
        val dayOfWeek = toDayOfWeekFromString(conditions.dateTime)
        val currentTemp = conditions.temperature.value
        val icon = toIconFromData(conditions.icon)
        val item = WeatherItem(date, day, dayOfWeek, icon, "0", "0", "")
        item.type = ItemType.SINGLE
        item.currTemp = currentTemp
        item.address = addr
        item.humidity = conditions.humidity
        out.add(item)
        return true
    }

    private fun cerenceParseDailyForeCasts(
        forecasts: List<DailyForecast>,
        out: ArrayList<WeatherItem>,
        addr: String
    ): Boolean {
        CustomLogger.i("cerenceParseDailyForeCasts")
        for (item in forecasts) {
            val date = item.date
            val day = item.dayOfWeek//toDayOfWeek(item.dayOfWeek)
            val dayOfWeek = toDayOfWeek(item.dayOfWeek)
            val high = item.temperature.high.value
            val low = item.temperature.low.value
            val icon = toIconFromData(item.day.icon)
            var weatherItem = WeatherItem(date, day, dayOfWeek, icon, low, high, "")
            weatherItem.address = addr
            out.add(weatherItem)
        }
        return true
    }

    private fun toDayOfWeek(str: String) = when (str) {
        "Monday" -> DayOfWeek.MONDAY
        "Tuesday" -> DayOfWeek.TUESDAY
        "Wednesday" -> DayOfWeek.WEDNESDAY
        "Thursday" -> DayOfWeek.THURSDAY
        "Friday" -> DayOfWeek.FRIDAY
        "Saturday" -> DayOfWeek.SATURDAY
        "Sunday" -> DayOfWeek.SUNDAY
        else -> {
            CustomLogger.e("to do implemented $str")
            DayOfWeek.MONDAY
        }
    }

    private fun toIconFromData(icon: Icon) = when (icon.id) {
        1 -> WeatherIconType.MOSTLYCLOUD
        2 -> WeatherIconType.SUN
        3, 5, 58, 59 -> WeatherIconType.MOSTLYSUNNY
        4, 6, 20, 26, 27 -> WeatherIconType.CLOUD
        7, 8, 28, 51 -> WeatherIconType.FOG
        9, 10, 12, 13 -> WeatherIconType.RAINSNOW
        14 -> {
            if (icon.phrase.contains("showers")) {
                WeatherIconType.SHOWERS
            } else {
                WeatherIconType.RAIN
            }
        }

        15 -> WeatherIconType.RAINSUNNY
        16, 17, 60 -> WeatherIconType.THUNDER
        18 -> WeatherIconType.SNOW
        19 -> WeatherIconType.SNOWSUNNY
        else -> {
            CustomLogger.e("TO DO description $icon")
            WeatherIconType.SUN
        }
    }

    private fun toDayOfWeekFromString(str: String): DayOfWeek {
        val formatters = listOf(
            DateTimeFormatter.ISO_DATE_TIME,
            DateTimeFormatter.ISO_DATE,
            DateTimeFormatter.ofPattern("yy-MM-dd"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd")
        )
        for (formatter in formatters) {
            try {
                val trimData = str.replace("\\s".toRegex(), "")
                CustomLogger.i("$str, $trimData $formatter")
                val data = LocalDate.parse(trimData, formatter)
                return data.dayOfWeek
            } catch (e: DateTimeParseException) {
//                CustomLogger.e(e.message?.let { CustomLogger.e(it) }.toString())
            }
        }
        // TODO 여기까지오면 에러이다보니 에러처리해야함.
        CustomLogger.e("Current Data parse error.")
        return DayOfWeek.MONDAY
    }

}