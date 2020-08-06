package com.tommybart.chicagotraintracker.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.tommybart.chicagotraintracker.data.db.RouteDao
import com.tommybart.chicagotraintracker.data.models.Route
import com.tommybart.chicagotraintracker.data.models.Route.CHICAGO_ZONE_ID
import com.tommybart.chicagotraintracker.data.network.cta.ArrivalsNetworkDataSource
import com.tommybart.chicagotraintracker.data.network.cta.response.CtaApiResponse
import com.tommybart.chicagotraintracker.internal.extensions.TAG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime

class ArrivalsRepositoryImpl(
    private val routeDao: RouteDao,
    private val arrivalsNetworkDataSource: ArrivalsNetworkDataSource
) : ArrivalsRepository {

    init {
        arrivalsNetworkDataSource.downloadArrivalData.observeForever { ctaApiResponse ->
            persistFetchedArrivals(ctaApiResponse)
        }
    }

    override suspend fun getRouteData(): LiveData<List<Route>> {
        val currentDate = ZonedDateTime.now(ZoneId.of(CHICAGO_ZONE_ID)).toLocalDateTime()
        return withContext(Dispatchers.IO) {

            // TODO get stationIds from provider
            //val requestedStations = listOf(40530, 41220)
            val requestedStations = listOf(40530)

            initArrivalData(requestedStations)
            deleteOldData(requestedStations, currentDate)
            return@withContext Transformations.map(routeDao.getRoutesWithArrivals()) {
                routesWithArrivals -> routesWithArrivals.map { it.toRoute() }
            }
        }
    }

    private suspend fun initArrivalData(requestedStations: List<Int>) {
        if (isFetchArrivalDataNeeded()) {
            fetchArrivalData(requestedStations)
        }
    }

    private suspend fun fetchArrivalData(requestedStations: List<Int>) {
        Log.d(TAG, "fetching arrival data")
        arrivalsNetworkDataSource.fetchArrivalData(requestedStations)
    }

    private fun isFetchArrivalDataNeeded(): Boolean {
        // TODO transmission time dao (persist data from arrivals container)
        // TODO check against last transmission time
        return true
    }

    private fun deleteOldData(requestedStations: List<Int>, currentDate: LocalDateTime) {
        var deletedArrivals = routeDao.deleteArrivalsAtOldStations(requestedStations)
        deletedArrivals += routeDao.deleteOldArrivals(currentDate)
        val deletedRoutes = routeDao.deleteRoutesWithoutArrivals()
        Log.d(TAG, "Deleted $deletedArrivals arrivals and $deletedRoutes routes.")
    }

    private fun persistFetchedArrivals(fetchedArrivals: CtaApiResponse) {
        GlobalScope.launch(Dispatchers.IO) {
            fetchedArrivals.arrivalsContainer.routeWithArrivalsList.forEach { routeWithArrivals ->
                val routeId: Long = routeDao.upsertRoute(routeWithArrivals.routeEntry)
                routeDao.upsertArrivalsForRoute(routeId, routeWithArrivals.arrivals)
            }
        }
    }
}