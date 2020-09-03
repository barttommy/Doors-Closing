package com.tommybart.chicagotraintracker.data.network.chicagodataportal

import com.tommybart.chicagotraintracker.data.network.chicagodataportal.response.SodaApiResponse
import org.threeten.bp.LocalDate

interface StationNetworkDataSource {
    suspend fun fetchStationData(): SodaApiResponse
    suspend fun fetchIsUpdateNeeded(lastFetchDate: LocalDate): Boolean
}