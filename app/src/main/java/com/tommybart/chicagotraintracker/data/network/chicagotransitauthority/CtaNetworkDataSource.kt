package com.tommybart.chicagotraintracker.data.network.chicagotransitauthority

import com.tommybart.chicagotraintracker.data.network.chicagotransitauthority.response.CtaApiResponse

interface CtaNetworkDataSource {
    suspend fun fetchRouteData(requestedStationMapIds: List<Int>): CtaApiResponse?
}