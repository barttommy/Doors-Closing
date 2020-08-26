package com.tommybart.chicagotraintracker.data.repository

import androidx.lifecycle.LiveData
import com.tommybart.chicagotraintracker.data.models.Route
import com.tommybart.chicagotraintracker.internal.Resource
import com.tommybart.chicagotraintracker.internal.arrivalsstate.ArrivalsState
import org.threeten.bp.LocalDateTime

interface RouteRepository {
    fun getRouteData(
        arrivalsState: ArrivalsState,
        requestedStationMapIds: List<Int>,
        isFetchNeeded: Boolean
    ): LiveData<Resource<List<Route>>>

    suspend fun getRequestStationMapIds(arrivalsState: ArrivalsState): List<Int>?

    fun isFetchRouteDataNeeded(
        arrivalsState: ArrivalsState,
        requestedStationMapIds: List<Int>
    ): Boolean
}