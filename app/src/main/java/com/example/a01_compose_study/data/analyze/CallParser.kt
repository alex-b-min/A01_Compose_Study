package com.example.a01_compose_study.data.analyze

import android.provider.ContactsContract
import com.example.a01_compose_study.data.Contact
import com.example.a01_compose_study.data.DialogueMode
import com.example.a01_compose_study.data.Tags
import com.example.a01_compose_study.data.VRResult
import com.example.a01_compose_study.data.VRResultData
import com.example.a01_compose_study.data.pasing.CallModel
import com.example.a01_compose_study.domain.util.CustomLogger
import javax.inject.Inject

class CallParser @Inject constructor() :
    BaseParser<CallModel>() {
    init {
        CustomLogger.i("CallParser ${this.hashCode()}")
    }

    override fun analyze(vrresult: VRResult): ParseBundle<CallModel> {
        val bundle: ParseBundle<CallModel> = ParseBundle(ParseDomainType.CALL)
        val model = CallModel(vrresult.intention)
        checkIntentionVRResult(vrresult, ParseDomainType.CALL)
        bundle.dialogueMode = DialogueMode.CALL
        vrresult.result?.let { list ->

            val result = getFilteredList(list)
            if (result.size > 0) {
                for (item in result) {
                    var name = ""
                    var firstName = ""
                    var lastName = ""
                    var type = 0
                    var slotType = ""
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
                                        if (Tags.PhoneType.isEqual(name)) {
                                            it.items?.let { items ->
                                                if (items.size > 0) {
                                                    when (Tags.values()
                                                        .find { tag -> tag.isEqual(items[0].tag!!) }) {
                                                        Tags.Home -> {
                                                            type =
                                                                ContactsContract.CommonDataKinds.Phone.TYPE_HOME
                                                        }

                                                        Tags.Work,
                                                        Tags.Office -> {
                                                            type =
                                                                ContactsContract.CommonDataKinds.Phone.TYPE_WORK
                                                        }

                                                        Tags.Mobile,
                                                        Tags.Phone -> {
                                                            type =
                                                                ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE
                                                        }

                                                        else -> {
                                                            type =
                                                                ContactsContract.CommonDataKinds.Phone.TYPE_OTHER
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
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
        bundle.model = model
        return bundle
    }

    private fun getFilteredList(dataList: ArrayList<VRResultData>): ArrayList<VRResultData> {
        val result = ArrayList<VRResultData>()
        for (index in dataList.indices) {
            val item = dataList[index]
            //Nbest 1등의 confidence 값이 Accuracy threshold보다 클 때
            if (item.confidence > AccuracyThreshold) {
                result.add(item)
                // Nbest 결과의 1등과 2등의 Gap차이가 (500)보다 클 때 (1-Best High)
                if (dataList.size > index + 1) {
                    if (item.confidence > dataList[index + 1].confidence + GapThreshold) {
                        CustomLogger.i("CallParser NBestGap over ${item.confidence} > ${dataList[index + 1].confidence}")
                        break
                    }
                }
            }
        }
        if (result.size > 0) {
            CustomLogger.i("CallParser Filter 5500 over ${result.size}")
            return result
        }

        for (index in dataList.indices) {
            val item = dataList[index]

            if (item.confidence < IgnoreThreshold) continue

            result.add(item)
            if (dataList.size > index + 1) {
                if (item.confidence > dataList[index + 1].confidence + GapThreshold) {
                    CustomLogger.i("CallParser NBestGap over ${item.confidence} > ${dataList[index + 1].confidence}")
                    break
                }
            }
        }
        CustomLogger.i("CallParser Filter 4000 over ${result.size}")
        return result
    }
}