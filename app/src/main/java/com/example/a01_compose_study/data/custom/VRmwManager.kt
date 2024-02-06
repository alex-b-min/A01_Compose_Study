package com.example.a01_compose_study.data.custom

import android.util.Log
import com.example.a01_compose_study.data.HVRError
import com.example.a01_compose_study.data.VRResult
import com.example.a01_compose_study.domain.util.CustomLogger
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class VRmwManager(
    private val mwContext: MWContext
) {

    fun setVRError(error: HVRError) {
        mwContext.onVRError(error)
    }

    fun setVRResult() {
        val vrResultJsonString = "{ \"result\": [ { \"confidence\": 5018, \"intention\": \"Help\", \"phrase\": \"Help\", \"slot_count\": 0 } ] }"
        val gson = Gson()
        val vrResultType = object : TypeToken<VRResult>() {}.type

        val parsedVRResult: VRResult = gson.fromJson(vrResultJsonString, vrResultType)

        mwContext.onVRResult(parsedVRResult)
    }
}
