package com.tommybart.chicagotraintracker.data.repository

import androidx.lifecycle.LiveData
import com.tommybart.chicagotraintracker.data.models.Route
import com.tommybart.chicagotraintracker.internal.arrivalsstate.ArrivalsState

interface RouteRepository {
    fun getRouteData(arrivalsState: ArrivalsState): LiveData<List<Route>?>
}