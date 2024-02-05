package com.example.a01_compose_study.data.custom

import com.example.a01_compose_study.domain.model.HelpItemData
import com.example.a01_compose_study.domain.model.ScreenType
import com.example.a01_compose_study.domain.model.SealedDomainType
import com.example.a01_compose_study.domain.util.ScreenSizeType

data class ProcHelpData(
    val domainType: SealedDomainType,
    val screenType: ScreenType,
    val screenSizeType: ScreenSizeType,
    val data: List<HelpItemData>,
)