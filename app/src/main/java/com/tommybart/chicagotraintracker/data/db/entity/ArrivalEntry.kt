package com.tommybart.chicagotraintracker.data.db.entity


import androidx.room.Embedded
import com.google.gson.annotations.SerializedName

// TODO custom deserialization
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
    @Embedded
    val location: Location,
    @SerializedName("rt")
    val trainLine: String, // TODO - use enum? also figure out way to extract route vs train?
    @SerializedName("staId")
    val stationId: String,
    @SerializedName("staNm")
    val stationName: String
)