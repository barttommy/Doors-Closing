package com.tommybart.chicagotraintracker.ui.activities.main.arrivals

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.tommybart.chicagotraintracker.data.models.Route
import com.tommybart.chicagotraintracker.data.provider.RequestedStationsProvider
import com.tommybart.chicagotraintracker.data.repository.RouteArrivalsRepository
import com.tommybart.chicagotraintracker.internal.lazyDeferred
import kotlinx.coroutines.Deferred

class ArrivalsViewModel(
    private val routeArrivalsRepository: RouteArrivalsRepository,
    requestedStationsProvider: RequestedStationsProvider
) : ViewModel() {

    private val requestedStationMapIds: List<Int> =
        requestedStationsProvider.getRequestedStationMapIds()

    val routeData: Deferred<LiveData<List<Route>>> by lazyDeferred {
        routeArrivalsRepository.getRouteData(requestedStationMapIds)
    }
}
