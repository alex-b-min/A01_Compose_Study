package com.ftd.ivi.cerence.analyze

import com.example.a01_compose_study.data.DialogueMode
import com.example.a01_compose_study.data.Intentions
import com.example.a01_compose_study.data.LosPoiResult
import com.example.a01_compose_study.data.VRResult
import com.example.a01_compose_study.data.analyze.BaseParser
import com.example.a01_compose_study.data.analyze.ParseBundle
import com.example.a01_compose_study.data.analyze.ParseDomainType
import com.example.a01_compose_study.data.pasing.NaviItem
import com.example.a01_compose_study.data.pasing.NaviScenarioType
import com.example.a01_compose_study.data.pasing.NavigationModel
import com.example.a01_compose_study.domain.util.CustomLogger
import com.example.a01_compose_study.data.cerence.ContentResult

class NavigationParser(val dialogueMode: DialogueMode?) :
    BaseParser<NavigationModel>() {

    override fun analyze(result: VRResult): ParseBundle<NavigationModel> {
        CustomLogger.i("NavigationAnalyzer::analyze")
        val bundle: ParseBundle<NavigationModel> = ParseBundle(ParseDomainType.NAVI)
        val model = NavigationModel(result.intention)
        model.items = resultToItems(result) as MutableList<Any>
        model.scenarioType = parseNaviScenariotype(result)
        model.phrase =
            result.result?.get(0)?.slots?.get(0)?.vrResult?.nlu?.slot?.searchPhrase?.literal.toString()
        model.isDestinationSearch =
            result.result?.get(0)?.slots?.get(0)?.vrResult?.nlu?.slot?.relative_location?.literal.equals(
                "destination"
            )
        bundle.dialogue = "NAVI"
        bundle.dialogueMode = DialogueMode.NAVIGATION
        bundle.model = model

        val printItems = model.items.joinToString("\n")
        CustomLogger.i("Finish Parser data - $printItems")
//        CustomLogger.i("Finish Parser data - $items") // 이 방법도 가능한데, line별로 보여주는게 좋아보임.
        return bundle
    }

    private fun resultToItems(result: VRResult): MutableList<NaviItem> {
        if (result.result == null || result.result!!.size == 0) {
            CustomLogger.e("result data empty")
            return ArrayList()
        }
        val vrResultData = result.result!![0]
        if (vrResultData.slot_count == 0) {
            CustomLogger.e("slot_count empty")
            return ArrayList()
        }


        val items = when (result.intention) {
            "KakaoCommand" -> {
                val kakaoNaviResult = result.getKakaoNaviResult()
                if (kakaoNaviResult != null) {
                    return kakaoParser(kakaoNaviResult)
                } else {
                    return ArrayList()
                }
            }

            "CerenceCommand" -> {

                val contentType = result.getCerenceContentType()
                val contentResult = result.getCerenceContentResult()
                if (contentType != null && contentResult != null) {
                    return cerenceParser(contentType, contentResult)
                } else {
                    return ArrayList()
                }
            }

            else -> ArrayList<NaviItem>()
        }
        return items
    }

    // TODO NaviItem을 미사용하고 TomTom 데이터를 쓸거라 안쓰는 값이긴 한데 일단 유지
    private fun kakaoParser(poiResultList: List<LosPoiResult>): ArrayList<NaviItem> {
        val items = ArrayList<NaviItem>()
        for (poi in poiResultList) {
            items.add(NaviItem(poi.name, poi.address, poi.phone, poi.coord.lat, poi.coord.lon))
        }
        return items
    }

    private fun cerenceParser(
        contentType: String,
        contentResult: ContentResult
    ): ArrayList<NaviItem> {
        CustomLogger.i("CerenceParser contentType[$contentType]")
        val items = ArrayList<NaviItem>()
        if (contentType == "Search") {
            cerenceParserSearch(contentResult, items)
        } else if (contentType == "Parking") {
            cerenceParserParking(contentResult, items)
        } else {
            // TODO 다른 값들이 나오면 추가 작업이 필요함.
        }
        return items
    }

    private fun cerenceParserSearch(result: ContentResult, out: ArrayList<NaviItem>): Boolean {
        if (result.serverSearchResult.isNullOrEmpty()) {
            CustomLogger.i("no search result")
            return false
        }
        for (item in result.serverSearchResult) {
            val name = item.name
            val phone = ""
            val address = item.address
            val lat = item.coord.lat
            val lon = item.coord.lon
            out.add(NaviItem(name, address, phone, lat, lon))
        }
        return true
    }

    private fun cerenceParserParking(result: ContentResult, out: ArrayList<NaviItem>): Boolean {
        if (result.parkingInfoResultList.isNullOrEmpty()) {
            CustomLogger.i("no search result")
            return false
        }
        for (item in result.parkingInfoResultList) {
            val name = item.name
            val phone = item.phone ?: ""
            val address = item.parkingAddr
            val lat = item.coord.lat
            val lon = item.coord.lon
            out.add(NaviItem(name, address, phone, lat, lon))
        }
        return true
    }

    private fun parseNaviScenariotype(result: VRResult): NaviScenarioType {
        var type = NaviScenarioType.MAX
        if (result.result == null || result.result!!.size == 0) {
            CustomLogger.e("result data empty")
            return type
        }

        if (dialogueMode == DialogueMode.MAINMENU) {
            type = when (result.intention) {
                "KakaoCommand", "CerenceCommand" -> NaviScenarioType.SEARCHLIST
                "DestinationSearch" -> NaviScenarioType.POI
                "Waypoint" -> NaviScenarioType.WAYPOINT
                "NaviPreset" -> {
//                    when (result.result!![0].slots?.get(0)?.items?.get(0)?.id) {
//                        1 -> NaviScenarioType.FAVORITE
//                        2 -> NaviScenarioType.PRESET2
//                        3 -> NaviScenarioType.PRESET3
//                        else -> NaviScenarioType.MAX
//                    }
                    NaviScenarioType.FAVORITE
                }

                "Home" -> NaviScenarioType.HOME
                "Office" -> NaviScenarioType.OFFICE
                "ChangeHome" -> NaviScenarioType.CHANGEHOME
                "ChangeOffice" -> NaviScenarioType.CHANGEOFFICE
                "PreviousDestination" -> NaviScenarioType.PREVIOUS_DESTINATION
                "StopGuidance", "CancelRoute" -> NaviScenarioType.CANDLE_ROUTE
                "ShowRoute" -> NaviScenarioType.ROUTE_OVERVIEW
                "ShowOnMap" -> NaviScenarioType.MAP
                "DestinationInfo" -> NaviScenarioType.DESTINATION_INFORMATION

                else -> NaviScenarioType.MAX
            }
        } else if (Intentions.Waypoint.isEqual(result.intention)) {
            type = NaviScenarioType.WAYPOINT
        }
        CustomLogger.d("NaviScenariotype: ${type.name}")
        return type
    }
}