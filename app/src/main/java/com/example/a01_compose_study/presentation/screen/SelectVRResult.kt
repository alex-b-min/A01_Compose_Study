package com.example.a01_compose_study.presentation.screen

sealed class SelectVRResult {
    /**
     * 이거는 VR Result의 특정한 값을 직접 주입하여 테스트를 하기 위한 목적의 selaled class 임
     * 살제 개발 코드랑 상관없음
     */
    object HelpResult : SelectVRResult()
    object PttResult : SelectVRResult()
    object CallIndexListResult : SelectVRResult()
    object SendMsgResult: SelectVRResult()
    object SendMsgNameResult: SelectVRResult()
    object SendMsgNameMsgResult: SelectVRResult()
    object NoResult: SelectVRResult()
    object MessageReult: SelectVRResult()
    object ScrollIndexResult : SelectVRResult()
}