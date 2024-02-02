package com.example.a01_compose_study.data.pasing

import com.example.a01_compose_study.data.VRResult

// 아직 정의되지 않은 데이터는 여기에 다시 넣어놓음
class DefaultModel(var intent: String) : BaseModel(intent) {

    lateinit var recognizeResult: VRResult
    override fun toString(): String {
        return "${super.toString()}"
    }
}