package com.tommybart.chicagotraintracker.data.network.chicagodataportal

import android.util.Log
import com.tommybart.chicagotraintracker.data.db.entity.StationEntry
import com.tommybart.chicagotraintracker.internal.NoNetworkConnectionException
import com.tommybart.chicagotraintracker.internal.extensions.TAG

// TODO one interface?
class StationNetworkDataSourceImpl(
    private val chicagoDataPortalApiService: ChicagoDataPortalApiService
) : StationNetworkDataSource {

    override suspend fun fetchStationData(): List<StationEntry> {
        return try {
            chicagoDataPortalApiService
                .getStationDataAsync()
                .await()
        } catch (e: NoNetworkConnectionException) {
            Log.w(TAG, "No network connection", e)
            listOf()
        }
    }
}