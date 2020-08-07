package com.tommybart.chicagotraintracker.data.repository

import androidx.lifecycle.LiveData
import com.tommybart.chicagotraintracker.data.models.Route

interface RouteRepository {
    suspend fun getRouteData(): LiveData<List<Route>>
}