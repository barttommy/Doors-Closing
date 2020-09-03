package com.tommybart.chicagotraintracker.data.network.chicagodataportal

import android.util.Log
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.tommybart.chicagotraintracker.data.db.entity.StationEntry
import com.tommybart.chicagotraintracker.data.network.chicagodataportal.response.SodaApiResponse
import com.tommybart.chicagotraintracker.internal.extensions.TAG
import java.lang.reflect.Type

class SodaResponseDeserializer : JsonDeserializer<SodaApiResponse> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): SodaApiResponse {
        val stationList = mutableListOf<StationEntry>()
        json?.asJsonArray?.forEach { jsonElement ->
            try {
                val stationEntry: StationEntry? =
                    context?.deserialize(jsonElement, StationEntry::class.java)
                stationEntry?.let { addStation(it, stationList) }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to parse station", e)
            }
        }
        return SodaApiResponse(
            stationList.toList()
        )
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