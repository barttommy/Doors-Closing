package com.tommybart.chicagotraintracker.data.network.cta

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.tommybart.chicagotraintracker.data.network.cta.response.CtaApiResponse
import com.tommybart.chicagotraintracker.internal.NoNetworkConnectionException
import com.tommybart.chicagotraintracker.internal.extensions.TAG

class RouteArrivalsNetworkDataSourceImpl(
    private val ctaApiService: CtaApiService
) : RouteArrivalsNetworkDataSource {

    override val downloadRouteData: MutableLiveData<CtaApiResponse> = MutableLiveData()

    override suspend fun fetchRouteData(stationIds: List<Int>) {
        try {
            val fetchArrivalData = ctaApiService
                .getArrivalsAsync(stationIds)
                .await()
            downloadRouteData.postValue(fetchArrivalData)
        } catch(e: NoNetworkConnectionException) {
            Log.w(TAG, "No network connection", e)
        }
    }
}