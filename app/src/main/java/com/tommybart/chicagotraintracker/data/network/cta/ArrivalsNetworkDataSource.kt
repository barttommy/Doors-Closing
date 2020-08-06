package com.tommybart.chicagotraintracker.data.network.cta

import androidx.lifecycle.LiveData
import com.tommybart.chicagotraintracker.data.network.cta.response.CtaApiResponse

interface ArrivalsNetworkDataSource {
    val downloadArrivalData: LiveData<CtaApiResponse>
    suspend fun fetchArrivalData(stationIds: List<Int>)
}