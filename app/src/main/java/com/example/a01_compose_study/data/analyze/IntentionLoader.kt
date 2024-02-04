package com.example.a01_compose_study.data.analyze

import android.content.Context
import android.util.Log
import com.example.a01_compose_study.data.VRResult
import com.example.a01_compose_study.data.analyze.model.SupportIntention
import com.example.a01_compose_study.domain.model.BaseApplication
import com.example.a01_compose_study.domain.util.CustomLogger
import com.google.gson.Gson
import java.io.BufferedReader
import java.io.InputStreamReader

object IntentionLoader {

//    companion object {
//        val instance: IntentionLoader by lazy { IntentionLoader() }
//    }

    /**
     * supportIntentions이 null 임
     */

    private val supportIntentions = ArrayList<SupportIntention>()

    init {

    }

    fun init() {
        loadJSONFromAsset()
    }

    fun findDomain(vrResult: VRResult): ParseDomainType {
        var intention = vrResult.intention
        if (intention.equals("CerenceCommand", ignoreCase = true)) {
            intention = vrResult.domain
        } else if (intention.equals("KakaoCommand", ignoreCase = true)) {
            if (vrResult.domain.equals("kakaoi", ignoreCase = true)) {
                intention = vrResult.getKakaoTopic() ?: ""
            } else {
                intention = vrResult.domain
            }
        }
        return findDomain(intention)
    }

    fun checkIntention(domainType: ParseDomainType, vrResultIntention: String): Boolean {
        var intention = vrResultIntention
        return findDomain(intention) == domainType
    }

    fun printSupportIntentions() {
        CustomLogger.i("printSupportIntentions---------------")
        supportIntentions.forEach {
            CustomLogger.i("     Domain[${it.vrDomainType}]")
            it.intentions?.forEach {
                CustomLogger.i("     ---[${it}]")
            }
        }
    }

    private fun findDomain(intention: String): ParseDomainType {
        var foundDomain = ParseDomainType.UNSUPPORTED_DOMAIN
        Log.d("@@ supportIntentions : ", "${supportIntentions}")
        val matchingIntention = supportIntentions.find { supportIntention ->
            Log.d("@@ supportIntention : ", "${supportIntention}")
            supportIntention.intentions?.any {
                Log.d("@@ it : ", "${it}")
                Log.d("@@ intention : ", "${intention}")
                it.equals(intention, ignoreCase = true)
            } == true
        }
        matchingIntention?.let { supportIntention ->
            supportIntention.vrDomainType?.let {
                foundDomain = ParseDomainType.fromString(it) ?: ParseDomainType.UNSUPPORTED_DOMAIN
            }
        }
        return foundDomain
    }

    fun loadJSONFromAsset() {
        supportIntentions.clear()
        loadFilesFromFolder("json/parser")
        printSupportIntentions()
    }

    private fun loadFilesFromFolder(path: String) {
        try {
            val context = BaseApplication.appContext
            val gson = Gson()
            val assets = context.assets.list(path)
            if (assets.isNullOrEmpty()) return

            for (asset in assets) {
                val newPath = if (path.isEmpty()) asset else "$path/$asset"
                if (isDirectory(context, newPath)) { // 디렉터리인 경우, 재귀적으로 처리
                    loadFilesFromFolder(newPath)
                } else if (asset.endsWith(".json")) { // JSON 파일일 경우만 처리
                    context.assets.open(newPath).use { inputStream ->
                        BufferedReader(InputStreamReader(inputStream)).use { reader ->
                            val json = reader.readText()
                            val model = gson.fromJson(json, SupportIntention::class.java)
                            //TODO 읽은 리소소의 버전, 타겟 등 처리 추가
                            // 타겟은 A01 등 지원 단말 기기 모델 체크
                            // 리소스의 버전은 앱버전등 해당 인텐션이 처리되어있는지 여부 체크
                            supportIntentions.add(model)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.message?.let { CustomLogger.e(it) }
        }
    }

    private fun isDirectory(context: Context, path: String): Boolean {
        // 디렉터리인 경우, list 함수는 비어 있지 않은 배열을 반환합니다.
        // 파일인 경우, list 함수는 null 또는 비어 있는 배열을 반환합니다.
        val list = context.assets.list(path)
        return list != null && list.isNotEmpty()
    }


}
