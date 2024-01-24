package com.ftd.ivi.cerence.data.model.server.cerence.nlu

import com.ftd.ivi.cerence.data.model.server.cerence.nlu.slot.Slot


data class Nlu(
    val domain: String,
    val intent: String,
    val score: String,
    val slot: Slot
)