package com.example.a01_compose_study.data

import com.google.gson.Gson
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class VrConfig {

    @SerializedName("cfgVersion")
    @Expose
    var cfgVersion = "1.0"

    @SerializedName("appVersion")
    @Expose
    var appVersion = "1.0"

    @SerializedName("locale")
    @Expose
    var locale: String? = "default"

    @SerializedName("language")
    @Expose
    var language: String? = "default"

    @SerializedName("country")
    @Expose
    var country: String? = "default"

    @SerializedName("isSupportASR")
    @Expose
    var isSupportASR: Boolean = false

    @SerializedName("isSupportTTS")
    @Expose
    var isSupportTTS: Boolean = false

    @SerializedName("isSupportServer")
    @Expose
    var isSupportServer: Boolean = false

    @SerializedName("voiceAmplitudeThreshold")
    @Expose
    var voiceAmplitudeThreshold: Int = -30

    @SerializedName("unavailables")
    @Expose
    var unavailables: MutableList<DialogueMode> =
        mutableListOf()//DialogueMode.CALL, DialogueMode.SEND_MESSAGE)

    @SerializedName("disables")
    @Expose
    var disables: MutableList<DialogueMode> = mutableListOf()

    @SerializedName("parkingOnly")
    @Expose
    var parkingOnly: MutableList<DialogueMode> = mutableListOf()

    @SerializedName("ignitionOnly")
    @Expose
    var ignitionOnly: MutableList<DialogueMode> = mutableListOf()

    @SerializedName("rearUnsupported")
    @Expose
    var rearUnsupported: MutableList<DialogueMode> = mutableListOf()

    override fun toString(): String {
        return Gson().toJson(this)
    }

    fun copy(): VrConfig {
        return Gson().fromJson(this.toString(), VrConfig::class.java)
    }
}