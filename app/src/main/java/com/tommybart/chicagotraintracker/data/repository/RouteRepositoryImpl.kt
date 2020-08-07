package com.tommybart.chicagotraintracker.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.tommybart.chicagotraintracker.data.db.ResponseInfoDao
import com.tommybart.chicagotraintracker.data.db.RouteWithArrivalsDao
import com.tommybart.chicagotraintracker.data.models.Route
import com.tommybart.chicagotraintracker.data.models.Route.CHICAGO_ZONE_ID
import com.tommybart.chicagotraintracker.data.network.cta.RouteNetworkDataSource
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

class RouteRepositoryImpl(
    private val responseInfoDao: ResponseInfoDao,
    private val routeWithArrivalsDao: RouteWithArrivalsDao,
    private val routeNetworkDataSource: RouteNetworkDataSource
) : RouteRepository {

    init {
        routeNetworkDataSource.downloadRouteData.observeForever { ctaApiResponse ->
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
            return@withContext Transformations.map(routeWithArrivalsDao.getRoutesWithArrivals()) {
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
        val responseInfo = responseInfoDao.getResponseInfoSync() ?: return true
        val fetchDateTime = LocalDateTime.parse(responseInfo.transmissionTime)
        return fetchDateTime.isBefore(currentDateTime.minusMinutes(FETCH_DELAY))
    }

    private suspend fun fetchRouteData(requestedStations: List<Int>) {
        Log.d(TAG, "Fetching new arrival data")
        routeNetworkDataSource.fetchRouteData(requestedStations)
    }

    private fun deleteOldData(requestedStations: List<Int>, currentDateTime: LocalDateTime) {
        var deletedArrivals = routeWithArrivalsDao.deleteArrivalsAtOldStations(requestedStations)
        deletedArrivals += routeWithArrivalsDao.deleteOldArrivals(currentDateTime)
        val deletedRoutes = routeWithArrivalsDao.deleteRoutesWithoutArrivals()
        Log.d(TAG, "Deleted $deletedArrivals arrivals and $deletedRoutes routes.")
    }

    private fun persistFetchedData(ctaApiResponse: CtaApiResponse) {
        GlobalScope.launch(Dispatchers.IO) {
            ctaApiResponse.routeContainer.routeWithArrivalsList.forEach { routeWithArrivals ->
                val routeId: Long = routeWithArrivalsDao.upsertRoute(routeWithArrivals.routeEntry)
                routeWithArrivalsDao.upsertArrivalsForRoute(routeId, routeWithArrivals.arrivals)
            }
            responseInfoDao.upsert(ctaApiResponse.routeContainer.responseInfo)
        }
    }
}