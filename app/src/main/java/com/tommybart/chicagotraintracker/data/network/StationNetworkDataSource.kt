package com.tommybart.chicagotraintracker.data.network

import androidx.lifecycle.LiveData
import com.tommybart.chicagotraintracker.data.db.entity.StationEntry

interface StationNetworkDataSource {
    val downloadStationData: LiveData<List<StationEntry>> // TODO?

    suspend fun fetchStationData()
}