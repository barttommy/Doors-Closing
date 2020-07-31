package com.tommybart.chicagotraintracker.data.db.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.tommybart.chicagotraintracker.data.models.Location

@Entity(tableName = "station_data")
data class StationEntry(
    @SerializedName("map_id")
    val mapId: String,
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
    val blue: Boolean,
    @SerializedName("brn")
    val brown: Boolean,
    @SerializedName("g")
    val green: Boolean,
    @SerializedName("o")
    val orange: Boolean,
    @SerializedName("p")
    val purple: Boolean,
    @SerializedName("pexp")
    val purpleExpress: Boolean,
    @SerializedName("pnk")
    val pink: Boolean,
    val red: Boolean,
    @SerializedName("y")
    val yellow: Boolean
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}