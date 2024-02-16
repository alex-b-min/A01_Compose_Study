package com.example.a01_compose_study

import org.junit.Test

/**
 궁금증
 * 1. sealed class 안에서 다른 인스턴스(객체)의 값 참조가 가능할까?
 * 2. sealed class의 하위 타입에 대해 확장 함수 사용이 가능할까?

 결과
 * 1. sealed class 안에서 다른 인스턴스(객체)의 값 참조가 가능했습니다.
 * 2. sealed class 의 하위 타입에 대한 확장 함수 정의가 가능했습니다.
 */

sealed class Result {
    // 다른 인스턴스(객체)의 값 참조 여부 가능 확인
    val referenceValue = DummyObject.dummy

    data class Success(val message: String) : Result() {
        override fun toString(): String {
            return super.toString()
        }
        fun examSuccess() {

        }
    }
    data class Error(val error: Throwable) : Result() {
        override fun toString(): String {
            return super.toString()
        }
        fun examError() {

        }
    }
}

// sealed class인 Result의 하위 클래스에 대한 확장 함수를 정의
fun Result.Success.logMessage() {
    println("Success message: $message")
}

fun Result.Error.logError() {
    println("Error message: ${error.message}")
}

class ExampleSealedTest {
    @Test
    fun testSealedClassExtensions() {
        // Success 인스턴스 생성 및 확장 함수 호출
        val successResult = Result.Success("Data loaded successfully")
        successResult.logMessage()

        // Error 인스턴스 생성 및 확장 함수 호출
        val errorResult = Result.Error(Throwable("Failed to load data"))
        errorResult.logError()
    }
}
