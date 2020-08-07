package com.tommybart.chicagotraintracker.data.repository

import android.util.Log
import com.tommybart.chicagotraintracker.data.db.StationDao
import com.tommybart.chicagotraintracker.data.db.StationInfoDao
import com.tommybart.chicagotraintracker.data.db.entity.StationEntry
import com.tommybart.chicagotraintracker.data.db.entity.StationInfoEntry
import com.tommybart.chicagotraintracker.data.models.Route
import com.tommybart.chicagotraintracker.data.models.Station
import com.tommybart.chicagotraintracker.data.network.chicagodataportal.StationNetworkDataSource
import com.tommybart.chicagotraintracker.internal.extensions.TAG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime

const val CDP_FETCH_DELAY_DAYS: Long = 7

class StationRepositoryImpl(
    private val stationDao: StationDao,
    private val stationInfoDao: StationInfoDao,
    private val stationNetworkDataSource: StationNetworkDataSource
) : StationRepository {

    override suspend fun getStationData(): List<Station> {
        return withContext(Dispatchers.IO){
            fetchStationData()
            return@withContext stationDao.getStationEntriesSync().map {
                stationEntry -> stationEntry.toStation()
            }
        }
    }

    private suspend fun fetchStationData() {
        if (isFetchStationDataNeeded()) {
            deleteOldStationData()
            Log.d(TAG, "Fetching station data")
            saveFetchedStationData(stationNetworkDataSource.fetchStationData())
        }
    }

    /*
     * Data is extremely static - only changes when real world infrastructure changes (new stations
     * built / destroyed). Here we only fetch once a week. Could do once a day to ensure the app
     * works for a brand new station on its first day, at the cost of an slower load time more
     * often. TODO: Look into how many requests api allows, once a day might be good here.
     */
    private fun isFetchStationDataNeeded(): Boolean {
        val stationInfo = stationInfoDao.getStationInfoSync() ?: return true
        val fetchDateTime = LocalDateTime.parse(stationInfo.lastFetchTime)
        val currentDateTime = ZonedDateTime.now(ZoneId.of(Route.CHICAGO_ZONE_ID)).toLocalDateTime()
        return fetchDateTime.isBefore(currentDateTime.minusDays(CDP_FETCH_DELAY_DAYS))
    }

    private fun saveFetchedStationData(stationEntries: List<StationEntry>) {
        val currentDateTime = ZonedDateTime.now(ZoneId.of(Route.CHICAGO_ZONE_ID))
            .toLocalDateTime()
            .toString()
        stationDao.insertAll(stationEntries)
        stationInfoDao.upsert(StationInfoEntry(currentDateTime))
    }

    private fun deleteOldStationData() {
        val deletedStations = stationDao.deleteAll()
        Log.d(TAG, "Deleted $deletedStations stations")
    }
}