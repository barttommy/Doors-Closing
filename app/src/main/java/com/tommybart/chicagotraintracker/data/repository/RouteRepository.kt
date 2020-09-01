package com.tommybart.chicagotraintracker.data.repository

import androidx.lifecycle.LiveData
import com.tommybart.chicagotraintracker.data.models.Route
import com.tommybart.chicagotraintracker.internal.ArrivalState
import com.tommybart.chicagotraintracker.internal.Resource

interface RouteRepository {
    fun getRouteData(
        arrivalsState: ArrivalState,
        requestedStationMapIds: List<Int>,
        isFetchNeeded: Boolean
    ): LiveData<Resource<List<Route>>>

    suspend fun getRequestStationMapIds(arrivalsState: ArrivalState): List<Int>?

    fun isFetchRouteDataNeeded(
        arrivalsState: ArrivalState,
        requestedStationMapIds: List<Int>
    ): Boolean
}