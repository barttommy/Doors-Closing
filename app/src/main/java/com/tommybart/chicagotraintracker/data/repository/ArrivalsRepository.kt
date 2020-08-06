package com.tommybart.chicagotraintracker.data.repository

import androidx.lifecycle.LiveData
import com.tommybart.chicagotraintracker.data.db.entity.RouteWithArrivals

interface ArrivalsRepository {
    suspend fun getRouteData(): LiveData<List<RouteWithArrivals>>
}