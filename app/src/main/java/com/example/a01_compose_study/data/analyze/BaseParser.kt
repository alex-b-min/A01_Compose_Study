package com.example.a01_compose_study.data.analyze

import com.example.a01_compose_study.data.VRResult
import com.example.a01_compose_study.domain.util.CustomLogger

abstract class BaseParser<T> {
    init {

    }

    abstract fun analyze(result: VRResult): ParseBundle<T>

    fun checkIntentionVRResult(vrResult: VRResult, domainType: ParseDomainType) {
        CustomLogger.i("checkIntentionVRResult---------------")
        // VRResult의 각 intention을 확인하여 해당하는 Domain이 아닌 경우 제거
        vrResult.result?.let { result ->
            if (result.size > 0) {
                val iterator = result.iterator()
                while (iterator.hasNext()) {
                    val item = iterator.next()
                    var intention = item.intention
                    if (intention.equals("CerenceCommand", ignoreCase = true)) {
                        intention = vrResult.domain
                    } else if (intention.equals("KakaoCommand", ignoreCase = true)) {
                        intention = if (vrResult.domain.equals("kakaoi", ignoreCase = true)) {
                            vrResult.getKakaoTopic() ?: ""
                        } else {
                            vrResult.domain
                        }
                    }
                    intention?.let {
                        if (!IntentionLoader.checkIntention(domainType, intention)) {
                            CustomLogger.i("Not This Domain [$domainType] intention [$intention]---------------")
                            iterator.remove()
                        }
                    }
                }
            }
        }
        printIntention(vrResult)
    }

    private fun printIntention(result: VRResult) {
        // check
        CustomLogger.i("checkIntentionVRResult After printIntention---------------")
        result.result?.forEach {
            CustomLogger.i("     intention[${it.intention}]")
        }
    }

    val IgnoreThreshold = 4000
    val AccuracyThreshold = 5500
    val GapThreshold = 500
}