package com.ftd.ivi.cerence.analyze

import com.example.a01_compose_study.data.VRResult
import com.example.a01_compose_study.data.analyze.BaseParser
import com.example.a01_compose_study.data.analyze.ParseBundle
import com.example.a01_compose_study.data.analyze.ParseDomainType
import com.example.a01_compose_study.data.pasing.DefaultModel

class DefaultParser :
    BaseParser<DefaultModel>() {

    override fun analyze(result: VRResult): ParseBundle<DefaultModel> {
        var bundle: ParseBundle<DefaultModel> = ParseBundle(ParseDomainType.MAX)
        var model = DefaultModel(result.intention)
        // TODO 정의되지 않은 타입은 DefaultModel에 다시 VRResult를 넣고있음.
        model.recognizeResult = result
        bundle.model = model
        return bundle
    }
}