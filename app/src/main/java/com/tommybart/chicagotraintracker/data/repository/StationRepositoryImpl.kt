package com.tommybart.chicagotraintracker.data.repository

import android.util.Log
import com.tommybart.chicagotraintracker.data.db.StationDao
import com.tommybart.chicagotraintracker.data.db.StationInfoDao
import com.tommybart.chicagotraintracker.data.db.entity.StationInfoEntry
import com.tommybart.chicagotraintracker.data.models.Route
import com.tommybart.chicagotraintracker.data.models.Station
import com.tommybart.chicagotraintracker.data.network.chicagodataportal.CHECK_FOR_UPDATES_DELAY_DAYS
import com.tommybart.chicagotraintracker.data.network.chicagodataportal.SodaApiResponse
import com.tommybart.chicagotraintracker.data.network.chicagodataportal.StationNetworkDataSource
import com.tommybart.chicagotraintracker.internal.extensions.TAG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime

class StationRepositoryImpl(
    private val stationDao: StationDao,
    private val stationInfoDao: StationInfoDao,
    private val stationNetworkDataSource: StationNetworkDataSource
) : StationRepository {

    override suspend fun getStationData(): List<Station> {
        val currentDate = ZonedDateTime.now(ZoneId.of(Route.CHICAGO_ZONE_ID)).toLocalDate()
        return withContext(Dispatchers.IO) {
            fetchStationData(currentDate)
            return@withContext stationDao.getStationEntriesSync().map { stationEntry ->
                stationEntry.toStation()
            }
        }
    }

    private suspend fun fetchStationData(currentDate: LocalDate) {
        if (isFetchStationDataNeeded(currentDate)) {
            deleteOldStationData()
            Log.d(TAG, "Fetching station data")
            persistStationInfo(currentDate, currentDate)
            val response = stationNetworkDataSource.fetchStationData()
            persistFetchedStations(response)
        } else {
            Log.d(TAG, "Fetch station data not needed")
        }
    }

    private suspend fun isFetchStationDataNeeded(currentDate: LocalDate): Boolean {
        val stationInfo = stationInfoDao.getStationInfoSync() ?: return true
        val lastUpdateCheckDateTime = stationInfo.lastUpdateCheckDate
        val delay = currentDate.minusDays(CHECK_FOR_UPDATES_DELAY_DAYS)
        return if (delay.isBefore(lastUpdateCheckDateTime)) {
            Log.d(TAG, "Already checked for updates today")
            Log.d(TAG, "Station: Delay: $delay | Last Update: $lastUpdateCheckDateTime")
            false
        } else {
            fetchIsUpdateNeeded(currentDate, stationInfo.lastFetchDate)
        }
    }

    private suspend fun fetchIsUpdateNeeded(
        currentDate: LocalDate,
        lastFetchDate: LocalDate
    ): Boolean {
        Log.d(TAG, "Checking if station data needs to be updated")
        updateLastUpdateCheckDate(currentDate)
        return stationNetworkDataSource.fetchIsUpdateNeeded(lastFetchDate)
    }

    private fun persistStationInfo(
        currentDate: LocalDate,
        lastUpdateCheckDate: LocalDate
    ) {
        stationInfoDao.upsert(StationInfoEntry(currentDate, lastUpdateCheckDate))
    }

    private fun updateLastUpdateCheckDate(newUpdateCheckDate: LocalDate) {
        stationInfoDao.updateLastUpdateCheckDate(newUpdateCheckDate)
    }

    private fun persistFetchedStations(sodaApiResponse: SodaApiResponse) {
        stationDao.insertAll(sodaApiResponse.stationEntries)
    }

    private fun deleteOldStationData() {
        val deletedStations = stationDao.deleteAll()
        Log.d(TAG, "Deleted $deletedStations stations")
    }
}