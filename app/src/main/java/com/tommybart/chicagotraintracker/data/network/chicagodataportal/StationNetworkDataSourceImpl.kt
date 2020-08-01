package com.tommybart.chicagotraintracker.data.network.chicagodataportal

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.tommybart.chicagotraintracker.data.db.entity.StationEntry
import com.tommybart.chicagotraintracker.internal.NoNetworkConnectionException
import com.tommybart.chicagotraintracker.internal.TAG

class StationNetworkDataSourceImpl(
    private val chicagoDataPortalApiService: ChicagoDataPortalApiService
) : StationNetworkDataSource {

    override val downloadStationData: MutableLiveData<List<StationEntry>> = MutableLiveData()

    override suspend fun fetchStationData() {
        try {
            val fetchStationData: List<StationEntry> = chicagoDataPortalApiService
                .getStationDataAsync()
                .await()
            downloadStationData.postValue(fetchStationData)
        } catch (e: NoNetworkConnectionException) {
            Log.e(TAG, "No network connection", e)
        }
    }
}