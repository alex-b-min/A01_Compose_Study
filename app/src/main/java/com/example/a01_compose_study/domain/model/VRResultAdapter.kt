package com.example.a01_compose_study.domain.model

import com.example.a01_compose_study.domain.util.ScreenSizeType

sealed class VRResultAdapter {

    data class Success(
        val data: Any,
        val domainType: SealedDomainType,
        val screenType: ScreenType,
        val screenSizeType: ScreenSizeType,
    ) : VRResultAdapter()

    data class Error(val errorMessage: String) : VRResultAdapter()

    object NoData : VRResultAdapter()
}