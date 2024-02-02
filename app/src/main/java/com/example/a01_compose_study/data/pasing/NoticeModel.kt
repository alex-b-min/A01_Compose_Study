package com.example.a01_compose_study.data.pasing

import com.example.a01_compose_study.data.pasing.BaseModel

class NoticeModel : BaseModel("Notice") {
    var noticeString: String = ""
    var noticePromptId: String = ""
    var args: HashMap<String, Any> = HashMap()
    var screenExist: Boolean = true
}