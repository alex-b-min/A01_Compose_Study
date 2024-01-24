package com.example.a01_compose_study.data

import com.example.a01_compose_study.domain.model.ContentResult
import com.ftd.ivi.cerence.data.model.server.kakao.weather.Instruction


class VRResult {

    @SerializedName("result")
    @Expose
    var result: ArrayList<VRResultData>? = null

    // 해당 데이터는 onRecognized를 받은 데이터를 한번더 가공해서 사용할 데이터 모델이다.
//    var uiData

    val intention: String
        get() = if (!result.isNullOrEmpty() && result!![0].intention != null) result!![0].intention!! else ""
    val domain: String
        get() = if (!result.isNullOrEmpty() && result!![0].domain != null) result!![0].domain!! else ""

    fun getKakaoTopic(): String? {
        if (result.isNullOrEmpty()) {
            return ""
        }
        return result?.first()?.slots?.first()?.vrActionResult?.cpActionData?.cpMeta?.topic
    }

    fun getCerenceContentType(slotIndex: Int = 0): String? {
        return try {
            result?.first()?.slots!![slotIndex].contentType!!
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun getSlotCount(): Int? {
        return try {
            result?.first()?.slot_count
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun getCerenceContentResult(slotIndex: Int = 0): ContentResult? {
        return try {
            result?.first()?.slots!![slotIndex].contentResult!!
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun getKakaoInstruction(): Instruction? {
        return try {
            result?.get(0)?.slots?.get(1)?.instruction
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun getKakaoNaviResult(): List<LosPoiResult>? {
        return try {
            result?.first()?.slots!![1].kakaoResult!!.losResponse.losPoiResultList
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun getKakaoTTSText(): String? {
        return try {
            result?.get(0)?.slots?.get(0)?.vrActionResult?.cpActionData?.ttsText
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun getCerenceTTSText(): String? {
        return try {
            result?.get(0)?.slots?.get(0)?.prompt
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun getIndexValue(): Int? {
        return try {
            result?.get(0)?.slots?.get(0)?.items?.get(0)?.value?.toInt()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun getPhrase(): String? {
        return try {
            result?.first()?.phrase
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun getFirstResult(): VRResultData? {
        return result?.first()
    }

    fun getIntentionOrNull(): String? {
        return try {
            result?.first()?.intention
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun toString(): String {
        return "${super.toString()} $result"
    }
}

class VRResultData {

    @SerializedName("sampleName")
    @Expose
    var sampleName: String? = null

    @SerializedName("sampleNumber")
    @Expose
    var sampleNumber: String? = null

    @SerializedName("confidence")
    @Expose
    var confidence = 0

    @SerializedName("intention")
    @Expose
    var intention: String? = null

    @SerializedName("domain")
    @Expose
    var domain: String? = null

    @SerializedName("phrase")
    @Expose
    var phrase: String? = null

    @SerializedName("slot_count")
    @Expose
    var slot_count = 0

    @SerializedName("slots")
    @Expose
    var slots: ArrayList<Slots>? = null

    fun getFirstSlot(): Slots? {
        return slots?.first()
    }
}

class Slots {

    @SerializedName("items")
    @Expose
    var items: ArrayList<Items>? = null

    @SerializedName("name")
    @Expose
    var name: String? = null

    // 카카오 내비 관련 결과포맷
    @SerializedName("cpContents")
    var kakaoResult: CpContents? = null

    // 카카오 날씨 관련 결과포맷
    @SerializedName("instruction")
    val instruction: Instruction? = null
    val linkInstruction: String? = null
    val vrActionResult: VrActionResult? = null

    // 세렌스 날씨 관련 결과포맷
    val contentResult: ContentResult? = null
    val contentType: String? = null
    val prompt: String? = null
    val vrResult: VrResult? = null

    fun getFirstItem(): Items? {
        return items?.first()
    }
}

class Items {

    @SerializedName("id")
    @Expose
    var id = 0

    @SerializedName("tag")
    @Expose
    var tag: String? = null

    @SerializedName("value")
    @Expose
    var value: String? = null

}
