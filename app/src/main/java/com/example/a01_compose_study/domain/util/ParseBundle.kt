package com.example.a01_compose_study.domain.util

import android.os.Parcel
import android.os.Parcelable
import com.example.a01_compose_study.data.DialogueMode
import com.example.a01_compose_study.domain.model.ParseDomainType

class ParseBundle<T>(var type: ParseDomainType) : Parcelable {
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

//    constructor(parcel: Parcel) : this(TODO("type")) {
//        dialogue = parcel.readString()
//        prompt = gitparcel.readString()
//        promptId = parcel.readInt()
//        isStartRecog = parcel.readByte() != 0.toByte()
//        isBack = parcel.readByte() != 0.toByte()
//        isExit = parcel.readByte() != 0.toByte()
//        contextId = parcel.readValue(Int::class.java.classLoader) as? Int
//        phrase = parcel.readString()
//        isBundleConsumed = parcel.readByte() != 0.toByte()
//    }

    fun print() {
        CustomLogger.i("AnalyzeType[$type]  Dialogue[$dialogue] Prompt[$prompt]")
        CustomLogger.i("model $model")
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(dialogue)
        parcel.writeString(prompt)
        parcel.writeInt(promptId)
        parcel.writeByte(if (isStartRecog) 1 else 0)
        parcel.writeByte(if (isBack) 1 else 0)
        parcel.writeByte(if (isExit) 1 else 0)
        parcel.writeValue(contextId)
        parcel.writeString(phrase)
        parcel.writeByte(if (isBundleConsumed) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

//    companion object CREATOR : Parcelable.Creator<ParseBundle> {
//        override fun createFromParcel(parcel: Parcel): ParseBundle {
//            return ParseBundle(parcel)
//        }
//
//        override fun newArray(size: Int): Array<ParseBundle?> {
//            return arrayOfNulls(size)
//        }
//    }
}