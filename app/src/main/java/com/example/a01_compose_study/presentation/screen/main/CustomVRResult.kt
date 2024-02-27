package com.example.a01_compose_study.presentation.screen.main

sealed class CustomVRResult {
    /**
     * 이거는 VR Result의 특정한 값을 직접 주입하여 테스트를 하기 위한 목적의 selaled class 임
     * 살제 개발 코드랑 상관없음
     */
    object CallIndexListResult : CustomVRResult()
    object ScrollIndexResult : CustomVRResult()
}