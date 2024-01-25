package com.example.a01_compose_study.domain.util

import java.util.Timer
import java.util.TimerTask

class CustomTimer(val delayInMillis: Long = 5000L, val action: () -> Unit) {
    private var timer: Timer? = null
    fun start() {
        stop() // 현재 실행 중인 타이머가 있다면 중지
        timer = Timer().apply {
            schedule(object : TimerTask() {
                override fun run() {
                    action.invoke()
                }
            }, delayInMillis)
        }
    }

    fun stop() {
        timer?.cancel()
        timer = null
    }

    fun restart() {
        stop()
        start()
    }
}