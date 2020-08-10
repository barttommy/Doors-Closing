package com.tommybart.chicagotraintracker.data.provider

import android.content.Context

class RequestedStationsProviderImpl(
    context: Context
) : PreferenceProvider(context), RequestedStationsProvider {

    // TODO Implement for nearby trains

    override fun getRequestedStationMapIds(): List<Int> {
        val clarkLakeStationId = 40380
        val defaultStationId = preferences.getInt(DEFAULT_STATION_PREFERENCE, clarkLakeStationId)
        return listOf(defaultStationId)
    }
}