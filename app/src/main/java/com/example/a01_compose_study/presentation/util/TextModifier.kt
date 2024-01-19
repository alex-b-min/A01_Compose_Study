package com.example.a01_compose_study.presentation.util

object TextModifier {

    /**
     * 주어진 범위에서 값의 상대적인 위치를 0과 1 사이로 정규화하는 함수
     * 이 함수를 통해 투명도(Alpha)를 구해 글자가 나타났다 사라지는 효과 구현
     */
    fun Float.normalize(start: Float, end: Float): Float {
        return (this - start) / (end - start)
    }
}