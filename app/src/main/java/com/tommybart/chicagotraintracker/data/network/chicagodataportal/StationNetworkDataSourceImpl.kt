package com.tommybart.chicagotraintracker.data.network.chicagodataportal

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.tommybart.chicagotraintracker.data.db.entity.station.StationEntry
import com.tommybart.chicagotraintracker.internal.NoNetworkConnectionException
import com.tommybart.chicagotraintracker.internal.extensions.TAG

// TODO one interface?
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
            Log.w(TAG, "No network connection", e)
        }
    }
}