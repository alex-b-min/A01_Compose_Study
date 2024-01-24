package com.ftd.ivi.cerence.data.model.server.cerence

import com.ftd.ivi.cerence.data.model.server.cerence.nlu.Nlu

data class VrResult(
    val nlu: Nlu,
    val recognitionText: String
)