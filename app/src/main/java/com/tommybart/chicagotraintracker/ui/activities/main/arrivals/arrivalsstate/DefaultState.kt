package com.tommybart.chicagotraintracker.ui.activities.main.arrivals.arrivalsstate

import androidx.lifecycle.LiveData
import com.tommybart.chicagotraintracker.data.models.Route
import kotlinx.coroutines.Deferred

/*
 * DefaultState is the initial state of ArrivalsFragment, displaying arrivals nearby or arrivals
 * at the station specified in settings. This state isn't specifically for a "default station"
 * (however, default stations do fall under this umbrella).
 */
class DefaultState: ArrivalsState {
    override suspend fun getRouteDataAsync(
        arrivalsStateContext: ArrivalsStateContext
    ): Deferred<LiveData<List<Route>>> {
        arrivalsStateContext.arrivalsState = this
        return arrivalsStateContext.arrivalsViewModel.getRouteDataAsync()
    }
}