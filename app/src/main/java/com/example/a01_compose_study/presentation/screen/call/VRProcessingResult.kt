package com.example.a01_compose_study.presentation.screen.call

/**
 * VR에 대한 결과를 받아서 이벤트를 발생시키는 용도의 클래스
 */
sealed class VRProcessingResult {

    object Yes : VRProcessingResult()
    object No : VRProcessingResult()
    object None : VRProcessingResult()
    object OtherNumber : VRProcessingResult()
}