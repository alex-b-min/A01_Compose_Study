package com.example.a01_compose_study.domain.util

enum class DialogueMode(val value: String) {
    NONE("NONE"),
    MAINMENU("MAINMENU"),
    MAINMENU_RADIO_GLOBAL("MAINMENU.RADIO.GLOBAL"),
    MAINMENU_SERVER("MAINMENU.SERVER"),
    YESNO("YES.NO"),
    LIST("LIST"),
    JARVIS_LIST("JARVIS.LIST"),
    CALL("CALL"),
    CALLNAME("CALL.NAME"),
    DIAL_NUMBER("DIAL.NUMBER"),
    DIAL_NUMBER_DEPTH("DIAL.NUMBER.DEPTH"),
    RADIO("RADIO"),
    NAVIGATION("NAVIGATION"),
    NAVI_DESTINATION_CONFIRM_ADDWAYPOINT("NAVI.DESTINATION.CONFIRM.ADDWAYPOINT"),
    NAVI_DESTINATION_CONFIRM_ADDWAYPOINT_DIAL("NAVI.DESTINATION.CONFIRM.ADDWAYPOINT.DIAL"),
    NAVI_DESTINATION_CONFIRM("NAVI.DESTINATION.CONFIRM"),
    NAVI_DESTINATION_CONFIRM_DIAL("NAVI.DESTINATION.CONFIRM.DIAL"),
    NAVI_DESTINATION_CONFIRM_EDITROUTE("NAVI.DESTINATION.CONFIRM.EDITROUTE"),
    NAVI_DESTINATION_CONFIRM_EDITROUTE_DIAL("NAVI.DESTINATION.CONFIRM.EDITROUTE.DIAL"),
    NAVI_FIND_POI("NAVI.FIND.POI"),
    NAVI_SERVER("NAVI.SERVER"),
    SEND_MESSAGE("SEND.MESSAGE"),
    SEND_MESSAGE_NAME("SEND.MESSAGE.NAME"),
    SEND_MESSAGE_NAME_CHANGE("SEND.MESSAGE.NAME.CHANGE"),
    SERVER_DICTATION("SERVER.DICTATION"),
    WEATHER("WEATHER"),
    HELP("HELP"),
    LAUNCHAPP("LAUNCHAPP"),
    ;
    fun isConfirmDialog(): Boolean {
        when (this) {
            CALLNAME,
            SEND_MESSAGE_NAME,
            SEND_MESSAGE_NAME_CHANGE,
            NAVI_DESTINATION_CONFIRM_ADDWAYPOINT,
            NAVI_DESTINATION_CONFIRM_ADDWAYPOINT_DIAL,
            NAVI_DESTINATION_CONFIRM,
            NAVI_DESTINATION_CONFIRM_DIAL,
            YESNO -> {
                return true
            }

            else -> {

            }
        }
        return false
    }
}