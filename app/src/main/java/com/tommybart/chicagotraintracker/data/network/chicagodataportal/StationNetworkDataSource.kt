package com.tommybart.chicagotraintracker.data.network.chicagodataportal

import androidx.lifecycle.LiveData
import com.tommybart.chicagotraintracker.data.db.entity.StationEntry

interface StationNetworkDataSource {
    val downloadStationData: LiveData<List<StationEntry>>
    suspend fun fetchStationData()
}