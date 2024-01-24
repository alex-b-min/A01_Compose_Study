package com.example.a01_compose_study.domain.model

import com.example.a01_compose_study.domain.model.BaseModel

data class CommonModel(var intent: String) : BaseModel(intent) {

    var prompt: String = ""
    var index: Int? = null
    var isPrev = false
    var isNext = false

}
