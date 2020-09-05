package com.tommybart.chicagotraintracker.data.network.chicagotransitauthority.response

import com.google.gson.annotations.SerializedName

data class CtaApiResponse(
    @SerializedName("ctatt")
    val arrivalsContainer: ArrivalsContainer
)