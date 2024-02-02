package com.example.a01_compose_study.data.analyze

import com.example.a01_compose_study.data.DialogueMode
import com.example.a01_compose_study.data.VRResult
import com.example.a01_compose_study.data.pasing.HelpModel
import com.example.a01_compose_study.domain.util.CustomLogger

class HelpParser :
    BaseParser<HelpModel>() {
    init {
        CustomLogger.i("HelpParser ${this.hashCode()}")
    }

    override fun analyze(vrresult: VRResult): ParseBundle<HelpModel> {
        val bundle: ParseBundle<HelpModel> = ParseBundle(ParseDomainType.HELP)
        val model = HelpModel(vrresult.intention)
        bundle.dialogueMode = DialogueMode.HELP
        CustomLogger.i("HelpParser dialogueMode ${bundle.dialogueMode}")
        bundle.model = model
        return bundle
    }
}
