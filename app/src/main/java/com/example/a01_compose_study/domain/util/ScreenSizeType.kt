package com.example.a01_compose_study.domain.util

sealed class ScreenSizeType {
    object Small: ScreenSizeType()
    object Middle: ScreenSizeType()
    object Large: ScreenSizeType()
}