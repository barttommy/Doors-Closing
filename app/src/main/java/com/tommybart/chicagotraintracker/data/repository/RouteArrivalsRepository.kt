package com.tommybart.chicagotraintracker.data.repository

import androidx.lifecycle.LiveData
import com.tommybart.chicagotraintracker.data.models.Route

interface RouteArrivalsRepository {
    suspend fun getRouteData(requestedStationIds: List<Int>): LiveData<List<Route>>
}