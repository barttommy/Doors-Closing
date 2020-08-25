package com.tommybart.chicagotraintracker.data.network.chicagotransitauthority.response

import com.google.gson.annotations.SerializedName
import com.tommybart.chicagotraintracker.data.db.entity.statearrivals.ArrivalEntry

data class ArrivalsContainer(
    @SerializedName("tmst")
    val transmissionTime: String,
    @SerializedName("errCd")
    val errorCode: Int?,
    @SerializedName("errNm")
    val errorName: String?,
    @SerializedName("eta")
    val arrivalEntries: List<ArrivalEntry>
)