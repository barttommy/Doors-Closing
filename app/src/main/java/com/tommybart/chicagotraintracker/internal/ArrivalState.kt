package com.tommybart.chicagotraintracker.internal

import com.tommybart.chicagotraintracker.data.models.Station

const val DEFAULT_STATE_ID = 0
const val LOCATION_STATE_ID = 1
const val SEARCH_STATE_ID = 2

sealed class ArrivalState(
    val id: Int,
    val title: String
) {
    class Default : ArrivalState(DEFAULT_STATE_ID, "Default Station")
    class Location : ArrivalState(LOCATION_STATE_ID, "Nearby Arrivals")
    data class Search(val station: Station) : ArrivalState(SEARCH_STATE_ID, station.name)
}
