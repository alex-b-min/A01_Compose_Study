package com.example.a01_compose_study.data.custom

import android.util.Log
import com.example.a01_compose_study.data.DialogueMode
import com.example.a01_compose_study.data.DomainType
import com.example.a01_compose_study.data.VRResult
import com.example.a01_compose_study.data.analyze.ParseDomainType
import com.example.a01_compose_study.data.analyze.ParserFactory
//import com.example.a01_compose_study.domain.util.ParserFactory
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import javax.inject.Singleton

@Singleton
class MWContext {
    fun onVRResult(vrResult: VRResult) {

        val bundle = ParserFactory().dataParsing(vrResult, dialogueMode = DialogueMode.HELP)

        Log.d("@@ vrResult", "${vrResult}")
        Log.d("@@ bundle", "${bundle}")
        Log.d("@@ bundle22", "${bundle?.print()}")
        Log.d("@@ bundle33", "${bundle?.type}")
    }
}
