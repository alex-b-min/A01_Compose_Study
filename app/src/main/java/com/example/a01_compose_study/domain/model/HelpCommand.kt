package com.example.a01_compose_study.domain.model

import javax.inject.Inject

class HelpCommand @Inject constructor(){
    fun makeHelpCommand(
        isSupportServer: Boolean,
        isOnlineVR: Boolean
    ): MutableList<HelpItemData> {
        // 플랫폼/지역/언어에 따라 표시
        // 서버 VR 전용 명령어 확인
        // 차량/옵션에 따라 지원하는 명령어 표시 확인
        // 이후 도메인별로 Command 정리
        var domainList: MutableList<String> = mutableListOf()

        // Navigation (Always, Optional) - 서버 VR 전용, 차량 확인 필요
        domainList.add("TID_CMN_GUID_42_01")

        // Contact (Always)
        domainList.add("TID_CMN_GUID_12_02")

        // Weather (Optional) - 서버 VR 전용
        if (isSupportServer && isOnlineVR) {
            domainList.add("TID_CMN_GUID_32_05")
        }

        // Radio (EU, RU), DAB 지원 확인 필요
        domainList.add("TID_CMN_GUID_42_02")

        return getDomainCommand(domainList, isSupportServer, isOnlineVR)
    }

    fun getDomainCommand(
        domainList: MutableList<String>,
        isSupportServer: Boolean,
        isOnlineVR: Boolean,
    ): MutableList<HelpItemData> {
        var domainIdList: MutableList<HelpItemData> = mutableListOf()
        for (domain in domainList) {
            var domainList: MutableList<String> = mutableListOf()
            when (domain) {
                // Navigation
                "TID_CMN_GUID_42_01" -> {
                    domainList.add("TID_CMN_GUID_13_05_3")
                    domainList.add("TID_CMN_GUID_13_05_4")
                    domainList.add("TID_CMN_GUID_43_06")
                    domainList.add("TID_CMN_GUID_43_03")
                    domainList.add("TID_CMN_GUID_43_09")

//                    domainIdList?.add(HelpItemData(domain, domainList))
                }
                // Contact
                "TID_CMN_GUID_12_02" -> {
                    domainList.add("TID_CMN_GUID_13_12")
                    domainList.add("TID_CMN_GUID_13_13")
                    if (isSupportServer && isOnlineVR) {
                        domainList.add("TID_CMN_GUID_13_13_1")
                    }
//                    domainIdList?.add(HelpItemData(domain, domainList))
                }
                // Weather
                "TID_CMN_GUID_32_05" -> {
                    // 모두 서버 전용
                    domainList.add("TID_CMN_GUID_54_01")
                    domainList.add("TID_CMN_GUID_54_02")
                    domainList.add("TID_CMN_GUID_54_03")
                    domainList.add("TID_CMN_GUID_54_04")
//                    domainIdList?.add(HelpItemData(domain, domainList))
                }
                // Radio (EU, RU)
                "TID_CMN_GUID_42_02" -> {
                    // EU, RU (DAB 지원하는 경우)
                    domainList.add("TID_CMN_GUID_43_22_10")
                    // EU, RU (No DAB 경우)
                    domainList.add("TID_CMN_GUID_43_22_11")
                    // EU, RU
                    domainList.add("TID_CMN_GUID_43_22_12")
                    // EU, RU
                    domainList.add("TID_CMN_GUID_43_23_5")
//                    domainIdList?.add(HelpItemData(domain, domainList))
                }
            }
        }
        return domainIdList
    }
}
