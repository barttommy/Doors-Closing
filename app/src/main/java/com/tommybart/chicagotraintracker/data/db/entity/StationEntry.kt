package com.tommybart.chicagotraintracker.data.db.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.tommybart.chicagotraintracker.data.models.AvailableTrainLines
import com.tommybart.chicagotraintracker.data.models.Location
import com.tommybart.chicagotraintracker.data.models.Station

@Entity(tableName = "station_data")
data class StationEntry(
    /* ID naming convention is inconsistent between APIs (CTA response: stationId & stopId VS. CTA
    request (mapId vs. stationId) VS. CDP: mapId & stopId). For this app's purposes, "mapId"
    will refer to the unique "parent" mapId. The "child" station identifiers (referring to specific
    platform at a station) aren't used or saved in this project */
    @SerializedName("map_id")
    val mapId: Int,
    @SerializedName("station_name")
    val stationName: String,
    @SerializedName("station_descriptive_name")
    val stationDescriptiveName: String,
    @SerializedName("stop_name")
    val stopName: String,
    @SerializedName("direction_id")
    val cardinalDirection: String,
    @Embedded
    val location: Location,
    @SerializedName("ada")
    val disabilityAccessible: Boolean,
    var red: Boolean,
    var blue: Boolean,
    @SerializedName("brn")
    var brown: Boolean,
    @SerializedName("g")
    var green: Boolean,
    @SerializedName("o")
    var orange: Boolean,
    @SerializedName("pnk")
    var pink: Boolean,
    @SerializedName("p")
    var purple: Boolean,
    @SerializedName("pexp")
    var purpleExpress: Boolean,
    @SerializedName("y")
    var yellow: Boolean
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null

    fun toStation() = Station(
        mapId, stationName, stationDescriptiveName, convertAvailableTrainLines(), location
    )

    private fun convertAvailableTrainLines() = AvailableTrainLines(
        red, blue, brown, green, orange, pink, purple, purpleExpress, yellow
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        else if (other !is StationEntry) return false
        return mapId == other.mapId
    }

    @Ignore
    private var hashCode: Int = 0
    override fun hashCode(): Int {
        if (hashCode == 0) {
            hashCode = 17
            hashCode = 37 * hashCode + mapId.hashCode()
        }
        return hashCode
    }
}