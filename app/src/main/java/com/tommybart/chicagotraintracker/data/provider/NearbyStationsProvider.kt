package com.tommybart.chicagotraintracker.data.provider

import android.location.Location

interface NearbyStationsProvider {
    suspend fun getNearbyStationMapIds(location: Location): List<Int>
}