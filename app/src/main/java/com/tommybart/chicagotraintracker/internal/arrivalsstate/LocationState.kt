package com.tommybart.chicagotraintracker.internal.arrivalsstate

const val LOCATION_STATE_ID = 1

class LocationState: ArrivalsState {
    override val id: Int = LOCATION_STATE_ID
    override val title: String = "Nearby Arrivals"
}