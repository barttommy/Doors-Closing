package com.tommybart.chicagotraintracker.data.repository

import androidx.lifecycle.LiveData
import com.tommybart.chicagotraintracker.data.models.Route

interface ArrivalsRepository {
    suspend fun getRouteData(): LiveData<List<Route>>
}