package com.tommybart.chicagotraintracker.ui.activities.main.arrivals.arrivalsstate

import androidx.lifecycle.LiveData
import com.tommybart.chicagotraintracker.data.models.Route
import kotlinx.coroutines.Deferred

interface ArrivalsState {
    val title: String
    suspend fun getRouteDataAsync(
        arrivalsStateContext: ArrivalsStateContext
    ): Deferred<LiveData<List<Route>>>
}