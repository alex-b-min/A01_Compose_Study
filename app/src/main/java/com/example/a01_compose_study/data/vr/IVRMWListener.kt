package com.example.a01_compose_study.data.vr

interface IVRMWListener {
    fun onTTSStateChanged(state: Int, requestId: String)
    fun onVRResult(result: String)
    fun onVRStateChanged(state: Int, requestId: String)
    fun onVRPartialResultUpdated(result: String)
    fun onVoiceAmplitudeLevelChanged(level: Int, negativeSign: Boolean)
    fun onVRErrorEventRecieved(error: Int, requestId: String)
    fun onTTSErrorEventRecieved(error: Int, requestId: String)
    fun onVREventReceived(event: Int, requestId: String)
    fun onVRConfigEventReceived(event: Int)
    fun onTTSConfigEventReceived(event: Int, languageType: Int)
    fun onTTSEventReceived(event: Int, requestId: String)
    fun onSpeechDetected(event: Int)
    fun onVRLanguageSupportedReceived(
        available: Int,
        languageType: Int,
        online: Int,
        keywordSpotAvailable: Int
    )

    fun onVRSupportedLanguageReceived(supportedLangList: String)
    fun onTTSLanguageSupportedReceived(available: Int, languageType: Int)
    fun onTTSSupportedLanguageReceived(supportedLangList: String)
    fun onPromptStatusUpdated(status: Int)

    fun onCrashDetected()
}