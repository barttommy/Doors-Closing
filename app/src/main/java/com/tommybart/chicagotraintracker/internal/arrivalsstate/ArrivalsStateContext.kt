package com.tommybart.chicagotraintracker.internal.arrivalsstate

import com.tommybart.chicagotraintracker.ui.activities.main.arrivals.ArrivalsViewModel

class ArrivalsStateContext(
    val arrivalsViewModel: ArrivalsViewModel
) {
    var arrivalsState: ArrivalsState? = null
}