package com.example.a01_compose_study.data.repository.vr

import com.example.a01_compose_study.domain.model.ScreenType
import com.example.a01_compose_study.domain.model.SealedDomainType
import com.example.a01_compose_study.domain.model.HelpItemData
import com.example.a01_compose_study.domain.model.HelpVRData
import com.example.a01_compose_study.domain.model.VRResult
import com.example.a01_compose_study.domain.repository.vr.VRRepository
import com.example.a01_compose_study.domain.util.ScreenSizeType
import javax.inject.Inject

class VRRepositoryImpl @Inject constructor() : VRRepository {
    override fun onVRResult(): VRResult {
        /**
         * VR에 대한 결과를 받아오고 파싱을 하여 각 도메인 화면에 적합한 데이터를 UI에 뿌려주어야 한다.
         * 현재는 VR에 대한 결과를 예상할 수 없어 List<HelpVRData> 타입의 DummyVRHelpData 사용해
         * Help에 관한 데이터를 특정지어 결과를 반환한다.
         */
        val helpVRDataList = createDummyVRHelpData()
        return if (helpVRDataList is List<*> && helpVRDataList.isNotEmpty()) {
            VRResult.Success(
                data = parseVRDataToItemData(helpVRDataList as List<HelpVRData>),
                domainType = SealedDomainType.Help,
                screenType = ScreenType.HelpList,
                screenSizeType = ScreenSizeType.Large
            )
        } else if (helpVRDataList is List<*> && helpVRDataList.isEmpty()) {
            VRResult.NoData
        } else {
            VRResult.Error(
                errorMessage = "errorMessage"
            )
        }
    }

    private fun createDummyVRHelpData(): Any {
        return listOf(
            HelpVRData(
                "Navigation",
                "Find Filling station in London",
                mutableListOf("command1_1", "command1_2", "command1_3")
            ),
            HelpVRData(
                "Call",
                "Call John Smith",
                mutableListOf("command2_1", "command2_2", "command2_3")
            ),
            HelpVRData(
                "Weather",
                "How is the weather",
                mutableListOf("command3_1", "command3_2", "command3_3")
            ),
            HelpVRData(
                "Radio",
                "DAB/FM List",
                mutableListOf("command3_1", "command3_2", "command3_3")
            )
        )
    }

    private fun parseVRDataToItemData(vrDataList: List<HelpVRData>): List<HelpItemData> {
        return vrDataList.map { vrData ->
            HelpItemData(
                domainId = mapDomainId(vrData.domainId),
                command = vrData.command,
                commandsDetail = vrData.commandsDetail
            )
        }
    }

    private fun mapDomainId(vrDomainId: String): SealedDomainType {
        // 여기에서 적절한 로직을 추가하여 HelpVRData의 domainId를 HelpItemData의 domainId로 매핑
        // 예를 들어, "Navigation"이면 SealedDomainType.Navigation을 반환하는 식으로
        val domainType = when (vrDomainId) {
            "Navigation" -> {
                SealedDomainType.Navigation
            }

            "Call" -> {
                SealedDomainType.Call
            }

            "Weather" -> {
                SealedDomainType.Weather
            }

            "Radio" -> {
                SealedDomainType.Radio
            }

            "SendMessage" -> {
                SealedDomainType.SendMessage
            }

            "MainMenu" -> {
                SealedDomainType.MainMenu
            }

            "Announce" -> {
                SealedDomainType.Announce
            }

            "Help" -> {
                SealedDomainType.Help
            }

            else -> {
                SealedDomainType.None
            }
        }
        return domainType
    }
}