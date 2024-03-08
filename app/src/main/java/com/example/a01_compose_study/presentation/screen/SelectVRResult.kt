package com.example.a01_compose_study.presentation.screen

sealed class SelectVRResult {
    /**
     * 이 클래스는 음성인식을 한 결과(VRResult)를 직접 주입하기 위한 용도로 음성인식에 대한 상황을 연출 하기 위한 목적의 selaled class 임
     * 살제 개발 코드랑 상관없음
     */
    object HelpResult : SelectVRResult()
    object PttResult : SelectVRResult()
    object CallListResult : SelectVRResult()
    object CallIndexListResult : SelectVRResult()
    object CallRecognizedContact : SelectVRResult()
    object CallOtherNameResult : SelectVRResult()
    object CallYesResult : SelectVRResult()
    object CallNoResult : SelectVRResult()
    data class ScrollIndexResult(val inputIndex: Int) : SelectVRResult()
}