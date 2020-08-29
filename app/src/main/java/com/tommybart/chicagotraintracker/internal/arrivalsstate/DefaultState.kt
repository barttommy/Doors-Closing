package com.tommybart.chicagotraintracker.internal.arrivalsstate

/*
 * DefaultState is the initial state of ArrivalsFragment, displaying arrivals nearby or arrivals
 * at the station specified in settings. This state isn't specifically for a "default station"
 * (however, default stations do fall under this umbrella).
 */
const val DEFAULT_STATE_ID = 0

class DefaultState : ArrivalsState {

    override val id: Int = DEFAULT_STATE_ID
    override val title: String = "Default Station"
}