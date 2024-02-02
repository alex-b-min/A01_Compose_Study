package com.example.a01_compose_study.data.vr

interface MediaInInterface {
    fun init(): Boolean
    fun deinit(): Boolean
//    fun recordOpen(channelCnt: Int, streamFormat: Int, sampleRate: Int): Boolean
//    fun recordClose(): Boolean
//    fun read(buffer: ByteArray, bufferSize: Int): ByteArray
}

interface TTSInterface {
//    fun startPlay(sampleRate: Int, sourceType: Int): Boolean
//    fun stopPlay(): Boolean
//    fun write(buffer: ByteArray, bufferSize: Int): Boolean
}

interface MediaOutInterface {
    fun init(): Boolean
//    fun deinit(): Boolean
//    fun startPlay(channelCnt: Int, streamFormat: Int, sampleRate: Int): Boolean
//    fun stopPlay(): Boolean
//    fun write(buffer: ByteArray, bufferSize: Int): Boolean
}