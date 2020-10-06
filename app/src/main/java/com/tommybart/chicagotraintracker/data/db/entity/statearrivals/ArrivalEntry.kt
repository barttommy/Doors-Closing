package com.tommybart.chicagotraintracker.data.db.entity.statearrivals

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

/*
 * Note: Unfortunately, runNumber seems to be unreliable.
 */
@Entity(tableName = "arrival_data")
data class ArrivalEntry(
    @SerializedName("staId")
    val mapId: Int,
    @SerializedName("staNm")
    val stationName: String,
    @SerializedName("destNm")
    val destinationName: String,
    @SerializedName("rn")
    val runNumber: Int,
    @SerializedName("rt")
    val trainLine: String,
    @SerializedName("prdt")
    val predictionTime: String,
    @SerializedName("arrT")
    val arrivalTime: String,
    @SerializedName("isApp")
    val isApproaching: Int,
    @SerializedName("isDly")
    val isDelayed: Int,
    @SerializedName("heading")
    val bearing: Double?,
    @SerializedName("lat")
    val latitude: Double?,
    @SerializedName("lon")
    val longitude: Double?
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
    var stateId: Int? = null
}