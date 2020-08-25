package com.tommybart.chicagotraintracker.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.tommybart.chicagotraintracker.data.db.StateArrivalsDao
import com.tommybart.chicagotraintracker.data.db.entity.statearrivals.StateArrivals
import com.tommybart.chicagotraintracker.data.db.entity.statearrivals.StateInfoEntry
import com.tommybart.chicagotraintracker.data.models.Route
import com.tommybart.chicagotraintracker.data.models.Route.CHICAGO_ZONE_ID
import com.tommybart.chicagotraintracker.data.network.chicagotransitauthority.CTA_FETCH_DELAY_MINUTES
import com.tommybart.chicagotraintracker.data.network.chicagotransitauthority.CtaNetworkDataSource
import com.tommybart.chicagotraintracker.data.network.chicagotransitauthority.response.CtaApiResponse
import com.tommybart.chicagotraintracker.data.provider.RequestedStationsProvider
import com.tommybart.chicagotraintracker.internal.arrivalsstate.*
import com.tommybart.chicagotraintracker.internal.extensions.TAG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime

class RouteRepositoryImpl(
    private val stateArrivalsDao: StateArrivalsDao,
    private val ctaNetworkDataSource: CtaNetworkDataSource,
    private val requestedStationsProvider: RequestedStationsProvider
) : RouteRepository {

    // TODO Refactor to use NetworkBoundResource? Need a way to show in progress operations, swipe
    //  to refresh doesn't work as expected in this implementation

    override fun getRouteData(arrivalsState: ArrivalsState): LiveData<List<Route>?> {
        refreshRouteData(arrivalsState)
        return Transformations
            .map(stateArrivalsDao.getStateArrivals(arrivalsState.id)) { stateArrivals ->
                stateArrivals?.toRouteList()
        }
    }

    private fun refreshRouteData(arrivalsState: ArrivalsState) {
        GlobalScope.launch(Dispatchers.IO) {
            val requestDateTime = ZonedDateTime.now(ZoneId.of(CHICAGO_ZONE_ID)).toLocalDateTime()
            val stateInfoEntry = stateArrivalsDao.getStateArrivalsSync(arrivalsState.id)
                ?.stateInfoEntry
            val lastRequestMapIds = stateInfoEntry?.lastRequestMapIds
            if (lastRequestMapIds == null || hasRequestChanged(arrivalsState, lastRequestMapIds)) {
                getNewRequestMapIds(arrivalsState)?.let {
                    updateRouteData(arrivalsState.id, it, requestDateTime, true)
                }
            } else {
                val lastTransmission = stateInfoEntry.transmissionTime
                updateRouteData(
                    arrivalsState.id,
                    lastRequestMapIds,
                    requestDateTime,
                    isFetchRouteDataNeeded(requestDateTime, lastTransmission)
                )
            }
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

    private suspend fun getNewRequestMapIds(arrivalsState: ArrivalsState): List<Int>? {
        return when (arrivalsState.id) {
            LOCATION_STATE_ID -> requestedStationsProvider.getNewLocationRequestMapIds()
            DEFAULT_STATE_ID -> listOf(
                requestedStationsProvider.getNewDefaultRequestMapId() ?: return null
            )
            SEARCH_STATE_ID -> listOf((arrivalsState as SearchState).searchStation.mapId)
            else -> throw IllegalArgumentException()
        }
    }

    private fun isFetchRouteDataNeeded(
        requestDateTime: LocalDateTime,
        lastTransmission: LocalDateTime
    ): Boolean {
        val delay = requestDateTime.minusMinutes(CTA_FETCH_DELAY_MINUTES)
        Log.d(TAG, "Route: Delay: $delay | Last Update: $lastTransmission")
        return lastTransmission.isBefore(delay)
    }

    private suspend fun updateRouteData(
        arrivalsStateId: Int,
        requestedStationMapIds: List<Int>,
        requestDateTime: LocalDateTime,
        isFetchNeeded: Boolean
    ) {
        if (isFetchNeeded) {
            fetchRouteData(arrivalsStateId, requestedStationMapIds, requestDateTime)
        } else {
            Log.d(TAG, "Fetch is not needed")
            deleteOldRouteData(arrivalsStateId, requestedStationMapIds, requestDateTime)
        }
    }

    private fun deleteOldRouteData(
        arrivalsStateId: Int,
        requestedStationMapIds: List<Int>,
        requestDateTime: LocalDateTime
    ) {
        val deleteCount = stateArrivalsDao.deleteOldData(
            arrivalsStateId,
            requestedStationMapIds,
            requestDateTime
        )
        Log.d(TAG, "Deleted $deleteCount old arrivals.")
    }

    private suspend fun fetchRouteData(
        arrivalsStateId: Int,
        requestedStationMapIds: List<Int>,
        requestDateTime: LocalDateTime
    ) {
        Log.d(TAG, "Fetching new route data")
        val ctaApiResponse = ctaNetworkDataSource.fetchRouteData(requestedStationMapIds)
        ctaApiResponse?.let { persistFetchedRouteData(arrivalsStateId, requestedStationMapIds, it) }
            ?: deleteOldRouteData(arrivalsStateId, requestedStationMapIds, requestDateTime)
    }

    private fun persistFetchedRouteData(
        arrivalsStateId: Int,
        requestedStationMapIds: List<Int>,
        ctaApiResponse: CtaApiResponse
    ) {
        GlobalScope.launch(Dispatchers.IO) {
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