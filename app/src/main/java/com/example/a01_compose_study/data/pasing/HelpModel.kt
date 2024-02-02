package com.example.a01_compose_study.data.pasing

import com.example.a01_compose_study.presentation.util.StringUtils

/**
 * sHelp data
 *
 * @property domainIdList
 *
 * @see HelpModel
 */
// Help 는 Model를 인식한 데이터가 아닌 보여줄 데이터로 사용한다.
// domain list, command list가 있고 다른 Model들과 다르게 사용한다.
data class HelpModel(var intent: String) : BaseModel(intent) {

    var domainIdList: MutableList<HelpItemData> = mutableListOf()
}

/**
 * Help data
 *
 * @property domainId                ex:"navigation"
 * @property commandIdList               ex: "["맥도날드DT 안내해 줘", "서울역으로 가자", "인천국제공항 안내 시작"]"
 *
 */
data class HelpItemData(
    var domainId: String = StringUtils.EMPTY,
    var commandIdList: MutableList<String> = mutableListOf()
)
