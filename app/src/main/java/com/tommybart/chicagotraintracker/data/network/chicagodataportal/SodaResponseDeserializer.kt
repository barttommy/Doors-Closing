package com.tommybart.chicagotraintracker.data.network.chicagodataportal

import android.util.Log
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.tommybart.chicagotraintracker.data.db.entity.StationEntry
import com.tommybart.chicagotraintracker.data.models.Location
import com.tommybart.chicagotraintracker.internal.extensions.TAG
import java.lang.reflect.Type

class SodaResponseDeserializer : JsonDeserializer<SodaApiResponse> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): SodaApiResponse {

        val stationList = mutableListOf<StationEntry>()
        json?.asJsonArray?.forEach {
            try {
                val station = it.asJsonObject
                val location = station.get("location").asJsonObject
                val stationEntry = StationEntry(
                    station.get("map_id").asInt,
                    station.get("station_name").asString,
                    station.get("station_descriptive_name").asString,
                    station.get("stop_name").asString,
                    station.get("direction_id").asString,
                    Location(location.get("latitude").asDouble, location.get("longitude").asDouble),
                    station.get("ada").asBoolean,
                    station.get("red").asBoolean,
                    station.get("blue").asBoolean,
                    station.get("brn").asBoolean,
                    station.get("g").asBoolean,
                    station.get("o").asBoolean,
                    station.get("pnk").asBoolean,
                    station.get("p").asBoolean,
                    station.get("pexp").asBoolean,
                    station.get("y").asBoolean
                )
                addStation(stationEntry, stationList)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to parse station", e)
            }
        }
        return SodaApiResponse(stationList.toList())
    }

    private fun addStation(stationEntry: StationEntry, stationList: MutableList<StationEntry>) {
        if (stationList.contains(stationEntry)) {
            val preExistingStation = stationList[stationList.indexOf(stationEntry)]
            preExistingStation.red = preExistingStation.red || stationEntry.red
            preExistingStation.blue = preExistingStation.blue || stationEntry.blue
            preExistingStation.brown = preExistingStation.brown || stationEntry.brown
            preExistingStation.green = preExistingStation.green || stationEntry.green
            preExistingStation.orange = preExistingStation.orange || stationEntry.orange
            preExistingStation.pink = preExistingStation.pink || stationEntry.pink
            preExistingStation.purple = preExistingStation.purple || stationEntry.purple
            preExistingStation.purpleExpress = preExistingStation.purpleExpress
                || stationEntry.purpleExpress
            preExistingStation.yellow = preExistingStation.yellow || stationEntry.yellow
        } else {
            stationList.add(stationEntry)
        }
    }
}