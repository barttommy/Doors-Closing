package com.tommybart.chicagotraintracker.ui.arrivals

import androidx.lifecycle.ViewModel
import com.tommybart.chicagotraintracker.data.repository.StationRepository
import com.tommybart.chicagotraintracker.internal.lazyDeferred

class ArrivalsViewModel(
    private val stationRepository: StationRepository
) : ViewModel() {
    val stationData by lazyDeferred {
        stationRepository.getStationData()
    }
}
