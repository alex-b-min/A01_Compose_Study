package com.example.a01_compose_study.data.analyze

import com.example.a01_compose_study.data.DialogueMode
import com.example.a01_compose_study.data.Intentions
import com.example.a01_compose_study.data.VRResult
import com.example.a01_compose_study.data.pasing.CommonModel
import com.example.a01_compose_study.domain.util.CustomLogger

class CommonParser(val dialogueMode: DialogueMode) :
    BaseParser<CommonModel>() {
    init {
        CustomLogger.i("CommonAnalyzer ${this.hashCode()}")
    }

    override fun analyze(vrresult: VRResult): ParseBundle<CommonModel> {
        val bundle: ParseBundle<CommonModel> = ParseBundle(ParseDomainType.MAX)
        bundle.dialogueMode = dialogueMode
        val model = CommonModel(vrresult.intention)
        if (Intentions.Yes.isEqual(model.intention) || Intentions.No.isEqual(model.intention) || Intentions.OtherNumber.isEqual(
                model.intention
            )
        ) {
            vrresult.getPhrase()?.let {
                model.prompt = it
            }
        } else {
            listData(model, vrresult)
        }
        bundle.model = model
        return bundle
    }

    private fun listData(model: CommonModel, vrresult: VRResult) {
        val string = vrresult.getPhrase()
        model.prompt = "${string} selected"
        model.index = vrresult.getIndexValue()
        vrresult.getIntentionOrNull()?.let {
            if (Intentions.NextPage.isEqual(it)) {
                model.isNext = true
            } else if (Intentions.PreviousPage.isEqual(it)) {
                model.isPrev = true
            }
        }

    }
}