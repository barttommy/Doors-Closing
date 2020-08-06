package com.tommybart.chicagotraintracker.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.tommybart.chicagotraintracker.data.db.RouteDao
import com.tommybart.chicagotraintracker.data.db.entity.RouteWithArrivals
import com.tommybart.chicagotraintracker.data.network.cta.ArrivalsNetworkDataSource
import com.tommybart.chicagotraintracker.data.network.cta.CHICAGO_ZONE_ID
import com.tommybart.chicagotraintracker.data.network.cta.response.CtaApiResponse
import com.tommybart.chicagotraintracker.internal.extensions.TAG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

    override suspend fun getRouteData(): LiveData<List<RouteWithArrivals>> {

//        fun deleteOldArrivals(): Int {
//            val currentDate = ZonedDateTime.now(ZoneId.of(CHICAGO_ZONE_ID))
//                .toLocalDateTime()
//                .toString()
//            Log.d(TAG, currentDate)
//            return routeDao.deleteOldArrivals(currentDate)
//        }

        return withContext(Dispatchers.IO) {
            initArrivalData()
//            val deletedArrivals = deleteOldArrivals()
//            val deletedRoutes = routeDao.deleteRoutesWithoutArrivals()
//            Log.d(TAG, "Deleted $deletedArrivals arrivals and $deletedRoutes routes.")
            return@withContext routeDao.getRoutesWithArrivals()
        }
    }

    private suspend fun initArrivalData() {
        if (isFetchArrivalDataNeeded()) {
            fetchArrivalData()
        }
    }

    private suspend fun fetchArrivalData() {
        // TODO get mapIds from provider
        Log.d(TAG, "fetching arrival data")
        arrivalsNetworkDataSource.fetchArrivalData(listOf("40530", "41220"))
    }

    private fun isFetchArrivalDataNeeded(): Boolean {
        // TODO transmission time dao (persist data from arrivals container)
        // TODO check against last transmission time
        return true
    }

    private fun persistFetchedArrivals(fetchedArrivals: CtaApiResponse) {
        GlobalScope.launch(Dispatchers.IO) {
            fetchedArrivals.arrivalsContainer.routeWithArrivalsList.forEach { routeWithArrivals ->
                val routeId: Long = routeDao.upsertRoute(routeWithArrivals.route)
                routeDao.upsertArrivalsForRoute(routeId, routeWithArrivals.arrivals)
            }
        }
    }
}