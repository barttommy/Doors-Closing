package com.tommybart.chicagotraintracker.data.network.chicagodataportal

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.tommybart.chicagotraintracker.data.db.entity.StationEntry
import com.tommybart.chicagotraintracker.internal.NoNetworkConnectionException
import com.tommybart.chicagotraintracker.internal.extensions.TAG
import org.threeten.bp.LocalDate
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StationNetworkDataSourceImpl(
    private val sodaApiService: SodaApiService
) : StationNetworkDataSource {

    override suspend fun fetchStationData(): SodaApiResponse {
        return try {
            sodaApiService.getStationDataAsync().await()
        } catch (e: NoNetworkConnectionException) {
            Log.w(TAG, "No network connection", e)
            SodaApiResponse(listOf())
        }
    }

    // Checks if update is needed by comparing the last fetch date to any updates on the database
    // See: SodaApiService.kt
    override suspend fun fetchIsUpdateNeeded(lastFetchDate: LocalDate): Boolean {
        return try {
            sodaApiService.getStationsWhereAsync(":updated_at > '$lastFetchDate'")
                .await()
                .stationEntries
                .isNotEmpty()
        } catch (e: NoNetworkConnectionException) {
            Log.w(TAG, "No network connection", e)
            false
        }
    }
}