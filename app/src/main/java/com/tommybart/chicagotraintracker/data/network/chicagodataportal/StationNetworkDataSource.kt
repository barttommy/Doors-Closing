package com.tommybart.chicagotraintracker.data.network.chicagodataportal

import com.tommybart.chicagotraintracker.data.db.entity.StationEntry

interface StationNetworkDataSource {
    suspend fun fetchStationData(): List<StationEntry>
}