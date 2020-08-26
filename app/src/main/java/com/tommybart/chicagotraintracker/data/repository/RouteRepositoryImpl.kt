package com.tommybart.chicagotraintracker.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.tommybart.chicagotraintracker.data.db.StateArrivalsDao
import com.tommybart.chicagotraintracker.data.db.entity.statearrivals.StateArrivals
import com.tommybart.chicagotraintracker.data.db.entity.statearrivals.StateInfoEntry
import com.tommybart.chicagotraintracker.data.models.Route
import com.tommybart.chicagotraintracker.data.models.Route.CHICAGO_ZONE_ID
import com.tommybart.chicagotraintracker.data.network.ApiResponse
import com.tommybart.chicagotraintracker.data.network.chicagotransitauthority.CTA_FETCH_DELAY_MINUTES
import com.tommybart.chicagotraintracker.data.network.chicagotransitauthority.CtaApiService
import com.tommybart.chicagotraintracker.data.network.chicagotransitauthority.response.CtaApiResponse
import com.tommybart.chicagotraintracker.data.provider.RequestedStationsProvider
import com.tommybart.chicagotraintracker.internal.Resource
import com.tommybart.chicagotraintracker.internal.arrivalsstate.*
import com.tommybart.chicagotraintracker.internal.extensions.TAG
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime

private const val CTA_MAX_STATION_REQUEST = 4

class RouteRepositoryImpl(
    private val stateArrivalsDao: StateArrivalsDao,
    private val requestedStationsProvider: RequestedStationsProvider,
    private val ctaApiService: CtaApiService
) : RouteRepository {

    override fun getRouteData(
        arrivalsState: ArrivalsState,
        requestedStationMapIds: List<Int>,
        isFetchNeeded: Boolean
    ): LiveData<Resource<List<Route>>> {

        val requestDateTime = ZonedDateTime.now(ZoneId.of(CHICAGO_ZONE_ID)).toLocalDateTime()

        return object : NetworkBoundResource<List<Route>, CtaApiResponse>() {
            override fun saveCallResult(item: CtaApiResponse) {
                persistFetchedRouteData(arrivalsState.id, requestedStationMapIds, item)
            }

            override fun shouldFetch(data: List<Route>?): Boolean {
                if (!isFetchNeeded)
                    deleteOldRouteData(arrivalsState.id, requestedStationMapIds, requestDateTime)
                return isFetchNeeded
            }

            override fun loadFromDb(): LiveData<List<Route>> {
                return Transformations
                    .map(stateArrivalsDao.getStateArrivals(arrivalsState.id)) { it.toRouteList() }
            }

            override fun createCall(): LiveData<ApiResponse<CtaApiResponse>> {
                return ctaApiService
                    .getArrivals(requestedStationMapIds.take(CTA_MAX_STATION_REQUEST))
            }

            override fun onFetchFailed() {
                super.onFetchFailed()
                deleteOldRouteData(arrivalsState.id, requestedStationMapIds, requestDateTime)
            }
        }.asLiveData()
    }

    override suspend fun getRequestStationMapIds(arrivalsState: ArrivalsState): List<Int>? {
        val lastRequestMapIds = stateArrivalsDao.getStateArrivalsSync(arrivalsState.id)
            ?.stateInfoEntry
            ?.lastRequestMapIds
        return if (lastRequestMapIds == null ||
            hasRequestChanged(arrivalsState, lastRequestMapIds)
        ) {
            getNewRequestStationMapIds(arrivalsState)
        } else {
            lastRequestMapIds
        }
    }

    override fun isFetchRouteDataNeeded(
        arrivalsState: ArrivalsState,
        requestedStationMapIds: List<Int>
    ): Boolean {
        val stateInfoEntry = stateArrivalsDao.getStateArrivalsSync(arrivalsState.id)?.stateInfoEntry
        val lastRequestMapIds = stateInfoEntry?.lastRequestMapIds
        return if (stateInfoEntry == null || lastRequestMapIds != requestedStationMapIds) {
            true
        } else {
            val lastTransmission = stateInfoEntry.transmissionTime
            val requestDateTime = ZonedDateTime.now(ZoneId.of(CHICAGO_ZONE_ID)).toLocalDateTime()
            val delay = requestDateTime.minusMinutes(CTA_FETCH_DELAY_MINUTES)
            Log.d(TAG, "Route: Delay: $delay | Last Update: $lastTransmission")
            return lastTransmission.isBefore(delay)
        }
    }

    private suspend fun hasRequestChanged(
        arrivalsState: ArrivalsState,
        lastRequestMapIds: List<Int>
    ): Boolean {
        return when (arrivalsState.id) {
            LOCATION_STATE_ID -> requestedStationsProvider.hasLocationRequestChanged(
                lastRequestMapIds
            )
            DEFAULT_STATE_ID -> {
                return if (lastRequestMapIds.isEmpty() || lastRequestMapIds.size != 1) true
                else requestedStationsProvider.hasDefaultRequestChanged(lastRequestMapIds[0])
            }
            SEARCH_STATE_ID -> {
                return if (lastRequestMapIds.isEmpty() || lastRequestMapIds.size != 1) true
                else lastRequestMapIds[0] != (arrivalsState as SearchState).searchStation.mapId
            }
            else -> throw IllegalArgumentException()
        }
    }

    private suspend fun getNewRequestStationMapIds(arrivalsState: ArrivalsState): List<Int>? {
        return when (arrivalsState.id) {
            LOCATION_STATE_ID -> requestedStationsProvider.getNewLocationRequestMapIds()
            DEFAULT_STATE_ID -> listOf(requestedStationsProvider.getNewDefaultRequestMapId())
            SEARCH_STATE_ID -> listOf((arrivalsState as SearchState).searchStation.mapId)
            else -> throw IllegalArgumentException()
        }
    }

    private fun deleteOldRouteData(
        arrivalsStateId: Int,
        requestedStationMapIds: List<Int>,
        requestDateTime: LocalDateTime
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val deleteCount = stateArrivalsDao.deleteOldData(
                arrivalsStateId,
                requestedStationMapIds,
                requestDateTime
            )
            Log.d(TAG, "Deleted $deleteCount old arrivals.")
        }
    }

    private fun persistFetchedRouteData(
        arrivalsStateId: Int,
        requestedStationMapIds: List<Int>,
        ctaApiResponse: CtaApiResponse
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val arrivalsContainer = ctaApiResponse.arrivalsContainer
            val stateInfoEntry = StateInfoEntry(
                arrivalsStateId,
                requestedStationMapIds,
                LocalDateTime.parse(arrivalsContainer.transmissionTime)
            )
            val stateArrivals = StateArrivals(stateInfoEntry, arrivalsContainer.arrivalEntries)
            stateArrivalsDao.updateStateArrivals(stateArrivals)
        }
    }
}