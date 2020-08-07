package com.tommybart.chicagotraintracker.data.network.cta

import androidx.lifecycle.LiveData
import com.tommybart.chicagotraintracker.data.network.cta.response.CtaApiResponse

interface RouteNetworkDataSource {
    val downloadRouteData: LiveData<CtaApiResponse>
    suspend fun fetchRouteData(stationIds: List<Int>)
}