package com.tommybart.chicagotraintracker.data.network.chicagotransitauthority

import android.util.Log
import com.tommybart.chicagotraintracker.data.network.chicagotransitauthority.response.CtaApiResponse
import com.tommybart.chicagotraintracker.internal.NoNetworkConnectionException
import com.tommybart.chicagotraintracker.internal.extensions.TAG

/*
 * Cta api only allows you to request 4 specific mapIds in one request. If we have more than 4,
 * we just request the first 4 (which will be the stations closest to the user, coming from
 * the NearbyStationProvider.kt)
 */
private const val CTA_MAX_STATION_REQUEST = 4

class CtaNetworkDataSourceImpl(
    private val ctaApiService: CtaApiService
) : CtaNetworkDataSource {

    override suspend fun fetchRouteData(requestedStationMapIds: List<Int>): CtaApiResponse? {
        return try {
            ctaApiService
                .getArrivalsAsync(requestedStationMapIds.take(CTA_MAX_STATION_REQUEST))
                .await()
        } catch (e: NoNetworkConnectionException) {
            Log.w(TAG, "No network connection", e)
            null
        }
    }
}