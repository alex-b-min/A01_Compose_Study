package com.example.a01_compose_study.data.analyze

import com.example.a01_compose_study.data.DialogueMode
import com.example.a01_compose_study.data.VRResult
import com.example.a01_compose_study.data.pasing.CommonModel
import com.example.a01_compose_study.domain.util.CustomLogger

class LaunchAppParser :
    BaseParser<CommonModel>() {
    init {
        CustomLogger.i("LaunchAppParser ${this.hashCode()}")
    }

    override fun analyze(vrresult: VRResult): ParseBundle<CommonModel> {
        val bundle: ParseBundle<CommonModel> = ParseBundle(ParseDomainType.LAUNCHAPP)
        val model = CommonModel(vrresult.intention)
        model.prompt = "${vrresult.getPhrase()}"
        bundle.dialogueMode = DialogueMode.LAUNCHAPP
        CustomLogger.i("LaunchAppParser dialogueMode ${bundle.dialogueMode}")
        bundle.model = model
        return bundle
    }
}