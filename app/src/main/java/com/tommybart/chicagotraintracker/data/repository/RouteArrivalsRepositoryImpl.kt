package com.tommybart.chicagotraintracker.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.tommybart.chicagotraintracker.data.db.RouteArrivalsInfoDao
import com.tommybart.chicagotraintracker.data.db.RouteArrivalsDao
import com.tommybart.chicagotraintracker.data.models.Route
import com.tommybart.chicagotraintracker.data.models.Route.CHICAGO_ZONE_ID
import com.tommybart.chicagotraintracker.data.network.cta.RouteArrivalsNetworkDataSource
import com.tommybart.chicagotraintracker.data.network.cta.response.CtaApiResponse
import com.tommybart.chicagotraintracker.internal.extensions.TAG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime

const val FETCH_DELAY: Long = 1

class RouteArrivalsRepositoryImpl(
    private val routeArrivalsInfoDao: RouteArrivalsInfoDao,
    private val routeArrivalsDao: RouteArrivalsDao,
    private val routeArrivalsNetworkDataSource: RouteArrivalsNetworkDataSource
) : RouteArrivalsRepository {

    init {
        routeArrivalsNetworkDataSource.downloadRouteData.observeForever { ctaApiResponse ->
            persistFetchedData(ctaApiResponse)
        }
    }

    override suspend fun getRouteData(): LiveData<List<Route>> {
        val currentDateTime = ZonedDateTime.now(ZoneId.of(CHICAGO_ZONE_ID)).toLocalDateTime()

        return withContext(Dispatchers.IO) {

            // TODO get stationIds from provider
            //val requestedStations = listOf(40530, 41220)
            val requestedStations = listOf(40530)

            initRouteData(requestedStations, currentDateTime)
            deleteOldData(requestedStations, currentDateTime)
            return@withContext Transformations.map(routeArrivalsDao.getRoutesWithArrivals()) {
                routesWithArrivals -> routesWithArrivals.map { it.toRoute() }
            }
        }
    }

    private suspend fun initRouteData(requestedStations: List<Int>,
                                      currentDateTime: LocalDateTime) {
        if (isFetchRouteDataNeeded(currentDateTime)) {
            fetchRouteData(requestedStations)
        }
    }

    // TODO return true if location changed (location currently unimplemented)
    private fun isFetchRouteDataNeeded(currentDateTime: LocalDateTime): Boolean {
        val responseInfo = routeArrivalsInfoDao.getResponseInfoSync() ?: return true
        val fetchDateTime = LocalDateTime.parse(responseInfo.transmissionTime) ?: return true
        return fetchDateTime.isBefore(currentDateTime.minusMinutes(FETCH_DELAY))
    }

    private suspend fun fetchRouteData(requestedStations: List<Int>) {
        Log.d(TAG, "Fetching new arrival data")
        routeArrivalsNetworkDataSource.fetchRouteData(requestedStations)
    }

    private fun deleteOldData(requestedStations: List<Int>, currentDateTime: LocalDateTime) {
        var deletedArrivals = routeArrivalsDao.deleteArrivalsAtOldStations(requestedStations)
        deletedArrivals += routeArrivalsDao.deleteOldArrivals(currentDateTime)
        val deletedRoutes = routeArrivalsDao.deleteRoutesWithoutArrivals()
        Log.d(TAG, "Deleted $deletedArrivals arrivals and $deletedRoutes routes.")
    }

    private fun persistFetchedData(ctaApiResponse: CtaApiResponse) {
        GlobalScope.launch(Dispatchers.IO) {
            ctaApiResponse.routeArrivalsContainer.routeArrivalsList.forEach { routeWithArrivals ->
                val routeId: Long = routeArrivalsDao.upsertRoute(routeWithArrivals.routeEntry)
                routeArrivalsDao.upsertArrivalsForRoute(routeId, routeWithArrivals.arrivals)
            }
            routeArrivalsInfoDao.upsert(ctaApiResponse.routeArrivalsContainer.routeArrivalsInfo)
        }
    }
}