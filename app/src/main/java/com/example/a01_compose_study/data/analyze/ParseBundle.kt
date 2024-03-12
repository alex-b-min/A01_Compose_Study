package com.example.a01_compose_study.data.analyze

import com.example.a01_compose_study.data.DialogueMode
import com.example.a01_compose_study.domain.util.CustomLogger
import com.example.a01_compose_study.presentation.screen.SelectVRResult

class ParseBundle<T>(var type: ParseDomainType) {
    // type 을 가지고 각 사용하는곳에서 확인후 model을 캐스팅하도록,
    var model: T? = null

    var dialogue: String = ""
    var prompt: String = ""
    var promptId: Int = -1
    var isStartRecog: Boolean = false
    var dialogueMode: DialogueMode = DialogueMode.NONE
    var isBack = false
    var isExit = false
    var contextId: Int? = null
    var phrase = ""
    var isBundleConsumed = false
    var selectVRResult: SelectVRResult = SelectVRResult.None

    fun print() {
        CustomLogger.i("AnalyzeType[$type]  Dialogue[$dialogue] Prompt[$prompt]")
        CustomLogger.i("model $model")
    }
}