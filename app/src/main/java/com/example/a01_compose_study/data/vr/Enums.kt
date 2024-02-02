package com.example.a01_compose_study.data.vr

enum class WindowMode {
    NONE,
    SMALL,
    MEDIUM,
    LARGE
}

enum class ModuleStatus {
    UNLOAD,
    INITIALIZING,
    READY
}

enum class ProcState {
    NONE,
    WAIT,
    READY,
    FINISH,
    CANCEL,
}

enum class Tags(val value: String) {
    OtherNumber("OtherNumber"),
    PhoneType("PhoneType"),
    Home("Home"),
    Work("Work"),
    Office("Office"),
    Mobile("Mobile"),
    Phone("Phone"),
    Server("Server"),
    PARTIAL("PARTIAL"),
    FINAL("FINAL"),
    FULL_FIRST("FULL_FIRST"),
    Wlan0("wlan0"),
    Eth0("eth0")
    ;

    fun isEqual(string: String): Boolean {
        return this.value.equals(string, true)
    }
}

enum class Intentions(val value: String) {
    Yes("Yes"),
    No("No"),
    OtherNumber("OtherNumber"),
    NextPage("NextPage"),
    PreviousPage("PreviousPage"),
    Back("Back"),
    Exit("Exit"),
    MessageContent("MessageContent"),
    ChangeSMS("ChangeSMS"),
    WaitServerCreateSMS("WaitServerCreateSMS"),
    Waypoint("Waypoint")
    ;

    fun isEqual(string: String): Boolean {
        return this.value.equals(string, true)
    }
}

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

enum class PhonebookDownloadState(val value: Int) {
    ACTION_PULL_NOT_REQUEST(0),
    ACTION_PULL_START(1),
    ACTION_PULL_COMPLETE(2),
    ACTION_PULL_FAIL(3)
}

enum class RadioMode(val value: Int) {
    NONE(0),
    DAB_FM(1),
    FM(2),
    AM(3),
    DAB(4)
}

enum class RadioLanguageCode(val target: Int, val id: Int) {
    BUL(HLanguageType.BULGARIAN.id, 2),
    CES(HLanguageType.CZECH.id, 4),
    DAN(HLanguageType.DANISH.id, 5),
    NLD(HLanguageType.DUTCH.id, 6),
    ENG_GBR(HLanguageType.UK_ENGLISH.id, 7),
    ENG_USA(HLanguageType.US_ENGLISH.id, 8),
    FIN(HLanguageType.FINNISH.id, 9),
    FRA(HLanguageType.EU_FRENCH.id, 10),
    DEU(HLanguageType.GERMAN.id, 12),
    ITA(HLanguageType.ITALIAN.id, 16),
    NOR(HLanguageType.NORWEGIAN.id, 20),
    POL(HLanguageType.POLISH.id, 21),
    POR(HLanguageType.EU_PORTUGUESE.id, 23),
    RUS(HLanguageType.RUSSIAN.id, 24),
    SLK(HLanguageType.SLOVAK.id, 30),
    SPA(HLanguageType.EU_SPANISH.id, 31),
    SWE(HLanguageType.SWEDISH.id, 33),
    TUR(HLanguageType.TURKISH.id, 37),
    GRC(HLanguageType.GREEK.id, 41)
}

enum class HLanguageType(val value: String, val id: Int) {
    KOREAN("ko", 0),
    US_ENGLISH("en_US", 1),
    AM_SPANISH("es_AM", 2),
    CA_FRENCH("fr_CA", 3),
    BRA_PORTUGUESE("pt_BR", 4),
    CHINESE("zh_CN", 5),
    JAPANESE("ja", 6),
    INDONESIAN("id", 7),
    AU_ENGLISH("en_AU", 8),
    UK_ENGLISH("en_GB", 9),
    GERMAN("de", 10),
    EU_FRENCH("fr", 11),
    ITALIAN("it", 12),
    EU_SPANISH("es", 13),
    EU_PORTUGUESE("pt", 14),
    DUTCH("nl", 15),
    DANISH("da", 16),
    SWEDISH("sv", 17),
    NORWEGIAN("no", 18),
    NORWEGIAN_A01("nb", 18),
    POLISH("pl", 19),
    CZECH("cs", 20),
    SLOVAK("sk", 21),
    RUSSIAN("ru", 22),
    UKRAINIAN("uk", 23),
    SLOVENE("sl", 24),
    GREEK("el", 25),
    FINNISH("fi", 26),
    HUNGARIAN("hu", 27),
    TURKISH("tr", 28),
    RUMANIAN("ro", 29),
    BULGARIAN("bg", 30),
    CROATIAN("hr", 31),
    ARABIC("ar", 32),
    PERSIAN("fa", 33),
    HEBREW("he", 34),
    HINDI("hi", 35),
    BENGALI("bn", 36),
    MARATHI("mr", 37),
    TELUGU("te", 39),
    TAMIL("ta", 40),
    GUJARATI("gu", 41),
    KANNADA("kn", 42),
    ODIA("or", 43),
    MALAYALAM("ml", 44),
    PUNJABI("pa", 45),
    MALAY("ms", 46),
    TAIWAN("zh_TW", 47),
    MAX("MAX", 999)
}