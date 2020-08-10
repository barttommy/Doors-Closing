package com.tommybart.chicagotraintracker.data.network.chicagotransitauthority

import androidx.lifecycle.LiveData

interface RouteArrivalsNetworkDataSource {
    val downloadRouteData: LiveData<CtaApiResponse>
    suspend fun fetchRouteData(requestedStationMapIds: List<Int>)
}