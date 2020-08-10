package com.tommybart.chicagotraintracker.data.repository

import androidx.lifecycle.LiveData
import com.tommybart.chicagotraintracker.data.models.Route

interface RouteArrivalsRepository {
    suspend fun getRouteData(requestedStationMapIds: List<Int>): LiveData<List<Route>>
}