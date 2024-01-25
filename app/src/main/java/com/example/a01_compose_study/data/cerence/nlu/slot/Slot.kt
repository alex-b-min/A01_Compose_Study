package com.ftd.ivi.cerence.data.model.server.cerence.nlu.slot

import com.ftd.ivi.cerence.data.model.server.cerence.nlu.slot.items.Anchor
import com.ftd.ivi.cerence.data.model.server.cerence.nlu.slot.items.Cat
import com.ftd.ivi.cerence.data.model.server.cerence.nlu.slot.items.Category
import com.ftd.ivi.cerence.data.model.server.cerence.nlu.slot.items.Date
import com.ftd.ivi.cerence.data.model.server.cerence.nlu.slot.items.RelativeLocation
import com.ftd.ivi.cerence.data.model.server.cerence.nlu.slot.items.SearchLocation
import com.ftd.ivi.cerence.data.model.server.cerence.nlu.slot.items.SearchPhrase
import com.google.gson.annotations.SerializedName

data class Slot(
    val Date: Date,
    val Category: Category,
    val SearchLocation: SearchLocation,
    val anchor: Anchor,
    val cat: Cat,
    val relative_location: RelativeLocation
) {
    @SerializedName("Search-phrase")
    val searchPhrase: SearchPhrase? = null
}