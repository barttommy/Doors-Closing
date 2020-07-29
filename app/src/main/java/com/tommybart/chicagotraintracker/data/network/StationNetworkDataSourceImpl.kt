package com.tommybart.chicagotraintracker.data.network

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.tommybart.chicagotraintracker.data.db.entity.StationEntry
import com.tommybart.chicagotraintracker.internal.NoNetworkConnectionException

class StationNetworkDataSourceImpl(
    private val chicagoDataPortalApiService: ChicagoDataPortalApiService
) : StationNetworkDataSource {

    override val downloadStationData: MutableLiveData<List<StationEntry>> = MutableLiveData()

    override suspend fun fetchStationData() {
        try {
            val fetchStationData = chicagoDataPortalApiService
                .getStationDataAsync()
                .await()
            downloadStationData.postValue(fetchStationData)
        } catch (e: NoNetworkConnectionException) {
            Log.e("fetchStationData:", "No network connection", e)
        }
    }
}