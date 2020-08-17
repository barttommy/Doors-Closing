package com.tommybart.chicagotraintracker.data.repository

import androidx.lifecycle.LiveData
import com.tommybart.chicagotraintracker.data.models.Route

interface RouteArrivalsRepository {
    suspend fun getRouteData(): LiveData<List<Route>>
    suspend fun getRouteDataSearch(searchMapId: Int): LiveData<List<Route>>
}