package com.tommybart.chicagotraintracker.ui.arrivals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tommybart.chicagotraintracker.data.repository.RouteArrivalsRepository
import com.tommybart.chicagotraintracker.data.repository.StationRepository

class ArrivalsViewModelFactory(
    private val stationRepository: StationRepository,
    private val routeArrivalsRepository: RouteArrivalsRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ArrivalsViewModel(stationRepository, routeArrivalsRepository) as T
    }
}