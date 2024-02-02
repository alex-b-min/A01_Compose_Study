package com.example.a01_compose_study.data.analyze

import com.example.a01_compose_study.data.DialogueMode
import com.example.a01_compose_study.data.VRResult
import com.example.a01_compose_study.data.VRResultData
import com.example.a01_compose_study.data.pasing.RadioItem
import com.example.a01_compose_study.data.pasing.RadioModel
import com.example.a01_compose_study.domain.util.CustomLogger

class RadioParser :
    BaseParser<RadioModel>() {

    override fun analyze(vrresult: VRResult): ParseBundle<RadioModel> {
        CustomLogger.i("RadioParser::analyze")
        var bundle: ParseBundle<RadioModel> = ParseBundle(ParseDomainType.RADIO)
        var model = RadioModel(vrresult.intention)

        // Media 인텐션이면 무시
        if (checkIsMedia(vrresult)) {
            bundle = ParseBundle(ParseDomainType.UNSUPPORTED_DOMAIN)
            return bundle
        }


        bundle.dialogueMode = DialogueMode.RADIO
        if (procIntention(vrresult, model)) {
            bundle.model = model
            return bundle
        }

        vrresult.result?.let { result ->
            resultRadioData(model, result)
        }

        val printItems = model.items.joinToString("\n")
        CustomLogger.i("Finish Parser data - $printItems")

        bundle.model = model
        return bundle
    }

    fun checkIsMedia(vrresult: VRResult): Boolean {
        vrresult.getFirstResult()?.getFirstSlot()?.getFirstItem()?.let {
            it.value?.let { value ->
                return value.equals("Media", ignoreCase = true)
            }
        }
        return false
    }

    fun procIntention(vrresult: VRResult, model: RadioModel): Boolean {
        val intention = vrresult.getIntentionOrNull()
        intention?.let { intention ->
            when (intention) {
                "VolumeAVOff" -> {
                    return true
                }

                else -> {
                    return false
                }
            }
        }
        return false
    }


    private fun resultRadioData(model: RadioModel, result: ArrayList<VRResultData>) {
        CustomLogger.i("RadioParser resultData")

        if (result.size > 0) {
            // "Play
            if (result[0].slot_count > 0) {
                for (item in result) {
                    var id = ""
                    var channelName = ""
                    var frequencyKhz = 0

                    if (item.slot_count > 0) {
                        item.slots?.let { slots ->
                            slots[0].items?.forEachIndexed { index, items ->
                                id = items.id.toString()
                                channelName = items.value.toString()
                            }
                            model.items.add(
                                RadioItem(
                                    id = id,
                                    channelName = channelName
                                )
                            )
                        }
                    }
                }
            }
            // "Radio" 만 발화 시 slot count = 0
            else {
                //intention만 들고 가면 되는데..?
                CustomLogger.i("RadioParser result slot count 0")
            }
        }
    }
}