package com.example.a01_compose_study.data.vr

data class VRMWEventInfo(var event: VRMWEvent) {
    var language: HLanguageType = HLanguageType.MAX
    var languageAvailable: Int = -1
    var online: Int = -1

    override fun toString(): String {
        return "event[$event], language[$language], ${super.toString()}"
    }
}

enum class VRMWEvent {
    NONE,
    SYSTEM_LANGUAGE,
    LANGUAGE_SUPPORTED,
    TTSM_INITIALIZE,
    TTSM_INITIALIZED,
    TTSM_RELEASE,
    VRMW_INITIALIZE,
    VRMW_INITIALIZED,
    VRMW_RELEASE,
    VRMW_START,
    TTSM_START,
    SPEAK,
    RECOGNIZE,
    MAX
}

enum class VRMWState {
    NONE,
    UNLOAD,
    RESTARING,
    TTSM_INITIALIZING,
    VRMA_INITIALIZING,
    IDLE,
    PROCESSING,
    MAX
}