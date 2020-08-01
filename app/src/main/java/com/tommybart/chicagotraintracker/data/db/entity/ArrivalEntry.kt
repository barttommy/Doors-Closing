package com.tommybart.chicagotraintracker.data.db.entity


import com.google.gson.annotations.SerializedName
import com.tommybart.chicagotraintracker.data.models.Location

data class ArrivalEntry(
    @SerializedName("arrT")
    val arrivalTime: String,
    @SerializedName("destNm")
    val finalDestination: String,
    val heading: Double,
    @SerializedName("isApp")
    val isApproaching: Boolean, // TODO?
    @SerializedName("isDly")
    val isDelayed: Boolean,
    //TODO?
    val lat: String,
    val lon: String,
    @SerializedName("rt")
    val routeName: String,
    @SerializedName("staId")
    val stationId: String,
    val staNm: String,
    val stpDe: String,
    val stpId: String,
    val trDr: String
)