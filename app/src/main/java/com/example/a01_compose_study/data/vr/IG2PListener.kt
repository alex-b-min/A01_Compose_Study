package com.example.a01_compose_study.data.vr

interface IG2PListener {
    fun onG2PStatusChanged(mode: Int, state: Int, deviceId: String)
    fun onG2PCompleted(mode: Int, deviceId: String, count: Int)
}