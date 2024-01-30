package com.example.a01_compose_study.domain.model

import com.example.a01_compose_study.domain.ScreenType
import com.example.a01_compose_study.domain.SealedDomainType
import com.example.a01_compose_study.domain.util.ScreenSizeType

sealed class VRResult {

    data class Success(
        val data: Any,
        val domainType: SealedDomainType,
        val screenType: ScreenType,
        val screenSizeType: ScreenSizeType,
    ) : VRResult()

    data class Error(val errorMessage: String) : VRResult()

    object NoData : VRResult()
}