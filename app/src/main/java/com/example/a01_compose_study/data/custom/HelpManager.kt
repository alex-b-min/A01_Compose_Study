package com.example.a01_compose_study.data.custom

import com.example.a01_compose_study.data.HVRError
import com.example.a01_compose_study.data.analyze.ParseBundle
import com.example.a01_compose_study.domain.model.HelpItemData
import com.example.a01_compose_study.domain.model.HelpVRData
import com.example.a01_compose_study.domain.model.ScreenType
import com.example.a01_compose_study.domain.model.SealedDomainType
import com.example.a01_compose_study.domain.util.ScreenSizeType
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HelpManager @Inject constructor() : VRResultListener{

    fun parsedData(bundle: ParseBundle<out Any>): ProcHelpData {
        /**
         * ParseBundle<out Any>? 타입의 bundle을 HelpData로 파싱하는 로직이 담겨야함
         */
        return procHelpIntention(bundle)
    }

    private fun procHelpIntention(bundle: ParseBundle<out Any>?): ProcHelpData {
        /**
         * 실제 ParseBundle<out Any>?의 bundle을 통해 ProcHelpData 객체를 생성하는 로직이 담겨야함
         */
        val helpVRDataList = createDummyVRHelpData()

        return parseVRDataToItemData(helpVRDataList as List<HelpVRData>)
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

    private fun parseVRDataToItemData(vrDataList: List<HelpVRData>): ProcHelpData {
        return ProcHelpData(
            domainType = SealedDomainType.Help,
            screenType = ScreenType.HelpList,
            screenSizeType = ScreenSizeType.Middle,
            data = vrDataList.map { vrData ->
                HelpItemData(
                    domainId = mapDomainId(vrData.domainId),
                    command = vrData.command,
                    commandsDetail = vrData.commandsDetail
                )

            }
        )
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

    override fun onReceiveBundle(bundle: ParseBundle<out Any>) {
        TODO("Not yet implemented")
    }

    override fun onBundleParsingErr() {
        TODO("Not yet implemented")
    }

    override fun onCancel() {
        TODO("Not yet implemented")
    }

    override fun onVRError(error: HVRError) {
        TODO("Not yet implemented")
    }
}