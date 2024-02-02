package com.example.a01_compose_study.data.analyze

import com.example.a01_compose_study.data.Contact
import com.example.a01_compose_study.data.DialogueMode
import com.example.a01_compose_study.data.Intentions
import com.example.a01_compose_study.data.Tags
import com.example.a01_compose_study.data.VRResult
import com.example.a01_compose_study.data.VRResultData
import com.example.a01_compose_study.data.pasing.SendMsgModel
import com.example.a01_compose_study.domain.util.CustomLogger

class SendMsgParser(val dialogueMode: DialogueMode?) :
    BaseParser<SendMsgModel>() {

    private val checkGarbage = "<...>"

    init {
        CustomLogger.i("SendMsgParser ${this.hashCode()}")
    }

    override fun analyze(vrresult: VRResult): ParseBundle<SendMsgModel> {
        val bundle: ParseBundle<SendMsgModel> = ParseBundle(ParseDomainType.SEND_MESSAGE)
        val model = SendMsgModel(vrresult.intention)
        bundle.dialogueMode =
            if (dialogueMode == DialogueMode.MAINMENU) DialogueMode.SEND_MESSAGE else dialogueMode!!
        CustomLogger.i("SendMsgParser dialogueMode ${bundle.dialogueMode}")
        vrresult.result?.let { result ->
            when (bundle.dialogueMode) {
                DialogueMode.SEND_MESSAGE -> resultData(model, result)
                DialogueMode.SEND_MESSAGE_NAME -> messageContentData(model, result)
                DialogueMode.SEND_MESSAGE_NAME_CHANGE -> changeMessageData(model, result)
                else -> {
                }
            }
            if (result.isNotEmpty()) {
                model.prompt = "${result[0].phrase}"
            }
        }
        bundle.model = model
        return bundle
    }

    private fun getFilteredList(dataList: ArrayList<VRResultData>): ArrayList<VRResultData> {
        val result = ArrayList<VRResultData>()
        // N-Best 1건만 확인
        for (index in dataList.indices) {
            val item = dataList[index]
            // Send Message check only Accuracy
            if (item.confidence > AccuracyThreshold) {
                result.add(item)
                break
            }
        }
        if (result.size > 0) {
            CustomLogger.i("SendMsgParser Filter 5500 over ${result.size}")
            return result
        }

        // N-Best
        for (index in dataList.indices) {
            val item = dataList[index]
            if (item.confidence < IgnoreThreshold) continue
            result.add(item)
        }
        CustomLogger.i("SendMsgParser Filter 4000 over ${result.size}")
        return result
    }

    private fun resultData(model: SendMsgModel, result: ArrayList<VRResultData>) {
        CustomLogger.i("SendMsgParser resultData")
        if (result.size > 0) {
            // "Send Message" 만 발화 시 list 표시 후 Name 발화 시 Accuracy 조건 판단
            if (result[0].slot_count == 0) {
                // index 표시 X 화면
                model.enableIndex = false
            } else {
                // "Send Message to <Name>
                val resultFilter = getFilteredList(result)
                if (resultFilter.size > 0) {
                    for (item in resultFilter) {
                        var name = ""
                        var firstName = ""
                        var lastName = ""
                        var slotType = ""
                        var type = 0

                        if (item.slot_count > 0) {
                            item.slots?.let { slots ->


                                slots[0].name?.let {
                                    slotType = it
                                }
                                slots[0].items?.forEachIndexed { index, items ->

                                    if (index == 0) {
                                        name = items.value.toString()
                                        firstName = name
                                    } else {
                                        lastName = items.value.toString()
                                        name += " $lastName"
                                    }
                                }
                                if (slots.size > 1) {
                                    slots[1].let {
                                        it.name?.let { name ->
                                            if (Tags.Server.isEqual(name)) {
                                                slots[1].items?.forEachIndexed { _, items ->
                                                    model.messageValue = items.value.toString()
                                                    item.phrase =
                                                        (if (item.phrase?.contains(checkGarbage) == true) {
                                                            checkPhrase(
                                                                item.intention,
                                                                item.phrase,
                                                                items.value
                                                            )
                                                        } else {
                                                            item.phrase
                                                        })
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    // just check Garbage
                                    item.phrase =
                                        (if (item.phrase?.contains(checkGarbage) == true) {
                                            checkPhrase(item.intention, item.phrase, "")
                                        } else {
                                            item.phrase
                                        })
                                }
                                model.items.add(
                                    Contact(
                                        name = name,
                                        nameRmSpecial = name,
                                        type = type,
                                        first_name = firstName,
                                        last_name = lastName,
                                        slotType = slotType
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun messageContentData(model: SendMsgModel, result: ArrayList<VRResultData>) {
        val intention = result[0].intention!!
        CustomLogger.i("messageContentData intention $intention")
        if (Intentions.No.isEqual(intention)) {
            CustomLogger.i("intention No")
        } else {
            model.messageValue = result[0].slots?.get(0)?.items?.get(0)?.value?.toString()!!
            CustomLogger.i("messageContentData model.messageValue ${model.messageValue}")
        }
    }

    private fun changeMessageData(model: SendMsgModel, result: ArrayList<VRResultData>) {
        val intention = result[0].intention!!
        CustomLogger.i("changeMessageData intention $intention")
        model.prompt = "recognized $intention"
    }

    fun checkPhrase(intentions: String?, phrase: String?, message: String?): String {
        var retPhrase = ""
        if (intentions?.let { Intentions.WaitServerCreateSMS.isEqual(it) } == true) {
            val pos = phrase?.indexOf(checkGarbage)
            if (pos != -1 && pos != null) {
                retPhrase = message?.let {
                    phrase.replaceRange(
                        pos,
                        pos + checkGarbage.length,
                        it
                    ).trim()
                }.toString()
                CustomLogger.i("SendMsgParser after check Garbage phrase $phrase retPhrase $retPhrase")
            }
        }
        return retPhrase
    }
}
