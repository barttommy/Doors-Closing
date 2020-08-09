package com.tommybart.chicagotraintracker.data.provider

import android.content.Context

class RequestedStationsProviderImpl(
    context: Context
) : PreferenceProvider(context), RequestedStationsProvider {

    override fun getRequestedStationIds(): List<Int> {
        val fullertonStationId = 41220
        val defaultStationId = preferences.getInt(DEFAULT_STATION_PREFERENCE, fullertonStationId)
        return listOf(defaultStationId)
    }
}