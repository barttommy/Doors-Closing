package com.tommybart.chicagotraintracker.ui.activities.search

import androidx.lifecycle.ViewModel
import com.tommybart.chicagotraintracker.data.repository.StationRepository
import com.tommybart.chicagotraintracker.internal.lazyDeferred

class SearchViewModel(
    stationRepository: StationRepository
) : ViewModel() {
    val stationData by lazyDeferred {
        stationRepository.getStationData()
    }
}