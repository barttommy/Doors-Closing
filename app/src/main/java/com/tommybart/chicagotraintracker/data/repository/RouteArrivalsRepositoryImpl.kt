package com.tommybart.chicagotraintracker.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.tommybart.chicagotraintracker.data.db.RouteArrivalsDao
import com.tommybart.chicagotraintracker.data.db.RouteArrivalsInfoDao
import com.tommybart.chicagotraintracker.data.models.Route
import com.tommybart.chicagotraintracker.data.models.Route.CHICAGO_ZONE_ID
import com.tommybart.chicagotraintracker.data.network.chicagotransitauthority.RouteArrivalsNetworkDataSource
import com.tommybart.chicagotraintracker.data.network.chicagotransitauthority.CtaApiResponse
import com.tommybart.chicagotraintracker.internal.extensions.TAG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime

const val CTA_FETCH_MINUTES_DELAY: Long = 1

class RouteArrivalsRepositoryImpl(
    private val routeArrivalsDao: RouteArrivalsDao,
    private val routeArrivalsInfoDao: RouteArrivalsInfoDao,
    private val routeArrivalsNetworkDataSource: RouteArrivalsNetworkDataSource
) : RouteArrivalsRepository {

    init {
        routeArrivalsNetworkDataSource.downloadRouteData.observeForever { ctaApiResponse ->
            persistFetchedData(ctaApiResponse)
        }
    }

    override suspend fun getRouteData(requestedStationMapIds: List<Int>): LiveData<List<Route>> {
        val currentDateTime = ZonedDateTime.now(ZoneId.of(CHICAGO_ZONE_ID)).toLocalDateTime()
        return withContext(Dispatchers.IO) {
            initRouteData(requestedStationMapIds, currentDateTime)
            deleteOldData(requestedStationMapIds, currentDateTime)
            return@withContext Transformations.map(routeArrivalsDao.getRoutesWithArrivals()) {
                routesWithArrivals -> routesWithArrivals.map { it.toRoute() }
            }
        }
    }

    private suspend fun initRouteData(requestedStationMapIds: List<Int>,
                                      currentDateTime: LocalDateTime) {
        if (isFetchRouteDataNeeded(currentDateTime)) {
            fetchRouteData(requestedStationMapIds)
        }
    }

    // TODO return true if location changed (location currently unimplemented)
    // TODO return true if default pref changed
    private fun isFetchRouteDataNeeded(currentDateTime: LocalDateTime): Boolean {
        return true
        TODO("Implement changes above")
        val responseInfo = routeArrivalsInfoDao.getRouteArrivalsInfoSync() ?: return true
        val fetchDateTime = LocalDateTime.parse(responseInfo.transmissionTime) ?: return true
        return fetchDateTime.isBefore(currentDateTime.minusMinutes(CTA_FETCH_MINUTES_DELAY))
    }

    private suspend fun fetchRouteData(requestedStationMapIds: List<Int>) {
        Log.d(TAG, "Fetching new arrival data")
        routeArrivalsNetworkDataSource.fetchRouteData(requestedStationMapIds)
    }

    private fun deleteOldData(requestedStationMapIds: List<Int>, currentDateTime: LocalDateTime) {
        var deletedArrivals = routeArrivalsDao.deleteArrivalsAtOldStations(requestedStationMapIds)
        deletedArrivals += routeArrivalsDao.deleteOldArrivals(currentDateTime)
        val deletedRoutes = routeArrivalsDao.deleteRoutesWithoutArrivals()
        Log.d(TAG, "Deleted $deletedArrivals arrivals and $deletedRoutes routes.")
    }

    private fun persistFetchedData(ctaApiResponse: CtaApiResponse) {
        GlobalScope.launch(Dispatchers.IO) {
            ctaApiResponse.routeArrivalsList.forEach { routeWithArrivals ->
                val routeId: Long = routeArrivalsDao.upsertRoute(routeWithArrivals.routeEntry)
                routeArrivalsDao.upsertArrivalsForRoute(routeId, routeWithArrivals.arrivals)
            }
            routeArrivalsInfoDao.upsert(
                ctaApiResponse.routeArrivalsInfo)
        }
    }
}