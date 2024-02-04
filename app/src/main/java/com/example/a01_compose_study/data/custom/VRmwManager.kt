package com.example.a01_compose_study.data.custom

import android.util.Log
import com.example.a01_compose_study.data.VRResult
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class VRmwManager(
    private val mwContext: MWContext
) {

    fun setVRResult() {
        val vrResultJsonString = "{ \"result\": [ { \"confidence\": 5018, \"intention\": \"Help\", \"phrase\": \"Help\", \"slot_count\": 0 } ] }"
        val gson = Gson()
        val vrResultType = object : TypeToken<VRResult>() {}.type

        val parsedVRResult: VRResult = gson.fromJson(vrResultJsonString, vrResultType)

//        Log.d("@@ parsedVRResult", "${parsedVRResult.result}")
//        Log.d("@@ parsedVRResult222", "${parsedVRResult.domain}")
//        Log.d("@@ parsedVRResult333", "${parsedVRResult.intention}")
//        Log.d("@@ parsedVRResult444", "${parsedVRResult}")
//        parsedVRResult.result?.forEach {
//            Log.d("@@ parsedVRResult555", "${it.domain}")
//            Log.d("@@ parsedVRResult555", "${it.intention}")
//            Log.d("@@ parsedVRResult555", "${it.confidence}")
//            Log.d("@@ parsedVRResult555", "${it.phrase}")
//            Log.d("@@ parsedVRResult555", "${it.slot_count}")
//            Log.d("@@ parsedVRResult555", "${it.sampleName}")
//            Log.d("@@ parsedVRResult555", "${it.sampleNumber}")
//            Log.d("@@ parsedVRResult555", "${it.slots}")
//        }

        mwContext.onVRResult(parsedVRResult)
    }
}
