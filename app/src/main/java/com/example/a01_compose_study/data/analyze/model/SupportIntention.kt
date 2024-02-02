package com.example.a01_compose_study.data.analyze.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SupportIntention {

    @SerializedName("formatVersion")
    @Expose
    var formatVersion: String? = null // format version

    @SerializedName("targetModel")
    @Expose
    var target: String? = null // "A01", "SW", "NIRO", ..

    @SerializedName("targetVersion")
    @Expose
    var targetVersion: String? = null // target version

    @SerializedName("vrDomainType")
    @Expose
    var vrDomainType: String? = null

    @SerializedName("intentions")
    @Expose
    var intentions: ArrayList<String>? = null
}

