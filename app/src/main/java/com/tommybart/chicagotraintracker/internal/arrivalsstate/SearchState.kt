package com.tommybart.chicagotraintracker.internal.arrivalsstate

import com.tommybart.chicagotraintracker.data.models.Station

/*
 * ArrivalsFragment state after user selects a station manually from searching
 */
const val SEARCH_STATE_ID = 2

class SearchState(
    val searchStation: Station
) : ArrivalsState {
    override val id: Int = SEARCH_STATE_ID
    override val title: String get() = searchStation.name
}