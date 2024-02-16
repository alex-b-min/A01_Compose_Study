package com.example.a01_compose_study.domain.util

/**
 * domainUiState의 초기값은 NoneScreen인데,
 * 이 초기값을 ScreenSizeType의 초기값인 0f로 설정하려면, Zero라는 하위 타입을 추가해야 했음
 */
sealed class ScreenSizeType {
    override fun toString(): String {
        return javaClass.simpleName
    }

    object Zero: ScreenSizeType()
    object Small: ScreenSizeType()
    object Middle: ScreenSizeType()
    object Large: ScreenSizeType()
}