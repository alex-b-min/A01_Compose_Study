package com.example.a01_compose_study

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Test

/**
 궁금증
 * 1. 같은 디스패쳐(Dispatchers.IO)를 사용하는 각각의 코루틴에서 sleep을 사용하면 현재 작업을 진행중인 코루틴 외의 다른 코루틴은 작업을 하지 못하는걸까?
 * 2. 같은 디스패쳐(Dispatchers.IO)를 사용하는 각각의 코루틴은 서로 다른 스레드에서 실행이 되는걸까?

 결과
 * 1. sleep() 함수를 사용하여 현재 코루틴이 완료될 때까지 다른 코루틴이 실행되지 않을 것으로 예상했지만, 비동기적으로 다른 코루틴과 동시에 실행되는 것처럼 보입니다.
 * 2. 테스트 코드가 아닌 일반 코드에서 Logcat을 확인한 결과, 서로 다른 코루틴이라도 같은 디스패처(Dispatchers.IO)를 할당해도 각각의 코루틴은 서로 다른 스레드 / 같은 프로세스 에서 작동하는 것으로 확인되었습니다.
 */

// sealedParsedData를 가정한 더미 데이터
val dummyData = MutableSharedFlow<String>()

class ExampleUnitTest {
    @Test
    fun testCollectData() = runBlocking {
        collectData()
        delay(1000)
        // 테스트에 필요한 가상의 데이터 생성
        dummyData.emit("데이터 생성")

        // 코루틴이 모두 완료될 때까지 대기
        delay(10000)
    }
}

// 데이터 발행 값 수집
suspend fun collectData() {
    CoroutineScope(Dispatchers.IO).launch(CoroutineName("Coroutine 1")) {
        dummyData.collect { dummyData ->
            println("${coroutineContext[CoroutineName]}: 데이터 수신 중...")
            Thread.sleep(3000)
            println("${coroutineContext[CoroutineName]}: 데이터 수신 완료")
        }
    }

    CoroutineScope(Dispatchers.IO).launch(CoroutineName("Coroutine 2")) {
        dummyData.collect { dummyData ->
            println("${coroutineContext[CoroutineName]}: 데이터 수신 중...")
            Thread.sleep(3000)
            println("${coroutineContext[CoroutineName]}: 데이터 수신 완료")
        }
    }

    CoroutineScope(Dispatchers.IO).launch(CoroutineName("Coroutine 3")) {
        dummyData.collect { dummyData ->
            println("${coroutineContext[CoroutineName]}: 데이터 수신 중...")
            Thread.sleep(3000)
            println("${coroutineContext[CoroutineName]}: 데이터 수신 완료")
        }
    }

    CoroutineScope(Dispatchers.IO).launch(CoroutineName("Coroutine 4")) {
        dummyData.collect { dummyData ->
            println("${coroutineContext[CoroutineName]}: 데이터 수신 중...")
            Thread.sleep(3000)
            println("${coroutineContext[CoroutineName]}: 데이터 수신 완료")
        }
    }
}

object DummyObject {

    val dummy = 1
}