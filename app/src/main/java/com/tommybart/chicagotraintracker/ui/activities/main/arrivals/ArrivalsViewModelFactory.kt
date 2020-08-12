package com.tommybart.chicagotraintracker.ui.activities.main.arrivals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tommybart.chicagotraintracker.data.provider.PreferenceProvider
import com.tommybart.chicagotraintracker.data.repository.RouteArrivalsRepository

class ArrivalsViewModelFactory(
    private val routeArrivalsRepository: RouteArrivalsRepository,
    private val preferenceProvider: PreferenceProvider
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ArrivalsViewModel(routeArrivalsRepository, preferenceProvider) as T
    }
}