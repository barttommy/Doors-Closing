package com.tommybart.chicagotraintracker.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.tommybart.chicagotraintracker.data.db.RouteArrivalsDao
import com.tommybart.chicagotraintracker.data.db.RouteArrivalsInfoDao
import com.tommybart.chicagotraintracker.data.db.RouteArrivalsRequestDao
import com.tommybart.chicagotraintracker.data.db.entity.RouteArrivalsRequestEntry
import com.tommybart.chicagotraintracker.data.models.Route
import com.tommybart.chicagotraintracker.data.models.Route.CHICAGO_ZONE_ID
import com.tommybart.chicagotraintracker.data.network.chicagotransitauthority.CTA_FETCH_DELAY_MINUTES
import com.tommybart.chicagotraintracker.data.network.chicagotransitauthority.RouteArrivalsNetworkDataSource
import com.tommybart.chicagotraintracker.data.network.chicagotransitauthority.CtaApiResponse
import com.tommybart.chicagotraintracker.data.provider.RequestedStationsProvider
import com.tommybart.chicagotraintracker.internal.extensions.TAG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime

class RouteArrivalsRepositoryImpl(
    private val routeArrivalsDao: RouteArrivalsDao,
    private val routeArrivalsInfoDao: RouteArrivalsInfoDao,
    private val routeArrivalsRequestDao: RouteArrivalsRequestDao,
    private val routeArrivalsNetworkDataSource: RouteArrivalsNetworkDataSource,
    private val requestedStationsProvider: RequestedStationsProvider
) : RouteArrivalsRepository {

    init {
        routeArrivalsNetworkDataSource.downloadRouteData.observeForever { ctaApiResponse ->
            persistFetchedData(ctaApiResponse)
        }
    }

    override suspend fun getRouteData(): LiveData<List<Route>> {
        val currentDateTime = ZonedDateTime.now(ZoneId.of(CHICAGO_ZONE_ID)).toLocalDateTime()
        return withContext(Dispatchers.IO) {
            initRouteData(currentDateTime)
            return@withContext Transformations.map(routeArrivalsDao.getRoutesWithArrivals()) {
                routesWithArrivals -> routesWithArrivals.map { it.toRoute() }
            }
        }
    }

    // TODO clean up deleteOldData? Move stuff into a refresh function?
    private suspend fun initRouteData(currentDateTime: LocalDateTime) {
        val lastRequest = routeArrivalsRequestDao.getLastRequestSync()
        if (lastRequest == null ||
            requestedStationsProvider.hasRequestedStationsChanged(lastRequest.lastRequest)) {
            Log.d(TAG, "Getting new stations to request arrivals for")
            val requestMapIds = requestedStationsProvider.getNewRequestMapIds()
            fetchRouteData(requestMapIds)
            deleteOldData(requestMapIds, currentDateTime)
        } else {
            Log.d(TAG, "Request has not changed")
            deleteOldData(lastRequest.lastRequest, currentDateTime)
            if (isFetchRouteDataNeeded(currentDateTime)) {
                fetchRouteData(lastRequest.lastRequest)
            }
        }
    }

    private fun isFetchRouteDataNeeded(currentDateTime: LocalDateTime): Boolean {
        val responseInfo = routeArrivalsInfoDao.getRouteArrivalsInfoSync() ?: return true
        val fetchDateTime = responseInfo.transmissionTime
        val delay = currentDateTime.minusMinutes(CTA_FETCH_DELAY_MINUTES)
        Log.d(TAG, "Route: Delay: $delay | Last Update: $fetchDateTime")
        return fetchDateTime.isBefore(delay)
    }

    private suspend fun fetchRouteData(requestedStationMapIds: List<Int>) {
        Log.d(TAG, "Fetching new route data")
        persistRequest(requestedStationMapIds)
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
            routeArrivalsInfoDao.upsert(ctaApiResponse.routeArrivalsInfo)
        }
    }

    private fun persistRequest(requestedStationMapIds: List<Int>) {
        GlobalScope.launch(Dispatchers.IO) {
            routeArrivalsRequestDao.upsert(RouteArrivalsRequestEntry(requestedStationMapIds))
        }
    }
}