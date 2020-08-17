package com.tommybart.chicagotraintracker.data.network.chicagodataportal

import org.threeten.bp.LocalDate

interface StationNetworkDataSource {
    suspend fun fetchStationData(): SodaApiResponse
    suspend fun fetchIsUpdateNeeded(lastFetchDate: LocalDate): Boolean
}