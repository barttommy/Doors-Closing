package com.tommybart.chicagotraintracker.data.network.chicagotransitauthority

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.tommybart.chicagotraintracker.internal.NoNetworkConnectionException
import com.tommybart.chicagotraintracker.internal.extensions.TAG

class RouteArrivalsNetworkDataSourceImpl(
    private val ctaApiService: CtaApiService
) : RouteArrivalsNetworkDataSource {

    override val downloadRouteData: MutableLiveData<CtaApiResponse> = MutableLiveData()

    override suspend fun fetchRouteData(requestedStationMapIds: List<Int>) {
        try {
            val fetchArrivalData = ctaApiService
                .getArrivalsAsync(requestedStationMapIds)
                .await()
            downloadRouteData.postValue(fetchArrivalData)
        } catch(e: NoNetworkConnectionException) {
            Log.w(TAG, "No network connection", e)
        }
    }
}