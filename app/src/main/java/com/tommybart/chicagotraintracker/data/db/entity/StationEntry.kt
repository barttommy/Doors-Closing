package com.tommybart.chicagotraintracker.data.db.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.tommybart.chicagotraintracker.data.models.AvailableTrainLines
import com.tommybart.chicagotraintracker.data.models.Location
import com.tommybart.chicagotraintracker.data.models.Station
import com.tommybart.chicagotraintracker.internal.TrainLine

@Entity(tableName = "station_data")
data class StationEntry(
    /* ID naming convention is inconsistent between APIs (CTA: stationId & stopId VS.
    CDP: mapId & stopId). For this app's purposes, "stationId" will refer to the unique "parent"
    stationId (or, in this API's case,  mapId). The "child" station identifiers (referring to specific
    platform at a station) aren't used or saved in this project */
    @SerializedName("map_id")
    val stationId: Int,
    @SerializedName("station_name")
    val stationName: String,
    @SerializedName("station_descriptive_name")
    val stationDescriptiveName: String,
    @SerializedName("stop_name")
    val stopName: String,
    @SerializedName("direction_id")
    val directionId: String,
    @Embedded
    val location: Location,
    @SerializedName("ada")
    val disabilityAccessible: Boolean,
    val red: Boolean,
    val blue: Boolean,
    @SerializedName("brn")
    val brown: Boolean,
    @SerializedName("g")
    val green: Boolean,
    @SerializedName("o")
    val orange: Boolean,
    @SerializedName("pnk")
    val pink: Boolean,
    @SerializedName("p")
    val purple: Boolean,
    @SerializedName("pexp")
    val purpleExpress: Boolean,
    @SerializedName("y")
    val yellow: Boolean
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null

    fun toStation() = Station(
        stationId, stationName, stationDescriptiveName, convertAvailableTrainLines(), location
    )

    private fun convertAvailableTrainLines() = AvailableTrainLines(
        red, blue, brown, green, orange, pink, purple, purpleExpress, yellow
    )
}