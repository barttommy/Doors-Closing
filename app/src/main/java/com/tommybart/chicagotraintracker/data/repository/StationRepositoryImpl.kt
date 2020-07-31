package com.tommybart.chicagotraintracker.data.repository

import androidx.lifecycle.LiveData
import com.tommybart.chicagotraintracker.data.db.StationDao
import com.tommybart.chicagotraintracker.data.db.entity.StationEntry
import com.tommybart.chicagotraintracker.data.network.StationNetworkDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StationRepositoryImpl(
    private val stationDao: StationDao,
    private val stationNetworkDataSource: StationNetworkDataSource
) : StationRepository {

    init {
        stationNetworkDataSource.downloadStationData.observeForever { stationEntries ->
            saveFetchedStationData(stationEntries)
        }
    }

    override suspend fun getStationData(): LiveData<List<StationEntry>> {
        return withContext(Dispatchers.IO){
            initStationData()
            return@withContext stationDao.getStationData()
        }
    }

    private suspend fun initStationData() {
        if (isFetchStationDataNeeded()) {
            fetchStationData()
        }
    }

    private suspend fun fetchStationData() {
        stationNetworkDataSource.fetchStationData()
    }

    /* TODO: What's reasonable here? Data is extremely static - only changes when
        real world infrastructure changes (new stations built / destroyed). */
    private fun isFetchStationDataNeeded(): Boolean {
        return true
    }

    private fun saveFetchedStationData(stationEntries: List<StationEntry>) {
        GlobalScope.launch(Dispatchers.IO) {
            stationDao.upsert(stationEntries)
        }
    }
}