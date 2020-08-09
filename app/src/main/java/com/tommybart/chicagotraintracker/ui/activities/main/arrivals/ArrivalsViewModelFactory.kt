package com.tommybart.chicagotraintracker.ui.activities.main.arrivals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tommybart.chicagotraintracker.data.provider.RequestedStationsProvider
import com.tommybart.chicagotraintracker.data.repository.RouteArrivalsRepository
import com.tommybart.chicagotraintracker.data.repository.StationRepository

class ArrivalsViewModelFactory(
    private val routeArrivalsRepository: RouteArrivalsRepository,
    private val requestedStationsProvider: RequestedStationsProvider
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ArrivalsViewModel(routeArrivalsRepository, requestedStationsProvider) as T
    }
}