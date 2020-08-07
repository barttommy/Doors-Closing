package com.tommybart.chicagotraintracker.ui.arrivals

import androidx.lifecycle.ViewModel
import com.tommybart.chicagotraintracker.data.repository.RouteArrivalsRepository
import com.tommybart.chicagotraintracker.data.repository.StationRepository
import com.tommybart.chicagotraintracker.internal.lazyDeferred

class ArrivalsViewModel(
    private val stationRepository: StationRepository,
    private val routeArrivalsRepository: RouteArrivalsRepository
) : ViewModel() {

    val stationData by lazyDeferred {
        stationRepository.getStationData()
    }

    val routeData by lazyDeferred {
        routeArrivalsRepository.getRouteData()
    }
}
