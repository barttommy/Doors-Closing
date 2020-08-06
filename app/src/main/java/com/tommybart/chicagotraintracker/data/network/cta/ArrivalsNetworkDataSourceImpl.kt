package com.tommybart.chicagotraintracker.data.network.cta

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.tommybart.chicagotraintracker.data.network.cta.response.CtaApiResponse
import com.tommybart.chicagotraintracker.internal.NoNetworkConnectionException
import com.tommybart.chicagotraintracker.internal.extensions.TAG

class ArrivalsNetworkDataSourceImpl(
    private val ctaApiService: CtaApiService
) : ArrivalsNetworkDataSource {

    override val downloadArrivalData: MutableLiveData<CtaApiResponse> = MutableLiveData()

    override suspend fun fetchArrivalData(mapIds: List<String>) {
        try {
            val fetchArrivalData = ctaApiService
                .getArrivalsAsync(mapIds)
                .await()
            downloadArrivalData.postValue(fetchArrivalData)
        } catch(e: NoNetworkConnectionException) {
            Log.w(TAG, "No network connection", e)
        }
    }
}