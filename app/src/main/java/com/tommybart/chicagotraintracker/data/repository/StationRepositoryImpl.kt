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
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime

class StationRepositoryImpl(
    private val stationDao: StationDao,
    private val stationInfoDao: StationInfoDao,
    private val stationNetworkDataSource: StationNetworkDataSource
) : StationRepository {

    // TODO init right away? station data is needed everywhere

    override suspend fun getStationData(): List<Station> {
        val currentDateTime = ZonedDateTime.now(ZoneId.of(Route.CHICAGO_ZONE_ID)).toLocalDateTime()
        return withContext(Dispatchers.IO){
            fetchStationData(currentDateTime)
            return@withContext stationDao.getStationEntriesSync().map {
                stationEntry -> stationEntry.toStation()
            }
        }
    }

    private suspend fun fetchStationData(currentDateTime: LocalDateTime) {
        if (isFetchStationDataNeeded(currentDateTime)) {
            deleteOldStationData()
            Log.d(TAG, "Fetching station data")
            persistStationInfo(currentDateTime, currentDateTime)
            persistFetchedStations(stationNetworkDataSource.fetchStationData())
        } else {
            Log.d(TAG, "Fetch station data not needed")
        }
    }

    private suspend fun isFetchStationDataNeeded(currentDateTime: LocalDateTime): Boolean {
        val stationInfo = stationInfoDao.getStationInfoSync() ?: return true
        val lastUpdateCheckDateTime = LocalDateTime.parse(stationInfo.lastUpdateCheckDate)
        val delay = currentDateTime.minusDays(CHECK_FOR_UPDATES_DELAY_DAYS)
        return if (delay.isBefore(lastUpdateCheckDateTime)) {
            Log.d(TAG, "Already checked for updates today")
            false
        } else {
            fetchIsUpdateNeeded(currentDateTime, LocalDate.parse(stationInfo.lastFetchDate))
        }
    }

    private suspend fun fetchIsUpdateNeeded(currentDateTime: LocalDateTime,
                                            lastFetchDate: LocalDate): Boolean {
        Log.d(TAG, "Checking if station data needs to be updated")
        updateLastUpdateCheckDate(currentDateTime)
        return stationNetworkDataSource.fetchIsUpdateNeeded(lastFetchDate)
    }

    private fun persistStationInfo(currentDateTime: LocalDateTime,
                                   lastUpdateCheckDate: LocalDateTime) {
        stationInfoDao.upsert(
            StationInfoEntry(currentDateTime.toString(), lastUpdateCheckDate.toString()))
    }

    private fun updateLastUpdateCheckDate(newUpdateCheckDate: LocalDateTime) {
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