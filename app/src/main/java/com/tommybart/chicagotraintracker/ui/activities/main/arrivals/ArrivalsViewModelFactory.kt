package com.tommybart.chicagotraintracker.ui.activities.main.arrivals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tommybart.chicagotraintracker.data.provider.PreferenceProvider
import com.tommybart.chicagotraintracker.data.repository.RouteRepository

class ArrivalsViewModelFactory(
    private val routeRepository: RouteRepository,
    private val preferenceProvider: PreferenceProvider
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ArrivalsViewModel(routeRepository, preferenceProvider) as T
    }
}