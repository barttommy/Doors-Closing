package com.tommybart.chicagotraintracker.data.network.chicagodataportal

import androidx.lifecycle.LiveData
import org.threeten.bp.LocalDate

interface StationNetworkDataSource {
    suspend fun fetchStationData(): SodaApiResponse
    suspend fun fetchIsUpdateNeeded(lastFetchDate: LocalDate): Boolean
}