package com.tommybart.chicagotraintracker.ui.activities.main.arrivals.arrivalsstate

import com.tommybart.chicagotraintracker.ui.activities.main.arrivals.ArrivalsViewModel

class ArrivalsStateContext(
    val arrivalsViewModel: ArrivalsViewModel
) {
    var arrivalsState: ArrivalsState? = null
}