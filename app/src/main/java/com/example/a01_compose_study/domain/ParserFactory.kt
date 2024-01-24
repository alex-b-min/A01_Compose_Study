package com.example.a01_compose_study.domain

import com.example.a01_compose_study.data.DialogueMode
import com.example.a01_compose_study.domain.util.CustomLogger

class ParserFactory {


    fun dataParsing(vrResult: VRResult, dialogueMode: DialogueMode? = null): Unit {
        TODO("데이터 파싱 로직 구")
//        //val type = getDomainType(vrResult.intention, vrResult.domain, dialogueMode)
//        val type = getDomainTypeByLoader(vrResult)
//        val analyzer = createParser(type, dialogueMode)
//        CustomLogger.e("dataParsing Intention:${vrResult.intention}, dataSize : ${vrResult.result?.size}")
//        // TODO 여기서 내가 어떤 analyzer인지 알필요없이, 데이터를 넣어줄순있을것같은데...
//        // 이걸 사용하는 곳에서 model as NavigationModel이런식으로 해야 꺼낼수있을것 같긴한데...
//        // Bundle에 타입을 같이 추가해주면 캐스팅이 가능하려나....
//        val bundle = analyzer?.analyze(vrResult)
//        if (Intentions.Back.isEqual(vrResult.intention)) {
//            bundle?.isBack = true
//        }
//        if (Intentions.Exit.isEqual(vrResult.intention)) {
//            bundle?.isExit = true
//        }
//        if (type == ParseDomainType.LAUNCHAPP) {
//            bundle?.type = ParseDomainType.LAUNCHAPP
//        }
//        if (type == ParseDomainType.UNSUPPORTED_DOMAIN) {
//            bundle?.type = ParseDomainType.UNSUPPORTED_DOMAIN
//        }
//        if (!vrResult.result.isNullOrEmpty()) {
//            vrResult.result?.first()?.phrase?.let {
//                bundle?.phrase = it
//            }
//        }
//        return bundle
    }

    private fun createParser(
        domainType: ParseDomainType,
        dialogueMode: DialogueMode?
    ): Unit {
        TODO("타입에 따라 적절한 파서 생성")
        CustomLogger.i("createAnalyzer")

//        return when (domainType) {
//            ParseDomainType.NAVI -> NavigationParser(dialogueMode)
//            ParseDomainType.CALL -> CallParser()
//            ParseDomainType.WEATHER -> WeatherParser()
//            ParseDomainType.RADIO -> RadioParser()
//            ParseDomainType.SEND_MESSAGE -> SendMsgParser(dialogueMode)
//            ParseDomainType.HELP -> HelpParser()
//            ParseDomainType.LAUNCHAPP -> LaunchAppParser()
//            ParseDomainType.COMMON -> {
//                dialogueMode?.let {
//                    CommonParser(dialogueMode)
//                }
//            }
//
//            else -> {
//                dialogueMode?.let {
//                    CommonParser(dialogueMode)
//                }
//            }
//        }
    }

//    private fun getDomainTypeByLoader(vrResult: VRResult): ParseDomainType {
//        return IntentionLoader.findDomain(vrResult)
//    }

    private fun getDomainType(
        intent: String,
        domain: String,
        dialogueMode: DialogueMode?
    ): ParseDomainType {
        // intent, domain 에 따라서 domain type을 꺼냄.
        // domain 은 실질적으로 서버 결과에서만 사용하는 데이터임.
        var type = ParseDomainType.MAX
        when (intent) {
            //
            "KakaoCommand" -> {
                when (domain) {
                    "P3Navi" -> type = ParseDomainType.NAVI
                    "kakaoi" -> {
                        // 카카오 커맨드는 weather뿐만아니라 다른 데이터도 kakaoi로 오긴하는데, 이걸 보려면,
                        // slots -> vrActionResult -> cpActionData -> cpMeta -> topic을 봐야함.
                        // 그런데 카카오로 할건 아니기에 틀만 맞춰놓음
                        type = ParseDomainType.WEATHER
                    }

                    else -> type = ParseDomainType.NAVI
                }
            }

            //
            "CerenceCommand" -> {
                when (domain) {
                    "Search" -> type = ParseDomainType.NAVI
                    "Parking" -> type = ParseDomainType.NAVI
                    "Weather" -> type = ParseDomainType.WEATHER
                    else -> type = ParseDomainType.UNSUPPORTED_DOMAIN
                }
            }

            //
            "Call" -> type = ParseDomainType.CALL
            //
            "Radio", "AM", "FM", "AMList", "FMList", "DABFM", "DABFMList", "RadioStation", "RadioStationGarbage" -> type =
                ParseDomainType.RADIO

            //
            // TODO A01에서는 미지원
//            "PreviousSearch",
//            "ResumeGuidance",
//            "FrequentlyVisit",
//            "AddressBook",
//            "RouteOption",
//            "ReRoute",
            "Waypoint",
            "NaviPreset",
            "Home",
            "Office",
            "ChangeHome",
            "ChangeOffice",
            "DestinationSearch",
            "PreviousDestination",
            "StopGuidance",
            "CancelRoute",
            "DestinationInfo",
            "ShowRoute",
            "ShowOnMap" -> type = ParseDomainType.NAVI

            //
            "CreateSMS", "WaitServerCreateSMS", "MessageContent", "ChangeSMS" -> type =
                ParseDomainType.SEND_MESSAGE

            //
            "Yes", "No" -> {
                when (dialogueMode) {
                    DialogueMode.SEND_MESSAGE_NAME, DialogueMode.SEND_MESSAGE_NAME_CHANGE -> type =
                        ParseDomainType.SEND_MESSAGE

                    else -> {
                        type = ParseDomainType.COMMON
                    }
                }
            }

            //
            "LineNumber",
            "OtherNumber",
            "List",
            "Exit" -> type = ParseDomainType.COMMON

            "Help" -> type = ParseDomainType.HELP

            else -> type = ParseDomainType.UNSUPPORTED_DOMAIN
        }
        CustomLogger.i("Intention[$intent] DialogMode [$dialogueMode] Domain[$domain] AnalyzeDomainType[$type]")
        return type
    }
}