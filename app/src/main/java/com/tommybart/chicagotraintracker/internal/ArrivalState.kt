package com.tommybart.chicagotraintracker.internal

import java.io.Serializable

sealed class ArrivalState(
    val id: Int,
    val title: String
) : Serializable {

    companion object {
        const val DEFAULT_STATE_ID = 0
        const val LOCATION_STATE_ID = 1
        const val SEARCH_STATE_ID = 2
    }

    class Default : ArrivalState(DEFAULT_STATE_ID, "Default Station")
    class Location : ArrivalState(LOCATION_STATE_ID, "Nearby Arrivals")
    data class Search(val mapId: Int) : ArrivalState(SEARCH_STATE_ID, "Search Result")
}