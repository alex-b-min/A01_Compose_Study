package com.example.a01_compose_study.domain.model

data class HelpModel(var intent: String) : BaseModel(intent) {
    var domainIdList: MutableList<HelpItemData> = mutableListOf()
}

data class HelpVRData(
    var domainId: String = "",
    var command: String,
    var commandsDetail: List<String> = mutableListOf()
)
data class HelpItemData(
    var domainId: SealedDomainType = SealedDomainType.None,
    var command: String,
    var commandsDetail: List<String> = mutableListOf()
)