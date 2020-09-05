package com.tommybart.chicagotraintracker.data.repository

import com.tommybart.chicagotraintracker.data.models.Station

interface StationRepository {
    suspend fun getStationData(): List<Station>
}